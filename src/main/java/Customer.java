import com.squareup.moshi.Json;

public class Customer {
    @Json(name = "customer_id")
    private final long customerId;
    private String name;

    private String email;
    private int status;

    public Customer(long customerId, String name, String email, int status) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    public long getCustomer_id() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}