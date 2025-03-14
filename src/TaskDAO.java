import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private Connection connection;

    public TaskDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Create a Task + Send Notification
    public boolean createTask(String title, String description, int assignedTo, int createdBy, String priority, String dueDate) {
        String sql = "INSERT INTO Tasks (title, description, assigned_to, created_by, priority, status, due_date) VALUES (?, ?, ?, ?, ?, 'Pending', ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, assignedTo);
            stmt.setInt(4, createdBy);
            stmt.setString(5, priority);
            stmt.setString(6, dueDate);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int taskId = generatedKeys.getInt(1);
                    sendNotification(assignedTo, "You have been assigned a new task: " + title + " - " + description);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Send Notification
    private void sendNotification(int userId, String message) {
        String sql = "INSERT INTO Notifications (user_id, message, is_read) VALUES (?, ?, FALSE)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Get Only Pending Tasks Assigned to a User & Show Assigned By
    public List<Task> getUserTasks(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT t.*, u.username AS assigner_name FROM Tasks t LEFT JOIN Users u ON t.created_by = u.user_id WHERE t.assigned_to = ? AND t.status != 'Completed'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String assignerName = resultSet.getInt("created_by") == userId ? "Assigned by Me" : "Assigned by " + resultSet.getString("assigner_name");

                Task task = new Task(
                        resultSet.getInt("task_id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getInt("assigned_to"),
                        resultSet.getInt("created_by"),
                        resultSet.getString("priority"),
                        resultSet.getString("status"),
                        resultSet.getDate("due_date"),
                        assignerName
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // ✅ Mark Task as Completed & Notify Assigner
    public boolean completeTask(int taskId) {
        String getTaskSql = "SELECT created_by, title, description FROM Tasks WHERE task_id = ?";
        String updateTaskSql = "UPDATE Tasks SET status = 'Completed' WHERE task_id = ?";
        try (PreparedStatement getTaskStmt = connection.prepareStatement(getTaskSql)) {
            getTaskStmt.setInt(1, taskId);
            ResultSet resultSet = getTaskStmt.executeQuery();

            if (resultSet.next()) {
                int assignerId = resultSet.getInt("created_by");
                String taskTitle = resultSet.getString("title");
                String taskDescription = resultSet.getString("description");

                // Mark task as completed
                try (PreparedStatement updateTaskStmt = connection.prepareStatement(updateTaskSql)) {
                    updateTaskStmt.setInt(1, taskId);
                    int rowsUpdated = updateTaskStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        sendNotification(assignerId, "Task Completed: " + taskTitle + " - " + taskDescription);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
