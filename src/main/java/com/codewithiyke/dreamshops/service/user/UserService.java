package com.codewithiyke.dreamshops.service.user;

import com.codewithiyke.dreamshops.exceptions.AlreadyExistsException;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.User;
import com.codewithiyke.dreamshops.repository.UserRepository;
import com.codewithiyke.dreamshops.request.CreateUserRequest;
import com.codewithiyke.dreamshops.request.UserUpdateRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
  private final UserRepository userRepository;

  @Override
  public User getUserById(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
  }

  @Override
  public User createUser(CreateUserRequest request) {
    return Optional.of(request)
        .filter(user -> !userRepository.existsByEmail(request.getEmail()))
        .map(
            req -> {
              User user = new User();
              user.setFirstName(request.getFirstName());
              user.setLastName(request.getLastName());
              user.setEmail(request.getEmail());
              user.setPassword(request.getPassword());
              return userRepository.save(user);
            })
        .orElseThrow(
            () -> new AlreadyExistsException("Oops! " + request.getEmail() + " already exists!"));
  }

  @Override
  public User updateUser(UserUpdateRequest request, Long userId) {
    return userRepository
        .findById(userId)
        .map(
            existingUser -> {
              existingUser.setFirstName(request.getFirstName());
              existingUser.setLastName(request.getLastName());
              return userRepository.save(existingUser);
            })
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  @Override
  public void deleteUser(Long userId) {
    userRepository
        .findById(userId)
        .ifPresentOrElse(
            userRepository::delete,
            () -> {
              throw new ResourceNotFoundException("User not found!");
            });
  }
}
