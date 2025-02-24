package com.anlb.readcycle.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.anlb.readcycle.service.IFileService;
import com.anlb.readcycle.utils.exception.StorageException;

@Service
public class FileServiceImpl implements IFileService {
    
    @Value("${anlb.upload-file.base-uri}")
    private String baseURI;

    /**
     * Creates a directory at the specified location if it does not already exist.
     *
     * @param folder the URI string representing the directory path.
     * @throws URISyntaxException if the provided folder string is not a valid URI.
     */
    @Override
    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stores the uploaded file with a unique filename.
     *
     * @param file the uploaded {@link MultipartFile} to be stored.
     * @return the generated unique filename.
     * @throws URISyntaxException if the constructed URI is invalid.
     * @throws IOException if an I/O error occurs while storing the file.
     */
    @Override
    public String store(MultipartFile file) throws URISyntaxException, IOException {
        // create unique filename
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        URI uri = new URI(baseURI + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    /**
     * Validates the uploaded file to ensure it is not empty and has an allowed extension.
     *
     * @param file the uploaded {@link MultipartFile} to be validated.
     * @throws StorageException if the file is empty or has an invalid extension.
     */
    @Override
    public void validationFile(MultipartFile file) throws StorageException {
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload a file.");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException("Invalid file extension. only allows " + allowedExtensions.toString());
        }
    }

    /**
     * Deletes a file from storage.
     *
     * @param fileName the name of the file to be deleted.
     * @throws URISyntaxException if the file path URI is invalid.
     * @throws StorageException 
     * @throws IOException if an error occurs while deleting the file.
     */
    @Override
    public void delete(String fileName) throws URISyntaxException, StorageException, IOException {
        URI uri = new URI(baseURI + "/" + fileName);
        Path path = Paths.get(uri);

        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

}
