package com.anlb.readcycle.controller.admin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.anlb.readcycle.domain.dto.response.file.UploadFileResponseDTO;
import com.anlb.readcycle.service.FileService;
import com.anlb.readcycle.utils.anotation.ApiMessage;
import com.anlb.readcycle.utils.exception.StorageException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {

    @Value("${anlb.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    @PostMapping("/file/upload")
    @ApiMessage("Upload single file")
    public ResponseEntity<UploadFileResponseDTO> upload(@RequestParam(name = "file") MultipartFile file) throws StorageException, URISyntaxException, IOException {
        this.fileService.validationFile(file);
        // create a directory if not exist
        this.fileService.createDirectory(baseURI + "");
        // store file
        String uploadFile = this.fileService.store(file);
        UploadFileResponseDTO response = new UploadFileResponseDTO(uploadFile, Instant.now());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/file/delete/{file}")
    @ApiMessage("Delete single file")
    public ResponseEntity<Void> deleteFile(@PathVariable("file") String file) throws StorageException, URISyntaxException, IOException {
        this.fileService.delete(file);
        return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .build();
    }
}
