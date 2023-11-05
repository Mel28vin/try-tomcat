import com.squareup.moshi.Json;

public class Item {
    @Json(name = "item_id")
    private final long itemId;

    private String name;
    private int status;
    private double rate;
    private String description;

    public Item(long itemId, String name, int status, double rate, String description) {
        this.itemId = itemId;
        this.name = name;
        this.status = status;
        this.rate = rate;
        this.description = description;
    }

    public long getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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