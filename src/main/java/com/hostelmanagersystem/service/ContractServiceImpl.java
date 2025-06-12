package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.*;
import com.hostelmanagersystem.dto.response.ContractResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.entity.manager.Tenant;
import com.hostelmanagersystem.enums.ContractStatus;
import com.hostelmanagersystem.enums.RoomStatus;
import com.hostelmanagersystem.enums.TenantStatus;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.ContractMapper;
import com.hostelmanagersystem.mapper.RoomMapper;
import com.hostelmanagersystem.repository.ContractRepository;
import com.hostelmanagersystem.repository.RoomRepository;
import com.hostelmanagersystem.repository.TenantRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractServiceImpl implements ContractService {
    ContractRepository contractRepository;
    ContractMapper contractMapper;
    RoomRepository roomRepository;
    TenantRepository tenantRepository;
    MongoTemplate mongoTemplate;

    static final int EXPIRING_SOON_DAYS = 7;
    private final RoomMapper roomMapper;

    @Override
    public void renewContract(String contractId, int additionalMonths) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new AppException(ErrorCode.CONTRACT_CANNOT_BE_RENEWED);
        }

        contract.setEndDate(contract.getEndDate().plusMonths(additionalMonths));
        contractRepository.save(contract);
    }


    public List<ContractResponse> getAllContractsByOwner(String ownerId) {
        return contractRepository.findByOwnerId(ownerId).stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    @Override
    public ContractResponse createContract(ContractCreateRequest request, String ownerId) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        // Phòng phải ở trạng thái AVAILABLE mới được tạo hợp đồng
        if (room.getStatus() != RoomStatus.RESERVED) {
            throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
        }

        Contract contract = contractMapper.toEntity(request);
        contract.setOwnerId(ownerId);
        contract.setCreatedAt(LocalDate.now());
        contract.setUpdatedAt(LocalDate.now());

        // Mặc định mới tạo hợp đồng, trạng thái là PENDING (chờ bắt đầu, chưa hiệu lực)
        contract.setStatus(ContractStatus.PENDING);

        String pdfUrl = generatePdf(contract);
        contract.setPdfUrl(pdfUrl);

        contract = contractRepository.save(contract);

        // Cập nhật trạng thái phòng sang RESERVED (đã có hợp đồng chờ duyệt)
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);



        return contractMapper.toResponse(contract);
    }

    @Override
    public ContractResponse approveContract(String contractId, String ownerId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (!contract.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (contract.getStatus() != ContractStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_CONTRACT_STATUS); // Chỉ duyệt hợp đồng ở trạng thái PENDING
        }

        // Duyệt hợp đồng thành công -> chuyển trạng thái ACTIVE
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setUpdatedAt(LocalDate.now());

        // Cập nhật trạng thái phòng sang OCCUPIED / ACTIVE
        Room room = roomRepository.findById(contract.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        // Cập nhật tenant status nếu có
        Tenant tenant = tenantRepository.findById(contract.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        tenant.setStatus(TenantStatus.CONTRACT_CONFIRMED);
        tenantRepository.save(tenant);

        contract = contractRepository.save(contract);
        return contractMapper.toResponse(contract);
    }

    @Override
    public ContractResponse updateContract(String contractId, ContractUpdateRequest request, String ownerId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        if (!contract.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        contractMapper.updateContractFromRequest(request, contract);
        contract.setUpdatedAt(LocalDate.now());

        // Nếu update ngày bắt đầu/kết thúc -> có thể cần update trạng thái hợp đồng (nếu muốn)
        contract = updateContractStatusByDate(contract);

        contract = contractRepository.save(contract);
        return contractMapper.toResponse(contract);
    }

    @Override
    public ContractResponse renewContract(String contractId, ContractRenewRequest request, String ownerId) {
        Contract oldContract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        if (!oldContract.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        // Kết thúc hợp đồng cũ (hoặc để trạng thái COMPLETED hoặc EXPIRED tùy nghiệp vụ)
        oldContract.setStatus(ContractStatus.EXPIRED);
        oldContract.setUpdatedAt(LocalDate.now());
        contractRepository.save(oldContract);

        Contract newContract = new Contract();
        newContract.setOwnerId(ownerId);
        newContract.setTenantId(oldContract.getTenantId());
        newContract.setRoomId(oldContract.getRoomId());
        newContract.setStartDate(request.getNewStartDate());
        newContract.setEndDate(request.getNewEndDate());
        newContract.setDeposit(oldContract.getDeposit());
        newContract.setMonthlyPrice(oldContract.getMonthlyPrice());
        newContract.setTerms(oldContract.getTerms());
        newContract.setStatus(ContractStatus.PENDING);  // Hợp đồng mới lúc đầu là PENDING, chờ duyệt
        newContract.setCreatedAt(LocalDate.now());
        newContract.setUpdatedAt(LocalDate.now());
        newContract.setPdfUrl(generatePdf(newContract));

        newContract = contractRepository.save(newContract);

        // Có thể update trạng thái phòng nếu cần
        // Ví dụ: phòng chuyển sang RESERVED khi có hợp đồng mới PENDING
        Room room = roomRepository.findById(newContract.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);

        return contractMapper.toResponse(newContract);
    }

    @Override
    public ContractResponse terminateContract(String contractId, ContractTerminationRequest request, String ownerId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        if (!contract.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        contract.setStatus(ContractStatus.TERMINATED);
        contract.setTerminationReason(request.getReason());
        contract.setTerminationDate(request.getTerminationDate());
        contract.setUpdatedAt(LocalDate.now());
        contract = contractRepository.save(contract);

        // Cập nhật trạng thái phòng sang CLEANING hoặc AVAILABLE tùy nghiệp vụ
        Room room = roomRepository.findById(contract.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        room.setStatus(RoomStatus.CLEANING);
        roomRepository.save(room);

        // Cập nhật tenant status nếu có
        Tenant tenant = tenantRepository.findById(contract.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
         tenant.setStatus(TenantStatus.COMPLETED);
         tenantRepository.save(tenant);

        return contractMapper.toResponse(contract);
    }

    @Override
    public ContractResponse cancelContract(String contractId, String ownerId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        if (!contract.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        // Chỉ cho hủy khi hợp đồng đang PENDING
        if (contract.getStatus() != ContractStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_CONTRACT_STATUS);
        }
        contract.setStatus(ContractStatus.CANCELLED);
        contract.setUpdatedAt(LocalDate.now());
        contract = contractRepository.save(contract);

        // Cập nhật trạng thái phòng về AVAILABLE nếu hợp đồng bị hủy
        Room room = roomRepository.findById(contract.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        return contractMapper.toResponse(contract);
    }

    @Override
    public ContractResponse getContractById(String contractId, String userId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        if (!contract.getOwnerId().equals(userId) && !contract.getTenantId().equals(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        // Tự động cập nhật trạng thái hợp đồng theo ngày
        contract = updateContractStatusByDate(contract);
        contractRepository.save(contract);

        return contractMapper.toResponse(contract);
    }

    @Override
    public List<ContractResponse> getContractsByOwner(String ownerId) {
        List<Contract> contracts = contractRepository.findByOwnerId(ownerId);

        // Cập nhật trạng thái cho từng hợp đồng (nếu cần)
        contracts = contracts.stream()
                .map(this::updateContractStatusByDate)
                .collect(Collectors.toList());
        contractRepository.saveAll(contracts);

        return contracts.stream()
                .map(contractMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractResponse> getContractsByTenant(String tenantId) {
        List<Contract> contracts = contractRepository.findByTenantId(tenantId);

        contracts = contracts.stream()
                .map(this::updateContractStatusByDate)
                .collect(Collectors.toList());
        contractRepository.saveAll(contracts);

        return contracts.stream()
                .map(contractMapper::toResponse)
                .collect(Collectors.toList());
    }



    @Override
    public ContractResponse signContract(String contractId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (!userId.equals(contract.getOwnerId()) && userId.equals(contract.getTenantId())) {
            throw new AppException(ErrorCode.THIS_ROOM_IS_NOT_YOUR);
        }
        if (userId.equals(contract.getOwnerId()) && !userId.equals(contract.getTenantId())) {
            throw new AppException(ErrorCode.THIS_ROOM_IS_NOT_YOUR);
        }


//        if (userId.equals(contract.getOwnerId()) && request.isOwnerSignature()) {
//            contract.setOwnerSigned(true);
//        }
//        if (userId.equals(contract.getTenantId()) && request.isTenantSignature()) {
//            contract.setTenantSigned(true);
//        }
//

//
        Room room = roomRepository.findById(contract.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        Tenant tenant = tenantRepository.findById(contract.getTenantId())
                .orElseThrow(()-> new AppException(ErrorCode.TENANT_NOT_FOUND));
        contract.setUpdatedAt(LocalDate.now());


        if (!(Boolean.TRUE.equals(contract.getOwnerSigned()) && Boolean.TRUE.equals(contract.getTenantSigned()))) {
            throw new RuntimeException("Hop dong chua duoc ky");
        }
        // Ký xong, hợp đồng chuyển sang trạng thái PENDING (chờ duyệt)
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setSignedAt(LocalDate.now());
        room.setStatus(RoomStatus.OCCUPIED);
        tenant.setStatus(TenantStatus.CONTRACT_CONFIRMED);
        tenantRepository.save(tenant);
        roomRepository.save(room);
        contract = contractRepository.save(contract);

        return contractMapper.toResponse(contract);
    }

    @Override
    public List<ContractResponse> getContractHistoryByTenant(String tenantId, String ownerId) {
        // Lấy tất cả các hợp đồng có tenantId khớp và ownerId là người sở hữu
        List<Contract> contracts = contractRepository.findByTenantIdAndOwnerId(tenantId, ownerId);

        // Chuyển đổi sang DTO
        return contracts.stream()
                .map(contractMapper::toResponse)
                .collect(Collectors.toList());
    }


    // Hàm hỗ trợ cập nhật trạng thái hợp đồng theo ngày
    private Contract updateContractStatusByDate(Contract contract) {
        LocalDate now = LocalDate.now();

        if (contract.getStatus() == ContractStatus.TERMINATED ||
                contract.getStatus() == ContractStatus.CANCELLED) {
            // Trạng thái này không đổi theo thời gian
            return contract;
        }

        if (contract.getEndDate() != null && now.isAfter(contract.getEndDate())) {
            contract.setStatus(ContractStatus.EXPIRED);
        } else if (contract.getStartDate() != null && now.isBefore(contract.getStartDate())) {
            contract.setStatus(ContractStatus.PENDING);
        } else if (contract.getEndDate() != null &&
                !now.isAfter(contract.getEndDate()) &&
                !now.isBefore(contract.getStartDate())) {
            // Đang trong khoảng hiệu lực hợp đồng
            long daysToExpire = ChronoUnit.DAYS.between(now, contract.getEndDate());
            if (daysToExpire <= EXPIRING_SOON_DAYS) {
                contract.setStatus(ContractStatus.EXPIRING_SOON);
            } else {
                contract.setStatus(ContractStatus.ACTIVE);
            }
        }
        return contract;
    }

    private String generatePdf(Contract contract) {
        String fileName = "contract_" + contract.getTenantId() + "_" + UUID.randomUUID() + ".pdf";
        String folderPath = "/contracts/"; // Cấu hình đường dẫn lưu file ở đây
        String filePath = folderPath + fileName;
        String url = "http://localhost:8080/api/v1/contracts/" + fileName; // Thay thành domain thật

        try {
            // Tạo thư mục nếu chưa tồn tại
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String startDate = contract.getStartDate().format(formatter);
            String endDate = contract.getEndDate().format(formatter);
            String createdAt = contract.getCreatedAt().format(formatter);

            document.add(new Paragraph("HỢP ĐỒNG THUÊ PHÒNG TRỌ")
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20f));
            document.add(new Paragraph("Mã hợp đồng: " + contract.getId()));
            document.add(new Paragraph("Người cho thuê (Landlord ID): " + contract.getOwnerId()));
            document.add(new Paragraph("Người thuê (Tenant ID): " + contract.getTenantId()));
            document.add(new Paragraph("Phòng thuê (Room ID): " + contract.getRoomId()));
            document.add(new Paragraph("Ngày bắt đầu: " + startDate));
            document.add(new Paragraph("Ngày kết thúc: " + endDate));
            document.add(new Paragraph("Tiền cọc: " + contract.getDeposit() + " VND"));
            document.add(new Paragraph("Tiền thuê hàng tháng: " + contract.getMonthlyPrice() + " VND"));
            document.add(new Paragraph("Điều khoản:"));
            document.add(new Paragraph(contract.getTerms()).setFontSize(11).setMarginBottom(10f));
            document.add(new Paragraph("Trạng thái hợp đồng: " + contract.getStatus()));
            document.add(new Paragraph("Ngày tạo: " + createdAt));

            document.close();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo PDF hợp đồng", e);
        }

        return url;
    }

    @Override
    public List<ContractResponse> searchContracts(ContractFilterRequest request) {
        Criteria criteria = new Criteria();

        // Chỉ lọc theo owner đang đăng nhập
        criteria.and("ownerId").is(request.getOwnerId());

        // Lọc theo các điều kiện khác nếu có
        if (request.getTenantName() != null && !request.getTenantName().isBlank()) {
            criteria.and("tenant.name").regex(request.getTenantName(), "i");
        }

        if (request.getRoomId() != null && !request.getRoomId().isBlank()) {
            criteria.and("roomId").is(request.getRoomId());
        }

        if (request.getStatus() != null) {
            criteria.and("status").is(request.getStatus());
        }

        if (request.getStartDateFrom() != null && request.getStartDateTo() != null) {
            criteria.and("startDate").gte(request.getStartDateFrom()).lte(request.getStartDateTo());
        } else if (request.getStartDateFrom() != null) {
            criteria.and("startDate").gte(request.getStartDateFrom());
        } else if (request.getStartDateTo() != null) {
            criteria.and("startDate").lte(request.getStartDateTo());
        }

        if (request.getEndDateFrom() != null && request.getEndDateTo() != null) {
            criteria.and("endDate").gte(request.getEndDateFrom()).lte(request.getEndDateTo());
        } else if (request.getEndDateFrom() != null) {
            criteria.and("endDate").gte(request.getEndDateFrom());
        } else if (request.getEndDateTo() != null) {
            criteria.and("endDate").lte(request.getEndDateTo());
        }

        Query query = new Query(criteria);

        List<Contract> contracts = mongoTemplate.find(query, Contract.class);
        return contracts.stream()
                .map(contractMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ContractResponse confirmDepositPayment(String contractId, String ownerId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        // Kiểm tra trạng thái hợp đồng phù hợp để xác nhận
        if (contract.getStatus() != ContractStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_CONTRACT_STATUS);
        }

        // Đánh dấu đã xác nhận tiền cọc
        contract.setOwnerSigned(true);
        contract.setUpdatedAt(LocalDate.now());
        contractRepository.save(contract);

        // Cập nhật trạng thái người thuê
        Tenant tenant = tenantRepository.findById(contract.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        tenant.setStatus(TenantStatus.DEPOSITED);
        tenantRepository.save(tenant);

        // Cập nhật trạng thái phòng
        Room room = roomRepository.findById(contract.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);

        return contractMapper.toResponse(contract);
    }

}
