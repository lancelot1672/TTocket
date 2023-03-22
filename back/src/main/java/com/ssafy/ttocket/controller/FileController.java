package com.ssafy.ttocket.controller;

import com.ssafy.ttocket.dto.ResponseDto;
import com.ssafy.ttocket.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/image")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> imageUpload(MultipartFile[] files){
        log.debug("FileController-Post-/image/upload filesize : {}", files.length);
        ResponseDto responseDto = fileService.upload(files);

        return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.OK);
    }
}