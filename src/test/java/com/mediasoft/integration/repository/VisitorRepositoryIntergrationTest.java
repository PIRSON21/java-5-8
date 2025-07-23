package com.mediasoft.integration.repository;

import com.mediasoft.entity.Sex;
import com.mediasoft.entity.Visitor;
import com.mediasoft.integration.BaseIntegrationTest;
import com.mediasoft.repository.VisitorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VisitorRepositoryIntergrationTest extends BaseIntegrationTest {

    @Autowired
    private VisitorRepository visitorRepository;

    @Test
    void shouldSaveAndFindVisitor() {
        Visitor visitor = new Visitor();
        visitor.setName("John Doe");
        visitor.setAge(25);
        visitor.setSex(Sex.MALE);

        Visitor savedVisitor = visitorRepository.save(visitor);
        Visitor foundVisitor = visitorRepository.findById(savedVisitor.getId()).orElse(null);

        assertThat(foundVisitor).isNotNull();
        assertThat(foundVisitor.getName()).isEqualTo("John Doe");
        assertThat(foundVisitor.getAge()).isEqualTo(25);
        assertThat(foundVisitor.getSex()).isEqualTo(Sex.MALE);
    }
}
