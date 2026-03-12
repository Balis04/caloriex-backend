package com.example.caloryxbackend.customfood;

import com.example.caloryxbackend.entities.CustomFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomFoodRepository extends JpaRepository<CustomFood, UUID> {

    Optional<CustomFood> findByIdAndAuth0Id(UUID id, String auth0Id);

    List<CustomFood> findAllByAuth0Id(String auth0Id);

    @Query("SELECT cf FROM CustomFood cf WHERE cf.auth0Id <> :auth0Id")
    List<CustomFood> findAllNotOwnedBy(@Param("auth0Id") String auth0Id);
}
