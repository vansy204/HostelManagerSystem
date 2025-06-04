package com.hostelmanagersystem.entity.manager;

import com.hostelmanagersystem.enums.ContractStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document(value = "contracts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contract {
    @MongoId
    String id;

    String ownerId;
    String tenantId;
    String roomId;

    LocalDate startDate;
    LocalDate endDate;

    Double deposit;
    Double monthlyPrice;
    String terms;

    ContractStatus status; // ACTIVE, EXPIRING_SOON, ENDED

    Boolean ownerSigned;   // chủ trọ đã ký?
    Boolean tenantSigned;     // người thuê đã ký?

    LocalDate signedAt;

    String pdfUrl;

    LocalDate createdAt;
    LocalDate updatedAt;

    String terminationReason;
    LocalDate terminationDate;
    byte[] pdfFile;
}
