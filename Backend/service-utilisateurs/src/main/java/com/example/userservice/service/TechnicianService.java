package com.example.userservice.service;
import com.example.userservice.dto.Requests;
import com.example.userservice.entity.*;
import com.example.userservice.exception.ApiException;
import com.example.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @Transactional
public class TechnicianService{
 private final UserRepository users;public TechnicianService(UserRepository users){this.users=users;}
 private User technician(Long id){User u=users.findById(id).orElseThrow(()->ApiException.notFound("Utilisateur"));if(u.getRole().getName()!=RoleName.TECHNICIAN||u.getTechnicianProfile()==null)throw new ApiException(HttpStatus.BAD_REQUEST,"L'utilisateur n'a pas le rôle TECHNICIAN");return u;}
 public TechnicianProfile get(Long id){return technician(id).getTechnicianProfile();}
 public TechnicianProfile update(Long id,Requests.TechnicianPayload p){TechnicianProfile t=technician(id).getTechnicianProfile();t.update(p.speciality(),p.serviceZone());return t;}
 public TechnicianProfile availability(Long id,Requests.AvailabilityPayload p){TechnicianProfile t=technician(id).getTechnicianProfile();t.setAvailability(p.availability());return t;}
}
