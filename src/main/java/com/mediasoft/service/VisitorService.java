package com.mediasoft.service;

import com.mediasoft.entity.Visitor;
import com.mediasoft.repository.VisitorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitorService {
    private final VisitorRepository visitorRepository;

    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public void save(Visitor visitor) {
        visitorRepository.save(visitor);
    }

    public Visitor findById(Long id) {
        return visitorRepository.findById(id);
    }

    public void remove(Long id) {
        visitorRepository.remove(id);
    }

    public List<Visitor> findAll() {
        return visitorRepository.findAll();
    }
}
