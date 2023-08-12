package com.gugucon.shopping.member.service;

import com.gugucon.shopping.member.domain.vo.Email;
import com.gugucon.shopping.member.dto.request.LoginRequest;
import com.gugucon.shopping.member.dto.response.LoginResponse;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gugucon.shopping.member.domain.entity.Member;
import com.gugucon.shopping.member.repository.MemberRepository;
import com.gugucon.shopping.member.utils.JwtProvider;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberService(final MemberRepository memberRepository, final JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    public LoginResponse login(final LoginRequest loginRequest) {
        Member member = memberRepository.findByEmail(new Email(loginRequest.getEmail()))
            .orElseThrow(() -> new ShoppingException(ErrorCode.EMAIL_NOT_REGISTERED));
        validatePassword(loginRequest, member);

        final String accessToken = jwtProvider.generateToken(String.valueOf(member.getId()));
        return LoginResponse.from(accessToken);
    }

    private void validatePassword(final LoginRequest loginRequest, final Member member) {
        if (!member.getPassword().hasValue(loginRequest.getPassword())) {
            throw new ShoppingException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
    }
}
