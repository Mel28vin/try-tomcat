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

    public static String getContactPerson(long customerId, long contactPersonId) {
        CustomerContactPerson temp = null;
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement st = con.prepareStatement("Select name, email from CustomerContactPersonTable Where customer_id = ? and contact_person_id = ?")) {
            st.setLong(1, customerId);
            st.setLong(2, contactPersonId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                temp = new CustomerContactPerson(contactPersonId, rs.getString("name"), rs.getString("email"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        JsonAdapter<CustomerContactPerson> jsonAdapter = moshi.adapter(CustomerContactPerson.class).indent("  ");
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

    public static boolean addContactPerson(long customerId, String jsonString) {
        JsonAdapter<CustomerContactPerson> jsonAdapter = moshi.adapter(CustomerContactPerson.class).indent("  ");

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("Insert into CustomerContactPersonTable (customer_id, name, email) values (?, ?, ?)");
            CustomerContactPerson temp = jsonAdapter.fromJson(jsonString);

            assert temp != null;
            st.setLong(1, customerId);
            st.setString(2, temp.getName());
            st.setString(3, temp.getEmail());

            st.executeUpdate();

            return true;
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static String updateCustomer(long customerId, String jsonString) {
        JsonAdapter<Customer> jsonAdapter = moshi.adapter(Customer.class).indent("  ");

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("UPDATE CustomerTable SET name = COALESCE(?, name), email = COALESCE(?, email) WHERE customer_id = ?");
            Customer currCustomer = jsonAdapter.fromJson(jsonString);
            assert currCustomer != null;
            st.setString(1, currCustomer.getName());
            st.setString(2, currCustomer.getEmail());
            st.setLong(3, customerId);
            st.executeUpdate();

            st = con.prepareStatement("UPDATE CustomerContactPersonTable SET name = COALESCE(?, name), email = COALESCE(?, email) WHERE customer_id = ? AND contact_person_id = ?");
            List<CustomerContactPerson> arr = currCustomer.getContactPeople();
            if (arr != null)
                for (CustomerContactPerson contactPerson : arr) {
                    st.setString(1, contactPerson.getName());
                    st.setString(2, contactPerson.getEmail());
                    st.setLong(3, customerId);
                    st.setLong(4, contactPerson.getContactPersonId());
                    st.executeUpdate();
                }

        } catch (SQLException | IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }

        return getCustomer(customerId);
    }

    public static boolean setStatus(long customerId, int status) {
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement st = con.prepareStatement("Update CustomerTable Set status = ? where customer_id = ?")) {
            st.setInt(1, status);
            st.setLong(2, customerId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            return false;
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

    public static boolean removeContactPerson(long customerId, long contactPersonId) {
        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("DELETE FROM CustomerContactPersonTable WHERE customer_id=? and contact_person_id=?");
            st.setLong(1, customerId);
            st.setLong(2, contactPersonId);
            st.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}