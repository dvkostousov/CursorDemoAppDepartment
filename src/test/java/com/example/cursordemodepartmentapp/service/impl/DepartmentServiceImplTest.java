package com.example.cursordemodepartmentapp.service.impl;

import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import com.example.cursordemodepartmentapp.entity.Department;
import com.example.cursordemodepartmentapp.repository.DepartmentRepository;
import com.example.cursordemodepartmentapp.mapper.DepartmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

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
        MockitoAnnotations.openMocks(this);
        departmentService = new DepartmentServiceImpl(departmentRepository, departmentMapper);
        department = new Department();
        department.setId(1L);
        department.setName("Test Department");
        departmentInfo = new DepartmentInfo();
        departmentInfo.setId(1L);
        departmentInfo.setName("Test Department");
        lenient().when(departmentMapper.toEntity(any())).thenAnswer(invocation -> {
            DepartmentInfo info = invocation.getArgument(0);
            if (info == null) return null;
            Department dep = new Department();
            dep.setId(info.getId());
            dep.setName(info.getName());
            return dep;
        });
        lenient().when(departmentMapper.toDto(any())).thenAnswer(invocation -> {
            Department dep = invocation.getArgument(0);
            if (dep == null) return null;
            DepartmentInfo info = new DepartmentInfo();
            info.setId(dep.getId());
            info.setName(dep.getName());
            return info;
        });
    }

    @Nested
    class ConstructorTests {
        @Test
        void shouldCreateInstance() {
            assertNotNull(departmentService);
        }
    }

    @Nested
    class GetCardTests {
        @Test
        void shouldReturnDepartmentInfo() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

            DepartmentInfo result = departmentService.getCard(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test Department", result.getName());
            verify(departmentRepository).findById(1L);
        }

        @Test
        void shouldThrowException_WhenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> departmentService.getCard(null));
            assertThrows(IllegalArgumentException.class, () -> departmentService.deleteCard(null));
            verify(departmentRepository, never()).findById(any());
        }

        @Test
        void shouldThrowException_WhenDepartmentNotFound() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> departmentService.getCard(1L));
            verify(departmentRepository).findById(1L);
        }

        @Test
        void shouldHandleBoundaryIds() {
            // Test with zero ID
            when(departmentRepository.findById(0L)).thenReturn(Optional.of(department));
            DepartmentInfo result = departmentService.getCard(0L);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(departmentRepository).findById(0L);

            // Test with max long ID
            when(departmentRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.of(department));
            result = departmentService.getCard(Long.MAX_VALUE);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(departmentRepository).findById(Long.MAX_VALUE);
        }
    }

    @Nested
    class GetAllCardsTests {
        @Test
        void shouldReturnListOfDepartmentInfo() {
            Department department2 = new Department();
            department2.setId(2L);
            department2.setName("Test Department 2");

            when(departmentRepository.findAll()).thenReturn(Arrays.asList(department, department2));

            List<DepartmentInfo> result = departmentService.getAllCards();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals("Test Department", result.get(0).getName());
            assertEquals(2L, result.get(1).getId());
            assertEquals("Test Department 2", result.get(1).getName());
            verify(departmentRepository).findAll();
        }

        @Test
        void shouldReturnEmptyList_WhenNoDepartmentsExist() {
            when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

            List<DepartmentInfo> result = departmentService.getAllCards();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(departmentRepository).findAll();
        }

        @Test
        void shouldHandleDuplicateIds() {
            Department department2 = new Department();
            department2.setId(1L);
            department2.setName("Test Department 2");

            when(departmentRepository.findAll()).thenReturn(Arrays.asList(department, department2));

            List<DepartmentInfo> result = departmentService.getAllCards();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals("Test Department", result.get(0).getName());
            assertEquals(1L, result.get(1).getId());
            assertEquals("Test Department 2", result.get(1).getName());
            verify(departmentRepository).findAll();
        }
    }

    @Nested
    class CreateCardTests {
        @Test
        void shouldCreateDepartment() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setName("Test Department");
            Department department = new Department();
            department.setId(1L);
            department.setName("Test Department");
            when(departmentRepository.save(any(Department.class))).thenReturn(department);

            Long id = departmentService.createCard(departmentInfo);
            assertNotNull(id);
            assertEquals(1L, id);
        }

        @Test
        void shouldThrowException_WhenDepartmentInfoIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                departmentService.createCard(null);
            });
        }

        @Test
        void shouldThrowException_WhenNameIsInvalid() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setName(null);
            assertThrows(IllegalArgumentException.class, () -> {
                departmentService.createCard(departmentInfo);
            });

            departmentInfo.setName("");
            assertThrows(IllegalArgumentException.class, () -> {
                departmentService.createCard(departmentInfo);
            });
        }

        @Test
        void shouldThrowException_WhenSaveFails() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setName("Test Department");
            when(departmentRepository.save(any(Department.class))).thenThrow(new DataIntegrityViolationException("Save failed"));

            assertThrows(DataIntegrityViolationException.class, () -> {
                departmentService.createCard(departmentInfo);
            });
        }

        @Test
        void shouldHandleSpecialCharactersAndLongNames() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setName("a".repeat(256));
            assertThrows(IllegalArgumentException.class, () -> {
                departmentService.createCard(departmentInfo);
            });
        }

        @Test
        void shouldThrowException_WhenNameIsDuplicate() {
            DepartmentInfo info = new DepartmentInfo();
            info.setName("Duplicate");
            Department existingDepartment = new Department();
            existingDepartment.setName("Duplicate");
            existingDepartment.setId(1L);

            when(departmentRepository.findAll()).thenReturn(List.of(existingDepartment));

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> departmentService.createCard(info)
            );
            assertEquals("Department with this name already exists", exception.getMessage());
        }

        @Test
        void shouldThrowException_WhenNameIsEmpty() {
            DepartmentInfo info = new DepartmentInfo();
            info.setName("");
            assertThrows(IllegalArgumentException.class, () -> departmentService.createCard(info));
        }
    }

    @Nested
    class UpdateCardTests {
        @Test
        void shouldUpdateDepartment() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setId(1L);
            departmentInfo.setName("Updated Department");
            Department department = new Department();
            department.setId(1L);
            department.setName("Updated Department");

            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(departmentRepository.save(any(Department.class))).thenReturn(department);
            when(departmentMapper.toEntity(departmentInfo)).thenReturn(department);

            assertDoesNotThrow(() -> departmentService.updateCard(departmentInfo));
            verify(departmentRepository).findById(1L);
            verify(departmentRepository).save(any(Department.class));
        }

        @Test
        void shouldThrowException_WhenDepartmentInfoIsNull() {
            assertThrows(IllegalArgumentException.class, () -> departmentService.updateCard(null));
        }

        @Test
        void shouldThrowException_WhenIdIsNull() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setId(null);
            departmentInfo.setName("Updated Department");
            assertThrows(IllegalArgumentException.class, () -> departmentService.updateCard(departmentInfo));
        }

        @Test
        void shouldThrowException_WhenNameIsInvalid() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setId(1L);
            departmentInfo.setName(null);
            assertThrows(IllegalArgumentException.class, () -> departmentService.updateCard(departmentInfo));

            departmentInfo.setName("");
            assertThrows(IllegalArgumentException.class, () -> departmentService.updateCard(departmentInfo));
        }

        @Test
        void shouldThrowException_WhenDepartmentDoesNotExist() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setId(1L);
            departmentInfo.setName("Updated Department");

            when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> departmentService.updateCard(departmentInfo));
            verify(departmentRepository).findById(1L);
            verify(departmentRepository, never()).save(any());
        }

        @Test
        void shouldThrowException_WhenSaveFails() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setId(1L);
            departmentInfo.setName("Updated Department");
            Department department = new Department();
            department.setId(1L);
            department.setName("Updated Department");

            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(departmentRepository.save(any(Department.class))).thenThrow(new RuntimeException("Save failed"));
            when(departmentMapper.toEntity(departmentInfo)).thenReturn(department);

            assertThrows(RuntimeException.class, () -> departmentService.updateCard(departmentInfo));
            verify(departmentRepository).findById(1L);
            verify(departmentRepository).save(any(Department.class));
        }

        @Test
        void shouldHandleSpecialCharactersAndLongNames() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setId(1L);
            departmentInfo.setName("a".repeat(256));
            assertThrows(IllegalArgumentException.class, () -> departmentService.updateCard(departmentInfo));
        }
    }

    @Nested
    class DeleteCardTests {
        @Test
        void shouldDeleteDepartment() {
            when(departmentRepository.existsById(1L)).thenReturn(true);
            doNothing().when(departmentRepository).deleteById(1L);

            departmentService.deleteCard(1L);

            verify(departmentRepository).existsById(1L);
            verify(departmentRepository).deleteById(1L);
        }

        @Test
        void shouldThrowException_WhenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> departmentService.deleteCard(null));
            verify(departmentRepository, never()).existsById(any());
            verify(departmentRepository, never()).deleteById(any());
        }

        @Test
        void shouldThrowException_WhenDepartmentDoesNotExist() {
            when(departmentRepository.existsById(1L)).thenReturn(false);

            assertThrows(RuntimeException.class, () -> departmentService.deleteCard(1L));
            verify(departmentRepository).existsById(1L);
            verify(departmentRepository, never()).deleteById(any());
        }

        @Test
        void shouldThrowException_WhenDeleteFails() {
            when(departmentRepository.existsById(1L)).thenReturn(true);
            doThrow(new RuntimeException("Delete failed")).when(departmentRepository).deleteById(1L);

            assertThrows(RuntimeException.class, () -> departmentService.deleteCard(1L));
            verify(departmentRepository).existsById(1L);
            verify(departmentRepository).deleteById(1L);
        }

        @Test
        void shouldHandleBoundaryIds() {
            // Test with zero ID
            when(departmentRepository.existsById(0L)).thenReturn(true);
            doNothing().when(departmentRepository).deleteById(0L);
            departmentService.deleteCard(0L);
            verify(departmentRepository).deleteById(0L);

            // Test with max long ID
            when(departmentRepository.existsById(Long.MAX_VALUE)).thenReturn(true);
            doNothing().when(departmentRepository).deleteById(Long.MAX_VALUE);
            departmentService.deleteCard(Long.MAX_VALUE);
            verify(departmentRepository).deleteById(Long.MAX_VALUE);
        }
    }

    @Test
    void shouldThrowException_WhenDepartmentExists() {
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo.setName("Test Department");
        Department department = new Department();
        department.setName("Test Department");

        when(departmentMapper.toEntity(departmentInfo)).thenReturn(department);
        when(departmentRepository.save(department)).thenThrow(new DataIntegrityViolationException("Department already exists"));

        assertThrows(DataIntegrityViolationException.class, () -> departmentService.createCard(departmentInfo));
    }

    @Test
    void shouldHandleSpecialCharactersAndLongNames() {
        DepartmentInfo departmentInfo = new DepartmentInfo();
        departmentInfo.setName("a".repeat(256));
        assertThrows(IllegalArgumentException.class, () -> {
            departmentService.createCard(departmentInfo);
        });
    }
} 