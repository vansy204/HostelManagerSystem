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
      
    TENANT_NOT_FOUND(1022,"Không tìm thấy khách thuê phòng",HttpStatus.BAD_REQUEST),
    TENANT_REQUEST_ALREADY_EXISTS(1023, "Bạn đã gửi yêu cầu thuê phòng này rồi.",HttpStatus.BAD_REQUEST)

    ;


    int code;
    String message;
    HttpStatusCode httpStatusCode;
}
