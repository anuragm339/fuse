package com.fuse.service.impl;

import com.fuse.dto.ChangePasswordDto;
import com.fuse.dto.ResetPasswordRequest;
import com.fuse.dto.UserRequestDto;
import com.fuse.entity.Users;
import com.fuse.enums.RoleEnums;
import com.fuse.errors.EmailAlreadyUsedException;
import com.fuse.errors.UserNotFoundException;
import com.fuse.errors.UsernameAlreadyUsedException;
import com.fuse.repository.UserRepository;
import com.fuse.service.UserService;
import com.fuse.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired @Lazy
    private PasswordEncoder passwordEncoder;
    @Override
    public Optional<Users> getUserByUserName(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public Optional<Users> activateRegistration(String key) {
        Optional<Users> byKey = userRepository.findByKey(key);
        if(byKey.isPresent()){
            Users users = byKey.get();
            users.setActive(true);
            userRepository.save(users);
        }
        return byKey;
    }

    @Override
    public Users registerUser(UserRequestDto userRequestDto, String password) {
        userRepository.findByEmail(userRequestDto.getEmail().toLowerCase())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new UsernameAlreadyUsedException();
                    }
                });
        userRepository
                .findByEmail(userRequestDto.getEmail())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new EmailAlreadyUsedException();
                    }
                });
        Users newUser = new Users();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setEmail(userRequestDto.getEmail().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setActive(false);
        newUser.setKey(RandomUtils.getRandomUuid());
        newUser.setRole(RoleEnums.USER.name());
        return userRepository.save(newUser);
    }

    @Override
    public Users changePassword(ChangePasswordDto changePasswordDto) {
        Optional<Users> byEmail = userRepository.findByEmail(changePasswordDto.getUsername());
        if(!byEmail.isPresent()){
            throw new UserNotFoundException("There is no user with username "+changePasswordDto.getUsername());
        }
        String updatedPassword = passwordEncoder.encode(changePasswordDto.getUpdatedPassword());
        Users users = byEmail.get();
        users.setPassword(updatedPassword);
        return userRepository.save(users);
    }

    private boolean removeNonActivatedUser(Users existingUser) {
        if (existingUser.isActive()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

    @Override
    public Optional<Users> forgetPassword(String email) {
        Optional<Users> users = userRepository.findByEmail(email)
                .filter(Users::isActive)
                .map(user -> {
                    user.setResetKey(RandomUtils.getRandomUuid());
                    user.setResetDate(LocalDateTime.now());
                    user.setResetTimes(user.getResetTimes()+1);
                    return user;
                });
        if(!users.isPresent()){
            throw new UserNotFoundException("There is no user with username "+email+" Or Reset Limit has been reached");
        }
        Users save = userRepository.save(users.get());
        return Optional.of(save);
    }

    @Override
    public Users resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String resetKey = resetPasswordRequest.getResetKey();
        String password = resetPasswordRequest.getPassword();
        Optional<Users> byResetKey = userRepository.findByResetKey(resetKey);
        if(!byResetKey.isPresent() || byResetKey.get().isResetThreshold()){
            throw new UserNotFoundException("No user found for reset key");
        }
        Users users = byResetKey.get();
        String encodedPassword = passwordEncoder.encode(password);
        users.setPassword(encodedPassword);
        return userRepository.save(users);
    }
}
