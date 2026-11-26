package com.example.userservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="login_attempt",indexes=@Index(name="idx_login_attempt_user_time",columnList="user_id,attempted_at"))
public class LoginAttempt {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private LocalDateTime attemptedAt;
 @Column(length=64) private String ipAddress;
 @Column(nullable=false) private boolean success;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private User user;
 protected LoginAttempt(){}
 public LoginAttempt(String ipAddress,boolean success){this.attemptedAt=LocalDateTime.now();this.ipAddress=ipAddress;this.success=success;}
 public void setUser(User user){this.user=user;} public Long getId(){return id;} public LocalDateTime getAttemptedAt(){return attemptedAt;} public String getIpAddress(){return ipAddress;} public boolean isSuccess(){return success;}
}
