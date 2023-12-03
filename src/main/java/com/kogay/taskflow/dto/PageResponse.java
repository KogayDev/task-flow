package com.kogay.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    List<T> content;
    Metadata metadata;

    @JsonCreator
    public PageResponse(@JsonProperty("content") List<T> content,
                        @JsonProperty("metadata") Metadata metadata) {
        this.content = content;
        this.metadata = metadata;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        var metadata = new Metadata(page.getNumber(), page.getSize(), page.getNumberOfElements(), page.getTotalElements(), page.getTotalPages());
        return new PageResponse<>(page.getContent(), metadata);
    }

    @Value
    public static class Metadata {
        int page;
        int size;
        int numberOfElements;
        long totalElements;
        long totalPages;

        @JsonCreator
        public Metadata(@JsonProperty("page") int page,
                        @JsonProperty("size") int size,
                        @JsonProperty("numberOfElements") int numberOfElements,
                        @JsonProperty("totalElements") long totalElements,
                        @JsonProperty("totalPages") long totalPages) {
            this.page = page;
            this.size = size;
            this.numberOfElements = numberOfElements;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }
}
