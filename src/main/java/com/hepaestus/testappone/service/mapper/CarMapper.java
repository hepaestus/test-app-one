package com.hepaestus.testappone.service.mapper;

import com.hepaestus.testappone.domain.*;
import com.hepaestus.testappone.service.dto.CarDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Car} and its DTO {@link CarDTO}.
 */
@Mapper(componentModel = "spring", uses = { DriverMapper.class, PersonMapper.class })
public interface CarMapper extends EntityMapper<CarDTO, Car> {
    @Mapping(target = "driver", source = "driver", qualifiedByName = "id")
    @Mapping(target = "passengers", source = "passengers", qualifiedByName = "idSet")
    CarDTO toDto(Car s);

    @Mapping(target = "removePassengers", ignore = true)
    Car toEntity(CarDTO carDTO);
}
