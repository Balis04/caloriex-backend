package com.example.caloriexbackend.user.repository;

import com.example.caloriexbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByAuth0Id(String auth0Id);

    boolean existsByAuth0Id(String auth0Id);
}
