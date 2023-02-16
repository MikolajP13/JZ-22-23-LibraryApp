package pl.edu.wszib.mpudelko.library.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UserDataValidatorTest {
    static UserDataValidator userDataValidator;

    @BeforeAll
    public static void prepareAll(){
        userDataValidator = UserDataValidator.getInstance();
    }

    @Test
    public void nonUnicodeAlphabetCharactersTest(){
        String actual = "qwerty";
        Assertions.assertTrue(userDataValidator.isValid(actual));
    }

    @Test
    public void unicodeAlphabetCharactersTest(){
        String actual1 = "áéíóúñü";
        String actual2 = "ąężźóńłć";
        String actual3 = "ÆæØøÅåß";

        Assertions.assertTrue(userDataValidator.isValid(actual1));
        Assertions.assertTrue(userDataValidator.isValid(actual2));
        Assertions.assertTrue(userDataValidator.isValid(actual3));
    }

    @Test
    public void unicodeAlphabetCharactersWithDashTest() {
        String actual1 = "áéíóúñü-áéíóúñü";
        String actual2 = "ąężźóńłć-ąężźóńłć";
        String actual3 = "ÆæØøÅåß-ÆæØøÅåß";

        Assertions.assertTrue(userDataValidator.isValid(actual1));
        Assertions.assertTrue(userDataValidator.isValid(actual2));
        Assertions.assertTrue(userDataValidator.isValid(actual3));
    }

    @Test
    public void nonUnicodeAndUnicodeAlphabetCharactersWithDigitsTest(){
        String actual1 = "qwerty123";
        String actual2 = "ąężźóńłć123";

        Assertions.assertFalse(userDataValidator.isValid(actual1));
        Assertions.assertFalse(userDataValidator.isValid(actual2));
    }

    @Test
    public void nonUnicodeAndUnicodeAlphabetCharactersWithPunctuationCharacterTest(){
        String actual1 = "qwerty!@#$%^&*()-+[]\\|';.";
        String actual2 = "ąężźóńłć!@#$%^&*()-+[]\\|';.";

        Assertions.assertFalse(userDataValidator.isValid(actual1));
        Assertions.assertFalse(userDataValidator.isValid(actual2));
    }

    @Test
    public void emptyInputTest(){
        String actual = "";
        Assertions.assertFalse(userDataValidator.isValid(actual));
    }

    @Test
    public void whitespaceCharacterTest(){
        String actual1 = " ";
        String actual2 = "\t";
        String actual3 = "\n";
        String actual4 = "\r";

        Assertions.assertFalse(userDataValidator.isValid(actual1));
        Assertions.assertFalse(userDataValidator.isValid(actual2));
        Assertions.assertFalse(userDataValidator.isValid(actual3));
        Assertions.assertFalse(userDataValidator.isValid(actual4));
    }
}
