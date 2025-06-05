package com.example.cursordemodepartmentapp.service.impl;

import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import com.example.cursordemodepartmentapp.entity.Department;
import com.example.cursordemodepartmentapp.mapper.DepartmentMapper;
import com.example.cursordemodepartmentapp.repository.DepartmentRepository;
import com.example.cursordemodepartmentapp.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    
    @Override
    public DepartmentInfo getCard(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Department id cannot be null");
        }
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return departmentMapper.toDto(department);
    }
    
    @Override
    public Long createCard(DepartmentInfo card) {
        if (card == null) {
            throw new IllegalArgumentException("Department info cannot be null");
        }
        if (card.getName() == null || card.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        if (card.getName().length() > 255) {
            throw new IllegalArgumentException("Department name cannot be longer than 255 characters");
        }
        if (departmentRepository.findAll().stream().anyMatch(d -> d.getName().equals(card.getName()))) {
            throw new IllegalArgumentException("Department with this name already exists");
        }
        Department department = departmentMapper.toEntity(card);
        return departmentRepository.save(department).getId();
    }
    
    @Override
    public void updateCard(DepartmentInfo card) {
        if (card == null) {
            throw new IllegalArgumentException("Department info cannot be null");
        }
        if (card.getId() == null) {
            throw new IllegalArgumentException("Department id cannot be null");
        }
        if (card.getName() == null || card.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        if (card.getName().length() > 255) {
            throw new IllegalArgumentException("Department name cannot be longer than 255 characters");
        }
        Department department = departmentRepository.findById(card.getId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + card.getId()));
        if (departmentRepository.findAll().stream().anyMatch(d -> d.getName().equals(card.getName()) && !d.getId().equals(card.getId()))) {
            throw new IllegalArgumentException("Department with this name already exists");
        }
        Department updatedDepartment = departmentMapper.toEntity(card);
        departmentRepository.save(updatedDepartment);
    }
    
    @Override
    public void deleteCard(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Department id cannot be null");
        }
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    @Override
    public List<DepartmentInfo> getAllCards() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }
} 