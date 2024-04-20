package com.fuse.service;

import com.fuse.entity.Users;

public interface EmailService {
    void sendActivationEmail(Users user);
    void sendPasswordResetMail(Users user);
}
