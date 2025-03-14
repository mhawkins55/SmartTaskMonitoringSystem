import java.sql.*;

public class AdminDAO {
    private Connection connection;

    public AdminDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // ✅ Remove a User by ID
    public boolean removeUser(int userId) {
        String deleteNotificationsSQL = "DELETE FROM Notifications WHERE user_id = ?";
        String deleteAssignedTasksSQL = "DELETE FROM Tasks WHERE assigned_to = ?";
        String deleteCreatedTasksSQL = "DELETE FROM Tasks WHERE created_by = ?";
        String deleteUserSQL = "DELETE FROM Users WHERE user_id = ?";

        try (PreparedStatement deleteNotificationsStmt = connection.prepareStatement(deleteNotificationsSQL);
             PreparedStatement deleteAssignedTasksStmt = connection.prepareStatement(deleteAssignedTasksSQL);
             PreparedStatement deleteCreatedTasksStmt = connection.prepareStatement(deleteCreatedTasksSQL);
             PreparedStatement deleteUserStmt = connection.prepareStatement(deleteUserSQL)) {

            // Step 1: Delete related notifications
            deleteNotificationsStmt.setInt(1, userId);
            deleteNotificationsStmt.executeUpdate();

            // Step 2: Delete all tasks assigned to this user
            deleteAssignedTasksStmt.setInt(1, userId);
            deleteAssignedTasksStmt.executeUpdate();

            // Step 3: Delete all tasks created by this user (if they were an Assignee)
            deleteCreatedTasksStmt.setInt(1, userId);
            deleteCreatedTasksStmt.executeUpdate();

            // Step 4: Delete the user
            deleteUserStmt.setInt(1, userId);
            int rowsDeleted = deleteUserStmt.executeUpdate();

            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    // ✅ Clear a Specific Task by ID
    public boolean clearTask(int taskId) {
        String sql = "DELETE FROM Tasks WHERE task_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Clear All Tasks
    public boolean clearAllTasks() {
        String sql = "DELETE FROM Tasks";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

