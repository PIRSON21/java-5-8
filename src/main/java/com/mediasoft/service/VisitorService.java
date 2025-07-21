package com.mediasoft.service;

import com.mediasoft.dto.VisitorRequestDTO;
import com.mediasoft.dto.VisitorResponseDTO;

import java.util.List;

public interface VisitorService {
    VisitorResponseDTO create(VisitorRequestDTO visitorRequestDTO);
    List<VisitorResponseDTO> getAll();
    VisitorResponseDTO getById(Long id);
    void delete(Long id);
    VisitorResponseDTO update(Long id, VisitorRequestDTO visitorRequestDTO);
}
