public class Customer {
    private final int customer_id;
    private String name;

    private String email;
    private int status;

    public Customer(int customer_id, String name, String email, int status) {
        this.customer_id = customer_id;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    public int getCustomer_id() {
        return customer_id;
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