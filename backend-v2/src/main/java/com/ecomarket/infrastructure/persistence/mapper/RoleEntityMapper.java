package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Role;
import com.ecomarket.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre RoleEntity y Role
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleEntityMapper {
    
    Role toDomain(RoleEntity entity);
    
    RoleEntity toEntity(Role domain);
}
