package com.platform.booking.recording.AuthService.UserServiceTests;

import com.platform.booking.recording.AuthService.dtos.PageUserDTO;
import com.platform.booking.recording.AuthService.dtos.UserForGetRequestDTO;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.repositories.jpa.UserRepository;
import com.platform.booking.recording.AuthService.services.UserService;
import com.platform.booking.recording.AuthService.util.Mapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllUsersTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("findAllUsers: Successfully maps page content to DTOs and sets metadata")
    void findAllUsers_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("user2@example.com");

        UserForGetRequestDTO getDto1 = new UserForGetRequestDTO();
        getDto1.setEmail("user1@example.com");

        UserForGetRequestDTO getDto2 = new UserForGetRequestDTO();
        getDto2.setEmail("user2@example.com");

        List<User> userList = List.of(user1, user2);
        Page<User> userPage = new PageImpl<>(userList, pageable, 2);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(mapper.userToGetDTO(user1)).thenReturn(getDto1);
        when(mapper.userToGetDTO(user2)).thenReturn(getDto2);

        // Act
        PageUserDTO result = userService.findAllUsers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getDtos().size());
        assertEquals("user1@example.com", result.getDtos().get(0).getEmail());
        assertEquals("user2@example.com", result.getDtos().get(1).getEmail());

        verify(userRepository, times(1)).findAll(pageable);
        verify(mapper, times(1)).userToGetDTO(user1);
        verify(mapper, times(1)).userToGetDTO(user2);
    }

    @Test
    @DisplayName("findAllUsers: Returns empty DTO list when repository returns empty page")
    void findAllUsers_EmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = Page.empty(pageable);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        PageUserDTO result = userService.findAllUsers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getDtos().isEmpty());

        verify(userRepository, times(1)).findAll(pageable);
        verify(mapper, never()).userToGetDTO(any());
    }
}