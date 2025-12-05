package system.models;

public class Medicine {
    private final int id;
    private final String name;
    private final String description;
    private final int stock;
    private final String unit;

    public Medicine(int id, String name, String description, int stock, String unit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.unit = unit;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getStock() {
        return stock;
    }
    public String getUnit() {
        return unit;
    }
    @Override
    public String toString() {
        return name + " (" + stock + " " + unit + " left)";
    }
}
