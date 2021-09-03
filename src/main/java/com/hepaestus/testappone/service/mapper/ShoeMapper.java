package com.hepaestus.testappone.service.mapper;

import com.hepaestus.testappone.domain.*;
import com.hepaestus.testappone.service.dto.ShoeDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shoe} and its DTO {@link ShoeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ShoeMapper extends EntityMapper<ShoeDTO, Shoe> {
    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<ShoeDTO> toDtoIdSet(Set<Shoe> shoe);
}
