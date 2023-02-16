package pl.edu.wszib.mpudelko.library.core;

import org.junit.jupiter.api.*;
import pl.edu.wszib.mpudelko.library.user.User;

public class AuthenticatorTest {
    static Authenticator authenticator;
    static User expected;
    User actual;
    User user;

    @BeforeAll
    public static void prepareAll(){
        authenticator = Authenticator.getInstance();
        expected = new User(1,"Admin", "admin", "admin", "106b84851d4e3c50e6c37cdcb625de42", User.UserRole.ADMIN);
    }

    @BeforeEach
    public void prepare(){
        actual = new User();
        user = new User();
    }

    @Test
    public void correctLoginAndPasswordAuthenticationTest(){
        user.setLogin("Admin");
        user.setPassword("admin");

        authenticator.authenticate(user);
        actual = authenticator.getLoggedUser();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void incorrectLoginAndPasswordAuthenticationTest(){
        user.setLogin("IncorrectLogin");
        user.setPassword("IncorrectPassword");

        authenticator.authenticate(user);
        actual = authenticator.getLoggedUser();

        Assertions.assertNotEquals(expected, actual);
        Assertions.assertNull(actual);
    }

    @Test
    public void correctLoginAndIncorrectPasswordAuthenticationTest() {
        user.setLogin("Admin");
        user.setPassword("IncorrectPassword");

        authenticator.authenticate(user);
        actual = authenticator.getLoggedUser();

        Assertions.assertNotEquals(expected, actual);
        Assertions.assertNull(actual);
    }

    @Test
    public void incorrectLoginAndCorrectPasswordAuthenticationTest(){
        user.setLogin("IncorrectLogin");
        user.setPassword("admin");

        authenticator.authenticate(user);
        actual = authenticator.getLoggedUser();

        Assertions.assertNotEquals(expected, actual);
        Assertions.assertNull(actual);
    }

}
