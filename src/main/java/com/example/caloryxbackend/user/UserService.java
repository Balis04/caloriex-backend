package com.example.caloryxbackend.user;

import com.example.caloryxbackend.common.exception.BadRequestException;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.common.security.AuthenticatedUser;
import com.example.caloryxbackend.common.security.AuthenticatedUserService;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.user.payload.request.RegisterRequest;
import com.example.caloryxbackend.user.payload.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() throws NotFoundException {
        User user = authenticatedUserService.getUser();

        return userMapper.toResponse(user);
    }

    @Transactional
    public void registerUser(RegisterRequest request) {
        AuthenticatedUser currentUser = authenticatedUserService.getAuthenticatedUser();

        if (userRepository.existsByAuth0id(currentUser.auth0Id())) {
            throw new BadRequestException("User already exists");
        }

        User user = new User();
        user.setAuth0id(currentUser.auth0Id());

        if (currentUser.email() != null && !currentUser.email().isBlank()) {
            user.setEmail(currentUser.email());
        }

        userMapper.updateForRegistration(request, user);

        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateUser(RegisterRequest request) throws NotFoundException{

        User user = authenticatedUserService.getUser();

        userMapper.updateFromRequest(request, user);

        userRepository.save(user);

        return userMapper.toResponse(user);
    }
}
