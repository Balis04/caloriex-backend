package com.example.caloriexbackend.user.controller;

import com.example.caloriexbackend.user.service.UserService;
import com.example.caloriexbackend.user.payload.request.UserRequest;
import com.example.caloriexbackend.user.payload.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User profiles", description = "Endpoints for users to manage their profiles")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(
            summary = "Get current user profile",
            description = "Returns the profile of the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user profile",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User profile not found",
                    content = @Content
            )
    })
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @Operation(
            summary = "Create user profile",
            description = "Creates a user profile for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User profile created successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body </br>" +
                            "User already exists",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            )
    })
    @PostMapping("/profile")
    public ResponseEntity<Void> createProfile(@RequestBody @Valid UserRequest request) {
        userService.createUserProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Update current user profile",
            description = "Updates the profile of the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User profile not found",
                    content = @Content
            )
    })
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }
}
