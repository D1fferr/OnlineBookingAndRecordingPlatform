package com.platform.booking.recording.AuthService.repositories.redis;

import com.platform.booking.recording.AuthService.models.ResetPassword;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepository extends CrudRepository<ResetPassword, String> {




}
