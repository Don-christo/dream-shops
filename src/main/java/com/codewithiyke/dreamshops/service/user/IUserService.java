package com.codewithiyke.dreamshops.service.user;

import com.codewithiyke.dreamshops.dto.UserDto;
import com.codewithiyke.dreamshops.model.User;
import com.codewithiyke.dreamshops.request.CreateUserRequest;
import com.codewithiyke.dreamshops.request.UserUpdateRequest;

public interface IUserService {
  User getUserById(Long userId);

  User createUser(CreateUserRequest request);

  User updateUser(UserUpdateRequest request, Long userId);

  void deleteUser(Long userId);

  UserDto convertUserToDto(User user);

  User getAuthenticatedUser();
}
