package entity;

public class Staff implements UserAuthentication{

    private String id;
    private String password;
    private String name;
    private boolean isLogin;

    public Staff(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.isLogin = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    @Override
    public boolean authenticate(String id, String password) {
        return this.id.equals(id) && this.password.equals(password);
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", isLogin=" + isLogin +
                '}';
    }
}
