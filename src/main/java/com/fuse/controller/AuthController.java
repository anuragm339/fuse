package com.fuse.controller;

import com.fuse.dto.JwtResponse;
import com.fuse.dto.LoginDto;
import com.fuse.entity.Users;
import com.fuse.security.JwtUtils;
import com.fuse.security.UserDetailServiceImpl;
import com.fuse.security.UserDetailsImpl;
import com.fuse.service.TokenService;
import com.fuse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserDetailServiceImpl securedDetailService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          TokenService tokenService, UserDetailServiceImpl securedDetailService,
                          PasswordEncoder encoder, JwtUtils jwtUtils){
        this.userService=userService;
        this.authenticationManager=authenticationManager;
        this.tokenService=tokenService;
        this.securedDetailService=securedDetailService;
        this.encoder=encoder;
        this.jwtUtils=jwtUtils;
    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody LoginDto loginRequest){
        Optional<Users> user = this.userService.getUserByUserName(loginRequest.getUsername());
        if (!user.isPresent() || !user.get().isActive()) {
            throw new UsernameNotFoundException("Unable to find any user, if you are newly registered. Check your email for verification token.");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        JwtResponse jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String tokenRefrence = this.tokenService.createToken(userDetails.getId(), jwt.getAccessToken(), jwt.getExpires());
        jwt.setAccessToken(tokenRefrence);

        return ResponseEntity.ok(jwt);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity refreshToken() {
        Authentication authetication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userdetail = (UserDetailsImpl) authetication.getPrincipal();
        JwtResponse jwt = jwtUtils.generateJwtToken(authetication);

        String newrefreenceKey = this.tokenService.updateToken(userdetail.getId(), jwt.getAccessToken(), jwt.getExpires());
        jwt.setAccessToken(newrefreenceKey);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/auth/sign-out")
    public void signoutToken() {
        Authentication authetication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userdetail = (UserDetailsImpl) authetication.getPrincipal();


        this.tokenService.clearToken(userdetail.getId());
    }


}
