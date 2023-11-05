import com.squareup.moshi.Json;

import java.util.List;

public class Customer {
    @Json(name = "customer_id")
    private final long customerId;
    private String name;

    private String email;
    private int status;

    @Json(name = "contact_people")
    private List<CustomerContactPerson> contactPeople;

    public Customer(long customerId, String name, String email, int status, List<CustomerContactPerson> contactPeople) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.status = status;
        this.contactPeople = contactPeople;
    }

    public Customer(String name, String email, int status, List<CustomerContactPerson> contactPeople) {
        this.customerId = 0;
        this.name = name;
        this.email = email;
        this.status = status;
        this.contactPeople = contactPeople;
    }

    public long getCustomerId() {
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

    public List<CustomerContactPerson> getContactPeople() {
        return contactPeople;
    }

    public void setContactPeople(List<CustomerContactPerson> contactPeople) {
        this.contactPeople = contactPeople;
    }
}