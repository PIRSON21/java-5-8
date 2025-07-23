package com.mediasoft.service.impl;

import com.mediasoft.dto.VisitorRequestDTO;
import com.mediasoft.dto.VisitorResponseDTO;
import com.mediasoft.entity.Visitor;
import com.mediasoft.exception.ResourceNotFoundException;
import com.mediasoft.mapper.VisitorMapper;
import com.mediasoft.repository.VisitorRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorServiceTest {

    @Mock
    private VisitorRepository visitorRepository;

    @Mock
    private VisitorMapper visitorMapper;

    @InjectMocks
    private VisitorService visitorService;

    private VisitorRequestDTO requestDTO;
    private VisitorResponseDTO responseDTO;
    private Visitor visitor;

    @BeforeEach
    void setUp() {
        requestDTO = new VisitorRequestDTO(
                "John Doe",
                15,
                "MALE"
        );
        responseDTO = new VisitorResponseDTO(
                1L,
                "John Doe",
                15,
                "MALE"
        );
        visitor = new Visitor();
        visitor.setId(1L);
    }

    @Test
    void create_ShouldCreateVisitor() {
        when(visitorMapper.toVisitor(requestDTO)).thenReturn(visitor);
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toVisitorResponseDTO(visitor)).thenReturn(responseDTO);

        VisitorResponseDTO result = visitorService.create(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(visitorMapper).toVisitor(requestDTO);
        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toVisitorResponseDTO(visitor);
    }

    @Test
    void getAll_ShouldReturnAllVisitors() {
        List<Visitor> visitors = Arrays.asList(visitor);
        when(visitorRepository.findAll()).thenReturn(visitors);
        when(visitorMapper.toVisitorResponseDTO(visitor)).thenReturn(responseDTO);

        List<VisitorResponseDTO> result = visitorService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO, result.get(0));
        verify(visitorRepository).findAll();
        verify(visitorMapper).toVisitorResponseDTO(visitor);
    }

    @Test
    void getById_WhenVisitorExists_ShouldReturnVisitor() {
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorMapper.toVisitorResponseDTO(visitor)).thenReturn(responseDTO);

        VisitorResponseDTO result = visitorService.getById(1L);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(visitorRepository).findById(1L);
        verify(visitorMapper).toVisitorResponseDTO(visitor);
    }

    @Test
    void getById_WhenVisitorNotExists_ShouldReturnNull() {
        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            VisitorResponseDTO result = visitorService.getById(1L);
        } catch (ResourceNotFoundException e) {
            assertEquals("Посетитель с ID 1 не найден", e.getMessage());
        }

        verify(visitorRepository).findById(1L);
        verify(visitorMapper, never()).toVisitorResponseDTO(any());
    }

    @Test
    void delete_ShouldDeleteVisitor() {
        visitorService.delete(1L);

        verify(visitorRepository).deleteById(1L);
    }

    @Test
    void update_WhenVisitorExists_ShouldUpdateVisitor() {
        Visitor updatedVisitor = new Visitor();
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorMapper.toVisitor(requestDTO)).thenReturn(updatedVisitor);
        when(visitorRepository.save(updatedVisitor)).thenReturn(updatedVisitor);
        when(visitorMapper.toVisitorResponseDTO(updatedVisitor)).thenReturn(responseDTO);

        VisitorResponseDTO result = visitorService.update(1L, requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        assertEquals(1L, updatedVisitor.getId());
        verify(visitorRepository).findById(1L);
        verify(visitorMapper).toVisitor(requestDTO);
        verify(visitorRepository).save(updatedVisitor);
        verify(visitorMapper).toVisitorResponseDTO(updatedVisitor);
    }

    @Test
    void update_WhenVisitorNotExists_ShouldReturnNull() {
        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            VisitorResponseDTO result = visitorService.update(1L, requestDTO);
        } catch (ResourceNotFoundException e) {
            assertEquals("Посетитель с ID 1 не найден", e.getMessage());
        }

        verify(visitorRepository).findById(1L);
        verify(visitorMapper, never()).toVisitor(any());
        verify(visitorRepository, never()).save(any());
    }
}