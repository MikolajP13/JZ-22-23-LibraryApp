package pl.edu.wszib.mpudelko.library.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {
    private final static PasswordValidator passwordValidator = new PasswordValidator();
    private final String passwordPattern = "^(?=.*\\d)(?=.*\\p{Upper})(?=.*\\p{Lower})(?=.*\\p{Punct}).{8,}$";
    private final Pattern pattern = Pattern.compile(passwordPattern);

    private PasswordValidator() {
    }

    public boolean isValid(String password){
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static PasswordValidator getInstance(){
        return passwordValidator;
    }
}
