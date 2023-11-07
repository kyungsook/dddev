package com.d103.dddev.api.file.controller;

import com.d103.dddev.api.common.ResponseVO;
import com.d103.dddev.api.file.repository.dto.ProfileDto;
import com.d103.dddev.api.file.service.DocumentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentServiceImpl documentService;

    @PostMapping("/saveImg/{documentId}")
    ResponseEntity<?> saveImg(@RequestHeader String Authorization, @PathVariable("documentId") String documentId, @RequestPart MultipartFile file) {
        try {
            String path = documentService.saveImg(documentId, file);
            ResponseVO<String> responseVO = ResponseVO.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message("이미지 저장 성공")
                    .data(path)
                    .build();

            return new ResponseEntity<>(responseVO, HttpStatus.OK);
        } catch (Exception e) {
            ResponseVO<String> responseVO = ResponseVO.<String>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseVO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
