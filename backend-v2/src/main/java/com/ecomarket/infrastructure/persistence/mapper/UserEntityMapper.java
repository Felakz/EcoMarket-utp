package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.User;
import com.ecomarket.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre UserEntity (JPA) y User (Dominio)
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleEntityMapper.class})
public interface UserEntityMapper {
    
    User toDomain(UserEntity entity);
    
    UserEntity toEntity(User domain);
}
