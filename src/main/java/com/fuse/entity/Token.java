package com.fuse.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "token")
public class Token implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq")
    @SequenceGenerator(name = "token_seq", sequenceName = "token_seq", allocationSize = 1)

    @Column(name = "id")
    private long id;
    @Column(name = "token")
    private String token;
    @Column(name = "user_id")
    private long userId;

    @Column(name = "reference_key")
    private String referenceKey;

    @Column(name = "refresh_expiry_date")
    private Date refreshExpiryDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "create_date")
    private Date createDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReferenceKey() {
        return referenceKey;
    }

    public void setReferenceKey(String referenceKey) {
        this.referenceKey = referenceKey;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getRefreshExpiryDate() {
        return refreshExpiryDate;
    }

    public void setRefreshExpiryDate(Date expiryDate) {
        this.refreshExpiryDate = expiryDate;
    }

    public Date getCreateExpiryDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


}


