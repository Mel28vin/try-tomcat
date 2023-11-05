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

    public static String getAllItems() {
        List<Item> allItems = new ArrayList<>();

        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement st = con.prepareStatement("Select * from ItemTable");
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                long itemId = rs.getLong("item_id");
                String name = rs.getString("name");
                double rate = rs.getDouble("rate");
                String description = rs.getString("description");
                int status = rs.getInt("status");
                Item temp = new Item(itemId, name, status, rate, description);
                allItems.add(temp);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Type type = Types.
                newParameterizedType(List.class, Item.class);
        JsonAdapter<List<Item>> jsonAdapter = moshi.adapter(type);

        return jsonAdapter.toJson(allItems);
    }

    public static String getItem(long itemId) {
        Item temp = null;
        try (Connection con = DBConnectionManager.getConnection()) {

            PreparedStatement st = con.prepareStatement("Select * from ItemTable where item_id=?");
            st.setLong(1, itemId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double rate = rs.getDouble("rate");
                String description = rs.getString("description");
                int status = rs.getInt("status");
                temp = new Item(itemId, name, status, rate, description);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        JsonAdapter<Item> jsonAdapter = moshi.adapter(Item.class).indent("  ");
        return jsonAdapter.toJson(temp);
    }

    public static boolean addItem(String jsonString) {
        Type type = Types.newParameterizedType(Map.class, String.class, String.class);
        JsonAdapter<Map<String, String>> jsonAdapter = moshi.adapter(type);

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("Insert into ItemTable (name, rate, description) VALUES (?, ?, ?)");
            Map<String, String> itemData = jsonAdapter.fromJson(jsonString);
            assert itemData != null;
            if (itemData.containsKey("name") &&
                    itemData.containsKey("rate") &&
                    itemData.containsKey("description")) {
                st.setString(1, itemData.get("name"));
                st.setString(2, itemData.get("rate"));
                st.setString(3, itemData.get("description"));
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

    public static boolean setStatus(long itemId, int status) {
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement st = con.prepareStatement("Update ItemTable Set status = ? where item_id = ?")) {
            st.setInt(1, status);
            st.setLong(2, itemId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean updateItem(long itemId, String jsonString) {
        Type type = Types.newParameterizedType(Map.class, String.class, String.class);
        JsonAdapter<Map<String, String>> jsonAdapter = moshi.adapter(type);

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("UPDATE ItemTable SET name = COALESCE(?, name), rate = COALESCE(?, rate), description = COALESCE(?, description) WHERE item_id = ?");
            Map<String, String> itemData = jsonAdapter.fromJson(jsonString);

            if (itemData != null) {
                st.setString(1, itemData.getOrDefault("name", null));
                st.setString(2, itemData.getOrDefault("rate", null));
                st.setString(3, itemData.getOrDefault("description", null));
                st.setLong(4, itemId);

                int rowsUpdated = st.executeUpdate();

                return rowsUpdated > 0;
            } else {
                return false;
            }

        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeItem(long itemId) {

        try (Connection con = DBConnectionManager.getConnection()) {
            PreparedStatement st = con.prepareStatement("DELETE FROM ItemTable WHERE item_id=?");
            st.setLong(1, itemId);
            st.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}