package com.project.deartime.global.exception;

import lombok.Getter;

@Getter
public class CoreApiException extends RuntimeException {

    private final ErrorCode errorCode;

    // 메시지 기반 생성자
    public CoreApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 커스텀 메시지
    public CoreApiException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }

    // 원인 예외 포함 생성자
    public CoreApiException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);  // 부모에 cause 전달
        this.errorCode = errorCode;
    }
}
