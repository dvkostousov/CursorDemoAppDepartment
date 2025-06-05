package com.example.cursordemodepartmentapp.repository;

import com.example.cursordemodepartmentapp.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DepartmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Test Department");
    }

    @Nested
    class FindByIdTests {
        @Test
        void shouldReturnDepartment_WhenExists() {
            Department department = new Department();
            department.setName("Test Department");
            departmentRepository.saveAndFlush(department);
            Optional<Department> found = departmentRepository.findById(department.getId());
            assertTrue(found.isPresent());
            assertEquals("Test Department", found.get().getName());
        }

        @Test
        void shouldReturnEmpty_WhenNotExists() {
            Optional<Department> result = departmentRepository.findById(999L);
            assertFalse(result.isPresent());
        }

        @Test
        void shouldHandleNullId() {
            assertThrows(InvalidDataAccessApiUsageException.class, () -> {
                departmentRepository.findById(null);
            });
        }

        @Test
        void shouldHandleBoundaryIds() {
            Department department = new Department();
            department.setName("Boundary");
            departmentRepository.saveAndFlush(department);
            Optional<Department> found = departmentRepository.findById(department.getId());
            assertTrue(found.isPresent());
        }
    }

    @Nested
    class SaveTests {
        @Test
        void shouldPersistDepartment() {
            Department savedDepartment = departmentRepository.save(department);

            assertNotNull(savedDepartment.getId());
            Department foundDepartment = entityManager.find(Department.class, savedDepartment.getId());
            assertNotNull(foundDepartment);
            assertEquals(department.getName(), foundDepartment.getName());
        }

        @Test
        void shouldUpdateExistingDepartment() {
            Department department = new Department();
            department.setName("Test Department");
            department = departmentRepository.save(department);
            assertNotNull(department.getId());

            department.setName("Updated Department");
            Department updatedDepartment = departmentRepository.save(department);
            assertEquals("Updated Department", updatedDepartment.getName());
        }

        @Test
        void shouldHandleNullFields() {
            department.setName(null);
            assertThrows(jakarta.validation.ConstraintViolationException.class, () -> {
                departmentRepository.save(department);
            });
        }

        @Test
        void shouldHandleEmptyFields() {
            department.setName("");
            assertThrows(jakarta.validation.ConstraintViolationException.class, () -> {
                departmentRepository.save(department);
            });
        }

        @Test
        void shouldHandleSpecialCharactersAndLongNames() {
            Department department = new Department();
            department.setName("a".repeat(256));
            assertThrows(DataIntegrityViolationException.class, () -> {
                departmentRepository.saveAndFlush(department);
            });
        }

        @Test
        void shouldNotSaveDepartment_WhenNameIsTooLong() {
            Department department = new Department();
            department.setName("a".repeat(256));
            assertThrows(Exception.class, () -> departmentRepository.saveAndFlush(department));
        }

        @Test
        void shouldNotSaveDepartment_WhenNameIsEmpty() {
            Department department = new Department();
            department.setName("");
            department.setIdAddress(1L);
            assertThrows(jakarta.validation.ConstraintViolationException.class, () -> departmentRepository.saveAndFlush(department));
        }

        @Test
        void shouldSaveDepartment_WithValidIdAddress() {
            Department department = new Department();
            department.setName("Test Department");
            department.setIdAddress(1L);
            Department savedDepartment = departmentRepository.save(department);
            
            assertNotNull(savedDepartment.getId());
            Department foundDepartment = entityManager.find(Department.class, savedDepartment.getId());
            assertNotNull(foundDepartment);
            assertEquals(1L, foundDepartment.getIdAddress());
        }

        @Test
        void shouldUpdateDepartment_WithNewIdAddress() {
            Department department = new Department();
            department.setName("Test Department");
            department.setIdAddress(1L);
            department = departmentRepository.save(department);
            
            department.setIdAddress(2L);
            Department updatedDepartment = departmentRepository.save(department);
            assertEquals(2L, updatedDepartment.getIdAddress());
        }

        @Test
        void shouldSaveDepartment_WithNullIdAddress() {
            Department department = new Department();
            department.setName("Test Department");
            department.setIdAddress(null);
            Department savedDepartment = departmentRepository.save(department);
            
            assertNotNull(savedDepartment.getId());
            Department foundDepartment = entityManager.find(Department.class, savedDepartment.getId());
            assertNotNull(foundDepartment);
            assertNull(foundDepartment.getIdAddress());
        }
    }

    @Nested
    class FindAllTests {
        @Test
        void shouldFindAllDepartments() {
            Department department1 = new Department();
            department1.setName("Test Department 1");
            departmentRepository.save(department1);

            Department department2 = new Department();
            department2.setName("Test Department 2");
            departmentRepository.save(department2);

            List<Department> departments = departmentRepository.findAll();
            assertEquals(2, departments.size());
            assertTrue(departments.stream().anyMatch(d -> d.getName().equals("Test Department 1")));
            assertTrue(departments.stream().anyMatch(d -> d.getName().equals("Test Department 2")));
        }

        @Test
        void shouldReturnEmptyList_WhenNoDepartments() {
            List<Department> departments = departmentRepository.findAll();
            assertTrue(departments.isEmpty());
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void shouldRemoveDepartment() {
            Department department = new Department();
            department.setName("Test Department");
            department = departmentRepository.save(department);
            assertNotNull(department.getId());

            departmentRepository.delete(department);
            assertFalse(departmentRepository.existsById(department.getId()));
        }

        @Test
        void shouldHandleNonExistentId() {
            assertDoesNotThrow(() -> departmentRepository.deleteById(999L));
        }

        @Test
        void shouldHandleNullId() {
            assertThrows(InvalidDataAccessApiUsageException.class, () -> {
                departmentRepository.deleteById(null);
            });
        }

        @Test
        void shouldHandleBoundaryIds() {
            Department department = new Department();
            department.setName("Test Department");
            department = departmentRepository.save(department);
            assertNotNull(department.getId());

            departmentRepository.deleteById(department.getId());
            assertFalse(departmentRepository.existsById(department.getId()));
        }
    }
} 