package com.example.userservice.controller;
import com.example.userservice.dto.*;
import com.example.userservice.entity.*;
import com.example.userservice.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/users")
public class UserController{
 private final UserService users;private final TechnicianService technicians;
 public UserController(UserService users,TechnicianService technicians){this.users=users;this.technicians=technicians;}
 @PostMapping public Responses.UserView create(@Valid @RequestBody Requests.Register p){return Responses.user(users.register(p));}
 @GetMapping public Page<Responses.UserView> list(@RequestParam(required=false) RoleName role,@RequestParam(required=false) UserStatus status,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return users.list(role,status,PageRequest.of(page,size,Sort.by("id").descending())).map(Responses::user);}
 @GetMapping("/{id}") public Responses.UserView get(@PathVariable Long id){return Responses.user(users.get(id));}
 @PutMapping("/{id}") public Responses.UserView update(@PathVariable Long id,@Valid @RequestBody Requests.UpdateUser p){return Responses.user(users.update(id,p));}
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id){users.delete(id);}
 @PatchMapping("/{id}/deactivate") public Responses.UserView deactivate(@PathVariable Long id){return Responses.user(users.deactivate(id));}
 @PatchMapping("/{id}/reactivate") public Responses.UserView reactivate(@PathVariable Long id){return Responses.user(users.reactivate(id));}
 @PatchMapping("/{id}/password") public void password(@PathVariable Long id,@Valid @RequestBody Requests.ChangePassword p){users.changePassword(id,p);}
 @PostMapping("/authenticate") public Responses.UserView authenticate(@Valid @RequestBody Requests.Credentials p,HttpServletRequest req){return Responses.user(users.authenticate(p,req.getRemoteAddr()));}
 @GetMapping("/{id}/login-attempts") public Page<Responses.AttemptView> attempts(@PathVariable Long id,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return users.loginAttempts(id,PageRequest.of(page,size,Sort.by("attemptedAt").descending())).map(Responses::attempt);}
 @GetMapping("/{id}/technician-profile") public Responses.TechnicianView technician(@PathVariable Long id){return Responses.technician(technicians.get(id));}
 @PutMapping("/{id}/technician-profile") public Responses.TechnicianView updateTechnician(@PathVariable Long id,@Valid @RequestBody Requests.TechnicianPayload p){return Responses.technician(technicians.update(id,p));}
 @PatchMapping("/{id}/technician-profile/availability") public Responses.TechnicianView availability(@PathVariable Long id,@Valid @RequestBody Requests.AvailabilityPayload p){return Responses.technician(technicians.availability(id,p));}
}
