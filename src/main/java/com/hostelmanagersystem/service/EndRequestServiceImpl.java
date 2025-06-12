package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.EndContractRequest;
import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.entity.manager.EndRequest;
import com.hostelmanagersystem.enums.ContractStatus;
import com.hostelmanagersystem.enums.RequestStatus;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.repository.ContractRepository;
import com.hostelmanagersystem.repository.EndRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndRequestServiceImpl implements EndRequestService {

    private final EndRequestRepository endRequestRepository;
    private final ContractRepository contractRepository;

    @Override
    public void createEndRequest(String tenantId, String contractId, String reason) {
        EndRequest request = EndRequest.builder()
                .tenantId(tenantId)
                .contractId(contractId)
                .reason(reason)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        endRequestRepository.save(request);
    }

    @Override
    public void confirmRequest(String requestId) {
        EndRequest request = endRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

        if (request.isConfirmed()) {
            throw new AppException(ErrorCode.REQUEST_ALREADY_PROCESSED);
        }

        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        contract.setStatus(ContractStatus.TERMINATED); // cập nhật trạng thái kết thúc
        contractRepository.save(contract);

        request.setConfirmed(true);
        request.setConfirmedAt(LocalDateTime.now());
        endRequestRepository.save(request);
    }
    @Override
    public List<EndRequest> getAllPendingRequests() {
        return endRequestRepository.findByStatus(RequestStatus.PENDING);
    }

}

