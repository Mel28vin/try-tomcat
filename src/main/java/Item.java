public class Item {
    private final int item_id;

    private String name;
    private short status;
    private double rate;
    private String description;

    public Item(int item_id, String name, short status, double rate, String description) {
        this.item_id = item_id;
        this.name = name;
        this.status = status;
        this.rate = rate;
        this.description = description;
    }

    public int getItem_id() {
        return item_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}