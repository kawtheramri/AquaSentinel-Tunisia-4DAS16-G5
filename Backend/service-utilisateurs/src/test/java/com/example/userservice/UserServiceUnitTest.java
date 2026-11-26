package com.example.userservice;

import com.example.userservice.dto.Requests;
import com.example.userservice.entity.*;
import com.example.userservice.exception.ApiException;
import com.example.userservice.repository.*;
import com.example.userservice.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {
 @Test void registerHashesPasswordAndAssignsRole(){
  UserRepository users=mock(UserRepository.class);RoleService roles=mock(RoleService.class);LoginAttemptRepository attempts=mock(LoginAttemptRepository.class);
  Role role=new Role(RoleName.CONSUMER,"Consumer");when(roles.required(RoleName.CONSUMER)).thenReturn(role);when(users.existsByEmail(anyString())).thenReturn(false);when(users.save(any(User.class))).thenAnswer(i->i.getArgument(0));
  BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();UserService service=new UserService(users,roles,attempts,encoder);
  User result=service.register(new Requests.Register("Ada","Lovelace","ada@example.com",null,"securePass1",RoleName.CONSUMER,null,null));
  assertTrue(encoder.matches("securePass1",result.getPasswordHash()));assertEquals(RoleName.CONSUMER,result.getRole().getName());
 }
 @Test void locksAccountOnFifthFailedAttempt(){
  UserRepository users=mock(UserRepository.class);RoleService roles=mock(RoleService.class);LoginAttemptRepository attempts=mock(LoginAttemptRepository.class);BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
  User user=new User("A","B","a@example.com",null,encoder.encode("correctpass"),new Role(RoleName.CONSUMER,"Consumer"));
  when(users.findByEmail("a@example.com")).thenReturn(java.util.Optional.of(user));
  UserService service=new UserService(users,roles,attempts,encoder);
  for(int i=0;i<4;i++)assertThrows(ApiException.class,()->service.authenticate(new Requests.Credentials("a@example.com","wrongpass"),"127.0.0.1"));
  ApiException ex=assertThrows(ApiException.class,()->service.authenticate(new Requests.Credentials("a@example.com","wrongpass"),"127.0.0.1"));
  assertEquals(423,ex.getStatus().value());assertEquals(UserStatus.LOCKED,user.getStatus());assertEquals(5,user.getFailedLoginCount());
 }
}
