package com.rag.api.document;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DocumentSearchResponse {

    private List<DocumentDTO> documents;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

}