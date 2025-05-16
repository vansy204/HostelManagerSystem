package com.hostelmanagersystem.dto.request;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpenseRequest {
    Double amount;
    String purpose;
    String note;
    LocalDate date;
}
