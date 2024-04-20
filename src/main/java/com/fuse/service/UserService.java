package com.fuse.service;

import com.fuse.dto.ChangePasswordDto;
import com.fuse.dto.ResetPasswordRequest;
import com.fuse.dto.UserRequestDto;
import com.fuse.entity.Users;

import java.util.Optional;

public interface UserService {
    Optional<Users> getUserByUserName(String username);
    Optional<Users> activateRegistration(String key);
    Users registerUser(UserRequestDto userRequestDto, String password);
    Users changePassword(ChangePasswordDto changePasswordDto);
    Optional<Users> forgetPassword(String email);
    Users resetPassword(ResetPasswordRequest resetPasswordRequest);

}
