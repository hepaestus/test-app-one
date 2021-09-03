package com.hepaestus.testappone.service.mapper;

import com.hepaestus.testappone.domain.*;
import com.hepaestus.testappone.service.dto.PersonDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Person} and its DTO {@link PersonDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, ShoeMapper.class })
public interface PersonMapper extends EntityMapper<PersonDTO, Person> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    @Mapping(target = "shoes", source = "shoes", qualifiedByName = "idSet")
    PersonDTO toDto(Person s);

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<PersonDTO> toDtoIdSet(Set<Person> person);

    @Mapping(target = "removeShoe", ignore = true)
    Person toEntity(PersonDTO personDTO);

    @Named("name")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PersonDTO toDtoName(Person person);
}
