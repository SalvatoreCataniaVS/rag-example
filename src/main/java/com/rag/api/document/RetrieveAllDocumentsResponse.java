package com.rag.api.document;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RetrieveAllDocumentsResponse {

    private List<DocumentDTO> documentList;

}