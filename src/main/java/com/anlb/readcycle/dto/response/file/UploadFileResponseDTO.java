package com.anlb.readcycle.dto.response.file;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResponseDTO {
    private String fileUploaded;
    private Instant uploadedAt;
}