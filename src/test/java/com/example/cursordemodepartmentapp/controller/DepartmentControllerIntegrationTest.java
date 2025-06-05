package com.example.cursordemodepartmentapp.controller;

import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import com.example.cursordemodepartmentapp.entity.Department;
import com.example.cursordemodepartmentapp.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DepartmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    private DepartmentInfo departmentInfo;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();

        departmentInfo = new DepartmentInfo();
        departmentInfo.setName("Test Department");
    }

    @Nested
    class CreateDepartmentTests {
        @Test
        void shouldCreateDepartmentAndReturnId() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", notNullValue()))
                    .andExpect(header().string("Location", notNullValue()))
                    .andReturn();

            Long id = objectMapper.readValue(result.getResponse().getContentAsString(), Long.class);
            assertNotNull(id);

            Department savedDepartment = departmentRepository.findById(id).orElse(null);
            assertNotNull(savedDepartment);
            assertEquals(departmentInfo.getName(), savedDepartment.getName());
        }

        @Test
        void shouldReturnBadRequest_WhenNameIsNull() throws Exception {
            departmentInfo.setName(null);

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isBadRequest());

            assertEquals(0, departmentRepository.count());
        }

        @Test
        void shouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
            departmentInfo.setName("");

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isBadRequest());

            assertEquals(0, departmentRepository.count());
        }

        @Test
        void shouldReturnBadRequest_WhenNameIsTooLong() throws Exception {
            departmentInfo.setName("a".repeat(256));

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isBadRequest());

            assertEquals(0, departmentRepository.count());
        }

        @Test
        void shouldReturnBadRequest_WhenDepartmentInfoIsNull() throws Exception {
            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("null"))
                    .andExpect(status().isBadRequest());

            assertEquals(0, departmentRepository.count());
        }

        @Test
        void shouldReturnBadRequest_WhenContentTypeIsInvalid() throws Exception {
            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("invalid content"))
                    .andExpect(status().isUnsupportedMediaType());

            assertEquals(0, departmentRepository.count());
        }

        @Test
        void shouldHandleSpecialCharacters() throws Exception {
            departmentInfo.setName("Test Department!@#$%^&*()_+");
            MvcResult result = mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", notNullValue()))
                    .andReturn();

            Long id = objectMapper.readValue(result.getResponse().getContentAsString(), Long.class);
            Department savedDepartment = departmentRepository.findById(id).orElse(null);
            assertNotNull(savedDepartment);
            assertEquals("Test Department!@#$%^&*()_+", savedDepartment.getName());
        }
    }

    @Nested
    class GetDepartmentTests {
        private Department savedDepartment;

        @BeforeEach
        void setUp() {
            Department department = new Department();
            department.setName("Test Department");
            savedDepartment = departmentRepository.save(department);
        }

        @Test
        void shouldReturnDepartment() throws Exception {
            mockMvc.perform(get("/api/departments/" + savedDepartment.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(savedDepartment.getId()))
                    .andExpect(jsonPath("$.name").value("Test Department"));
        }

        @Test
        void shouldReturnNotFound_WhenDepartmentDoesNotExist() throws Exception {
            mockMvc.perform(get("/api/departments/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
            mockMvc.perform(get("/api/departments/invalid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UpdateDepartmentTests {
        private Department savedDepartment;

        @BeforeEach
        void setUp() {
            Department department = new Department();
            department.setName("Test Department");
            savedDepartment = departmentRepository.save(department);
        }

        @Test
        void shouldUpdateDepartment() throws Exception {
            departmentInfo.setId(savedDepartment.getId());
            departmentInfo.setName("Updated Department");

            mockMvc.perform(put("/api/departments/" + savedDepartment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isNoContent());

            Department updatedDepartment = departmentRepository.findById(savedDepartment.getId()).orElse(null);
            assertNotNull(updatedDepartment);
            assertEquals("Updated Department", updatedDepartment.getName());
        }

        @Test
        void shouldReturnBadRequest_WhenIdMismatch() throws Exception {
            departmentInfo.setId(999L);
            departmentInfo.setName("Updated Department");

            mockMvc.perform(put("/api/departments/" + savedDepartment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isBadRequest());

            Department department = departmentRepository.findById(savedDepartment.getId()).orElse(null);
            assertNotNull(department);
            assertEquals("Test Department", department.getName());
        }

        @Test
        void shouldReturnBadRequest_WhenNameIsNull() throws Exception {
            departmentInfo.setId(savedDepartment.getId());
            departmentInfo.setName(null);

            mockMvc.perform(put("/api/departments/" + savedDepartment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isBadRequest());

            Department department = departmentRepository.findById(savedDepartment.getId()).orElse(null);
            assertNotNull(department);
            assertEquals("Test Department", department.getName());
        }

        @Test
        void shouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
            departmentInfo.setId(savedDepartment.getId());
            departmentInfo.setName("");

            mockMvc.perform(put("/api/departments/" + savedDepartment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isBadRequest());

            Department department = departmentRepository.findById(savedDepartment.getId()).orElse(null);
            assertNotNull(department);
            assertEquals("Test Department", department.getName());
        }

        @Test
        void shouldReturnNotFound_WhenDepartmentDoesNotExist() throws Exception {
            departmentInfo.setId(999L);
            departmentInfo.setName("Updated Department");

            mockMvc.perform(put("/api/departments/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnBadRequest_WhenContentTypeIsInvalid() throws Exception {
            mockMvc.perform(put("/api/departments/" + savedDepartment.getId())
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("invalid content"))
                    .andExpect(status().isUnsupportedMediaType());

            Department department = departmentRepository.findById(savedDepartment.getId()).orElse(null);
            assertNotNull(department);
            assertEquals("Test Department", department.getName());
        }
    }

    @Nested
    class DeleteDepartmentTests {
        private Department savedDepartment;

        @BeforeEach
        void setUp() {
            Department department = new Department();
            department.setName("Test Department");
            savedDepartment = departmentRepository.save(department);
        }

        @Test
        void shouldDeleteDepartment() throws Exception {
            mockMvc.perform(delete("/api/departments/" + savedDepartment.getId()))
                    .andExpect(status().isNoContent());

            assertFalse(departmentRepository.existsById(savedDepartment.getId()));
        }

        @Test
        void shouldReturnNotFound_WhenDepartmentDoesNotExist() throws Exception {
            mockMvc.perform(delete("/api/departments/999"))
                    .andExpect(status().isNotFound());

            assertTrue(departmentRepository.existsById(savedDepartment.getId()));
        }

        @Test
        void shouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
            mockMvc.perform(delete("/api/departments/invalid"))
                    .andExpect(status().isBadRequest());

            assertTrue(departmentRepository.existsById(savedDepartment.getId()));
        }
    }

    @Nested
    class GetAllDepartmentsTests {
        @BeforeEach
        void setUp() {
            Department department1 = new Department();
            department1.setName("Test Department 1");
            departmentRepository.save(department1);

            Department department2 = new Department();
            department2.setName("Test Department 2");
            departmentRepository.save(department2);
        }

        @Test
        void shouldReturnAllDepartments() throws Exception {
            mockMvc.perform(get("/api/departments"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].name").value("Test Department 1"))
                    .andExpect(jsonPath("$[1].name").value("Test Department 2"));
        }

        @Test
        void shouldReturnEmptyList_WhenNoDepartmentsExist() throws Exception {
            departmentRepository.deleteAll();

            mockMvc.perform(get("/api/departments"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }
} 