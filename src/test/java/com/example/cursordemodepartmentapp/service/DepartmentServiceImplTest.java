package com.example.cursordemodepartmentapp.service;

import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import com.example.cursordemodepartmentapp.entity.Department;
import com.example.cursordemodepartmentapp.mapper.DepartmentMapper;
import com.example.cursordemodepartmentapp.repository.DepartmentRepository;
import com.example.cursordemodepartmentapp.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private DepartmentInfo departmentInfo;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Test Department");

        departmentInfo = new DepartmentInfo();
        departmentInfo.setId(1L);
        departmentInfo.setName("Test Department");
    }

    @Test
    void getCard_ShouldReturnDepartmentInfo_WhenDepartmentExists() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentMapper.toDto(department)).thenReturn(departmentInfo);

        DepartmentInfo result = departmentService.getCard(1L);

        assertNotNull(result);
        assertEquals(departmentInfo.getId(), result.getId());
        assertEquals(departmentInfo.getName(), result.getName());
        verify(departmentRepository).findById(1L);
        verify(departmentMapper).toDto(department);
    }

    @Test
    void getCard_ShouldThrowException_WhenDepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> departmentService.getCard(1L));
        verify(departmentRepository).findById(1L);
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void createCard_ShouldReturnId_WhenDepartmentCreated() {
        when(departmentMapper.toEntity(departmentInfo)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(department);

        Long result = departmentService.createCard(departmentInfo);

        assertNotNull(result);
        assertEquals(department.getId(), result);
        verify(departmentMapper).toEntity(departmentInfo);
        verify(departmentRepository).save(department);
    }

    @Test
    void createCard_ShouldThrowException_WhenDepartmentInfoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> departmentService.createCard(null));
        verify(departmentMapper, never()).toEntity(any());
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateCard_ShouldUpdateDepartment_WhenDepartmentExists() {
        when(departmentRepository.findById(departmentInfo.getId())).thenReturn(Optional.of(department));
        when(departmentMapper.toEntity(departmentInfo)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(department);

        assertDoesNotThrow(() -> departmentService.updateCard(departmentInfo));
        verify(departmentRepository).findById(departmentInfo.getId());
        verify(departmentMapper).toEntity(departmentInfo);
        verify(departmentRepository).save(department);
    }

    @Test
    void updateCard_ShouldThrowException_WhenDepartmentNotFound() {
        when(departmentRepository.findById(departmentInfo.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> departmentService.updateCard(departmentInfo));
        verify(departmentRepository).findById(departmentInfo.getId());
        verify(departmentMapper, never()).toEntity(any());
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateCard_ShouldThrowException_WhenDepartmentInfoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> departmentService.updateCard(null));
        verify(departmentRepository, never()).findById(any());
        verify(departmentMapper, never()).toEntity(any());
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteCard_ShouldDeleteDepartment_WhenDepartmentExists() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(departmentRepository).deleteById(1L);

        assertDoesNotThrow(() -> departmentService.deleteCard(1L));
        verify(departmentRepository).existsById(1L);
        verify(departmentRepository).deleteById(1L);
    }

    @Test
    void deleteCard_ShouldThrowException_WhenDepartmentNotFound() {
        when(departmentRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> departmentService.deleteCard(1L));
        verify(departmentRepository).existsById(1L);
        verify(departmentRepository, never()).deleteById(any());
    }

    @Test
    void getAllCards_ShouldReturnListOfDepartmentInfo() {
        List<Department> departments = Arrays.asList(department);
        List<DepartmentInfo> departmentInfos = Arrays.asList(departmentInfo);

        when(departmentRepository.findAll()).thenReturn(departments);
        when(departmentMapper.toDto(department)).thenReturn(departmentInfo);

        List<DepartmentInfo> result = departmentService.getAllCards();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(departmentInfo.getId(), result.get(0).getId());
        assertEquals(departmentInfo.getName(), result.get(0).getName());
        verify(departmentRepository).findAll();
        verify(departmentMapper).toDto(department);
    }

    @Test
    void getAllCards_ShouldReturnEmptyList_WhenNoDepartmentsExist() {
        when(departmentRepository.findAll()).thenReturn(List.of());

        List<DepartmentInfo> result = departmentService.getAllCards();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(departmentRepository).findAll();
        verify(departmentMapper, never()).toDto(any());
    }
} 