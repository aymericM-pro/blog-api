package com.example.blog.service.impl;

import com.example.blog.domain.User;
import com.example.blog.dto.UsersDtos.UserResponse;
import com.example.blog.dto.UsersDtos.UserUpdateRequest;
import com.example.blog.enums.UserError;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.UserMapper;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    // READ ALL
    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(mapper::toResponse);
    }

    // READ ONE
    @Override
    public UserResponse findById(String id) {
        return mapper.toResponse(findUserById(id));
    }

    // UPDATE
    @Override
    public UserResponse update(String id, UserUpdateRequest request, String currentUserEmail) {
        User user = findUserById(id);
        mapper.updateUserFromDto(request, user);
        return mapper.toResponse(userRepository.save(user));
    }

    // DELETE
    @Override
    public void delete(String id, String currentUserEmail) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    // ─── HELPERS ─────────────────────────────────────────

    private User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(UserError.USER_NOT_FOUND));
    }

}
