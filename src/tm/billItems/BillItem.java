package tm.billItems;

public class BillItem {
    private String billId;
    private String itemName;
    private int quantity;
    private double ratePrice;
    private double price;

    public BillItem(String billId, String itemName, int quantity, double ratePrice, double price) {
        this.billId = billId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.ratePrice = ratePrice;
        this.price = price;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRatePrice() {
        return ratePrice;
    }

    public void setRatePrice(double ratePrice) {
        this.ratePrice = ratePrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
