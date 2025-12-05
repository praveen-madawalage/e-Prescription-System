package system.models;

public abstract class User {
    private final int id;
    private final String fullname;
    private final String username;

    public User (int id, String fullname, String username) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
    }
    public int getID() {return id;}

    public String getFullname() {return fullname;}

    public String getUsername() {return username;}

    @Override
    public String toString() {return fullname;}

    public abstract userRole getRole();

    public enum userRole {
        DOCTOR, PHARMACIST, PATIENT
    }
}
