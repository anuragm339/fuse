package com.fuse.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class UserRequestDto implements Serializable {
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 16;
    private String email;
    private String password;
    private String name;
    private String company;
    private Boolean agreements;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Boolean getAgreements() {
        return agreements;
    }

    public void setAgreements(Boolean agreements) {
        this.agreements = agreements;
    }
}
