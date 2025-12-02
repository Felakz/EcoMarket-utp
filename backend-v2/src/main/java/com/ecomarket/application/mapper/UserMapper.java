package com.ecomarket.application.mapper;

import com.ecomarket.application.dto.response.UserResponse;
import com.ecomarket.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper para User utilizando MapStruct
 * MapStruct genera automáticamente la implementación en tiempo de compilación
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})
public interface UserMapper {
    
    UserResponse toResponse(User user);
}
