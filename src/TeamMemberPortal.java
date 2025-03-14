import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TeamMemberPortal {
    private JFrame frame;
    private TaskDAO taskDAO;
    private NotificationDAO notificationDAO;
    private User user;

    public TeamMemberPortal(User user, TaskDAO taskDAO, NotificationDAO notificationDAO) {
        this.user = user;
        this.taskDAO = taskDAO;
        this.notificationDAO = notificationDAO;

        frame = new JFrame("Team Member Portal");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 1)); // ✅ Increased size for extra button

        JButton createTaskButton = new JButton("Create Task");
        JButton viewTasksButton = new JButton("View Tasks");
        JButton completeTaskButton = new JButton("Complete Task");
        JButton viewNotificationsButton = new JButton("View Notifications");
        JButton clearNotificationsButton = new JButton("Clear Notifications"); // ✅ New button
        JButton backButton = new JButton("Return to Main Menu");

        frame.add(createTaskButton);
        frame.add(viewTasksButton);
        frame.add(completeTaskButton);
        frame.add(viewNotificationsButton);
        frame.add(clearNotificationsButton); // ✅ Added Clear Notifications Button
        frame.add(backButton);

        createTaskButton.addActionListener(e -> createTask());
        viewTasksButton.addActionListener(e -> viewTasks());
        completeTaskButton.addActionListener(e -> completeTask());
        viewNotificationsButton.addActionListener(e -> viewNotifications());
        clearNotificationsButton.addActionListener(e -> clearNotifications()); // ✅ Added Action
        backButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void createTask() {
        String title = JOptionPane.showInputDialog(frame, "Enter task title:");
        String description = JOptionPane.showInputDialog(frame, "Enter task description:");
        String priority = JOptionPane.showInputDialog(frame, "Enter priority (Low, Medium, High):");
        String dueDate = JOptionPane.showInputDialog(frame, "Enter due date (YYYY-MM-DD):");

        if (taskDAO.createTask(title, description, user.getUserId(), user.getUserId(), priority, dueDate)) {
            JOptionPane.showMessageDialog(frame, "Task created successfully!");
        } else {
            JOptionPane.showMessageDialog(frame, "Task creation failed.");
        }
    }

    private void viewTasks() {
        List<Task> tasks = taskDAO.getUserTasks(user.getUserId());
        StringBuilder taskList = new StringBuilder("Your Tasks:\n");
        for (Task task : tasks) {
            taskList.append(task.getTaskId()).append(". ").append(task.getTitle())
                    .append(" [").append(task.getStatus()).append("] - Due: ").append(task.getDueDate()).append("\n");
        }
        JOptionPane.showMessageDialog(frame, taskList.toString());
    }

    private void completeTask() {
        List<Task> tasks = taskDAO.getUserTasks(user.getUserId());
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No tasks assigned to you.");
            return;
        }

        StringBuilder taskList = new StringBuilder("Select a Task ID to complete:\n");
        for (Task task : tasks) {
            taskList.append(task.getTaskId()).append(": ").append(task.getTitle()).append("\n");
        }

        String taskId = JOptionPane.showInputDialog(frame, taskList.toString());
        if (taskId != null && !taskId.trim().isEmpty()) {
            if (taskDAO.completeTask(Integer.parseInt(taskId))) {
                JOptionPane.showMessageDialog(frame, "Task marked as completed!");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to complete task.");
            }
        }
    }

    private void viewNotifications() {
        List<String> notifications = notificationDAO.getUnreadNotifications(user.getUserId());
        if (notifications.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No new notifications.");
        } else {
            JOptionPane.showMessageDialog(frame, "Your Notifications:\n" + String.join("\n", notifications));
        }
    }

    private void clearNotifications() { // ✅ Added function
        if (notificationDAO.clearNotifications(user.getUserId())) {
            JOptionPane.showMessageDialog(frame, "All notifications cleared.");
        } else {
            JOptionPane.showMessageDialog(frame, "No notifications to clear.");
        }
    }
}
