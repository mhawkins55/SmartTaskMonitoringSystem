import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssigneePortal {
    private JFrame frame;
    private TaskDAO taskDAO;
    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    private User user;

    public AssigneePortal(User user, TaskDAO taskDAO, NotificationDAO notificationDAO, UserDAO userDAO) {
        this.user = user;
        this.taskDAO = taskDAO;
        this.notificationDAO = notificationDAO;
        this.userDAO = userDAO;

        frame = new JFrame("Assignee Portal");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 1));

        JButton assignTaskButton = new JButton("Assign Task");
        JButton viewNotificationsButton = new JButton("View Notifications");
        JButton clearNotificationsButton = new JButton("Clear Notifications"); // ✅ Added
        JButton backButton = new JButton("Return to Main Menu");

        StyleManager.styleFrameBackground(frame);
        StyleManager.styleButtons(assignTaskButton, viewNotificationsButton, clearNotificationsButton, backButton);

        frame.add(assignTaskButton);
        frame.add(viewNotificationsButton);
        frame.add(clearNotificationsButton); // ✅ Added button
        frame.add(backButton);

        assignTaskButton.addActionListener(e -> assignTask());
        viewNotificationsButton.addActionListener(e -> viewNotifications());
        clearNotificationsButton.addActionListener(e -> clearNotifications()); // ✅ Added action
        backButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void assignTask() {
        List<User> teamMembers = userDAO.getTeamMembers();
        StringBuilder teamList = new StringBuilder("Select a Team Member ID:\n");
        for (User member : teamMembers) {
            teamList.append(member.getUserId()).append(": ").append(member.getUsername()).append("\n");
        }

        String assignedTo = JOptionPane.showInputDialog(frame, teamList.toString());
        if (assignedTo != null && !assignedTo.trim().isEmpty()) {
            String title = JOptionPane.showInputDialog(frame, "Enter task title:");
            String description = JOptionPane.showInputDialog(frame, "Enter task description:");
            String priority = JOptionPane.showInputDialog(frame, "Enter priority (Low, Medium, High):");
            String dueDate = JOptionPane.showInputDialog(frame, "Enter due date (YYYY-MM-DD):");

            if (taskDAO.createTask(title, description, Integer.parseInt(assignedTo), user.getUserId(), priority, dueDate)) {
                JOptionPane.showMessageDialog(frame, "Task assigned successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Task assignment failed.");
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
