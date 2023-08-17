package com.gugucon.shopping.member.service;

import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import com.gugucon.shopping.common.utils.JwtProvider;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.domain.vo.Password;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.request.SignupRequest;
import com.gugucon.shopping.member.dto.response.LoginResponse;
import com.gugucon.shopping.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(final LoginRequest loginRequest) {
        Member member = memberRepository.findByEmail(Email.from(loginRequest.getEmail()))
            .orElseThrow(() -> new ShoppingException(ErrorCode.EMAIL_NOT_REGISTERED));
        validatePassword(loginRequest, member);

        final String accessToken = jwtProvider.generateToken(String.valueOf(member.getId()));
        return LoginResponse.from(accessToken);
    }

    private void validatePassword(final LoginRequest loginRequest, final Member member) {
        if (!member.matchPassword(loginRequest.getPassword(), passwordEncoder)) {
            throw new ShoppingException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
    }

    public void signup(final SignupRequest signupRequest) {
        validateEmailNotExist(signupRequest.getEmail());
        validatePasswordChecked(signupRequest.getPassword(), signupRequest.getPasswordCheck());

        Password password = Password.of(signupRequest.getPassword(), passwordEncoder);
        Member member = Member.builder()
                              .email(signupRequest.getEmail())
                              .password(password)
                              .nickname(signupRequest.getNickname())
                              .build();
        memberRepository.save(member);
    }

    private void validateEmailNotExist(final String email) {
         if (memberRepository.findByEmail(Email.from(email)).isPresent()) {
             throw new ShoppingException(ErrorCode.EMAIL_ALREADY_EXIST);
         }
    }

    private void validatePasswordChecked(final String password, final String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new ShoppingException(ErrorCode.PASSWORD_CHECK_NOT_SAME);
        }
    }
}
