package com.smartstay.smartstay.mapper;

import com.smartstay.smartstay.dto.response.UserResponse;
import com.smartstay.smartstay.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
