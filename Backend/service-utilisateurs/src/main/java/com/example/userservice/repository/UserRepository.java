package com.example.userservice.repository;
import com.example.userservice.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface UserRepository extends JpaRepository<User,Long>{
 Optional<User> findByEmail(String email); boolean existsByEmail(String email); long countByRoleId(Long id);
 @Query("select u from User u join fetch u.role where (:role is null or u.role.name=:role) and (:status is null or u.status=:status)") Page<User> search(@Param("role") RoleName role,@Param("status") UserStatus status,Pageable pageable);
}
