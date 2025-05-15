package com.hostelmanagersystem.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpenseResponse {
    String id;
    Double amount;
    String purpose;
    String note;
    LocalDate date;
}
