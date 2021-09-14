package com.hepaestus.testappone.service.mapper;

import com.hepaestus.testappone.domain.Authority;
import com.hepaestus.testappone.domain.User;
import com.hepaestus.testappone.repository.UserRepository;
import com.hepaestus.testappone.service.MailService;
import com.hepaestus.testappone.service.ShoeService;
import com.hepaestus.testappone.service.UserService;
import com.hepaestus.testappone.service.dto.AdminUserDTO;
import com.hepaestus.testappone.service.dto.UserDTO;
import java.util.*;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

/**
 * Mapper for the entity {@link User} and its DTO called {@link UserDTO}.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as
 * MapStruct support is still in beta, and requires a manual step with an IDE.
 */
@Service
public class UserMapper {

    private final Logger log = LoggerFactory.getLogger(UserMapper.class);

    private final UserRepository userRepository;

    public UserMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> usersToUserDTOs(List<User> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).collect(Collectors.toList());
    }

    public UserDTO userToUserDTO(User user) {
        return new UserDTO(user);
    }

    public List<AdminUserDTO> usersToAdminUserDTOs(List<User> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToAdminUserDTO).collect(Collectors.toList());
    }

    public AdminUserDTO userToAdminUserDTO(User user) {
        return new AdminUserDTO(user);
    }

    public List<User> userDTOsToUsers(List<AdminUserDTO> userDTOs) {
        return userDTOs.stream().filter(Objects::nonNull).map(this::userDTOToUser).collect(Collectors.toList());
    }

    public User userDTOToUser(AdminUserDTO userDTO) {
        if (userDTO == null) {
            log.error("#### ERROR : Null User Passsed");
            return null;
        } else {
            User user = new User();
            user.setId(userDTO.getId());
            user.setLogin(userDTO.getLogin());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setImageUrl(userDTO.getImageUrl());
            user.setActivated(userDTO.isActivated());
            user.setLangKey(userDTO.getLangKey());
            Set<Authority> authorities = this.authoritiesFromStrings(userDTO.getAuthorities());
            user.setAuthorities(authorities);
            return user;
        }
    }

    private Set<Authority> authoritiesFromStrings(Set<String> authoritiesAsString) {
        Set<Authority> authorities = new HashSet<>();

        if (authoritiesAsString != null) {
            authorities =
                authoritiesAsString
                    .stream()
                    .map(
                        string -> {
                            Authority auth = new Authority();
                            auth.setName(string);
                            return auth;
                        }
                    )
                    .collect(Collectors.toSet());
        }

        return authorities;
    }

    public User userFromId(Long id) {
        if (id == null) {
            log.error("#### ERROR : Null User Passsed");
            return null;
        }
        Optional<User> userOpt = userRepository.findById(id);
        User user = userOpt.get();
        return user;
    }

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public UserDTO toDtoId(User user) throws Exception {
        try {
            if (user.getId() == null) {
                log.error("#### ERROR : Null User Passsed");
                throw new Exception("NULL USER PASSSED TO toDtoId() ");
            }
            Optional<User> userLookup = userRepository.findById(user.getId());
            UserDTO userDto = this.userToUserDTO(userLookup.get());
            return userDto;
        } catch (Exception e) {
            log.error("#### ERROR : " + e.getCause());
            return null;
        }
    }

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public Set<UserDTO> toDtoIdSet(Set<User> users) throws Exception {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<UserDTO> userSet = new HashSet<>();
        for (User userEntity : users) {
            userSet.add(this.toDtoId(userEntity));
        }

        return userSet;
    }

    @Named("login")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public UserDTO toDtoLogin(User user) {
        if (user == null) {
            log.error("#### ERROR : Null User Passsed");
            return null;
        }
        Optional<User> userLookup = userRepository.findOneByLogin(user.getLogin());
        User foundUser = userLookup.get();
        UserDTO userDTO = this.userToUserDTO(foundUser);
        // UserDTO userDto = new UserDTO();
        // userDto.setId(user.getId());
        // userDto.setLogin(user.getLogin()); // Added by Pete.
        return userDTO;
    }

    @Named("loginSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public Set<UserDTO> toDtoLoginSet(Set<User> users) {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<UserDTO> userSet = new HashSet<>();
        for (User userEntity : users) {
            userSet.add(this.toDtoLogin(userEntity));
        }

        return userSet;
    }
}
