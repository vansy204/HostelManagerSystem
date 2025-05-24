package com.hostelmanagersystem.service;

import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.entity.manager.Room;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class PdfGenerator {
    public String generateContractPdf(Contract contract, User tenant, Room room) {
        // Load template and fill in dynamic fields (using iText or PDFBox)
        // Save to file system or cloud and return the URL
        // For example purposes:
        String filename = "contract_" + contract.getId() + ".pdf";
        String filePath = "/pdf/contracts/" + filename;

        // Logic để sinh PDF từ template...
        // PDFWriter.writeContract(filePath, contract, tenant, room);

        return filePath; // hoặc upload lên S3 và trả về link
    }
}
