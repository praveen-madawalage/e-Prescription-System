package system.models;

public class Doctor extends User{
    public Doctor (int id, String fullname, String username) {
        super(id, fullname, username);
    }
    @Override
    public userRole getRole() {
        return userRole.DOCTOR;
    }
}
