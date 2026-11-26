package com.example.userservice.entity;

import jakarta.persistence.*;
@Entity @Table(name="technician_profile")
public class TechnicianProfile {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(length=120) private String speciality;
 @Column(length=160) private String serviceZone;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Availability availability=Availability.AVAILABLE;
 @OneToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false,unique=true) private User user;
 protected TechnicianProfile(){}
 public TechnicianProfile(String speciality,String serviceZone){this.speciality=speciality;this.serviceZone=serviceZone;}
 public Long getId(){return id;} public String getSpeciality(){return speciality;} public String getServiceZone(){return serviceZone;} public Availability getAvailability(){return availability;}
 public void setUser(User user){this.user=user;} public void update(String s,String z){speciality=s;serviceZone=z;} public void setAvailability(Availability a){availability=a;}
}
