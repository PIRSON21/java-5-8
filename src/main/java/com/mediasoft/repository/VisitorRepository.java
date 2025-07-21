package com.mediasoft.repository;

import com.mediasoft.entity.Visitor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorRepository {
    Visitor save(Visitor visitor);
    Visitor findById(Long id);
    void remove(Long id);
    List<Visitor> findAll();
}
