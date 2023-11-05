import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerUtils {
    static Moshi moshi = new Moshi.Builder().build();

    public static String getAllCustomers() {
        List<Customer> allCustomers = new ArrayList<>();

        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement st = con.prepareStatement("Select * from CustomerTable");
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                long customerId = rs.getLong("customer_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int status = rs.getInt("status");
                List<CustomerContactPerson> contactPeople = new ArrayList<>();

                PreparedStatement ps = con.prepareStatement("Select contact_person_id, name, email from CustomerContactPersonTable where customer_id=?");
                ps.setLong(1, customerId);
                ResultSet subRs = ps.executeQuery();

                while (subRs.next()) {
                    CustomerContactPerson temp = new CustomerContactPerson(
                            subRs.getLong("contact_person_id"),
                            subRs.getString("name"),
                            subRs.getString("email")
                    );
                    contactPeople.add(temp);
                }

                Customer temp = new Customer(customerId, name, email, status, contactPeople);
                allCustomers.add(temp);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Type type = Types.
                newParameterizedType(List.class, Customer.class);
        JsonAdapter<List<Customer>> jsonAdapter = moshi.adapter(type);

        return jsonAdapter.toJson(allCustomers);
    }

    public static String getCustomer(long customerId) {
        Customer temp = null;
        try (Connection con = DBConnectionManager.getConnection()) {

            PreparedStatement st = con.prepareStatement("Select * from CustomerTable where customer_id=?");
            st.setLong(1, customerId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                int status = rs.getInt("status");
                List<CustomerContactPerson> contactPeople = new ArrayList<>();

                PreparedStatement ps = con.prepareStatement("Select contact_person_id, name, email from CustomerContactPersonTable where customer_id=?");
                ps.setLong(1, customerId);
                ResultSet subRs = ps.executeQuery();

                while (subRs.next()) {
                    CustomerContactPerson contactPerson = new CustomerContactPerson(
                            subRs.getLong("contact_person_id"),
                            subRs.getString("name"),
                            subRs.getString("email")
                    );
                    contactPeople.add(contactPerson);
                }

                temp = new Customer(customerId, name, email, status, contactPeople);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        JsonAdapter<Customer> jsonAdapter = moshi.adapter(Customer.class).indent("  ");
        return jsonAdapter.toJson(temp);
    }

    public static String getCustomerContactPeople(long customerId) {
        List<CustomerContactPerson> contactPeople = new ArrayList<>();
        try (Connection con = DBConnectionManager.getConnection()) {

            PreparedStatement ps = con.prepareStatement("Select contact_person_id, name, email from CustomerContactPersonTable where customer_id=?");
            ps.setLong(1, customerId);
            ResultSet subRs = ps.executeQuery();

            while (subRs.next()) {
                CustomerContactPerson contactPerson = new CustomerContactPerson(
                        subRs.getLong("contact_person_id"),
                        subRs.getString("name"),
                        subRs.getString("email")
                );
                contactPeople.add(contactPerson);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Type type = Types.newParameterizedType(List.class, CustomerContactPerson.class);
        JsonAdapter<List<CustomerContactPerson>> jsonAdapter = moshi.adapter(type);
        return jsonAdapter.toJson(contactPeople);

    }

    public static boolean addCustomer(String jsonString) {
        JsonAdapter<Customer> jsonAdapter = moshi.adapter(Customer.class).indent("  ");

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("Insert into CustomerTable (name, email) VALUES (?, ?)");
            Customer currCustomer = jsonAdapter.fromJson(jsonString);
            assert currCustomer != null;
            st.setString(1, currCustomer.getName());
            st.setString(2, currCustomer.getEmail());
            st.executeUpdate();

            // get customer_id
            long customerId = 0;
            st = con.prepareStatement("Select max(customer_id) from CustomerTable");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                customerId = rs.getLong(1);
            } else {
                throw new SQLException("Inserted Customer not found");
            }

            st = con.prepareStatement("Insert into CustomerContactPersonTable (customer_id, name, email) VALUES (?, ?, ?)");
            st.setLong(1, customerId);
            List<CustomerContactPerson> arr = currCustomer.getContactPeople();
            for (CustomerContactPerson contactPerson : arr) {
                st.setString(2, contactPerson.getName());
                st.setString(3, contactPerson.getEmail());
                st.executeUpdate();
            }

        } catch (SQLException | IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateCustomer(long customerId, String jsonString) {
        Type type = Types.newParameterizedType(Map.class, String.class, String.class);
        JsonAdapter<Map<String, String>> jsonAdapter = moshi.adapter(type);

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("UPDATE CustomerTable SET name = COALESCE(?, name), email = COALESCE(?, email) WHERE customer_id = ?");
            Map<String, String> customerData = jsonAdapter.fromJson(jsonString);

            if (customerData != null) {
                st.setString(1, customerData.getOrDefault("name", null));

                st.setString(2, customerData.getOrDefault("email", null));

                st.setLong(3, customerId);

                int rowsUpdated = st.executeUpdate();

                return rowsUpdated > 0;
            } else {
                return false; // Invalid or missing data in the JSON
            }

        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace(); // Log or handle the exception as needed
            return false;        // Update failed
        }
    }

    public static boolean removeCustomer(long customerId) {

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("DELETE FROM CustomerTable WHERE customer_id=?");
            st.setLong(1, customerId);
            st.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}