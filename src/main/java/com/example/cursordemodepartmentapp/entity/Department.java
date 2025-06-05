package com.example.cursordemodepartmentapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "department")
@Getter
@Setter
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_address")
    private Long idAddress;
    
    @NotBlank(message = "Department name cannot be empty")
    @Column(name = "name")
    private String name;
} 