package com.example.userservice.dto;

import com.example.userservice.entity.*;
import java.time.LocalDateTime;
public final class Responses {
 private Responses(){}
 public record UserView(Long id,String firstName,String lastName,String email,String phone,UserStatus status,Integer failedLoginCount,LocalDateTime createdAt,RoleName role,TechnicianView technicianProfile){}
 public record RoleView(Long id,RoleName name,String description){}
 public record TechnicianView(Long id,String speciality,String serviceZone,Availability availability){}
 public record AttemptView(Long id,LocalDateTime attemptedAt,String ipAddress,boolean success){}
 public static UserView user(User u){return new UserView(u.getId(),u.getFirstName(),u.getLastName(),u.getEmail(),u.getPhone(),u.getStatus(),u.getFailedLoginCount(),u.getCreatedAt(),u.getRole().getName(),u.getTechnicianProfile()==null?null:technician(u.getTechnicianProfile()));}
 public static RoleView role(Role r){return new RoleView(r.getId(),r.getName(),r.getDescription());}
 public static TechnicianView technician(TechnicianProfile p){return new TechnicianView(p.getId(),p.getSpeciality(),p.getServiceZone(),p.getAvailability());}
 public static AttemptView attempt(LoginAttempt a){return new AttemptView(a.getId(),a.getAttemptedAt(),a.getIpAddress(),a.isSuccess());}
}
