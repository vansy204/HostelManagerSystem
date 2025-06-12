package com.hostelmanagersystem.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    USER_EXISTED(1001, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1003, "Tên đăng nhập phải lớn hơn {min} ký tự", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Mật khẩu phải lớn hơn {min} ký tự", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Tên đăng nhập hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1008, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1009, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    ROOM_EXISTED(1011,"Phòng đã tồn tại",HttpStatus.BAD_REQUEST),
    ROOM_NOT_EXISTED(1012,"Phòng không tồn tại",HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1013,"Không tồn tại vai trò này",HttpStatus.BAD_REQUEST),

    BOOKING_NOT_FOUND(1014,"Không tìm thấy yêu cầu đặt phòng",HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1015,"Email đã tồn tại! ",HttpStatus.BAD_REQUEST),
    PHONE_EXISTED( 1016,"Số điện thoại đã tồn tại ",HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(1017,"Tên đăng nhập đã tồn tại",HttpStatus.BAD_REQUEST),

    REQUEST_BEING_PROCESSED(1019,"Yêu cầu đã được xử lý, không thể hủy.",HttpStatus.BAD_REQUEST),
    REQUEST_NOT_FOUND(1020,"Không tìm thấy yêu cầu của bạn.",HttpStatus.BAD_REQUEST),

    USER_HAD_BANNED(1014,"Tài khoản của bạn đã bị khoá", HttpStatus.BAD_REQUEST),
    ROOM_HAD_BEEN_ACCEPT(1015,"Phòng này đã được chấp nhận đăng", HttpStatus.BAD_REQUEST),

    ROOM_NOT_FOUND(1016,"Phòng không tồn tại",HttpStatus.BAD_REQUEST),
    CONTRACT_NOT_FOUND(1017,"Không tìm thấy hợp đồng", HttpStatus.BAD_REQUEST),
    TENANT_NOT_FOUND(1018,"Không tìm thấy khách thuê", HttpStatus.BAD_REQUEST),
    TENANT_REQUEST_ALREADY_EXISTS(1019, "Bạn đã gửi yêu cầu thuê phòng này rồi.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(1020,"Truy cập bị từ chối", HttpStatus.UNAUTHORIZED),
  
    INVALID_REQUEST_STATUS(1021,"Trạng thái yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    ROOM_NOT_RESERVED(1022,"Đã có người đặt cọc hoặc giữ chỗ", HttpStatus.BAD_REQUEST),
    ROOM_NOT_AVAILABLE(1023,"Phòng không có sẵn", HttpStatus.BAD_REQUEST),
    ROOM_NOT_PENDING(1041,"Phòng chưa được duyệt", HttpStatus.BAD_REQUEST),
    INVALID_CONTRACT_STATUS(1024,"Trạng thái hợp đồng không hợp lệ", HttpStatus.BAD_REQUEST),

    CAN_NOT_WRITE(1025,"Bạn không có quyền ghi chỉ số cho phòng này",HttpStatus.FORBIDDEN),
    INVALID_STATUS_CHANGE(1026,"Thay đổi trạng thái không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_CHECKIN_DATE(1027,"Thời gian không hợp lệ", HttpStatus.UNAUTHORIZED ),
    LEASE_TERM_NOT_DEFINED(1028, "Phòng chưa cấu hình thời gian thuê tối thiểu", HttpStatus.UNAUTHORIZED),
    ROOM_NOT_IN_CLEANING(1029, "Phòng chưa đựoc dọn dẹp", HttpStatus.UNAUTHORIZED),

    STATUS_MUST_BE_PROVIDED(1030,"Trạng thái phải được cung cấp", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(1031,"Trạng thái không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_TENANT_STATUS(1032, "Trạng thái không hợp lệ!",HttpStatus.BAD_REQUEST),
    THIS_ROOM_IS_NOT_YOUR(1033,"Bạn không phải chủ phòng",HttpStatus.BAD_REQUEST),
    UTILITY_USAGE_NOT_FOUND(1035,"Tiện ích sử dụng không tìm thấy", HttpStatus.BAD_REQUEST),
    UTILITY_INVOICE_NOT_FOUND(1036,"Tiện ích hóa đơn không tìm thấy", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_ELECTRIC(1037,"Chỉ số điện mới phải lớn hơn hoặc bằng chỉ số cũ", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_WATER(1038,"Chỉ số nước mới phải lớn hơn hoặc bằng chỉ số cũ", HttpStatus.BAD_REQUEST),
    INVALID_ELECTRICITY_READING(1039,"Chỉ số điện không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_WATER_READING(1040,"Chỉ số nước không hợp lệ", HttpStatus.BAD_REQUEST),
    UTILITY_CONFIG_NOT_FOUND(1042,"Chưa cấu hình đơn giá tiện ích", HttpStatus.BAD_REQUEST),
    INVOICE_NOT_FOUND(1043,"Không tìm thấy hóa đơn", HttpStatus.BAD_REQUEST),
    INVOICE_ALREADY_PAID(1045, "Hóa đơn đã được thanh toán ", HttpStatus.BAD_REQUEST),
    CONTRACT_CANNOT_BE_RENEWED(1046, "Hợp đồng không thể gia hạn", HttpStatus.BAD_REQUEST),
    REQUEST_ALREADY_PROCESSED(1047,"Yêu cầu đã được xử lý trước đó",HttpStatus.BAD_REQUEST),

    ;



    int code;
    String message;
    HttpStatusCode httpStatusCode;
}