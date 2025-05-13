package com.hostelmanagersystem.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncomeResponse {
    String id;
    Double amount;
    String source;
    String note;
    LocalDate date;
}
