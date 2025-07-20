package com.mediasoft.repository;

import com.mediasoft.entity.Visitor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class VisitorRepository {
    private final List<Visitor> visitors = new ArrayList<>();

    public void save(Visitor visitor) {
        for (int i = 0; i < visitors.size(); i++) {
            if (visitors.get(i).getId().equals(visitor.getId())) {
                visitors.set(i, visitor);
                return;
            }
        }
        visitors.add(visitor);
    }

    public List<Visitor> findAll() {
        return new ArrayList<>(visitors);
    }

    public Visitor findById(Long id) {
        return visitors.stream()
                .filter(visitor -> visitor.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void remove(Long id) {
        visitors.removeIf(visitor -> visitor.getId().equals(id));
    }
}
