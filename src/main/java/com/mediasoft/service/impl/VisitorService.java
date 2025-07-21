package com.mediasoft.service.impl;

import com.mediasoft.dto.VisitorRequestDTO;
import com.mediasoft.dto.VisitorResponseDTO;
import com.mediasoft.entity.Visitor;
import com.mediasoft.mapper.VisitorMapper;
import com.mediasoft.repository.impl.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorService implements com.mediasoft.service.VisitorService {
    private final VisitorRepository visitorRepository;
    private final VisitorMapper visitorMapper;

    @Override
    public VisitorResponseDTO create(VisitorRequestDTO visitorRequestDTO) {
        Visitor visitor = visitorMapper.toVisitor(visitorRequestDTO);
        return visitorMapper.toVisitorResponseDTO(visitorRepository.save(visitor));
    }

    @Override
    public List<VisitorResponseDTO> getAll() {
        return visitorRepository.findAll().stream()
                .map(visitor -> visitorMapper.toVisitorResponseDTO(visitor))
                .toList();
    }

    @Override
    public VisitorResponseDTO getById(Long id) {
        Visitor visitor = visitorRepository.findById(id);
        if (visitor == null) {
            return null;
        }
        return visitorMapper.toVisitorResponseDTO(visitor);
    }

    @Override
    public void delete(Long id) {
        visitorRepository.remove(id);
    }

    @Override
    public VisitorResponseDTO update(Long id, VisitorRequestDTO visitorRequestDTO) {
        Visitor existingVisitor = visitorRepository.findById(id);
        if (existingVisitor == null) {
            return null;
        }
        Visitor updatedVisitor = visitorMapper.toVisitor(visitorRequestDTO);
        updatedVisitor.setId(existingVisitor.getId());
        return visitorMapper.toVisitorResponseDTO(visitorRepository.save(updatedVisitor));
    }
}
