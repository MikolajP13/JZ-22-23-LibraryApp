package pl.edu.wszib.mpudelko.library.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDataValidator {
    private static final UserDataValidator udv = new UserDataValidator();
    private final String userDataPattern = "(\\p{L}+)|(\\p{L}+-\\p{L}+)";
    private final Pattern pattern = Pattern.compile(userDataPattern);
    private UserDataValidator() {
    }

    public boolean isValid(String userData){
        Matcher matcher = pattern.matcher(userData);
        return matcher.matches();
    }

    public static UserDataValidator getInstance(){
        return udv;
    }
}
