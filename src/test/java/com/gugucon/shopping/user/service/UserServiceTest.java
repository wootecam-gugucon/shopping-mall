package com.gugucon.shopping.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.gugucon.shopping.user.domain.entity.User;
import com.gugucon.shopping.user.domain.vo.Email;
import com.gugucon.shopping.user.dto.request.LoginRequest;
import com.gugucon.shopping.user.dto.response.LoginResponse;
import com.gugucon.shopping.user.repository.UserRepository;
import com.gugucon.shopping.user.utils.JwtProvider;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("로그인에 성공한다.")
    void login() {
        /* given */
        final Long userId = 1L;
        final String userEmail = "test_email@woowafriends.com";
        final String userPassword = "test_password1!";
        final String accessToken = "test_access_token";

        final LoginRequest loginRequest = new LoginRequest(userEmail, userPassword);
        final User user = new User(userId, userEmail, userPassword);

        when(userRepository.findByEmail(new Email(userEmail))).thenReturn(Optional.of(user));
        when(jwtProvider.generateToken(String.valueOf(userId))).thenReturn(accessToken);

        /* when */
        LoginResponse loginResponse = userService.login(loginRequest);

        /* then */
        assertThat(loginResponse.getAccessToken()).isEqualTo(accessToken);
    }

}
