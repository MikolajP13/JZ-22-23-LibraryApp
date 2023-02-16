package pl.edu.wszib.mpudelko.library.database;

import pl.edu.wszib.mpudelko.library.book.Book;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class BookDAO {
    private final Connection connection;
    private static final BookDAO bookDAO = new BookDAO();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final int loanBookPeriod = 14;

    private BookDAO() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/librarydb", //"jdbc:mysql://localhost:3306/librarydb"
                    "root",
                    "");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addBook(Book book) {
        try {
            String sql = "INSERT INTO t_books (author, title, isbn, loanable) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getAuthor());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getIsbn());
            ps.setBoolean(4, book.isLoanable());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            book.setId(rs.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Book> getAllBooksBySearchType(SearchTypeForUser searchType){
        ArrayList<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM t_books");
        String query = null;
        try {
            if(searchType == SearchTypeForUser.DEFAULT_SEARCH){
                query = sql.toString();
            }else if(searchType == SearchTypeForUser.AVAILABLE_SEARCH){
               query = sql.append(" WHERE loanable = true").toString();
            }else if (searchType == SearchTypeForUser.LOANED_SEARCH) {
                query = sql.append(" WHERE loanable = false").toString();
            }

            PreparedStatement ps = this.connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Book book = new Book(rs.getInt("id"),
                        rs.getString("author"),
                        rs.getString("title"),
                        rs.getString("isbn"),
                        rs.getBoolean("loanable"));
                books.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    public static class UserBook {
        int id;
        int userID;
        int bookID;
        Date startDate;
        Date endDate;


        public UserBook() {
        }

        public UserBook(int userID, int bookID, Date startDate, Date endDate) {
            this.userID = userID;
            this.bookID = bookID;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public UserBook(int id, int userID, int bookID, Date startDate, Date endDate) {
            this.id = id;
            this.userID = userID;
            this.bookID = bookID;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getBookID() {
            return bookID;
        }

        public void setBookID(int bookID) {
            this.bookID = bookID;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj)
                return true;

            if(obj == null)
                return false;

            if(this.getClass() != obj.getClass())
                return false;

            UserBook otherUserBook = (UserBook) obj;

            return this.id == otherUserBook.getId() && this.userID == otherUserBook.getUserID()
                    && this.bookID == otherUserBook.getBookID() && this.startDate.compareTo(otherUserBook.getStartDate()) == 0
                    && this.endDate.compareTo(otherUserBook.getEndDate()) == 0;
        }
    }

    public List<UserBook> getAllBooksBySearchTypeForAdmin(SearchTypeForAdmin searchType){
        List<UserBook> userBookList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM t_loans");
        String query = null;
        try {
            if(searchType == SearchTypeForAdmin.DEADLINE_EXCEEDED_SEARCH){
                sql.append(" WHERE DATEDIFF(end_date, NOW()) < 0");
                query = sql.toString();
            }else if (searchType == SearchTypeForAdmin.LOANED_SEARCH) {
                query = sql.toString();
            }else if (searchType == SearchTypeForAdmin.CURRENTLY_LOANED_SEARCH){
                sql.append(" WHERE NOW() <= end_date");
                query = sql.toString();
            }

            PreparedStatement ps = this.connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                UserBook userBook = new UserBook(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("book_id"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date")
                );
                userBookList.add(userBook);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userBookList;
    }

    public Optional<Book> getBookByISBN(String bookISBN){
        try{
            String sql = "SELECT * FROM t_books WHERE isbn = ?";
            PreparedStatement ps = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, bookISBN);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return Optional.of(new Book(rs.getInt("id"),
                        rs.getString("author"),
                        rs.getString("title"),
                        rs.getString("isbn"),
                        rs.getBoolean("loanable")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    public Optional<Book> getBookByID(int bookID){
        try{
            String sql = "SELECT * FROM t_books WHERE id = ?";
            PreparedStatement ps = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, bookID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return Optional.of(new Book(
                        rs.getInt("id"),
                        rs.getString("author"),
                        rs.getString("title"),
                        rs.getString("isbn"),
                        rs.getBoolean("loanable")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    public boolean loanBook(String isbnToLoan, int userID){
        try{
            String sql = "SELECT * FROM t_books WHERE isbn = ?";
            PreparedStatement ps = this.connection.prepareStatement(sql);
            ps.setString(1, isbnToLoan);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                if(rs.getBoolean("loanable")){
                    String update = "UPDATE t_books SET loanable = ? WHERE id = ?";
                    int bookID = rs.getInt("id");
                    PreparedStatement psUpdate = this.connection.prepareStatement(update);
                    psUpdate.setBoolean(1, false);
                    psUpdate.setInt(2, bookID);

                    psUpdate.executeUpdate();

                    String sql2 = "INSERT INTO t_loans (user_id, book_id, start_date, end_date)"
                            + "VALUES(?, ?, ?, ?)";
                    Calendar calendar = Calendar.getInstance();

                    PreparedStatement psInsert = this.connection.prepareStatement(sql2);
                    psInsert.setInt(1, userID);
                    psInsert.setInt(2, bookID);
                    psInsert.setDate(3, Date.valueOf(sdf.format(calendar.getTime())));
                    calendar.add(Calendar.DATE, loanBookPeriod);
                    psInsert.setDate(4, Date.valueOf(sdf.format(calendar.getTime())));

                    psInsert.executeUpdate();

                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean removeBookByID(int bookID) {
        try {
            String sql = "DELETE FROM t_books where id = ?";
            PreparedStatement ps = this.connection.prepareStatement(sql);
            ps.setInt(1, bookID);
            if(ps.executeUpdate() > 0)
                return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public SimpleDateFormat getSdf() {
        return sdf;
    }

    public enum SearchTypeForUser{
        DEFAULT_SEARCH,
        AVAILABLE_SEARCH,
        LOANED_SEARCH
    }
    public enum SearchTypeForAdmin{
        DEADLINE_EXCEEDED_SEARCH,
        LOANED_SEARCH,
        CURRENTLY_LOANED_SEARCH
    }
    public static BookDAO getInstance(){
        return bookDAO;
    }
}
