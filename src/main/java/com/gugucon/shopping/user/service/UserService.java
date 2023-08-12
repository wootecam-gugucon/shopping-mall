package com.gugucon.shopping.user.service;

import com.gugucon.shopping.user.domain.vo.Email;
import com.gugucon.shopping.user.dto.request.LoginRequest;
import com.gugucon.shopping.user.dto.response.LoginResponse;
import com.gugucon.shopping.common.exception.ErrorCode;
import com.gugucon.shopping.common.exception.ShoppingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gugucon.shopping.user.domain.entity.User;
import com.gugucon.shopping.user.repository.UserRepository;
import com.gugucon.shopping.user.utils.JwtProvider;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public UserService(final UserRepository userRepository, final JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    public LoginResponse login(final LoginRequest loginRequest) {
        User user = userRepository.findByEmail(new Email(loginRequest.getEmail()))
            .orElseThrow(() -> new ShoppingException(ErrorCode.EMAIL_NOT_REGISTERED));
        validatePassword(loginRequest, user);

        final String accessToken = jwtProvider.generateToken(String.valueOf(user.getId()));
        return LoginResponse.from(accessToken);
    }

    private void validatePassword(final LoginRequest loginRequest, final User user) {
        if (!user.getPassword().hasValue(loginRequest.getPassword())) {
            throw new ShoppingException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
    }
}
