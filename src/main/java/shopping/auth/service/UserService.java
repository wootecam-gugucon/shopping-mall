package shopping.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.domain.entity.User;
import shopping.auth.domain.vo.Email;
import shopping.auth.dto.request.LoginRequest;
import shopping.auth.dto.response.LoginResponse;
import shopping.auth.repository.UserRepository;
import shopping.auth.utils.JwtProvider;
import shopping.common.exception.ErrorCode;
import shopping.common.exception.ShoppingException;

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
