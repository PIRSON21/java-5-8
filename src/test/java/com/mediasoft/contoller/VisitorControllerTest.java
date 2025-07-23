package com.mediasoft.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediasoft.controller.VisitorController;
import com.mediasoft.dto.VisitorRequestDTO;
import com.mediasoft.dto.VisitorResponseDTO;
import com.mediasoft.service.VisitorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitorController.class)
@ExtendWith(MockitoExtension.class)
class VisitorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitorService visitorService;

    @Autowired
    private ObjectMapper objectMapper;

    private VisitorRequestDTO requestDTO;
    private VisitorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new VisitorRequestDTO("Иван Иванов", 25, "MALE");
        responseDTO = new VisitorResponseDTO(1L, "Иван Иванов", 25, "MALE");
    }

    @Test
    void createVisitor_ShouldReturnCreatedVisitor() throws Exception {
        when(visitorService.create(any(VisitorRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/visitors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Иванов")))
                .andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.sex", is("MALE")));

        verify(visitorService).create(any(VisitorRequestDTO.class));
    }

    @Test
    void createVisitor_WithNullName_ShouldReturnCreatedVisitor() throws Exception {
        VisitorRequestDTO invalidRequest = new VisitorRequestDTO(null, 25, "MALE");

        mockMvc.perform(post("/api/visitors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk());

        verify(visitorService).create(any(VisitorRequestDTO.class));
    }

    @Test
    void createVisitor_WithNegativeAge_ShouldReturnBadRequest() throws Exception {
        VisitorRequestDTO invalidRequest = new VisitorRequestDTO("Петр Петров", -5, "MALE");

        mockMvc.perform(post("/api/visitors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(visitorService);
    }

    @Test
    void createVisitor_WithBlankSex_ShouldReturnBadRequest() throws Exception {
        VisitorRequestDTO invalidRequest = new VisitorRequestDTO("Анна Иванова", 30, "");

        mockMvc.perform(post("/api/visitors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(visitorService);
    }

    @Test
    void createVisitor_WithNullSex_ShouldReturnBadRequest() throws Exception {
        VisitorRequestDTO invalidRequest = new VisitorRequestDTO("Анна Иванова", 30, null);

        mockMvc.perform(post("/api/visitors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(visitorService);
    }

    @Test
    void getAllVisitors_ShouldReturnVisitorsList() throws Exception {
        VisitorResponseDTO secondVisitor = new VisitorResponseDTO(2L, "Мария Петрова", 28, "FEMALE");
        List<VisitorResponseDTO> visitors = Arrays.asList(responseDTO, secondVisitor);
        when(visitorService.getAll()).thenReturn(visitors);

        mockMvc.perform(get("/api/visitors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Иван Иванов")))
                .andExpect(jsonPath("$[0].age", is(25)))
                .andExpect(jsonPath("$[0].sex", is("MALE")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Мария Петрова")))
                .andExpect(jsonPath("$[1].age", is(28)))
                .andExpect(jsonPath("$[1].sex", is("FEMALE")));

        verify(visitorService).getAll();
    }

    @Test
    void getAllVisitors_WhenNoVisitors_ShouldReturnEmptyList() throws Exception {
        when(visitorService.getAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/visitors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(visitorService).getAll();
    }

    @Test
    void getVisitorById_WhenVisitorExists_ShouldReturnVisitor() throws Exception {
        when(visitorService.getById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/visitors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Иванов")))
                .andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.sex", is("MALE")));

        verify(visitorService).getById(1L);
    }

    @Test
    void getVisitorById_WhenVisitorNotExists_ShouldReturnNull() throws Exception {
        when(visitorService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/visitors/{id}", 999L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(visitorService).getById(999L);
    }

    @Test
    void updateVisitor_WhenVisitorExists_ShouldReturnUpdatedVisitor() throws Exception {
        VisitorResponseDTO updatedResponse = new VisitorResponseDTO(1L, "Иван Сидоров", 26, "MALE");
        when(visitorService.update(eq(1L), any(VisitorRequestDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/visitors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Сидоров")))
                .andExpect(jsonPath("$.age", is(26)))
                .andExpect(jsonPath("$.sex", is("MALE")));

        verify(visitorService).update(eq(1L), any(VisitorRequestDTO.class));
    }

    @Test
    void updateVisitor_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        VisitorRequestDTO invalidRequest = new VisitorRequestDTO("", -1, null);

        mockMvc.perform(put("/api/visitors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(visitorService);
    }

    @Test
    void deleteVisitor_ShouldCallServiceDelete() throws Exception {
        doNothing().when(visitorService).delete(1L);

        mockMvc.perform(delete("/api/visitors/{id}", 1L))
                .andExpect(status().isOk());

        verify(visitorService).delete(1L);
    }

    @Test
    void deleteVisitor_WithNonExistentId_ShouldStillReturn200() throws Exception {
        doNothing().when(visitorService).delete(999L);

        mockMvc.perform(delete("/api/visitors/{id}", 999L))
                .andExpect(status().isOk());

        verify(visitorService).delete(999L);
    }
}