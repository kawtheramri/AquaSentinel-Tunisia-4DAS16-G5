package com.example.userservice.repository;
import com.example.userservice.entity.LoginAttempt;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt,Long>{Page<LoginAttempt> findByUserId(Long userId,Pageable pageable);}
