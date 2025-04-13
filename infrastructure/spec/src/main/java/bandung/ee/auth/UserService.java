package bandung.ee.auth;

public interface UserService<ID, U> {

    boolean authenticate(String username, String password);

    U loadUser(ID id);

    U loadUserByName(String username);
}
