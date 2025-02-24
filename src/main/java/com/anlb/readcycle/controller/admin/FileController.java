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

import com.anlb.readcycle.dto.response.file.UploadFileResponseDto;
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

    /**
     * {@code POST  /file/upload} : upload of a single file.
     *
     * @param file The file to be uploaded.
     * @return A {@link ResponseEntity} containing an {@link UploadFileResponseDto} with the uploaded file details.
     * @throws StorageException If there is an error during file storage.
     * @throws URISyntaxException If there is an error in the file path URI.
     * @throws IOException If an I/O error occurs during file processing.
     */
    @PostMapping("/file/upload")
    @ApiMessage("Upload single file")
    public ResponseEntity<UploadFileResponseDto> upload(@RequestParam(name = "file") MultipartFile file) throws StorageException, URISyntaxException, IOException {
        fileService.validationFile(file);
        // create a directory if not exist
        fileService.createDirectory(baseURI + "");
        // store file
        String uploadFile = fileService.store(file);
        UploadFileResponseDto response = new UploadFileResponseDto(uploadFile, Instant.now());
        return ResponseEntity.ok().body(response);
    }

    /**
     * {@code DELETE  /file/delete/{file}} : Deletes a single file by its name.
     *
     * @param file The name of the file to be deleted.
     * @return A {@link ResponseEntity} with an HTTP status of {@code 204 No Content} if the deletion is successful.
     * @throws StorageException If there is an error during file deletion.
     * @throws URISyntaxException If there is an issue with the file path URI.
     * @throws IOException If an I/O error occurs while deleting the file.
     */
    @DeleteMapping("/file/delete/{file}")
    @ApiMessage("Delete single file")
    public ResponseEntity<Void> deleteFile(@PathVariable("file") String file) throws StorageException, URISyntaxException, IOException {
        fileService.delete(file);
        return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .build();
    }
}
