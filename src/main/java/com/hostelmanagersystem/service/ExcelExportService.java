package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.response.RoomResponse;
import com.hostelmanagersystem.dto.response.UserResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Room;
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
        createUserExcelHeaderRow(sheet, headerStyle);

        //tao data row
        createUserExcelDataRow(sheet,users, dataStyle);
        autoSizeColumns(sheet,7);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();

    }
    public byte[] exportRoomsToExcel(List<Room> rooms) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo thông tin phòng");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        createRoomExcelHeaderRow(sheet, headerStyle);
        createRoomExcelDataRow(sheet,rooms,dataStyle);
        autoSizeColumns(sheet,16);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();

    }
    private void createUserExcelHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeight((short) 500);
        String[] headers = {"STT","email", "Số điện thoại","Họ","Tên","Ngày tạo","Vai trò"};

        for(int i=0; i<headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }
    private void createRoomExcelHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeight((short) 500);
        String[] headers = {"STT", "Số phòng", "Diện tích","Giá", "Trạng thái","Loại phòng",
                            "Tiện ích","Thời hạn thuê","Tình trạng","tầng","mô tả","Ảnh/Video",
                            "Tỉnh","Quận","Phường","Địa chỉ chi tiết"};
        for(int i=0; i<headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }
    public void createRoomExcelDataRow(Sheet sheet, List<Room> rooms, CellStyle dataStyle) {
        int rowNum = 1;
        for(Room room : rooms) {
            Row row = sheet.createRow(rowNum++);

            //STT
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(rowNum-1);
            cell0.setCellStyle(dataStyle);

            // room number
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(room.getRoomNumber() != null ? room.getRoomNumber() : "");
            cell1.setCellStyle(dataStyle);

            //room size
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(room.getRoomSize() != null ? room.getRoomSize() + "m²" : "");
            cell2.setCellStyle(dataStyle);

            //price
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(room.getPrice() != null ? room.getPrice() : 0);
            cell3.setCellStyle(dataStyle);

            // trang thai
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(room.getStatus() != null ? room.getStatus().toString() : "");
            cell4.setCellStyle(dataStyle);

            //loaiphong
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(room.getRoomType() != null ? room.getRoomType() : "");
            cell5.setCellStyle(dataStyle);
            // tien ich
            Cell cell6 = row.createCell(6);
            StringBuilder facilities = new StringBuilder();
            if(room.getFacilities() != null) {
                room.getFacilities().forEach(facility -> {
                    facilities.append(facility.toString()).append(", \n ");
                });
            }
            cell6.setCellValue(facilities.toString());
            cell6.setCellStyle(dataStyle);

            Cell cell7 = row.createCell(7);
            cell7.setCellValue(room.getLeaseTerm() != null ? room.getLeaseTerm() : 0);
            cell7.setCellStyle(dataStyle);
            // tinh trang phong
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(room.getCondition() != null ? room.getCondition() : "");
            cell8.setCellStyle(dataStyle);

            // tang
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(room.getFloor() != null ? room.getFloor() : 0);
            cell9.setCellStyle(dataStyle);

            //mo ta
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(room.getDescription() != null ? room.getDescription() : "");
            cell10.setCellStyle(dataStyle);

            // media url
            Cell cell11 = row.createCell(11);
            StringBuilder mediaUrl = new StringBuilder();
            for (int i = 0; i < room.getMediaUrls().size(); i++) {
                mediaUrl.append(room.getMediaUrls().get(i)).append("\n ");
            }
            cell11.setCellValue(mediaUrl.toString());
            cell11.setCellStyle(dataStyle);

            // tinh
            Cell cell12 = row.createCell(12);
            cell12.setCellValue(room.getProvince() != null ? room.getProvince() : "");
            cell12.setCellStyle(dataStyle);

            //Quan
            Cell cell13 = row.createCell(13);
            cell13.setCellValue(room.getDistrict() != null ? room.getDistrict() : "");
            cell13.setCellStyle(dataStyle);

            // phuong
            Cell cell14 = row.createCell(14);
            cell14.setCellValue(room.getWard() != null ? room.getWard() : "");
            cell14.setCellStyle(dataStyle);

            //dia chi chi tiet
            Cell cell15 = row.createCell(15);
            cell15.setCellValue(room.getAddressText() != null ? room.getAddressText() : "");
            cell15.setCellStyle(dataStyle);
        }
    }
    private void createUserExcelDataRow(Sheet sheet,List<UserResponse> users,CellStyle dataStyle) {
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
            if(currentWidth <5000){
                sheet.setColumnWidth(i,5000);
            }
            //set maximum width
            if(currentWidth > 12000){
                sheet.setColumnWidth(i,12000);
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
