package com.example.cursordemodepartmentapp.mapper;

import com.example.cursordemodepartmentapp.entity.Department;
import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {
    
    public DepartmentInfo toDto(Department entity) {
        if (entity == null) {
            return null;
        }
        
        DepartmentInfo dto = new DepartmentInfo();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
    
    public Department toEntity(DepartmentInfo dto) {
        if (dto == null) {
            return null;
        }
        
        Department entity = new Department();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
} 