package system.models;

public class Patient extends User {
    public Patient (int id, String fullname, String username) {
        super(id, fullname, username);
    }
    @Override
    public userRole getRole() {
        return userRole.PATIENT;
    }
}
