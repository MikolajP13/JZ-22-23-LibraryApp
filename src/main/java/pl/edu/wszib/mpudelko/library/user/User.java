package pl.edu.wszib.mpudelko.library.user;

public class User {
    private int id;
    private String login;
    private String firstName;
    private String lastName;
    private String password;
    private UserRole role;

    public User() {
    }

    public User(String login, String firstName, String lastName, String password, UserRole role) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
    }

    public User(int id, String login, String firstName, String lastName, String password, UserRole role) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public enum UserRole{
        ADMIN,
        USER
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;

        if(o == null)
            return false;

        if(this.getClass() != o.getClass())
            return false;

        User otherUser = (User) o;

        return this.getId() == otherUser.getId() && this.getLogin().equals(otherUser.getLogin())
                && this.getFirstName().equals(otherUser.getFirstName()) && this.getLastName().equals(otherUser.getLastName())
                && this.getPassword().equals(otherUser.getPassword()) && this.getRole() == otherUser.getRole();
    }

    @Override
    public String toString() {
        return String.format("%d %-15s %-25s %-25s %-50s %-10s", this.id, this.login, this.firstName, this.lastName, this.password, this.role);
    }
}
