package com.hostelmanagersystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class AppException extends RuntimeException {

    private ErrorCode errorCode;
}
