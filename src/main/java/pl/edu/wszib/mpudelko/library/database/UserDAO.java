package pl.edu.wszib.mpudelko.library.database;

import pl.edu.wszib.mpudelko.library.user.User;

import java.sql.*;
import java.util.Optional;

public class UserDAO {
    private final Connection connection;
    private static final UserDAO userDAO = new UserDAO();

    private UserDAO(){
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

    public Optional<User> findByLogin(String login){
        try{
            String sql = "SELECT * FROM t_users WHERE login = ?";
            PreparedStatement ps = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return Optional.of(new User(rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        User.UserRole.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<User> getUserByID(int userID){
        try{
            String sql = "SELECT * FROM t_users WHERE id = ?";
            PreparedStatement ps = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        User.UserRole.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    public void addUserToDatabase(User user){
        try{
            String sql = "INSERT INTO t_users " +
                    "(login, first_name, last_name, password, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().toString());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            user.setId(rs.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static UserDAO getInstance(){
        return userDAO;
    }
}
