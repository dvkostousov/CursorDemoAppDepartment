package com.example.cursordemodepartmentapp.mapper;

import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import com.example.cursordemodepartmentapp.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentMapperTest {

    private DepartmentMapper mapper;
    private Department department;
    private DepartmentInfo departmentInfo;

    @BeforeEach
    void setUp() {
        mapper = new DepartmentMapper();
        department = new Department();
        department.setId(1L);
        department.setName("Test Department");

        departmentInfo = new DepartmentInfo();
        departmentInfo.setId(1L);
        departmentInfo.setName("Test Department");
    }

    @Nested
    class ConstructorTests {
        @Test
        void shouldCreateInstance() {
            DepartmentMapper mapper = new DepartmentMapper();
            assertNotNull(mapper);
        }
    }

    @Nested
    class ToDtoTests {
        @Test
        void shouldMapDepartmentToDepartmentInfo() {
            DepartmentInfo result = mapper.toDto(department);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test Department", result.getName());
        }

        @Test
        void shouldReturnNull_WhenDepartmentIsNull() {
            assertNull(mapper.toDto(null));
        }

        @Test
        void shouldHandleNullFields() {
            department.setId(null);
            department.setName(null);

            DepartmentInfo result = mapper.toDto(department);

            assertNotNull(result);
            assertNull(result.getId());
            assertNull(result.getName());
        }

        @Test
        void shouldHandleEmptyFields() {
            department.setName("");

            DepartmentInfo result = mapper.toDto(department);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("", result.getName());
        }

        @Test
        void shouldHandleSpecialCharactersAndLongNames() {
            // Test with special characters
            department.setName("Test Department!@#$%^&*()_+");
            DepartmentInfo result = mapper.toDto(department);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test Department!@#$%^&*()_+", result.getName());

            // Test with long name
            department.setName("a".repeat(1000));
            result = mapper.toDto(department);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("a".repeat(1000), result.getName());
        }

        @Test
        void shouldHandleBoundaryIds() {
            // Test with zero ID
            department.setId(0L);
            DepartmentInfo result = mapper.toDto(department);
            assertNotNull(result);
            assertEquals(0L, result.getId());
            assertEquals("Test Department", result.getName());

            // Test with max long ID
            department.setId(Long.MAX_VALUE);
            result = mapper.toDto(department);
            assertNotNull(result);
            assertEquals(Long.MAX_VALUE, result.getId());
            assertEquals("Test Department", result.getName());
        }
    }

    @Nested
    class ToEntityTests {
        @Test
        void shouldMapDepartmentInfoToDepartment() {
            Department result = mapper.toEntity(departmentInfo);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test Department", result.getName());
        }

        @Test
        void shouldReturnNull_WhenDepartmentInfoIsNull() {
            assertNull(mapper.toEntity(null));
        }

        @Test
        void shouldHandleNullFields() {
            departmentInfo.setId(null);
            departmentInfo.setName(null);

            Department result = mapper.toEntity(departmentInfo);

            assertNotNull(result);
            assertNull(result.getId());
            assertNull(result.getName());
        }

        @Test
        void shouldHandleEmptyFields() {
            departmentInfo.setName("");

            Department result = mapper.toEntity(departmentInfo);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("", result.getName());
        }

        @Test
        void shouldHandleSpecialCharactersAndLongNames() {
            // Test with special characters
            departmentInfo.setName("Test Department!@#$%^&*()_+");
            Department result = mapper.toEntity(departmentInfo);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test Department!@#$%^&*()_+", result.getName());

            // Test with long name
            departmentInfo.setName("a".repeat(1000));
            result = mapper.toEntity(departmentInfo);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("a".repeat(1000), result.getName());
        }

        @Test
        void shouldHandleBoundaryIds() {
            // Test with zero ID
            departmentInfo.setId(0L);
            Department result = mapper.toEntity(departmentInfo);
            assertNotNull(result);
            assertEquals(0L, result.getId());
            assertEquals("Test Department", result.getName());

            // Test with max long ID
            departmentInfo.setId(Long.MAX_VALUE);
            result = mapper.toEntity(departmentInfo);
            assertNotNull(result);
            assertEquals(Long.MAX_VALUE, result.getId());
            assertEquals("Test Department", result.getName());
        }
    }

    @Nested
    class ListMappingTests {
        @Test
        void shouldMapListOfDepartments() {
            List<Department> departments = Arrays.asList(
                createDepartment(1L, "Department 1"),
                createDepartment(2L, "Department 2")
            );

            List<DepartmentInfo> result = departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Department 1", result.get(0).getName());
            assertEquals("Department 2", result.get(1).getName());
        }

        @Test
        void shouldHandleNullList() {
            List<Department> departments = null;
            assertThrows(NullPointerException.class, () -> 
                departments.stream()
                    .map(mapper::toDto)
                    .collect(Collectors.toList())
            );
        }

        @Test
        void shouldHandleEmptyList() {
            List<Department> departments = Collections.emptyList();
            List<DepartmentInfo> result = departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void shouldHandleNullElements() {
            List<Department> departments = Arrays.asList(
                createDepartment(1L, "Department 1"),
                null,
                createDepartment(2L, "Department 2")
            );
            List<DepartmentInfo> result = departments.stream()
                .map(dep -> dep == null ? null : mapper.toDto(dep))
                .collect(Collectors.toList());
            assertNull(result.get(1));
        }

        @Test
        void shouldHandleNullFields() {
            List<Department> departments = Arrays.asList(
                createDepartment(1L, null),
                createDepartment(2L, "Department 2")
            );

            List<DepartmentInfo> result = departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertNull(result.get(0).getName());
            assertEquals("Department 2", result.get(1).getName());
        }

        @Test
        void shouldHandleEmptyFields() {
            List<Department> departments = Arrays.asList(
                createDepartment(1L, ""),
                createDepartment(2L, "Department 2")
            );

            List<DepartmentInfo> result = departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("", result.get(0).getName());
            assertEquals("Department 2", result.get(1).getName());
        }

        @Test
        void shouldHandleSpecialCharacters() {
            List<Department> departments = Arrays.asList(
                createDepartment(1L, "Department!@#$%^&*()_+"),
                createDepartment(2L, "Department 2")
            );

            List<DepartmentInfo> result = departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Department!@#$%^&*()_+", result.get(0).getName());
            assertEquals("Department 2", result.get(1).getName());
        }

        @Test
        void shouldHandleLongNames() {
            String longName = "a".repeat(255);
            List<Department> departments = Arrays.asList(
                createDepartment(1L, longName),
                createDepartment(2L, "Department 2")
            );

            List<DepartmentInfo> result = departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(longName, result.get(0).getName());
            assertEquals("Department 2", result.get(1).getName());
        }

        @Test
        void shouldHandleZeroIds() {
            List<Department> departments = Arrays.asList(
                createDepartment(0L, "Department 1"),
                createDepartment(2L, "Department 2")
            );

            List<DepartmentInfo> result = departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(0L, result.get(0).getId());
            assertEquals(2L, result.get(1).getId());
        }

        @Test
        void shouldHandleMaxLongIds() {
            List<Department> departments = Arrays.asList(
                createDepartment(Long.MAX_VALUE, "Department 1"),
                createDepartment(2L, "Department 2")
            );

            List<DepartmentInfo> result = departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(Long.MAX_VALUE, result.get(0).getId());
            assertEquals(2L, result.get(1).getId());
        }

        @Test
        void shouldHandleDuplicateIds() {
            List<Department> departments = Arrays.asList(
                createDepartment(1L, "Department 1"),
                createDepartment(1L, "Department 2")
            );

            List<DepartmentInfo> result = departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals(1L, result.get(1).getId());
            assertEquals("Department 1", result.get(0).getName());
            assertEquals("Department 2", result.get(1).getName());
        }
    }

    private static Department createDepartment(Long id, String name) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        return department;
    }
} 