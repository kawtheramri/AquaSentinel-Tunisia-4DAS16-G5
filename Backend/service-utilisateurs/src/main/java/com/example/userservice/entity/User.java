package com.example.userservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="users", indexes=@Index(name="idx_users_email",columnList="email",unique=true))
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=100) private String firstName;
 @Column(nullable=false,length=100) private String lastName;
 @Column(nullable=false,unique=true,length=255) private String email;
 @Column(length=40) private String phone;
 @Column(nullable=false) private String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private UserStatus status=UserStatus.ACTIVE;
 @Column(nullable=false) private Integer failedLoginCount=0;
 @Column(nullable=false,updatable=false) private LocalDateTime createdAt;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="role_id",nullable=false) private Role role;
 @OneToOne(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY) private TechnicianProfile technicianProfile;
 @OneToMany(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true) @OrderBy("attemptedAt DESC") private List<LoginAttempt> loginAttempts=new ArrayList<>();
 protected User() {}
 @PrePersist void onCreate(){if(createdAt==null)createdAt=LocalDateTime.now();if(status==null)status=UserStatus.ACTIVE;if(failedLoginCount==null)failedLoginCount=0;}
 public User(String firstName,String lastName,String email,String phone,String passwordHash,Role role){this.firstName=firstName;this.lastName=lastName;this.email=email;this.phone=phone;this.passwordHash=passwordHash;this.role=role;}
 public void addAttempt(LoginAttempt a){loginAttempts.add(a);a.setUser(this);}
 public void setTechnicianProfile(TechnicianProfile p){this.technicianProfile=p;if(p!=null)p.setUser(this);}
 public Long getId(){return id;} public String getFirstName(){return firstName;} public String getLastName(){return lastName;} public String getEmail(){return email;} public String getPhone(){return phone;} public String getPasswordHash(){return passwordHash;} public UserStatus getStatus(){return status;} public Integer getFailedLoginCount(){return failedLoginCount;} public LocalDateTime getCreatedAt(){return createdAt;} public Role getRole(){return role;} public TechnicianProfile getTechnicianProfile(){return technicianProfile;} public List<LoginAttempt> getLoginAttempts(){return loginAttempts;}
 public void update(String firstName,String lastName,String phone,Role role){this.firstName=firstName;this.lastName=lastName;this.phone=phone;this.role=role;}
 public void recordFailedLogin(){failedLoginCount++;if(failedLoginCount>=5)status=UserStatus.LOCKED;}
 public void recordSuccessfulLogin(){failedLoginCount=0;}
 public void changePasswordHash(String hash){passwordHash=hash;}
 public void deactivate(){status=UserStatus.INACTIVE;} public void reactivate(){status=UserStatus.ACTIVE;failedLoginCount=0;}
}
