import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private Connection connection;

    public NotificationDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Get Unread Notifications for a User
    public List<String> getUnreadNotifications(int userId) {
        List<String> notifications = new ArrayList<>();
        String sql = "SELECT notification_id, message FROM Notifications WHERE user_id = ? AND is_read = FALSE";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String message = resultSet.getInt("notification_id") + ": " + resultSet.getString("message");
                notifications.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    // ✅ Clear All Notifications for a User
    public boolean clearNotifications(int userId) {
        String sql = "DELETE FROM Notifications WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
