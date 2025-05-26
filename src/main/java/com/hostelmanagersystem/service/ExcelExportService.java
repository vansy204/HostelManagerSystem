package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.entity.identity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelExportService {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    public byte[] exportUsersToExcel(List<UserResponse> users) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo người dùng");

        //tao style cho header
        CellStyle headerStyle = createHeaderStyle(workbook);

        //tao style cho data
        CellStyle dataStyle = createDataStyle(workbook);

        //tao header row
        createHeaderRow(sheet, headerStyle);

        //tao data row
        createDataRow(sheet,users, dataStyle);
        autoSizeColumns(sheet,7);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();

    }
    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeight((short) 500);
        String[] headers = {"STT","email", "Số điện thoại","Họ","Tên","Ngày tạo","Vai trò"};

        for(int i=0; i<headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }
    private void createDataRow(Sheet sheet,List<UserResponse> users,CellStyle dataStyle) {
        int rowNum = 1;
        for(UserResponse user : users) {
            Row row = sheet.createRow(rowNum++);

            // STT
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(rowNum -1);
            cell0.setCellStyle(dataStyle);

            //email
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(user.getEmail() != null ? user.getEmail() : "");
            cell1.setCellStyle(dataStyle);

            //so dien thoai
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(user.getPhone() != null ? user.getPhone() : "");
            cell2.setCellStyle(dataStyle);

            //ho
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(user.getFirstName() != null ? user.getFirstName() : "");
            cell3.setCellStyle(dataStyle);

            //ten
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(user.getLastName() != null ? user.getLastName() : "");
            cell4.setCellStyle(dataStyle);

            // ngay tao
            Cell cell5 = row.createCell(5);
            String dateStr = user.getCreatedAt() != null ? formatter.format(user.getCreatedAt()) : "";
            cell5.setCellValue(dateStr);
            cell5.setCellStyle(dataStyle);

            //vai tro
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(user.getRoleName() != null ? user.getRoleName() : "");
            cell6.setCellStyle(dataStyle);
        }
    }
    private void autoSizeColumns(Sheet sheet,int columnCount) {
        for(int i=0; i<columnCount; i++) {
            sheet.autoSizeColumn(i);

            //set minimum width
            int currentWidth = sheet.getColumnWidth(i);
            if(currentWidth <3000){
                sheet.setColumnWidth(i,3000);
            }
            //set maximum width
            if(currentWidth > 8000){
                sheet.setColumnWidth(i,8000);
            }
        }
    }
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        // Font settings
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());

        // Style settings
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // Borders
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Borders
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        // Alignment
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }
}
