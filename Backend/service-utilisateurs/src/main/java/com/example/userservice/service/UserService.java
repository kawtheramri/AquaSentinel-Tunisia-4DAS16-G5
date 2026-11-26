package com.example.userservice.service;
import com.example.userservice.dto.Requests;
import com.example.userservice.entity.*;
import com.example.userservice.exception.ApiException;
import com.example.userservice.repository.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UserService{
 private final UserRepository users;private final RoleService roles;private final LoginAttemptRepository attempts;private final PasswordEncoder encoder;
 public UserService(UserRepository users,RoleService roles,LoginAttemptRepository attempts,PasswordEncoder encoder){this.users=users;this.roles=roles;this.attempts=attempts;this.encoder=encoder;}
 @Transactional public User register(Requests.Register p){String email=p.email().toLowerCase(java.util.Locale.ROOT);if(users.existsByEmail(email))throw ApiException.conflict("Cette adresse e-mail est déjà utilisée");Role role=roles.required(p.role());User u=users.save(new User(p.firstName(),p.lastName(),email,p.phone(),encoder.encode(p.password()),role));if(role.getName()==RoleName.TECHNICIAN)u.setTechnicianProfile(new TechnicianProfile(p.speciality(),p.serviceZone()));return u;}
 @Transactional(readOnly=true) public Page<User> list(RoleName role,UserStatus status,Pageable pageable){return users.search(role,status,pageable);}
 @Transactional(readOnly=true) public User get(Long id){return users.findById(id).orElseThrow(()->ApiException.notFound("Utilisateur"));}
 @Transactional public User update(Long id,Requests.UpdateUser p){User u=get(id);Role role=roles.required(p.role());if(role.getName()==RoleName.TECHNICIAN&&u.getTechnicianProfile()==null)u.setTechnicianProfile(new TechnicianProfile(null,null));if(role.getName()!=RoleName.TECHNICIAN)u.setTechnicianProfile(null);u.update(p.firstName(),p.lastName(),p.phone(),role);return u;}
 @Transactional public void delete(Long id){users.delete(get(id));}
 @Transactional public User deactivate(Long id){User u=get(id);u.deactivate();return u;}
 @Transactional public User reactivate(Long id){User u=get(id);u.reactivate();return u;}
 @Transactional public void changePassword(Long id,Requests.ChangePassword p){User u=get(id);if(!encoder.matches(p.oldPassword(),u.getPasswordHash()))throw new ApiException(HttpStatus.UNAUTHORIZED,"Ancien mot de passe incorrect");if(p.newPassword().length()<8)throw new ApiException(HttpStatus.BAD_REQUEST,"Le nouveau mot de passe doit contenir au moins 8 caractères");u.changePasswordHash(encoder.encode(p.newPassword()));}
 @Transactional(noRollbackFor=ApiException.class) public User authenticate(Requests.Credentials p,String ip){User u=users.findByEmail(p.email().toLowerCase()).orElse(null);if(u==null)throw new ApiException(HttpStatus.UNAUTHORIZED,"E-mail ou mot de passe incorrect");if(u.getStatus()==UserStatus.INACTIVE){u.addAttempt(new LoginAttempt(ip,false));throw new ApiException(HttpStatus.UNAUTHORIZED,"Compte inactif");}if(u.getStatus()==UserStatus.LOCKED){u.addAttempt(new LoginAttempt(ip,false));throw new ApiException(HttpStatus.LOCKED,"Compte verrouillé");}boolean ok=encoder.matches(p.password(),u.getPasswordHash());u.addAttempt(new LoginAttempt(ip,ok));if(ok){u.recordSuccessfulLogin();return u;}u.recordFailedLogin();if(u.getStatus()==UserStatus.LOCKED)throw new ApiException(HttpStatus.LOCKED,"Compte verrouillé après 5 échecs");throw new ApiException(HttpStatus.UNAUTHORIZED,"E-mail ou mot de passe incorrect");}
 @Transactional(readOnly=true) public Page<LoginAttempt> loginAttempts(Long id,Pageable pageable){get(id);return attempts.findByUserId(id,pageable);}
}
