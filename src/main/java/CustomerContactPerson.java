import com.squareup.moshi.Json;

public class CustomerContactPerson {
    @Json(name = "contact_person_id")
    private final long contactPersonId;
    private String name;
    private String email;

    public CustomerContactPerson(long contactPersonId, String name, String email) {
        this.contactPersonId = contactPersonId;
        this.name = name;
        this.email = email;
    }

    public long getContactPersonId() {
        return contactPersonId;
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
}