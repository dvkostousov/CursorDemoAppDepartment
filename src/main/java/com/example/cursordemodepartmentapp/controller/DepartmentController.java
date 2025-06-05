package com.example.cursordemodepartmentapp.controller;

import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/departments")
public interface DepartmentController {
    
    @GetMapping
    ResponseEntity<List<DepartmentInfo>> getAllDepartments();
    
    @GetMapping("/{id}")
    ResponseEntity<DepartmentInfo> getDepartment(@PathVariable Long id);
    
    @PostMapping
    ResponseEntity<Long> createDepartment(@RequestBody DepartmentInfo departmentInfo);
    
    @PutMapping("/{id}")
    ResponseEntity<Void> updateDepartment(@PathVariable Long id, @RequestBody DepartmentInfo departmentInfo);
    
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteDepartment(@PathVariable Long id);
} 