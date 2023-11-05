package com.d103.dddev.api.common.exceptionHandler;

import com.d103.dddev.api.common.ResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        ResponseVO responseVO = ResponseVO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("잘못된 형식입니다.")
                .build();
        return new ResponseEntity<>(responseVO, HttpStatus.BAD_REQUEST);
    }
}
