package com.example.cursordemodepartmentapp.controller.impl;

import com.example.cursordemodepartmentapp.controller.DepartmentController;
import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import com.example.cursordemodepartmentapp.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentControllerImpl implements DepartmentController {

    private final DepartmentService departmentService;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentInfo> getDepartment(@PathVariable Long id) {
        try {
            DepartmentInfo department = departmentService.getCard(id);
            return ResponseEntity.ok(department);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @PostMapping
    public ResponseEntity<Long> createDepartment(@RequestBody DepartmentInfo departmentInfo) {
        try {
            if (departmentInfo == null || departmentInfo.getName() == null || departmentInfo.getName().trim().isEmpty() || departmentInfo.getName().length() > 255) {
                return ResponseEntity.badRequest().build();
            }
            Long id = departmentService.createCard(departmentInfo);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", "/api/departments/" + id)
                    .body(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDepartment(@PathVariable Long id, @RequestBody DepartmentInfo departmentInfo) {
        try {
            if (departmentInfo == null || departmentInfo.getName() == null || departmentInfo.getName().trim().isEmpty() || departmentInfo.getName().length() > 255) {
                return ResponseEntity.badRequest().build();
            }
            if (departmentInfo.getId() == null) {
                return ResponseEntity.badRequest().build();
            }
            if (!id.equals(departmentInfo.getId())) {
                return ResponseEntity.badRequest().build();
            }
            departmentService.updateCard(departmentInfo);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteCard(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping
    public ResponseEntity<List<DepartmentInfo>> getAllDepartments() {
        try {
            List<DepartmentInfo> departments = departmentService.getAllCards();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 