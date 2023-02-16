package pl.edu.wszib.mpudelko.library.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LoginValidatorTest {

    static LoginValidator loginValidator;

    @BeforeAll
    public static void prepareAll(){
        loginValidator = LoginValidator.getInstance();
    }


    @Test
    public void correctLoginTest() {  // 6-15 long, no special characters and cannot start with a digit
        String actual = "Admin12345";

        Assertions.assertTrue(loginValidator.isValid(actual));
    }

    @Test
    public void lessThanSixCharactersTest(){
        String actual = "asdfg"; // 5

        Assertions.assertFalse(loginValidator.isValid(actual));
    }

    @Test
    public void moreThanFifteenCharactersTest(){
        String actual = "qwertyuiopasdfgh"; // 16

        Assertions.assertFalse(loginValidator.isValid(actual));
    }


    @Test
    public void startWithDigitTest(){
        String actual = "1admin";

        Assertions.assertFalse(loginValidator.isValid(actual));
    }

    @Test
    public void containsSpecialCharacterTest(){
        String actual = "admin12**";

        Assertions.assertFalse(loginValidator.isValid(actual));
    }

    @Test
    public void emptyLoginTest(){
        String actual = "";

        Assertions.assertFalse(loginValidator.isValid(actual));
    }
}
