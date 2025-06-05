package com.example.cursordemodepartmentapp.controller;

import com.example.cursordemodepartmentapp.controller.impl.DepartmentControllerImpl;
import com.example.cursordemodepartmentapp.dto.DepartmentInfo;
import com.example.cursordemodepartmentapp.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@WebMvcTest(DepartmentControllerImpl.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private DepartmentController departmentController;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentInfo departmentInfo;

    @BeforeEach
    void setUp() {
        departmentInfo = new DepartmentInfo();
        departmentInfo.setId(1L);
        departmentInfo.setName("Test Department");
    }

    @Nested
    class GetAllDepartmentsTests {
        @Test
        void shouldReturnAllDepartments() {
            List<DepartmentInfo> departments = Arrays.asList(
                new DepartmentInfo(), new DepartmentInfo()
            );
            when(departmentService.getAllCards()).thenReturn(departments);

            ResponseEntity<List<DepartmentInfo>> response = departmentController.getAllDepartments();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(departments, response.getBody());
        }

        @Test
        void shouldHandleServiceException() {
            when(departmentService.getAllCards()).thenThrow(new RuntimeException("Service error"));
            ResponseEntity<List<DepartmentInfo>> response = departmentController.getAllDepartments();
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Nested
    class GetDepartmentTests {
        @Test
        void shouldReturnDepartment() {
            DepartmentInfo department = new DepartmentInfo();
            when(departmentService.getCard(1L)).thenReturn(department);

            ResponseEntity<DepartmentInfo> response = departmentController.getDepartment(1L);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(department, response.getBody());
        }

        @Test
        void shouldHandleServiceException_WhenGettingDepartment() {
            when(departmentService.getCard(1L)).thenThrow(new RuntimeException("Service error"));
            ResponseEntity<DepartmentInfo> response = departmentController.getDepartment(1L);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    class CreateDepartmentTests {
        @Test
        void shouldReturnCreatedId() throws Exception {
            DepartmentInfo newDepartment = new DepartmentInfo();
            newDepartment.setName("New Department");

            when(departmentService.createCard(any(DepartmentInfo.class))).thenReturn(1L);

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newDepartment)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("1"))
                    .andExpect(header().string("Location", "/api/departments/1"));

            verify(departmentService).createCard(any(DepartmentInfo.class));
        }

        @Test
        void shouldReturnBadRequest_WhenNameIsNull() throws Exception {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setName(null);

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isBadRequest());

            verify(departmentService, never()).createCard(any());
        }

        @Test
        void shouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
            departmentInfo.setName("");

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequest_WhenNameIsTooLong() throws Exception {
            departmentInfo.setName("a".repeat(256));

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequest_WhenDepartmentInfoIsNull() throws Exception {
            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("null"))
                    .andExpect(status().isBadRequest());

            verify(departmentService, never()).createCard(any());
        }

        @Test
        void shouldReturnBadRequest_WhenContentTypeIsInvalid() throws Exception {
            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("invalid content"))
                    .andExpect(status().isUnsupportedMediaType());

            verify(departmentService, never()).createCard(any());
        }

        @Test
        void shouldHandleSpecialCharacters() throws Exception {
            departmentInfo.setName("Test Department!@#$%^&*()_+");
            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(departmentInfo)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", notNullValue()));
        }

        @Test
        void shouldHandleServiceException() throws Exception {
            DepartmentInfo newDepartment = new DepartmentInfo();
            newDepartment.setName("New Department");

            when(departmentService.createCard(any(DepartmentInfo.class)))
                    .thenThrow(new RuntimeException("Service error"));

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newDepartment)))
                    .andExpect(status().isInternalServerError());

            verify(departmentService).createCard(any(DepartmentInfo.class));
        }
    }

    @Nested
    class UpdateDepartmentTests {
        @Test
        void shouldUpdateDepartment() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setId(1L);
            departmentInfo.setName("Updated Department");
            doNothing().when(departmentService).updateCard(departmentInfo);

            ResponseEntity<Void> response = departmentController.updateDepartment(1L, departmentInfo);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        void shouldHandleServiceException_WhenUpdatingDepartment() {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setId(1L);
            departmentInfo.setName("Updated Department");
            doThrow(new RuntimeException("Service error")).when(departmentService).updateCard(departmentInfo);

            ResponseEntity<Void> response = departmentController.updateDepartment(1L, departmentInfo);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    class DeleteDepartmentTests {
        @Test
        void shouldReturnNoContent() throws Exception {
            doNothing().when(departmentService).deleteCard(1L);

            mockMvc.perform(delete("/api/departments/1"))
                    .andExpect(status().isNoContent());

            verify(departmentService).deleteCard(1L);
        }

        @Test
        void shouldReturnNotFound_WhenDepartmentDoesNotExist() throws Exception {
            doThrow(new RuntimeException("Department not found"))
                    .when(departmentService).deleteCard(1L);

            mockMvc.perform(delete("/api/departments/1"))
                    .andExpect(status().isNotFound());

            verify(departmentService).deleteCard(1L);
        }

        @Test
        void shouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
            mockMvc.perform(delete("/api/departments/invalid"))
                    .andExpect(status().isBadRequest());

            verify(departmentService, never()).deleteCard(any());
        }

        @Test
        void shouldHandleServiceException() throws Exception {
            doThrow(new RuntimeException("Service error")).when(departmentService).deleteCard(1L);
            mockMvc.perform(delete("/api/departments/1"))
                    .andExpect(status().isNotFound());
        }
    }
} 