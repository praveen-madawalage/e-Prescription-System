package system.models;

public class PrescriptionItem {
    private final int medicine_id;
    private final String medicineName;
    private final int quantity;
    private final String dosageInstructions;

    public PrescriptionItem (int medicine_id, String medicineName, int quantity, String dosageInstructions) {
        this.medicine_id = medicine_id;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.dosageInstructions = dosageInstructions;
    }

    public int getMedicineId() {return medicine_id;}

    public String getMedicineName() {return medicineName;}

    public int getQuantity() {return quantity;}

    public String getDosageInstructions() {return dosageInstructions;}

    @Override
    public String toString () {
        return medicineName + " quantity: " + quantity + "( " + dosageInstructions + " )";
    }
}
