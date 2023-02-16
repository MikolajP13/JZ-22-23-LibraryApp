package pl.edu.wszib.mpudelko.library.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PasswordValidatorTest {

    static PasswordValidator passwordValidator;

    @BeforeAll
    public static void prepareAll(){
        passwordValidator = PasswordValidator.getInstance();
    }

    @Test
    public void correctPasswordTest(){ // at least 8 characters, at least: one special character, one digit, lower and uppercase character
        String actual = "Password123!@#";

        Assertions.assertTrue(passwordValidator.isValid(actual));
    }

    @Test
    public void lessThanEightCharactersTest(){
        String actual = "Pass1!";

        Assertions.assertFalse(passwordValidator.isValid(actual));
    }

    @Test
    public void passwordWithoutUppercaseCharacterTest(){
        String actual = "password123!@#";

        Assertions.assertFalse(passwordValidator.isValid(actual));
    }

    @Test
    public void passwordWithoutDigitCharacterTest(){
        String actual = "Password!@#";

        Assertions.assertFalse(passwordValidator.isValid(actual));
    }

    @Test
    public void passwordWithoutSpecialCharacterTest(){
        String actual = "Password123";

        Assertions.assertFalse(passwordValidator.isValid(actual));
    }

    @Test
    public void whitespacePasswordTest(){
        String actual = "\t\n\r\f ";

        Assertions.assertFalse(passwordValidator.isValid(actual));
    }

    @Test
    public void emptyPasswordTest(){
        String actual = "";

        Assertions.assertFalse(passwordValidator.isValid(actual));
    }
}
