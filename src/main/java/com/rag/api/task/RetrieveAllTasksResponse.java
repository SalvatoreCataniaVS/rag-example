package com.rag.api.task;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RetrieveAllTasksResponse {

    private List<TaskDTO> taskList;

}