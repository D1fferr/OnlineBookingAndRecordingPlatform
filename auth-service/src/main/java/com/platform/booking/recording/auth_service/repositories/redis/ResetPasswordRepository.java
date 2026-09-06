package com.platform.booking.recording.auth_service.repositories.redis;

import com.platform.booking.recording.auth_service.models.ResetPassword;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepository extends CrudRepository<ResetPassword, String> {




}
