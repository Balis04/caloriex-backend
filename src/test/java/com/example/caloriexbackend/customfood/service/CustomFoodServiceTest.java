package com.example.caloriexbackend.customfood.service;

import com.example.caloriexbackend.common.exception.NotFoundException;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.customfood.mapper.CustomFoodMapper;
import com.example.caloriexbackend.customfood.payload.CustomFoodRequest;
import com.example.caloriexbackend.customfood.payload.CustomFoodResponse;
import com.example.caloriexbackend.customfood.repository.CustomFoodRepository;
import com.example.caloriexbackend.entities.CustomFood;
import com.example.caloriexbackend.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomFoodServiceTest {

    @Mock
    private CustomFoodRepository customFoodRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private CustomFoodMapper customFoodMapper;

    @InjectMocks
    private CustomFoodService customFoodService;

    @Test
    void createShouldMapSaveAndReturnResponse() {
        User user = user();
        CustomFoodRequest request = request();
        CustomFood entity = customFood();
        CustomFoodResponse response = response(entity.getId());

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(customFoodMapper.toEntity(request, user)).thenReturn(entity);
        when(customFoodRepository.save(entity)).thenReturn(entity);
        when(customFoodMapper.toResponse(entity)).thenReturn(response);

        CustomFoodResponse actual = customFoodService.create(request);

        assertSame(response, actual);
        verify(customFoodMapper).toEntity(request, user);
        verify(customFoodRepository).save(entity);
        verify(customFoodMapper).toResponse(entity);
    }

    @Test
    void updateShouldUpdateOwnedFoodAndReturnResponse() {
        User user = user();
        UUID foodId = UUID.randomUUID();
        CustomFoodRequest request = request();
        CustomFood entity = customFood();
        entity.setId(foodId);
        CustomFoodResponse response = response(foodId);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(customFoodRepository.findByIdAndUser(foodId, user)).thenReturn(Optional.of(entity));
        when(customFoodRepository.save(entity)).thenReturn(entity);
        when(customFoodMapper.toResponse(entity)).thenReturn(response);

        CustomFoodResponse actual = customFoodService.update(foodId, request);

        assertSame(response, actual);
        verify(customFoodMapper).updateEntity(entity, request, user);
        verify(customFoodRepository).save(entity);
    }

    @Test
    void updateShouldThrowWhenFoodIsNotOwnedByUser() {
        User user = user();
        UUID foodId = UUID.randomUUID();
        CustomFoodRequest request = request();

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(customFoodRepository.findByIdAndUser(foodId, user)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> customFoodService.update(foodId, request)
        );

        assertEquals("Custom food not found", exception.getMessage());
        verify(customFoodMapper, never()).updateEntity(any(), any(), any());
        verify(customFoodRepository, never()).save(any());
    }

    @Test
    void deleteShouldRemoveOwnedFood() {
        User user = user();
        UUID foodId = UUID.randomUUID();
        CustomFood entity = customFood();
        entity.setId(foodId);

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(customFoodRepository.findByIdAndUser(foodId, user)).thenReturn(Optional.of(entity));

        customFoodService.delete(foodId);

        verify(customFoodRepository).delete(entity);
    }

    @Test
    void deleteShouldThrowWhenFoodIsMissing() {
        User user = user();
        UUID foodId = UUID.randomUUID();

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(customFoodRepository.findByIdAndUser(foodId, user)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> customFoodService.delete(foodId)
        );

        assertEquals("Custom food not found", exception.getMessage());
        verify(customFoodRepository, never()).delete(any());
    }

    @Test
    void getAllShouldReturnMappedRepositoryResults() {
        List<CustomFood> entities = List.of(customFood(), customFood());
        List<CustomFoodResponse> responses = List.of(response(UUID.randomUUID()), response(UUID.randomUUID()));

        when(customFoodRepository.findAll()).thenReturn(entities);
        when(customFoodMapper.toResponseList(entities)).thenReturn(responses);

        List<CustomFoodResponse> actual = customFoodService.getAll();

        assertEquals(responses, actual);
    }

    @Test
    void getMineShouldReturnMappedOwnedFoods() {
        User user = user();
        List<CustomFood> entities = List.of(customFood());
        List<CustomFoodResponse> responses = List.of(response(UUID.randomUUID()));

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(customFoodRepository.findAllByUser(user)).thenReturn(entities);
        when(customFoodMapper.toResponseList(entities)).thenReturn(responses);

        List<CustomFoodResponse> actual = customFoodService.getMine();

        assertEquals(responses, actual);
        verify(customFoodRepository).findAllByUser(user);
    }

    @Test
    void getNotMineShouldReturnMappedFoodsOwnedByOthers() {
        User user = user();
        List<CustomFood> entities = List.of(customFood());
        List<CustomFoodResponse> responses = List.of(response(UUID.randomUUID()));

        when(authenticatedUserService.getUser()).thenReturn(user);
        when(customFoodRepository.findAllNotOwnedBy(user)).thenReturn(entities);
        when(customFoodMapper.toResponseList(entities)).thenReturn(responses);

        List<CustomFoodResponse> actual = customFoodService.getNotMine();

        assertEquals(responses, actual);
        verify(customFoodRepository).findAllNotOwnedBy(user);
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private CustomFood customFood() {
        CustomFood customFood = new CustomFood();
        customFood.setId(UUID.randomUUID());
        customFood.setName("Protein oats");
        customFood.setCalories(420.0);
        customFood.setProtein(25.0);
        customFood.setFat(10.0);
        customFood.setCarbohydrates(55.0);
        return customFood;
    }

    private CustomFoodRequest request() {
        CustomFoodRequest request = new CustomFoodRequest();
        request.setName("Protein oats");
        request.setCalories(420.0);
        request.setProtein(25.0);
        request.setFat(10.0);
        request.setCarbohydrates(55.0);
        return request;
    }

    private CustomFoodResponse response(UUID id) {
        return new CustomFoodResponse(id, "Protein oats", 420.0, 25.0, 10.0, 55.0);
    }
}
