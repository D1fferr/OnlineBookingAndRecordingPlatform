package com.platform.booking.recording.AuthService.repositories.redis;


import com.platform.booking.recording.AuthService.models.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {
}
