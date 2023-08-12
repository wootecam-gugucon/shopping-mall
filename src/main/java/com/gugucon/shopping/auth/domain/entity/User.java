package com.gugucon.shopping.auth.domain.entity;

import com.gugucon.shopping.auth.domain.vo.Email;
import com.gugucon.shopping.auth.domain.vo.Password;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private Email email;
    @Embedded
    private Password password;

    protected User() {
    }

    public User(final Long id, final String email, final String password) {
        this.id = id;
        this.email = new Email(email);
        this.password = new Password(password);
    }

    public User(final String email, final String password) {
        this(null, email, password);
    }

    public Long getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }
}
