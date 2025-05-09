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
    USER_HAD_BANNED(1014,"Tài khoản của bạn đã bị khoá", HttpStatus.BAD_REQUEST),
    ROOM_HAD_BEEN_ACCEPT(1015,"Phòng này đã được chấp nhận đăng", HttpStatus.BAD_REQUEST)
    ;
    int code;
    String message;
    HttpStatusCode httpStatusCode;
}
