package com.gugucon.shopping.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.gugucon.shopping.common.utils.JwtProvider;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.domain.vo.Password;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.response.LoginResponse;
import com.gugucon.shopping.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("로그인한다.")
    void login() {
        /* given */
        final Long memberId = 1L;
        final String userEmail = "test_email@woowafriends.com";
        final String userPassword = "test_password1!";
        final String accessToken = "test_access_token";

        when(passwordEncoder.encode(userPassword)).thenReturn(userPassword);
        when(passwordEncoder.matches(userPassword, userPassword)).thenReturn(true);

        final LoginRequest loginRequest = new LoginRequest(userEmail, userPassword);
        final Member member = Member.builder()
                .id(memberId)
                .email(Email.from(userEmail))
                .password(Password.of(userPassword, passwordEncoder))
                .build();

        when(memberRepository.findByEmail(Email.from(userEmail))).thenReturn(Optional.of(member));
        when(jwtProvider.generateToken(String.valueOf(memberId))).thenReturn(accessToken);

        /* when */
        LoginResponse loginResponse = memberService.login(loginRequest);

        /* then */
        assertThat(loginResponse.getAccessToken()).isEqualTo(accessToken);
    }

}
