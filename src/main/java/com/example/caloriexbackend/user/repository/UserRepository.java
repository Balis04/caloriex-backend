package com.example.caloriexbackend.user.repository;

import com.example.caloriexbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByAuth0id(String auth0id);

    boolean existsByAuth0id(String auth0id);
}
