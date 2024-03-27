package tm.item;

public class item {
    private String name;
    private String manufacture;
    private double price;
    private int quantity;
    private String category;

    public item() {
    }

    public item(String name, String manufacture, double price, int quantity, String category) {
        this.name = name;
        this.manufacture = manufacture;
        this.price = price;
        this.quantity = quantity;
        this.setCategory(category);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
