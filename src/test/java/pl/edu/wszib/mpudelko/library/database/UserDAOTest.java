package pl.edu.wszib.mpudelko.library.database;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.edu.wszib.mpudelko.library.core.Authenticator;
import pl.edu.wszib.mpudelko.library.user.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAOTest {
    static UserDAO userDAO;
    static Connection connection;

    @BeforeAll
    public static void prepareAll(){
        userDAO = UserDAO.getInstance();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/librarydb", //"jdbc:mysql://localhost:3306/librarydb"
                    "root",
                    "");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanAddedUser(int userID) {
        try {
            String sql = "DELETE FROM t_users where id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userID);
            ps.executeUpdate();

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
    public void getUserByExistingIDTest() {
        int id = 1;
        User expected = new User(1, "Admin", "admin", "admin", "106b84851d4e3c50e6c37cdcb625de42", User.UserRole.ADMIN);
        User actual;

        if (userDAO.getUserByID(id).isPresent())
            actual = userDAO.getUserByID(id).get();
        else
            actual = null;

        Assertions.assertEquals(expected, actual);
        Assertions.assertNotNull(actual);
    }

    @Test
    public void getUserByNonExistingIDTest() {
        int id = 0;
        User actual;

        if (userDAO.getUserByID(id).isPresent())
            actual = userDAO.getUserByID(id).get();
        else
            actual = null;

        Assertions.assertNull(actual);
    }

    @Test
    public void findUserByExistingLoginTest() {
        String login = "Admin";
        User expected = new User(1, "Admin", "admin", "admin", "106b84851d4e3c50e6c37cdcb625de42", User.UserRole.ADMIN);
        User actual;

        if (userDAO.findByLogin(login).isPresent())
            actual = userDAO.findByLogin(login).get();
        else
            actual = null;

        Assertions.assertEquals(expected, actual);
        Assertions.assertNotNull(actual);
    }

    @Test
    public void findUserByNonExistingLoginTest() {
        String login = "NonExistingUserLogin";
        User actual;

        if (userDAO.findByLogin(login).isPresent())
            actual = userDAO.findByLogin(login).get();
        else
            actual = null;

        Assertions.assertNull(actual);
    }

    @Test
    public void addUserTest(){
        String hashPassword = DigestUtils.md5Hex("Password12!@" + Authenticator.getInstance().getSeed());
        User expected = new User("TestLogin", "TestName", "TestName", hashPassword, User.UserRole.USER);

        userDAO.addUserToDatabase(expected);

        int userID = userDAO.findByLogin("TestLogin").get().getId();
        System.out.println("ID:" + userID);
        User actual = userDAO.getUserByID(expected.getId()).get();

        Assertions.assertEquals(expected, actual);

        cleanAddedUser(userID);
    }
}
