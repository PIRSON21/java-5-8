package com.mediasoft.mapper;

import com.mediasoft.dto.VisitorRequestDTO;
import com.mediasoft.dto.VisitorResponseDTO;
import com.mediasoft.entity.Visitor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitorMapper {
//    @Mapping(source = "sex", target = "Sex")
    Visitor toVisitor(VisitorRequestDTO visitorRequestDTO);
    VisitorResponseDTO toVisitorResponseDTO(Visitor visitor);
}
