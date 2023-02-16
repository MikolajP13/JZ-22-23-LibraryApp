package pl.edu.wszib.mpudelko.library.core;

import org.apache.commons.codec.digest.DigestUtils;
import pl.edu.wszib.mpudelko.library.database.UserDAO;
import pl.edu.wszib.mpudelko.library.user.User;

import java.util.Optional;

public class Authenticator {
    private final String seed = "Tmu9&qHE3D4QR@jS*MA!RaY*0Q8gErA7DOvSbia7uJ!fzU%j5W";
    private final static Authenticator authenticator = new Authenticator();
    private final UserDAO userDAO = UserDAO.getInstance();
    private User loggedUser = null;

    private Authenticator() {
    }

    public void authenticate(User user){
        Optional<User> userToAuthenticate = this.userDAO.findByLogin(user.getLogin());
        if(userToAuthenticate.isPresent() && userToAuthenticate.get().getPassword()
                .equals(DigestUtils.md5Hex(user.getPassword() + this.getSeed()))){
            this.loggedUser = userToAuthenticate.get();
        }else{
            this.loggedUser = null;
        }
    }

    public User getLoggedUser(){
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser){
        this.loggedUser = loggedUser;
    }

    public String getSeed(){
        return seed;
    }

    public static Authenticator getInstance(){
        return authenticator;
    }
}
