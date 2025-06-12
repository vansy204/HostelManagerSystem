package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.RenewContractRequest;

import com.hostelmanagersystem.entity.RenewRequest;
import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.entity.manager.Tenant;
import com.hostelmanagersystem.enums.RequestStatus;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.repository.ContractRepository;
import com.hostelmanagersystem.repository.RenewRequestRepository;
import com.hostelmanagersystem.repository.TenantRepository;
import com.hostelmanagersystem.service.RenewRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RenewRequestServiceImpl implements RenewRequestService {

    private final RenewRequestRepository renewRequestRepository;
    private final ContractRepository contractRepository;
    private final TenantRepository tenantRepository;

    @Override
    public void createRequest(RenewContractRequest dto) {
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        // Tính ngày kết thúc mới
        LocalDate newEndDate = contract.getEndDate().plusMonths(dto.getAdditionalMonths());

        RenewRequest request = RenewRequest.builder()
                .tenantId(dto.getTenantId())
                .contractId(dto.getContractId())
                .currentEndDate(contract.getEndDate())
                .newEndDate(newEndDate)
                .additionalMonths(dto.getAdditionalMonths())
                .reason(dto.getReason())
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        renewRequestRepository.save(request);
    }

    //Duyệt gia han
    @Override
    public void approveRequest(String requestId) {
        RenewRequest request = renewRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

        // Cập nhật trạng thái yêu cầu
        request.setStatus(RequestStatus.APPROVED);
        renewRequestRepository.save(request);

        // Cập nhật ngày hết hạn mới vào hợp đồng
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        contract.setEndDate(request.getNewEndDate());
        contractRepository.save(contract);
    }

    //Từ chối gia hạn
    @Override
    public void rejectRequest(String requestId) {
        RenewRequest request = renewRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

        request.setStatus(RequestStatus.REJECTED);
        renewRequestRepository.save(request);
    }

    @Override
    public List<RenewRequest> getRequestsByStatus(RequestStatus status) {
        return renewRequestRepository.findAllByStatus(status);
    }

    @Override
    public List<RenewRequest> getAllRequests() {
        return renewRequestRepository.findAll();
    }


}
