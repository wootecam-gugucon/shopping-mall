package shopping.auth.dto.request;

public final class LoginRequest {

    private String email;
    private String password;

    private LoginRequest() {
    }

    public LoginRequest(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
