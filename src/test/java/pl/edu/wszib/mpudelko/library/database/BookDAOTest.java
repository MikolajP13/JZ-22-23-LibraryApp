package pl.edu.wszib.mpudelko.library.database;

import org.junit.jupiter.api.*;
import pl.edu.wszib.mpudelko.library.book.Book;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookDAOTest {
    static Connection connection;
    static BookDAO bookDAO;
    int testBookID;
    static List<Book> bookList = new ArrayList<>();
    List<Book> bookActual;
    static List<BookDAO.UserBook> userBookList = new ArrayList<>();
    List<BookDAO.UserBook> userBooksActual;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeAll
    public static void prepareAll() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/librarydb", //"jdbc:mysql://localhost:3306/librarydb"
                    "root",
                    "");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        bookDAO = BookDAO.getInstance();

        try {
            String sql = "SELECT * FROM t_books";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = new Book(rs.getInt("id"),
                        rs.getString("author"),
                        rs.getString("title"),
                        rs.getString("isbn"),
                        rs.getBoolean("loanable"));
                bookList.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            String sql = "SELECT * FROM t_loans";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BookDAO.UserBook ub = new BookDAO.UserBook(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("book_id"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date")
                );
                userBookList.add(ub);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void clean() {
        bookActual = new ArrayList<>();
        userBooksActual = new ArrayList<>();
    }

    public void cleanAddedBook() {
        bookDAO.removeBookByID(testBookID);
    }
    public void cleanAfterLoaning(int bookID) {
        try {

            String updateQuery = "UPDATE t_books SET loanable = ? WHERE id = ?";
            PreparedStatement psUpdate = BookDAOTest.connection.prepareStatement(updateQuery);
            psUpdate.setBoolean(1, true);
            psUpdate.setInt(2, bookID);
            psUpdate.executeUpdate();

            String sql = "SELECT * FROM t_loans";
            PreparedStatement ps = BookDAOTest.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = ps.executeQuery();

            int loanID = 0;
            while (rs.next())
                loanID = rs.getInt("id");

            String deleteQuery = "DELETE FROM t_loans where id = ?";
            PreparedStatement ps2 = BookDAOTest.connection.prepareStatement(deleteQuery);
            ps2.setInt(1, loanID);
            ps2.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void cleanAll(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addBookTest() {
        Book expected = new Book("Author", "Title", "999");
        bookDAO.addBook(expected);

        Book actual = bookDAO.getBookByID(expected.getId()).get();
        testBookID = actual.getId();

        Assertions.assertEquals(expected, actual);

        cleanAddedBook();
    }

    @Test
    public void getAllBooksByDefaultSearchTypeTest() {
        List<Book> expected = bookList;

        bookActual = bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.DEFAULT_SEARCH);

        Assertions.assertEquals(expected, bookActual);
        Assertions.assertDoesNotThrow(() -> bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.DEFAULT_SEARCH));
    }

    @Test
    public void getAllBooksByAvailableSearchTypeTest() {
        List<Book> expected = bookList.stream().filter(Book::isLoanable).toList();

        bookActual = bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.AVAILABLE_SEARCH);

        Assertions.assertEquals(expected, bookActual);
        Assertions.assertDoesNotThrow(() -> bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.AVAILABLE_SEARCH));
    }

    @Test
    public void getAllBooksByLoanedSearchTypeTest() {
        List<Book> expected = bookList.stream().filter(b -> !b.isLoanable()).toList();
        bookActual = bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.LOANED_SEARCH);

        Assertions.assertEquals(expected, bookActual);
        Assertions.assertDoesNotThrow(() -> bookDAO.getAllBooksBySearchType(BookDAO.SearchTypeForUser.LOANED_SEARCH));
    }

    @Test
    public void getAllBooksByDeadlineExceededSearchTypeForAdminTest() {
        List<BookDAO.UserBook> expected = userBookList.stream().filter(ub -> ub.getEndDate().compareTo(Date.valueOf(sdf.format(calendar.getTime()))) < 0).collect(Collectors.toList());

        userBooksActual = bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.DEADLINE_EXCEEDED_SEARCH);

        Assertions.assertEquals(expected, userBooksActual);

        Assertions.assertDoesNotThrow(() -> bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.DEADLINE_EXCEEDED_SEARCH));
    }

    @Test
    public void getAllBooksByLoanedSearchTypeForAdminTest() {
        List<BookDAO.UserBook> expected = userBookList;

        List<BookDAO.UserBook> actual = bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.LOANED_SEARCH);

        Assertions.assertEquals(expected, actual);

        Assertions.assertDoesNotThrow(() -> bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.LOANED_SEARCH));
    }

    @Test
    public void getAllBooksByCurrentlyLoanedSearchTypeForAdminTest() {
        List<BookDAO.UserBook> expected = userBookList.stream()
                .filter(ub -> ub.getStartDate().compareTo(Date.valueOf(sdf.format(calendar.getTime()))) <= 0 && ub.getEndDate().compareTo(Date.valueOf(sdf.format(calendar.getTime()))) >= 0)
                .collect(Collectors.toList());

        List<BookDAO.UserBook> actual = bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.CURRENTLY_LOANED_SEARCH);

        Assertions.assertEquals(expected, actual);

        Assertions.assertDoesNotThrow(() -> bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.CURRENTLY_LOANED_SEARCH));
    }

    @Test
    public void getBookByExistingISBNTest() {
        String isbn = "102";
        Book expected = new Book(3, "Jaume Cabre", "Spaleni w ogniu", "102", true);
        Book actual;

        if (bookDAO.getBookByISBN(isbn).isPresent())
            actual = bookDAO.getBookByISBN(isbn).get();
        else
            actual = null;

        Assertions.assertEquals(expected, actual);
        Assertions.assertNotNull(actual);
    }

    @Test
    public void getBookByNonExistingISBNTest() {
        String isbn = "000";
        Book actual;

        if (bookDAO.getBookByISBN(isbn).isPresent())
            actual = bookDAO.getBookByISBN(isbn).get();
        else
            actual = null;

        Assertions.assertNull(actual);
    }

    @Test
    public void getBookByExistingIDTest() {
        int id = 3;
        Book expected = new Book(3, "Jaume Cabre", "Spaleni w ogniu", "102", true);
        Book actual;

        if (bookDAO.getBookByID(id).isPresent())
            actual = bookDAO.getBookByID(id).get();
        else
            actual = null;

        Assertions.assertEquals(expected, actual);
        Assertions.assertNotNull(actual);
    }

    @Test
    public void getBookByNonExistingIDTest() {
        int id = 0;
        Book actual;

        if (bookDAO.getBookByID(id).isPresent())
            actual = bookDAO.getBookByID(id).get();
        else
            actual = null;

        Assertions.assertNull(actual);
    }

    @Test
    public void existingIsbnAndUserLoanBookTest() {
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.DATE, 14);
        int bookID = 1;
        String bookISBN = "100";
        int userID = 1;
        int id = bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.LOANED_SEARCH).size();
        id += 1;

        BookDAO.UserBook expected = new BookDAO.UserBook(id, userID, bookID, Date.valueOf(this.sdf.format(calendarStart.getTime())), Date.valueOf(this.sdf.format(calendarEnd.getTime())));
        Assertions.assertTrue(bookDAO.loanBook(bookISBN, userID));

        int finalId = id;
        Optional<BookDAO.UserBook> actual = bookDAO.getAllBooksBySearchTypeForAdmin(BookDAO.SearchTypeForAdmin.LOANED_SEARCH).stream().filter(ub -> ub.getId() == finalId).findFirst();

        actual.ifPresent(userBook -> Assertions.assertEquals(expected, userBook));

        cleanAfterLoaning(bookID);
    }

    @Test
    public void nonExistingIsbnAndExistingUserLoanBookTest() {
        String bookISBN = "000";
        int userID = 1;

        Assertions.assertFalse(bookDAO.loanBook(bookISBN, userID));
    }

    @Test
    public void tryToLoanBorrowedBookTest() {
        String bookISBN = "119";
        int userID = 1;
        bookDAO.loanBook(bookISBN, userID);

        Assertions.assertFalse(bookDAO.loanBook(bookISBN, userID));

        cleanAfterLoaning(bookDAO.getBookByISBN(bookISBN).get().getId());
    }

    @Test
    public void removeBookByExistingIDTest() {
        Book book = new Book("Author", "Title", "999");
        bookDAO.addBook(book);
        int id = book.getId();

        Assertions.assertTrue(bookDAO.removeBookByID(id));
    }

    @Test
    public void removeBookByNonExistingIDTest() {
        int id = 000;
        Assertions.assertFalse(bookDAO.removeBookByID(id));
    }
}
