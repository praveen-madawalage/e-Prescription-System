package system.models;

public class Medicine {
    private int id;
    private String name;
    private String description;
    private int stock;
    private String unit;

    public Medicine(int id, String name, String description, int stock, String unit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.unit = unit;
    }
}
