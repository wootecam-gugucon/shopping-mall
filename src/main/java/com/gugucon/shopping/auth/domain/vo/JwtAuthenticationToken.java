package com.gugucon.shopping.auth.domain.vo;

import com.gugucon.shopping.auth.dto.MemberPrincipal;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String jwtToken;

    private MemberPrincipal principal;

    private Object credentials;

    public JwtAuthenticationToken(final String jwtToken) {
        super(null);
        this.jwtToken = jwtToken;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(final MemberPrincipal principal,
                                  final Object credentials,
                                  final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }
}
