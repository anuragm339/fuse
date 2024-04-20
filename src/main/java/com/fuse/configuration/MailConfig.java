package com.fuse.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    @Value("${app.email.host}")
    private String emailHost;
    @Value("${app.email.port}")
    private Integer emailPort;
    @Value("${app.email.username}")
    private String emailUsername;
    @Value("${app.email.password}")
    private String emailPassword;


    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort); // or the port your SMTP server is using
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // for debugging purposes, you can remove this in production

        return mailSender;
    }
}
