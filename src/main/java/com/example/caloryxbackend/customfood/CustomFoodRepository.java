package com.example.caloryxbackend.customfood;

import com.example.caloryxbackend.entities.CustomFood;
import com.example.caloryxbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomFoodRepository extends JpaRepository<CustomFood, UUID> {

    Optional<CustomFood> findByIdAndUser(UUID id, User user);

    List<CustomFood> findAllByUser(User user);

    @Query("SELECT cf FROM CustomFood cf WHERE cf.user <> :user")
    List<CustomFood> findAllNotOwnedBy(@Param("user") User user);
}
