package com.example.blog.mapper;

import com.example.blog.domain.Author;
import com.example.blog.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    Author toAuthor(User user);
}
