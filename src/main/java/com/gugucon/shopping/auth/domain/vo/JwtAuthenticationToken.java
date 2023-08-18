package com.gugucon.shopping.auth.domain.vo;

import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String jwtToken;

    private Object principal;

    private Object credentials;

    public JwtAuthenticationToken(final String jwtToken) {
        super(null);
        this.jwtToken = jwtToken;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(final Object principal,
                                  final Object credentials,
                                  final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final JwtAuthenticationToken that = (JwtAuthenticationToken) o;
        return Objects.equals(jwtToken, that.jwtToken) && Objects.equals(principal, that.principal)
            && Objects.equals(credentials, that.credentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), jwtToken, principal, credentials);
    }

    @Override
    public String toString() {
        return "JwtAuthenticationToken{" +
            "jwtToken='" + jwtToken + '\'' +
            ", principal=" + principal +
            ", credentials=" + credentials +
            '}';
    }
}
