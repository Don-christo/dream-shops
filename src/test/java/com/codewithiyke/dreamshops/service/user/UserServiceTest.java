package com.codewithiyke.dreamshops.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codewithiyke.dreamshops.dto.UserDto;
import com.codewithiyke.dreamshops.exceptions.AlreadyExistsException;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.User;
import com.codewithiyke.dreamshops.repository.UserRepository;
import com.codewithiyke.dreamshops.request.CreateUserRequest;
import com.codewithiyke.dreamshops.request.UserUpdateRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks private UserService userService;

    @Mock private UserRepository userRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encodedPass");
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));

        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_ShouldSaveUser_WhenEmailNotExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("jane@example.com");
        request.setPassword("12345");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createUser(request);

        assertNotNull(createdUser);
        assertEquals("jane@example.com", createdUser.getEmail());
        assertEquals("encodedPassword", createdUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("jane@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUserExists() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUser(request, 1L);

        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("User", updatedUser.getLastName());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Updated");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(request, 1L));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void convertUserToDto_ShouldMapUserToUserDto() {
        UserDto dto = new UserDto();
        dto.setEmail("john@example.com");

        when(modelMapper.map(testUser, UserDto.class)).thenReturn(dto);

        UserDto result = userService.convertUserToDto(testUser);

        assertEquals("john@example.com", result.getEmail());
        verify(modelMapper).map(testUser, UserDto.class);
    }

    @Test
    void getAuthenticatedUser_ShouldReturnUserFromSecurityContext() {
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(auth.getName()).thenReturn("john@example.com");
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);

        User result = userService.getAuthenticatedUser();

        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).findByEmail("john@example.com");
    }
}

