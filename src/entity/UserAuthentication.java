package entity;

@FunctionalInterface
public interface UserAuthentication {
    boolean authenticate(String id, String password);
}
