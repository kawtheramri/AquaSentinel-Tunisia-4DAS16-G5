package com.example.userservice.service;
import com.example.userservice.dto.Requests;
import com.example.userservice.entity.*;
import com.example.userservice.exception.ApiException;
import com.example.userservice.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @Transactional
public class RoleService{
 private final RoleRepository roles;private final UserRepository users;
 public RoleService(RoleRepository roles,UserRepository users){this.roles=roles;this.users=users;}
 public List<Role> all(){return roles.findAll();} public Role get(Long id){return roles.findById(id).orElseThrow(()->ApiException.notFound("Rôle"));}
 public Role create(Requests.RolePayload p){if(roles.findByName(p.name()).isPresent())throw ApiException.conflict("Ce rôle existe déjà");return roles.save(new Role(p.name(),p.description()));}
 public Role update(Long id,Requests.RolePayload p){Role r=get(id);roles.findByName(p.name()).filter(other->!other.getId().equals(id)).ifPresent(other->{throw ApiException.conflict("Ce rôle existe déjà");});r.setName(p.name());r.setDescription(p.description());return r;}
 public void delete(Long id){Role r=get(id);if(users.countByRoleId(id)>0)throw ApiException.conflict("Le rôle est utilisé par des utilisateurs");roles.delete(r);}
 public Role required(RoleName name){return roles.findByName(name).orElseThrow(()->new ApiException(HttpStatus.CONFLICT,"Rôle non initialisé: "+name));}
}
