package com.mediasoft.controller;

import com.mediasoft.dto.VisitorRequestDTO;
import com.mediasoft.dto.VisitorResponseDTO;
import com.mediasoft.service.VisitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
public class VisitorController {
    private final VisitorService visitorService;

    @PostMapping
    public VisitorResponseDTO createVisitor(@Valid @RequestBody VisitorRequestDTO visitorRequestDTO) {
        return visitorService.create(visitorRequestDTO);
    }

    @GetMapping
    public List<VisitorResponseDTO> getAllVisitors() {
        return visitorService.getAll();
    }

    @GetMapping("/{id}")
    public VisitorResponseDTO getVisitorById(@PathVariable Long id) {
        return visitorService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteVisitor(@PathVariable Long id) {
        visitorService.delete(id);
    }

    @PutMapping("/{id}")
    public VisitorResponseDTO updateVisitor(@PathVariable Long id, @Valid @RequestBody VisitorRequestDTO visitorRequestDTO) {
        return visitorService.update(id, visitorRequestDTO);
    }
}
