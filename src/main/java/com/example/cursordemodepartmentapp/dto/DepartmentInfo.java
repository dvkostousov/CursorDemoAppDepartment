package com.example.cursordemodepartmentapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentInfo {
    
    private Long id;

    private String name;
} 