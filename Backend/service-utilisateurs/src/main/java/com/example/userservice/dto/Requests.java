package com.example.userservice.dto;

import com.example.userservice.entity.*;
import jakarta.validation.constraints.*;

public final class Requests {
 private Requests(){}
 public record Register(@NotBlank @Size(max=100) String firstName,@NotBlank @Size(max=100) String lastName,@NotBlank @Email @Size(max=255) String email,@Size(max=40) String phone,@NotBlank @Size(min=8,max=100) String password,@NotNull RoleName role,@Size(max=120) String speciality,@Size(max=160) String serviceZone){}
 public record UpdateUser(@NotBlank @Size(max=100) String firstName,@NotBlank @Size(max=100) String lastName,@Size(max=40) String phone,@NotNull RoleName role){}
 public record Credentials(@NotBlank @Email String email,@NotBlank String password){}
 public record ChangePassword(@NotBlank String oldPassword,@NotBlank @Size(min=8,max=100) String newPassword){}
 public record RolePayload(@NotNull RoleName name,@Size(max=255) String description){}
 public record TechnicianPayload(@Size(max=120) String speciality,@Size(max=160) String serviceZone){}
 public record AvailabilityPayload(@NotNull Availability availability){}
}
