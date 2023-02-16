package pl.edu.wszib.mpudelko.library.core;

import pl.edu.wszib.mpudelko.library.book.Book;
import pl.edu.wszib.mpudelko.library.database.BookDAO;
import pl.edu.wszib.mpudelko.library.database.UserDAO;
import pl.edu.wszib.mpudelko.library.gui.GUI;
import pl.edu.wszib.mpudelko.library.user.User;

import java.util.Optional;


public class Core {
    private static final  Core core = new Core();
    private final GUI gui = GUI.getInstance();
    private final Authenticator authenticator = Authenticator.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();
    private final BookDAO bookDAO = BookDAO.getInstance();

    private Core() {
    }
    public void start() {
        boolean isRunning = true;
        DisplayStatus currentStatus = DisplayStatus.START_PAGE;

        while(isRunning){
            if(currentStatus == DisplayStatus.START_PAGE){
                currentStatus = displayStartPage();
            }
            if(currentStatus == DisplayStatus.MENU_PAGE){
                currentStatus = displayMenuPage();
            }
        }
    }

    private DisplayStatus displayStartPage(){
        switch (this.gui.showStartPage()){
            case "1" -> {
                if(signIn())
                    return DisplayStatus.MENU_PAGE;
            }
            case "2" -> {
                boolean result = signUp();
                this.gui.showSignUpResult(result);
                if (result && signIn())
                    return DisplayStatus.MENU_PAGE;
            }
            case "3" -> System.exit(0);
            default -> System.out.println("Option not supported! Sign in or sign up to get an access.");
        }
        return DisplayStatus.START_PAGE;
    }

    private DisplayStatus displayMenuPage(){
        boolean isAdmin = this.authenticator.getLoggedUser() != null
                && this.authenticator.getLoggedUser().getRole() == User.UserRole.ADMIN;

        switch (this.gui.showAppMenu()){
            case "1" -> this.gui.readDataForLoaningBook();
            case "2" -> this.gui.showAllBooks();
            case "3" -> this.gui.showAllAvailableBooks();
            case "4" -> this.gui.readDataForFindingBook();
            case "5" -> {
                return signOut();
            }
            case "6" -> {
                if(isAdmin) {
                    Optional<Book> book = this.gui.readDataForAddingBook();
                    if (book.isPresent()) {
                        bookDAO.addBook(book.get());
                        System.out.println("Book has been added successfully.");
                    } else
                        System.out.println("Invalid author, title or ISBN!");
                }
            }
            case "7" -> {
                if(isAdmin)
                    this.gui.showHistoryOrCurrentOfAllLoanedBooks();
            }
            case "8" -> {
                if(isAdmin)
                    this.gui.showAllLoanedBooksWithDeadlineExceeded();
            }
            default -> System.out.println("Option not supported!");
        }
        return DisplayStatus.MENU_PAGE;
    }
    private boolean signIn() {
        int counter = 3;
        while (counter != 0) {
            this.authenticator.authenticate(this.gui.readLoginAndPassword());
            if (authenticator.getLoggedUser() != null) {
                System.out.println("Welcome " + authenticator.getLoggedUser().getLogin() + "!");
                return true;
            }
            System.out.println("Incorrect login or password! " + (counter - 1) + " tries left.");
            counter--;
        }
        return false;
    }
    private boolean signUp() {
        User newUser = this.gui.readDataForNewUser();

        if (this.userDAO.findByLogin(newUser.getLogin()).isEmpty()) {
            this.userDAO.addUserToDatabase(newUser);
            return true;
        } else {
            return false;
        }
    }
    private DisplayStatus signOut() {
        this.authenticator.setLoggedUser(null);
        return DisplayStatus.START_PAGE;
    }
    public static Core getInstance(){
        return core;
    }
    public enum DisplayStatus{
        START_PAGE,
        MENU_PAGE
    }
}
