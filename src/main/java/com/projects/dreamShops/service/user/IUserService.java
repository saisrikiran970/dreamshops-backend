package com.projects.dreamShops.service.user;

import com.projects.dreamShops.dto.UserDto;
import com.projects.dreamShops.model.User;
import com.projects.dreamShops.requests.CreateUserRequest;
import com.projects.dreamShops.requests.UpdateUserRequest;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UpdateUserRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();
}
