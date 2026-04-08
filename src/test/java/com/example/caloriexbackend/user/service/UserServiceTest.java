package com.example.caloriexbackend.user.service;

import com.example.caloriexbackend.common.enums.ActivityLevel;
import com.example.caloriexbackend.common.enums.Gender;
import com.example.caloriexbackend.common.enums.GoalType;
import com.example.caloriexbackend.common.enums.UserRole;
import com.example.caloriexbackend.common.exception.BadRequestException;
import com.example.caloriexbackend.common.security.AuthenticatedUser;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.user.mapper.UserMapper;
import com.example.caloriexbackend.user.payload.request.UserRequest;
import com.example.caloriexbackend.user.payload.response.UserResponse;
import com.example.caloriexbackend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private UserService userService;

    @Test
    void getCurrentUserProfileShouldReturnMappedAuthenticatedUser() {
        User user = user();
        UserResponse response = response();

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse actual = userService.getCurrentUserProfile();

        assertSame(response, actual);
        verify(userMapper).toResponse(user);
    }

    @Test
    void createUserProfileShouldCreateNewUserWithAuthenticatedIdentity() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                "auth0|123",
                "user@example.com",
                true,
                "John Doe"
        );
        UserRequest request = request();

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.existsByAuth0Id("auth0|123")).thenReturn(false);

        userService.createUserProfile(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateForRegistration(any(), captor.capture());
        verify(userRepository).save(captor.getValue());
        assertEquals("auth0|123", captor.getValue().getAuth0Id());
        assertEquals("user@example.com", captor.getValue().getEmail());
    }

    @Test
    void createUserProfileShouldLeaveEmailNullWhenAuthenticatedEmailIsBlank() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                "auth0|123",
                " ",
                true,
                "John Doe"
        );
        UserRequest request = request();

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.existsByAuth0Id("auth0|123")).thenReturn(false);

        userService.createUserProfile(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateForRegistration(any(), captor.capture());
        assertNull(captor.getValue().getEmail());
    }

    @Test
    void createUserProfileShouldThrowWhenUserAlreadyExists() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                "auth0|123",
                "user@example.com",
                true,
                "John Doe"
        );

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.existsByAuth0Id("auth0|123")).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> userService.createUserProfile(request())
        );

        assertEquals("User already exists", exception.getMessage());
        verify(userMapper, never()).updateForRegistration(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserShouldMutatePersistAndReturnMappedResponse() {
        User user = user();
        UserRequest request = request();
        UserResponse response = response();

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse actual = userService.updateUser(request);

        assertSame(response, actual);
        verify(userMapper).updateFromRequest(request, user);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    private User user() {
        User user = new User();
        user.setFullName("John Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setGender(Gender.MALE);
        user.setRole(UserRole.USER);
        user.setHeightCm(180);
        user.setStartWeightKg(90.0);
        user.setActualWeightKg(88.0);
        user.setActivityLevel(ActivityLevel.MODERATE);
        user.setGoal(GoalType.CUT);
        user.setTargetWeightKg(80.0);
        user.setWeeklyGoalKg(0.5);
        return user;
    }

    private UserRequest request() {
        UserRequest request = new UserRequest();
        request.setFullName("John Doe");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setGender(Gender.MALE);
        request.setRole(UserRole.USER);
        request.setHeightCm(180);
        request.setStartWeightKg(90.0);
        request.setActualWeightKg(88.0);
        request.setActivityLevel(ActivityLevel.MODERATE);
        request.setGoal(GoalType.CUT);
        request.setTargetWeightKg(80.0);
        request.setWeeklyGoalKg(0.5);
        return request;
    }

    private UserResponse response() {
        return UserResponse.builder()
                .fullName("John Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .role(UserRole.USER)
                .heightCm(180)
                .startWeightKg(90.0)
                .actualWeightKg(88.0)
                .targetWeightKg(80.0)
                .weeklyGoalKg(0.5)
                .activityLevel(ActivityLevel.MODERATE)
                .goal(GoalType.CUT)
                .build();
    }
}
