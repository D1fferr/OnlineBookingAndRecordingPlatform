package com.platform.booking.recording.AuthService.repositories.jpa;

import com.platform.booking.recording.AuthService.models.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(@Email String email);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(CAST(u.id AS string)) LIKE LOWER(:search) OR " +
            "LOWER(u.email) LIKE LOWER(:search)")
    Page<User> findUsers(@Param("search") String search, Pageable pageable);


}
