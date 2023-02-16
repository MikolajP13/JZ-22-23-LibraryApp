package pl.edu.wszib.mpudelko.library.gui;

import org.apache.commons.codec.digest.DigestUtils;
import pl.edu.wszib.mpudelko.library.book.Book;
import pl.edu.wszib.mpudelko.library.core.Authenticator;
import pl.edu.wszib.mpudelko.library.core.LoginValidator;
import pl.edu.wszib.mpudelko.library.core.PasswordValidator;
import pl.edu.wszib.mpudelko.library.core.UserDataValidator;
import pl.edu.wszib.mpudelko.library.database.BookDAO;
import pl.edu.wszib.mpudelko.library.database.UserDAO;
import pl.edu.wszib.mpudelko.library.user.User;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class GUI {
    private final static Scanner scanner = new Scanner(System.in);
    private final static GUI gui = new GUI();
    private final Authenticator authenticator = Authenticator.getInstance();
    private final BookDAO bookDAO = BookDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();
    private final LoginValidator loginValidator = LoginValidator.getInstance();
    private final PasswordValidator passwordValidator = PasswordValidator.getInstance();
    private final UserDataValidator userDataValidator = UserDataValidator.getInstance();
    private final String format1 = "%-50s %-40s %-13s %-10s";
    private final String[] formatElements1 = new String[]{"Title", "Author", "ISBN", "Loanable"};
    private final String format2 = "%-25s %-25s %-50s %-40s %-25s %-25s";
    private final String[] formatElements2 = new String[]{"First name", "Last name", "Title", "Author", "Start of book loan date", "End of book loan date"};

    private final String format3 = "%-50s %-40s %-13s %-10s %-25s %-25s";
    private final String[] formatElements3 = new String[]{"Title", "Author", "ISBN", "Loanable", "Start of book loan date", "End of book loan date"};
    private GUI() {
    }

    public String showStartPage(){
        System.out.println("1. Sign in");
        System.out.println("2. Sign up");
        System.out.println("3. Quit");

        return scanner.nextLine();
    }

    public String showAppMenu(){

        System.out.println("1. Loan book");
        System.out.println("2. List all books");
        System.out.println("3. List all available books");
        System.out.println("4. Find book");
        System.out.println("5. Sign out");
        if(this.authenticator.getLoggedUser() != null
            && this.authenticator.getLoggedUser().getRole() == User.UserRole.ADMIN){
            System.out.println("6. Add book");
            System.out.println("7. List all loaned books");
            System.out.println("8. List all books with the deadline exceeded");
        }

        return scanner.nextLine();
    }

    public User readLoginAndPassword() {
        User user = new User();
        System.out.print("Login: ");
        user.setLogin(scanner.nextLine());
        System.out.print("Password: ");
        user.setPassword(scanner.nextLine());
        return user;
    }

    public User readDataForNewUser() {
        User newUser = new User();
        System.out.print("Enter you login: ");
        String login = scanner.nextLine();
        while (!this.loginValidator.isValid(login) || this.userDAO.findByLogin(login).isPresent()) {
            System.out.println("User name exists or login is incorrect!" +
                    "\n(info: login must be 6-15 long, no special characters and cannot start with a digit)");
            System.out.print("Enter you login: ");
            login = scanner.nextLine();
        }
        newUser.setLogin(login);

        System.out.print("Enter you password: ");
        String password = scanner.nextLine();
        while(!this.passwordValidator.isValid(password)) {
            System.out.println("(info: password must contain a length of at least 8 characters " +
                    "and at least: one special character, one digit, lower and uppercase character)");
            System.out.print("Enter you password: ");
            password = scanner.nextLine();
        }

        System.out.print("Enter your first name: ");
        String userFirstName = scanner.nextLine();
        while (!userDataValidator.isValid(userFirstName)){
            System.out.println("(info: first name cannot contain special characters or numbers)");
            System.out.print("Enter your first name: ");
            userFirstName = scanner.nextLine().trim();
        }

        System.out.print("Enter your last name: ");
        String userLastName = scanner.nextLine();
        while (!userDataValidator.isValid(userLastName)){
            System.out.println("(info: last name cannot contain special characters or numbers)");
            System.out.print("Enter your last name: ");
            userLastName = scanner.nextLine().trim();
        }

        newUser.setFirstName(userFirstName);
        newUser.setLastName(userLastName);
        newUser.setPassword(DigestUtils.md5Hex(password + authenticator.getSeed()));
        newUser.setRole(User.UserRole.USER);

        return newUser;
    }
    public void showSignUpResult(boolean result) {
        if (result)
            System.out.println("Registration was successful. Now log in to your account.");
        else
            System.out.println("User with this login already exists!");
    }
    public void readDataForLoaningBook(){
        System.out.print("Input book ISBN to loan: ");
        if(this.bookDAO.loanBook(scanner.nextLine(), authenticator.getLoggedUser().getId())){
            System.out.println("Success!");
        }else{
            System.out.println("Something go wrong!");
        }
    }

    public void showAllBooks(){
        showLabel(Label.ALL_BOOKS_LABEL.label, format1, formatElements1);
        this.bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.DEFAULT_SEARCH).forEach(System.out::println);
        System.out.println();
    }

    public void showAllAvailableBooks(){
        showLabel(Label.ALL_AVAILABLE_BOOKS_LABEL.label, format1, formatElements1);
        this.bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.AVAILABLE_SEARCH).forEach(System.out::println);
        System.out.println();
    }

    public void readDataForFindingBook(){
        List<Book> filterAvailableBooks = new ArrayList<>();
        List<Book> deadlineExceededBooks = new ArrayList<>();
        List<BookDAO.UserBook> filterLoanedBooks = new ArrayList<>();

        System.out.println("Find book by:");
        System.out.println("\t1. author");
        System.out.println("\t2. title");
        System.out.println("\t3. ISBN");

        switch (scanner.nextLine()){
            case "1" -> {
                System.out.print("Author to found: ");
                String input = scanner.nextLine();

                filterAvailableBooks = this.bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.DEFAULT_SEARCH)
                        .stream()
                        .filter(b -> b.getAuthor().toLowerCase().contains(input.toLowerCase()))
                        .filter(Book::isLoanable)
                        .collect(Collectors.toList());
                filterLoanedBooks = this.bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.CURRENTLY_LOANED_SEARCH)
                        .stream()
                        .filter(e -> bookDAO.getBookByID(e.getBookID()).get().getAuthor().toLowerCase().contains(input.toLowerCase()))
                        .toList();
                if(filterLoanedBooks.size() == 0){
                    deadlineExceededBooks = this.bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.DEFAULT_SEARCH)
                            .stream()
                            .filter(b -> b.getAuthor().toLowerCase().contains(input.toLowerCase()))
                            .filter(b -> !b.isLoanable())
                            .collect(Collectors.toList());
                }
            }
            case "2" -> {
                System.out.print("Title to found: ");
                String input = scanner.nextLine();

                filterAvailableBooks = this.bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.DEFAULT_SEARCH)
                        .stream()
                        .filter(b -> b.getTitle().toLowerCase().contains(input.toLowerCase()))
                        .filter(Book::isLoanable)
                        .collect(Collectors.toList());
                filterLoanedBooks = this.bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.CURRENTLY_LOANED_SEARCH)
                        .stream()
                        .filter(e -> bookDAO.getBookByID(e.getBookID()).get().getTitle().toLowerCase().contains(input.toLowerCase()))
                        .toList();
                if(filterLoanedBooks.size() == 0){
                    deadlineExceededBooks = this.bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.DEFAULT_SEARCH)
                            .stream()
                            .filter(b -> b.getAuthor().toLowerCase().contains(input.toLowerCase()))
                            .filter(b -> !b.isLoanable())
                            .collect(Collectors.toList());
                }
            }
            case "3" -> {
                System.out.print("ISBN to found: ");
                String input = scanner.nextLine();

                filterAvailableBooks = this.bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.DEFAULT_SEARCH)
                        .stream()
                        .filter(b -> String.valueOf(b.getIsbn()).contains(input))
                        .filter(Book::isLoanable)
                        .toList();
                filterLoanedBooks = this.bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.CURRENTLY_LOANED_SEARCH)
                        .stream()
                        .filter(e -> bookDAO.getBookByID(e.getBookID()).get().getIsbn().toLowerCase().contains(input.toLowerCase()))
                        .toList();
                if(filterLoanedBooks.size() == 0){
                    deadlineExceededBooks = this.bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.DEFAULT_SEARCH)
                            .stream()
                            .filter(b -> b.getAuthor().toLowerCase().contains(input.toLowerCase()))
                            .filter(b -> !b.isLoanable())
                            .collect(Collectors.toList());
                }
            }
            default -> System.out.println("Option not supported!");
        }

        if(filterAvailableBooks.size() == 0 && filterLoanedBooks.size() == 0) {
            System.out.println("No results found!\n");

        }else if(filterAvailableBooks.size() > 0 && filterLoanedBooks.size() == 0){
            showLabel(Label.ALL_FOUNDED_BOOKS_LABEL.label, format1, formatElements1);
            deadlineExceededBooks.forEach(System.out::println);
            filterAvailableBooks.forEach(System.out::println);

        }else if(filterAvailableBooks.size() == 0 && filterLoanedBooks.size() > 0){
            extractBookAndUserFromListAndShow(filterLoanedBooks, Label.ALL_CURRENTLY_LOANED_BOOKS_LABEL.label);

        }else{
            showLabel(Label.ALL_FOUNDED_BOOKS_LABEL.label, format1, formatElements1);
            filterAvailableBooks.forEach(System.out::println);
            extractBookAndUserFromListAndShow(filterLoanedBooks, Label.ALL_CURRENTLY_LOANED_BOOKS_LABEL.label);
        }
    }

    public Optional<Book> readDataForAddingBook(){
        String author, title, isbn;

        System.out.print("Author: ");
        author = scanner.nextLine().trim();
        System.out.print("Title: ");
        title = scanner.nextLine().trim();
        System.out.print("ISBN: ");
        isbn = scanner.nextLine().trim();

        if(author.matches("[\\p{L}\\s|.|\\-\\p{L}]+") && title.matches("[\\p{L}|\\d|\\p{Punct} ]+")
                && (bookDAO.getBookByISBN(isbn).isEmpty() && isbn.matches("\\d{1,13}"))){
            return Optional.of(new Book(author, title, isbn));
        }else {
            return Optional.empty();
        }
    }

    public void showHistoryOrCurrentOfAllLoanedBooks(){
        System.out.println("\t1. History of library loans");
        System.out.println("\t2. Current library loans");

        switch (scanner.nextLine()){
            case "1" -> {
                List<BookDAO.UserBook> userBooks = this.bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.LOANED_SEARCH).stream().toList();
                extractBookAndUserFromListAndShow(userBooks, Label.ALL_LOANED_BOOKS_LABEL.label);
            }
            case "2" -> {
                List<BookDAO.UserBook> userBooks2 = this.bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.LOANED_SEARCH).stream()
                        .filter(b -> {
                            Calendar dayBefore = Calendar.getInstance();
                            dayBefore.setTime(b.getStartDate());
                            dayBefore.add(Calendar.DATE, -1);
                            Calendar dayAfter = Calendar.getInstance();
                            dayAfter.setTime(b.getEndDate());
                            dayAfter.add(Calendar.DATE, 1);
                            return Date.valueOf(bookDAO.getSdf().format(Calendar.getInstance().getTime())).after(dayBefore.getTime())
                                    && Date.valueOf(bookDAO.getSdf().format(Calendar.getInstance().getTime())).before(dayAfter.getTime());
                        })
                        .toList();

                extractBookAndUserFromListAndShow(userBooks2, Label.ALL_LOANED_BOOKS_LABEL.label);
            }
            default -> System.out.println("Option not supported.");
        }
    }

    public void showAllLoanedBooksWithDeadlineExceeded(){
        List<BookDAO.UserBook> userBooks = this.bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.DEADLINE_EXCEEDED_SEARCH).stream().toList();
        extractBookAndUserFromListAndShow(userBooks, Label.ALL_LOANED_BOOKS_LABEL.label);
    }

    private void extractBookAndUserFromListAndShow(List<BookDAO.UserBook> userBooks, String label) {
        if (label.equals(Label.ALL_CURRENTLY_LOANED_BOOKS_LABEL.label)) {
            showLabel(label, format3, formatElements3);
        } else {
            showLabel(label, format2, formatElements2);
        }

        for (BookDAO.UserBook ub : userBooks) {
            Optional<User> optionalUser = this.userDAO.getUserByID(ub.getUserID());
            Optional<Book> optionalBook = this.bookDAO.getBookByID(ub.getBookID());
            if(optionalUser.isPresent() && optionalBook.isPresent() && label.equals(Label.ALL_CURRENTLY_LOANED_BOOKS_LABEL.label)){
                System.out.printf((format3) + "%n", optionalBook.get().getTitle(),
                        optionalBook.get().getAuthor(), optionalBook.get().getIsbn(),
                        optionalBook.get().isLoanable(), ub.getStartDate(), ub.getEndDate());
            }else if (optionalUser.isPresent() && optionalBook.isPresent()){
                System.out.printf((format2) + "%n", optionalUser.get().getFirstName(),
                        optionalUser.get().getLastName(), optionalBook.get().getTitle(),
                        optionalBook.get().getAuthor(), ub.getStartDate(), ub.getEndDate());
            }
        }
        System.out.println();
    }
    private void showLabel(String label, String format, Object[] elements){
        StringBuilder sb = new StringBuilder(label);
        sb.append("\n");
        sb.append(String.format(format, elements));
        System.out.println(sb);
    }

    private enum Label{
        ALL_BOOKS_LABEL("====List of all books in the library===="),
        ALL_AVAILABLE_BOOKS_LABEL("====List of all available books in the library===="),
        ALL_FOUNDED_BOOKS_LABEL("====List of all founded books in the library===="),
        ALL_LOANED_BOOKS_LABEL("====List of all loaned books in the library===="),
        ALL_CURRENTLY_LOANED_BOOKS_LABEL("Currently loaned books:");

        public final String label;
        Label(String label) {
            this.label = label;
        }
    }
    public static GUI getInstance(){
        return gui;
    }
}
