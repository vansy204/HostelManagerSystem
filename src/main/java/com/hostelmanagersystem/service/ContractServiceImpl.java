package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.ContractCreateRequest;
import com.hostelmanagersystem.dto.response.ContractResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.enums.ContractStatus;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.ContractMapper;
import com.hostelmanagersystem.repository.ContractRepository;
import com.hostelmanagersystem.repository.RoomRepository;
import com.hostelmanagersystem.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractServiceImpl implements ContractService{
    ContractRepository contractRepository;
    ContractMapper contractMapper;
    RoomRepository roomRepository;
    UserRepository tenantRepository;
    PdfGenerator pdfGenerator; // utility class for PDF

    @Override
    public ContractResponse createContract(String landlordId, ContractCreateRequest request) {
        Contract contract = contractMapper.toEntity(request);
        contract.setLandlordId(landlordId);
        contract.setCreatedAt(LocalDate.now());
        contract.setUpdatedAt(LocalDate.now());
        contract.setStatus(ContractStatus.ACTIVE);

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        User tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        // Generate PDF
        String pdfUrl = pdfGenerator.generateContractPdf(contract, tenant, room);
        contract.setPdfUrl(pdfUrl);

        contract = contractRepository.save(contract);
        return contractMapper.toResponse(contract);
    }
    @Override
    public String generatePdfAndStore(Contract contract) {
        Room room = roomRepository.findById(contract.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        User tenant = tenantRepository.findById(contract.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        // Gọi utility để sinh PDF và lưu, trả về đường dẫn url
        String pdfUrl = pdfGenerator.generateContractPdf(contract, tenant, room);

        // Cập nhật lại contract với link pdf
        contract.setPdfUrl(pdfUrl);
        contractRepository.save(contract);

        return pdfUrl;
    }

    @Override
    public ContractResponse getContractById(String contractId, User user) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        // Phân quyền: chỉ landlord hoặc tenant của hợp đồng mới xem được
        if (!contract.getLandlordId().equals(user.getId()) && !contract.getTenantId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return contractMapper.toResponse(contract);
    }
    @Override
    public void updateContractStatus() {
        List<Contract> contracts = contractRepository.findAll();
        for (Contract contract : contracts) {
            if (contract.getEndDate().isBefore(LocalDate.now())) {
                contract.setStatus(ContractStatus.ENDED);
            } else if (contract.getEndDate().minusDays(7).isBefore(LocalDate.now())) {
                contract.setStatus(ContractStatus.EXPIRING_SOON);
            } else {
                contract.setStatus(ContractStatus.ACTIVE);
            }
            contract.setUpdatedAt(LocalDate.now());
        }
        contractRepository.saveAll(contracts);
    }
}
