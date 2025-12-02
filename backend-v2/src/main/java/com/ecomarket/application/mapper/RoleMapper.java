package com.ecomarket.application.mapper;

import com.ecomarket.application.dto.response.RoleResponse;
import com.ecomarket.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper para Role
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    
    RoleResponse toResponse(Role role);
}
