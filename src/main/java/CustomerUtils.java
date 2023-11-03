import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class CustomerUtils {
    public static String getAllCustomers() {
        List<Customer> allCustomers = new ArrayList<>();

        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement st = con.prepareStatement("Select * from CustomerTable");
             ResultSet rs = st.executeQuery()) {

            while(rs.next()) {
                int customer_id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int status = rs.getInt("status");
                Customer temp = new Customer(customer_id, name, email, status);
                allCustomers.add(temp);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.
                newParameterizedType(List.class, Customer.class);
        JsonAdapter<List<Customer>> jsonAdapter = moshi.adapter(type);

        return jsonAdapter.toJson(allCustomers);


    }
}