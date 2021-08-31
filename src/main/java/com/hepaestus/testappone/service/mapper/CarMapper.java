package com.hepaestus.testappone.service.mapper;

import com.hepaestus.testappone.domain.*;
import com.hepaestus.testappone.service.dto.CarDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Car} and its DTO {@link CarDTO}.
 */
@Mapper(componentModel = "spring", uses = { DriverMapper.class })
public interface CarMapper extends EntityMapper<CarDTO, Car> {
    @Mapping(target = "driver", source = "driver", qualifiedByName = "id")
    CarDTO toDto(Car s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CarDTO toDtoId(Car car);
}
