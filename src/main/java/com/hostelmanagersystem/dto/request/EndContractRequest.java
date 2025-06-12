package com.hostelmanagersystem.dto.request;



import lombok.Data;



@Data
public class EndContractRequest {
    private String tenantId;
    private String contractId;
    private String reason;


}

