package com.fuse.controller;

import com.fuse.dto.ChangePasswordDto;
import com.fuse.dto.ResetPasswordRequest;
import com.fuse.dto.UserRequestDto;
import com.fuse.entity.Users;
import com.fuse.errors.InvalidPasswordException;
import com.fuse.errors.UserNotFoundException;
import com.fuse.service.EmailService;
import com.fuse.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api")
@RestController
public class UserController {
    private final UserService userService;

    private final EmailService emailService;

    public UserController(UserService userService,EmailService emailService){
        this.userService=userService;
        this.emailService=emailService;
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam(value = "key") String key) {
        Optional<Users> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new RuntimeException("No user was found for this activation key");
        }
        return "Successfully Activated";
    }

    @PostMapping("/auth/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@RequestBody UserRequestDto userRequestDto) {
        if (isPasswordLengthInvalid(userRequestDto.getPassword())) {
            throw new InvalidPasswordException();
        }
        Users user = userService.registerUser(userRequestDto, userRequestDto.getPassword());
        emailService.sendActivationEmail(user);
    }

    @PostMapping("/auth/forgot-password")
    @ResponseStatus(HttpStatus.CREATED)
    public void forgetPassword(@RequestBody String email) {
        Optional<Users> user = userService.forgetPassword(email);
        if(!user.isPresent()){
            throw new UserNotFoundException("No User found for the email "+email);
        }
        emailService.sendPasswordResetMail(user.get());
    }

    @PutMapping("/user/change-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Users changePassword(ChangePasswordDto changePasswordDto){
        Users users = userService.changePassword(changePasswordDto);
        return users;
    }
    private static boolean isPasswordLengthInvalid(String password) {
        return (
                StringUtils.isEmpty(password) ||
                        password.length() < UserRequestDto.PASSWORD_MIN_LENGTH ||
                        password.length() > UserRequestDto.PASSWORD_MAX_LENGTH
        );
    }
    @PostMapping("/account/reset-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Users resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return userService.resetPassword(resetPasswordRequest);
    }
}
