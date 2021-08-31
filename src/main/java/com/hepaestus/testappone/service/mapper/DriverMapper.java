package com.hepaestus.testappone.service.mapper;

import com.hepaestus.testappone.domain.*;
import com.hepaestus.testappone.service.dto.DriverDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Driver} and its DTO {@link DriverDTO}.
 */
@Mapper(componentModel = "spring", uses = { PersonMapper.class })
public interface DriverMapper extends EntityMapper<DriverDTO, Driver> {
    @Mapping(target = "person", source = "person", qualifiedByName = "id")
    DriverDTO toDto(Driver s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DriverDTO toDtoId(Driver driver);
}