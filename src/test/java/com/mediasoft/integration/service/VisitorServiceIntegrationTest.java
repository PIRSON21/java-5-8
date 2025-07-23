package com.mediasoft.integration.service;

import com.mediasoft.dto.VisitorRequestDTO;
import com.mediasoft.dto.VisitorResponseDTO;
import com.mediasoft.entity.Sex;
import com.mediasoft.exception.ResourceNotFoundException;
import com.mediasoft.integration.BaseIntegrationTest;
import com.mediasoft.service.VisitorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VisitorServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private VisitorService visitorService;

    private VisitorRequestDTO visitorRequestDTO1;
    private VisitorRequestDTO visitorRequestDTO2;

    @BeforeEach
    void setUp() {
        visitorRequestDTO1 = new VisitorRequestDTO(
                "Иван Иванов",
                25,
                Sex.MALE.toString()
        );

        visitorRequestDTO2 = new VisitorRequestDTO(
                "Анна Петрова",
                30,
                Sex.FEMALE.toString()
        );
    }

    @Test
    void shouldCreateVisitor() {
        VisitorResponseDTO created = visitorService.create(visitorRequestDTO1);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Иван Иванов");
        assertThat(created.getAge()).isEqualTo(25);
        assertThat(created.getSex()).isEqualTo(Sex.MALE.toString());
    }

    @Test
    void shouldGetAllVisitors() {
        VisitorResponseDTO visitor1 = visitorService.create(visitorRequestDTO1);
        VisitorResponseDTO visitor2 = visitorService.create(visitorRequestDTO2);

        List<VisitorResponseDTO> visitors = visitorService.getAll();

        assertThat(visitors).hasSize(2);
        assertThat(visitors)
                .extracting(VisitorResponseDTO::getName)
                .containsExactlyInAnyOrder("Иван Иванов", "Анна Петрова");
    }

    @Test
    void shouldGetVisitorById() {
        VisitorResponseDTO created = visitorService.create(visitorRequestDTO1);

        VisitorResponseDTO found = visitorService.getById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Иван Иванов");
        assertThat(found.getAge()).isEqualTo(25);
        assertThat(found.getSex()).isEqualTo(Sex.MALE.toString());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenVisitorNotExists() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> visitorService.getById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Посетитель с ID 999 не найден");
    }

    @Test
    void shouldUpdateVisitor() {
        VisitorResponseDTO created = visitorService.create(visitorRequestDTO1);

        VisitorRequestDTO updateRequest = new VisitorRequestDTO(
                "Иван Петров",
                26,
                Sex.MALE.toString()
        );

        VisitorResponseDTO updated = visitorService.update(created.getId(), updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("Иван Петров");
        assertThat(updated.getAge()).isEqualTo(26);
        assertThat(updated.getSex()).isEqualTo(Sex.MALE.toString());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentVisitor() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> visitorService.update(nonExistentId, visitorRequestDTO1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Посетитель с ID 999 не найден");
    }

    @Test
    void shouldDeleteVisitor() {
        VisitorResponseDTO created = visitorService.create(visitorRequestDTO1);

        visitorService.delete(created.getId());

        assertThatThrownBy(() -> visitorService.getById(created.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Посетитель с ID " + created.getId() + " не найден");
    }

    @Test
    void shouldReturnEmptyListWhenNoVisitorsExist() {
        List<VisitorResponseDTO> visitors = visitorService.getAll();

        assertThat(visitors).isEmpty();
    }

    @Test
    void shouldCreateMultipleVisitorsWithDifferentData() {
        VisitorRequestDTO visitor3 = new VisitorRequestDTO(
                "Петр Сидоров",
                35,
                Sex.MALE.toString()
        );

        VisitorResponseDTO created1 = visitorService.create(visitorRequestDTO1);
        VisitorResponseDTO created2 = visitorService.create(visitorRequestDTO2);
        VisitorResponseDTO created3 = visitorService.create(visitor3);

        List<VisitorResponseDTO> allVisitors = visitorService.getAll();
        assertThat(allVisitors).hasSize(3);
        assertThat(allVisitors)
                .extracting(VisitorResponseDTO::getName)
                .containsExactlyInAnyOrder("Иван Иванов", "Анна Петрова", "Петр Сидоров");

        assertThat(allVisitors)
                .extracting(VisitorResponseDTO::getAge)
                .containsExactlyInAnyOrder(25, 30, 35);
    }

    @Test
    void shouldMaintainDataIntegrityAfterUpdate() {
        VisitorResponseDTO created1 = visitorService.create(visitorRequestDTO1);
        VisitorResponseDTO created2 = visitorService.create(visitorRequestDTO2);

        VisitorRequestDTO updateRequest = new VisitorRequestDTO(
                "Обновленное Имя",
                40,
                Sex.FEMALE.toString()
        );

        VisitorResponseDTO updated = visitorService.update(created1.getId(), updateRequest);

        VisitorResponseDTO found1 = visitorService.getById(created1.getId());
        VisitorResponseDTO found2 = visitorService.getById(created2.getId());

        assertThat(found1.getName()).isEqualTo("Обновленное Имя");
        assertThat(found1.getAge()).isEqualTo(40);
        assertThat(found1.getSex()).isEqualTo(Sex.FEMALE.toString());

        assertThat(found2.getName()).isEqualTo("Анна Петрова");
        assertThat(found2.getAge()).isEqualTo(30);
        assertThat(found2.getSex()).isEqualTo(Sex.FEMALE.toString());
    }
}