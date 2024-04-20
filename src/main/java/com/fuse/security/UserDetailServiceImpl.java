package com.fuse.security;

import com.fuse.entity.Users;
import com.fuse.errors.UserNotFoundException;
import com.fuse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = this.userService.getUserByUserName(username);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User Not Found with username: " + username);
        }
        return UserDetailsImpl.build(user.get());
    }

}

