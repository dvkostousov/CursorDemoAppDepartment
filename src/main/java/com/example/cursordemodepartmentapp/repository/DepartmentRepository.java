package com.example.cursordemodepartmentapp.repository;

import com.example.cursordemodepartmentapp.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
} 