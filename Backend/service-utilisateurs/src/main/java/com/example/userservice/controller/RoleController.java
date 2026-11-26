package com.example.userservice.controller;
import com.example.userservice.dto.*;
import com.example.userservice.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/roles")
public class RoleController{
 private final RoleService roles;public RoleController(RoleService roles){this.roles=roles;}
 @GetMapping public List<Responses.RoleView> all(){return roles.all().stream().map(Responses::role).toList();}
 @GetMapping("/{id}") public Responses.RoleView get(@PathVariable Long id){return Responses.role(roles.get(id));}
 @PostMapping public Responses.RoleView create(@Valid @RequestBody Requests.RolePayload p){return Responses.role(roles.create(p));}
 @PutMapping("/{id}") public Responses.RoleView update(@PathVariable Long id,@Valid @RequestBody Requests.RolePayload p){return Responses.role(roles.update(id,p));}
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id){roles.delete(id);}
}
