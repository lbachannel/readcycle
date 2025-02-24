package com.anlb.readcycle.service;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.web.multipart.MultipartFile;

import com.anlb.readcycle.utils.exception.StorageException;

public interface IFileService {
    void createDirectory(String folder) throws URISyntaxException;
    String store(MultipartFile file) throws URISyntaxException, IOException;
    void validationFile(MultipartFile file) throws StorageException;
    void delete(String fileName) throws URISyntaxException, StorageException, IOException;
}
