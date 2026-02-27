package com.rag.api.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSearchResponse {

    private List<UserDTO> users;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

}