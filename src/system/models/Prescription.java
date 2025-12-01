package system.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prescription {
    private final int id;
    private final int doctorId;
    private final int patientId;
    private final String secureToken;
    private final String status;
    private final LocalDateTime createdAt;
    private final String notes;
    private final List<PrescriptionItem> items;

    public Prescription (int id,
                         int doctorId,
                         int patientId,
                         String secureToken,
                         String status,
                         LocalDateTime createdAt,
                         String notes,
                         List<PrescriptionItem> items) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.secureToken = secureToken;
        this.status = status;
        this.createdAt = createdAt;
        this.notes = notes;
        this.items = items;
    }
    public int getId() {
        return id;
    }
    public int getDoctorId() {
        return doctorId;
    }
    public String getSecureToken() {
        return secureToken;
    }
    public String getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public String getNotes() {
        return notes;
    }
    public List<PrescriptionItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
