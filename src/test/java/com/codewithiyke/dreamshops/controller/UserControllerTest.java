package com.codewithiyke.dreamshops.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codewithiyke.dreamshops.dto.UserDto;
import com.codewithiyke.dreamshops.exceptions.AlreadyExistsException;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.User;
import com.codewithiyke.dreamshops.request.CreateUserRequest;
import com.codewithiyke.dreamshops.request.UserUpdateRequest;
import com.codewithiyke.dreamshops.security.jwt.JwtUtils;
import com.codewithiyke.dreamshops.security.user.ShopUserDetailsService;
import com.codewithiyke.dreamshops.service.user.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private IUserService userService;
  @MockitoBean private JwtUtils jwtUtils;
  @MockitoBean private ShopUserDetailsService userDetailsService;

  private User testUser;
  private UserDto testUserDto;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setFirstName("John");
    testUser.setLastName("Doe");
    testUser.setEmail("john@example.com");
    testUser.setPassword("encodedPass");

    testUserDto = new UserDto();
    testUserDto.setId(1L);
    testUserDto.setFirstName("John");
    testUserDto.setLastName("Doe");
    testUserDto.setEmail("john@example.com");
  }

  @Test
  void getUserId_ShouldReturnUser_WhenUserExists() throws Exception {
    when(userService.getUserById(1L)).thenReturn(testUser);
    when(userService.convertUserToDto(testUser)).thenReturn(testUserDto);

    mockMvc
        .perform(get("/api/v1/users/{userId}/user", 1L))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Success"))
        .andExpect(jsonPath("$.data.email").value("john@example.com"));

    verify(userService).getUserById(1L);
    verify(userService).convertUserToDto(testUser);
  }

  @Test
  void getUserId_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
    when(userService.getUserById(1L)).thenThrow(new ResourceNotFoundException("User not found!"));

    mockMvc
        .perform(get("/api/v1/users/{userId}/user", 1L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found!"));

    verify(userService).getUserById(1L);
  }

  @Test
  void createUser_ShouldReturnUser_WhenSuccess() throws Exception {
    CreateUserRequest request = new CreateUserRequest();
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setEmail("john@example.com");
    request.setPassword("12345");

    when(userService.createUser(any(CreateUserRequest.class))).thenReturn(testUser);
    when(userService.convertUserToDto(testUser)).thenReturn(testUserDto);

    mockMvc
        .perform(
            post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Create User Success!"))
        .andExpect(jsonPath("$.data.email").value("john@example.com"));

    verify(userService).createUser(any(CreateUserRequest.class));
    verify(userService).convertUserToDto(testUser);
  }

  @Test
  void createUser_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
    CreateUserRequest request = new CreateUserRequest();
    request.setEmail("john@example.com");

    when(userService.createUser(any(CreateUserRequest.class)))
        .thenThrow(new AlreadyExistsException("Oops! john@example.com already exists!"));

    mockMvc
        .perform(
            post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Oops! john@example.com already exists!"));

    verify(userService).createUser(any(CreateUserRequest.class));
  }

  @Test
  void updateUser_ShouldReturnUpdatedUser_WhenUserExists() throws Exception {
    UserUpdateRequest request = new UserUpdateRequest();
    request.setFirstName("Updated");
    request.setLastName("User");

    testUser.setFirstName("Updated");
    testUser.setLastName("User");
    testUserDto.setFirstName("Updated");
    testUserDto.setLastName("User");

    when(userService.updateUser(any(UserUpdateRequest.class), eq(1L))).thenReturn(testUser);
    when(userService.convertUserToDto(testUser)).thenReturn(testUserDto);

    mockMvc
        .perform(
            put("/api/v1/users/{userId}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Update User Success!"))
        .andExpect(jsonPath("$.data.firstName").value("Updated"));

    verify(userService).updateUser(any(UserUpdateRequest.class), eq(1L));
    verify(userService).convertUserToDto(testUser);
  }

  @Test
  void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
    UserUpdateRequest request = new UserUpdateRequest();
    request.setFirstName("Updated");

    when(userService.updateUser(any(UserUpdateRequest.class), eq(1L)))
        .thenThrow(new ResourceNotFoundException("User not found"));

    mockMvc
        .perform(
            put("/api/v1/users/{userId}/update", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found"));

    verify(userService).updateUser(any(UserUpdateRequest.class), eq(1L));
  }

  @Test
  void deleteUser_ShouldReturnSuccess_WhenUserExists() throws Exception {
    doNothing().when(userService).deleteUser(1L);

    mockMvc
        .perform(delete("/api/v1/users/{userId}/delete", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Delete User Success!"));

    verify(userService).deleteUser(1L);
  }

  @Test
  void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
    doThrow(new ResourceNotFoundException("User not found!")).when(userService).deleteUser(1L);

    mockMvc
        .perform(delete("/api/v1/users/{userId}/delete", 1L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("User not found!"));

    verify(userService).deleteUser(1L);
  }
}
