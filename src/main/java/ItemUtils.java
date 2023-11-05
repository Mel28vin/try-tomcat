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

public class ItemUtils {
    static Moshi moshi = new Moshi.Builder().build();

    public static String getAllCustomers() {
        List<Customer> allCustomers = new ArrayList<>();

        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement st = con.prepareStatement("Select * from CustomerTable");
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                int customer_id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int status = rs.getInt("status");
                Customer temp = new Customer(customer_id, name, email, status, null);
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

    public static String getCustomer(int customer_id) {
        Customer temp = null;
        try (Connection con = DBConnectionManager.getConnection()) {

            PreparedStatement st = con.prepareStatement("Select * from CustomerTable where customer_id=?");
            st.setInt(1, customer_id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                int status = rs.getInt("status");
                temp = new Customer(customer_id, name, email, status, null);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        JsonAdapter<Customer> jsonAdapter = moshi.adapter(Customer.class).indent("  ");
        return jsonAdapter.toJson(temp);
    }

    public static boolean addCustomer(String jsonString) {
        Type type = Types.newParameterizedType(Map.class, String.class, String.class);
        JsonAdapter<Map<String, String>> jsonAdapter = moshi.adapter(type);

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("Insert into CustomerTable (name, email) VALUES (?, ?)");
            Map<String, String> customerData = jsonAdapter.fromJson(jsonString);
            assert customerData != null;
            if (customerData.containsKey("name") && customerData.containsKey("email")) {
                st.setString(1, customerData.get("name"));
                st.setString(2, customerData.get("email"));
                st.executeUpdate();
            } else {
                return false;
            }

        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateCustomer(int customer_id, String jsonString) {
        Type type = Types.newParameterizedType(Map.class, String.class, String.class);
        JsonAdapter<Map<String, String>> jsonAdapter = moshi.adapter(type);

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("UPDATE CustomerTable SET name = COALESCE(?, name), email = COALESCE(?, email) WHERE customer_id = ?");
            Map<String, String> customerData = jsonAdapter.fromJson(jsonString);

            if (customerData != null) {
                st.setString(1, customerData.getOrDefault("name", null));

                st.setString(2, customerData.getOrDefault("email", null));

                st.setInt(3, customer_id);

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

    public static boolean removeCustomer(int customer_id) {

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("DELETE FROM CustomerTable WHERE customer_id=?");
            st.setInt(1, customer_id);
            st.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}