package com.dimitar.financetracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Pagination and sorting parameters for list queries")
public class PageRequest {

    @Schema(description = "Page number (0-based)", example = "0", defaultValue = "0")
    @Builder.Default
    private int page = 0;

    @Schema(description = "Number of items per page", example = "20", defaultValue = "20")
    @Builder.Default
    private int size = 20;

    @Schema(description = "Field to sort by", example = "transactionDate", defaultValue = "id")
    @Builder.Default
    private String sortBy = "id";

    @Schema(description = "Sort direction (ASC or DESC)", example = "DESC", defaultValue = "DESC")
    @Builder.Default
    private String sortDirection = "DESC";

    public org.springframework.data.domain.PageRequest toPageable() {
        org.springframework.data.domain.Sort.Direction direction =
            "ASC".equalsIgnoreCase(sortDirection)
                ? org.springframework.data.domain.Sort.Direction.ASC
                : org.springframework.data.domain.Sort.Direction.DESC;

        return org.springframework.data.domain.PageRequest.of(
            page,
            size,
            org.springframework.data.domain.Sort.by(direction, sortBy)
        );
    }
}

