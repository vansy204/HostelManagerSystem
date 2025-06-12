package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.dto.response.InvoiceStatisticsResponse;
import com.hostelmanagersystem.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
// ===== UTILITY INVOICE (type = UTILITY) =====
// ===== UTILITY INVOICE (type = UTILITY) =====

    // Tạo hóa đơn tiền phòng cho 1 phòng, tháng cụ thể
    InvoiceResponse createRentInvoice(String ownerId, InvoiceCreateRequest request);

    // Lấy chi tiết hóa đơn tiền phòng theo ID
    InvoiceResponse getRentInvoiceDetail(String ownerId, String invoiceId);

    // Cập nhật trạng thái hóa đơn tiền phòng (thanh toán, hủy, ...)
    InvoiceResponse updateInvoiceStatus(String ownerId, String invoiceId, InvoiceStatus newStatus);

    // Lấy danh sách hóa đơn tiền phòng theo phòng và trạng thái
    List<InvoiceResponse> getInvoicesByRoomAndStatus(String ownerId, String roomId, InvoiceStatus status);

    // Lấy danh sách hóa đơn tiền phòng theo tháng (format: yyyy-MM)
    List<InvoiceResponse> getRentInvoicesByMonth(String ownerId, String month);
    void payInvoice(String invoiceId);

    // Xóa hóa đơn tiền phòng
    void deleteRentInvoice(String ownerId, String invoiceId);

    // Mở rộng các chức năng quản lý hóa đơn cho owner:

    // Lấy danh sách tất cả hóa đơn của owner (có thể phân trang)
    List<InvoiceResponse> getAllInvoicesByOwner(String ownerId, int page, int size);

    // Tìm kiếm hóa đơn theo từ khóa (có thể là mã hóa đơn, tên phòng, tenant, ...)
    List<InvoiceResponse> searchInvoices(String ownerId, String keyword, int page, int size);

    // Thống kê tổng doanh thu trong khoảng thời gian (theo tháng, năm, ...)
    InvoiceStatisticsResponse getInvoiceStatistics(String ownerId, String startDate, String endDate);

    // Gửi hóa đơn qua email cho tenant hoặc owner
    void sendInvoiceByEmail(String ownerId, String invoiceId, String email);

    // Lấy danh sách hóa đơn theo status
    List<InvoiceResponse> getInvoicesByStatus(String ownerId, InvoiceStatus status);

    // Đánh dấu hóa đơn là đã thanh toán
    InvoiceResponse markInvoiceAsPaid(String ownerId, String invoiceId);
    List<InvoiceResponse> getInvoiceByTenant(String tenantId);
    // Hủy hóa đơn (ví dụ khi hợp đồng kết thúc hoặc có lỗi)
    InvoiceResponse cancelInvoice(String ownerId, String invoiceId);
}
