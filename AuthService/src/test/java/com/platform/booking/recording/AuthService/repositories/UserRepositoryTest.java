package com.platform.booking.recording.AuthService.repositories;


import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.repositories.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends AbstractBaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = createUser("exampleadmin@example.com", "$2a$10$hashedpassword1", "ROLE_ADMIN");
        user2 = createUser("client@example.com", "$2a$10$hashedpassword2", "ROLE_CLIENT");

        entityManager.persist(user1);
        entityManager.persist(user2);

        entityManager.flush();
        entityManager.clear();
    }

    private User createUser(String email, String password, String role) {
        User user = new User();
        // ID is auto-generated via @UuidGenerator(style = TIME)
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setAvatarURL("https://example.com/avatar.png");
        user.setIsBlocked(false);
        user.setBlockReason(null);
        user.setCreatedAt(OffsetDateTime.now());
        return user;
    }

    @Test
    @DisplayName("findByEmail: Retrieves user successfully when email exists")
    void findByEmail_ReturnsUser() {
        // Act
        Optional<User> result = userRepository.findByEmail("exampleadmin@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user1.getId());
        assertThat(result.get().getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("findByEmail: Returns empty Optional when email does not exist")
    void findByEmail_ReturnsEmpty_WhenNotFound() {
        // Act
        Optional<User> result = userRepository.findByEmail("unknown@example.com");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findUsers: Filters users by partial email match")
    void findUsers_FiltersByEmail() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<User> result = userRepository.findUsers("%exampleadmin%", pageable);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting(User::getId)
                .containsExactly(user1.getId());
    }

    @Test
    @DisplayName("findUsers: Filters users by UUID string match")
    void findUsers_FiltersByUUID() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String exactUuid = user2.getId().toString();

        // Act
        Page<User> result = userRepository.findUsers("%" + exactUuid + "%", pageable);

        // Assert
        assertThat(result.getContent())
                .extracting(User::getId)
                .containsExactly(user2.getId());
    }
}
