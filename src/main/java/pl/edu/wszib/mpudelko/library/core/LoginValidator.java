package pl.edu.wszib.mpudelko.library.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginValidator {
    private final static LoginValidator loginValidator = new LoginValidator();
    private final String loginPattern = "^(\\p{Alpha})(\\p{Alnum}){5,14}$";
    private final Pattern pattern = Pattern.compile(loginPattern);

    private LoginValidator() {
    }

    public boolean isValid(String login){
        Matcher matcher = pattern.matcher(login);
        return matcher.matches();
    }

    public static LoginValidator getInstance(){
        return loginValidator;
    }
}
