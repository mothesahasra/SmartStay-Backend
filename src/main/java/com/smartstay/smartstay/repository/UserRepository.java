package com.smartstay.smartstay.repository;

import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Page<User> findByRole(UserRole role, Pageable pageable);

    Optional<User> findByUsername(String username);
}