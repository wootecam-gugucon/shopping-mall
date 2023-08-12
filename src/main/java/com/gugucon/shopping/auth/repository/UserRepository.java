package com.gugucon.shopping.auth.repository;

import java.util.Optional;

import com.gugucon.shopping.auth.domain.vo.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gugucon.shopping.auth.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(Email email);
}
