package shopping.auth.dto.response;

public final class LoginResponse {

    private String accessToken;

    private LoginResponse() {
    }

    private LoginResponse(final String accessToken) {
        this.accessToken = accessToken;
    }

    public static LoginResponse from(final String accessToken) {
        return new LoginResponse(accessToken);
    }

    public String getAccessToken() {
        return accessToken;
    }
}
