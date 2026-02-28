package com.rag.api.task;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TaskSearchResponse {

    private List<TaskDTO> tasks;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

}