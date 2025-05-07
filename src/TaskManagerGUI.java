import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class TaskManagerGUI {
    private JFrame frame;
    private UserDAO userDAO;
    private TaskDAO taskDAO;
    private NotificationDAO notificationDAO;
    private AdminDAO adminDAO;

    public TaskManagerGUI() {

        StyleManager.applyNimbusLookAndFeel();


        userDAO = new UserDAO();
        taskDAO = new TaskDAO();
        notificationDAO = new NotificationDAO();
        adminDAO = new AdminDAO();

        frame = new JFrame("Smart Task Monitoring System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        StyleManager.styleFrameBackground(frame);
        StyleManager.styleButtons(registerButton, loginButton, exitButton);


        frame.add(registerButton);
        frame.add(loginButton);
        frame.add(exitButton);


        // Register Button Action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        // Login Button Action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });

        // Exit Button Action
        exitButton.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    private void registerUser() {
        String username = JOptionPane.showInputDialog(frame, "Enter username:");
        String password = JOptionPane.showInputDialog(frame, "Enter password:");
        String email = JOptionPane.showInputDialog(frame, "Enter email:");
        String[] roles = {"Admin", "Team Member", "Assignee"};
        String role = (String) JOptionPane.showInputDialog(frame, "Select role:", "Role Selection",
                JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);

        if (username != null && password != null && email != null && role != null) {
            int userId = userDAO.registerUser(username, password, email, role);
            if (userId != -1) {
                JOptionPane.showMessageDialog(frame, "Registration Successful! User ID: " + userId);
            } else {
                JOptionPane.showMessageDialog(frame, "Registration Failed.");
            }
        }
    }

    private void loginUser() {
        String username = JOptionPane.showInputDialog(frame, "Enter username:");
        String password = JOptionPane.showInputDialog(frame, "Enter password:");

        if (username != null && password != null) {
            User user = userDAO.authenticateUser(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful! Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
                openUserPortal(user);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Credentials.");
            }
        }
    }

    private void openUserPortal(User user) {
        if (user.getRole().equals("Admin")) {
            new AdminPortal(adminDAO, userDAO);
        } else if (user.getRole().equals("Team Member")) {
            new TeamMemberPortal(user, taskDAO, notificationDAO);
        } else if (user.getRole().equals("Assignee")) {
            new AssigneePortal(user, taskDAO, notificationDAO, userDAO);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskManagerGUI::new);
    }
}
