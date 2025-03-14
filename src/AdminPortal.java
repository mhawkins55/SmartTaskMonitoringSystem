import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AdminPortal {
    private JFrame frame;
    private AdminDAO adminDAO;
    private UserDAO userDAO;

    public AdminPortal(AdminDAO adminDAO, UserDAO userDAO) {
        this.adminDAO = adminDAO;
        this.userDAO = userDAO;

        frame = new JFrame("Admin Portal");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(4, 1));

        JButton removeUserButton = new JButton("Remove User");
        JButton clearTaskButton = new JButton("Clear Task");
        JButton clearAllTasksButton = new JButton("Clear All Tasks");
        JButton backButton = new JButton("Return to Main Menu");

        frame.add(removeUserButton);
        frame.add(clearTaskButton);
        frame.add(clearAllTasksButton);
        frame.add(backButton);

        removeUserButton.addActionListener(e -> removeUser());
        clearTaskButton.addActionListener(e -> clearTask());
        clearAllTasksButton.addActionListener(e -> clearAllTasks());
        backButton.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void removeUser() {
        List<User> users = userDAO.getAllUsers();
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No users available.");
            return;
        }

        StringBuilder userList = new StringBuilder("Select a User ID to remove:\n");
        for (User user : users) {
            userList.append(user.getUserId()).append(": ").append(user.getUsername()).append(" (").append(user.getRole()).append(")\n");
        }

        String input = JOptionPane.showInputDialog(frame, userList.toString());
        if (input != null && !input.trim().isEmpty()) {
            int userId = Integer.parseInt(input);
            if (adminDAO.removeUser(userId)) {
                JOptionPane.showMessageDialog(frame, "User removed successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to remove user.");
            }
        }
    }

    private void clearTask() {
        String taskId = JOptionPane.showInputDialog(frame, "Enter Task ID to clear:");
        if (taskId != null && !taskId.trim().isEmpty()) {
            if (adminDAO.clearTask(Integer.parseInt(taskId))) {
                JOptionPane.showMessageDialog(frame, "Task cleared successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to clear task.");
            }
        }
    }

    private void clearAllTasks() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear ALL tasks?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (adminDAO.clearAllTasks()) {
                JOptionPane.showMessageDialog(frame, "All tasks cleared successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to clear all tasks.");
            }
        }
    }
}
