package com.example.blog.mapper;

import com.example.blog.domain.Author;
import com.example.blog.dto.ArticleDtos;
import com.example.blog.dto.AuthDtos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.blog.dto.AuthorsDtos.AuthorRequest;
import com.example.blog.dto.AuthorsDtos.AuthorResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    // Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Author toEntity(AuthorRequest request);

    AuthorResponse toResponse(Author author);

    List<AuthorResponse> toResponseList(List<Author> authors);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "role", source = "role")
    void updateAuthorFromDto(AuthorRequest request, @MappingTarget Author author);

}