package com.anlb.readcycle.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationResponseDTO {
    private Meta meta;
    private Object result;
}
