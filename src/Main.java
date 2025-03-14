import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) { // ✅ Keep the program running until user chooses to exit
            UserDAO userDAO = new UserDAO();
            TaskDAO taskDAO = new TaskDAO();
            NotificationDAO notificationDAO = new NotificationDAO();
            AdminDAO adminDAO = new AdminDAO();

            System.out.println("\n1. Register\n2. Login\n3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) { // Register
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();
                System.out.print("Enter email: ");
                String email = scanner.nextLine();
                System.out.print("Enter role (Admin, Team Member, Assignee): ");
                String role = scanner.nextLine();

                int userId = userDAO.registerUser(username, password, email, role);
                if (userId != -1) {
                    System.out.println("Registration successful! Your User ID is: " + userId);
                } else {
                    System.out.println("Registration failed.");
                }
            } else if (choice == 2) { // Login
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                User user = userDAO.authenticateUser(username, password);
                if (user != null) {
                    System.out.println("Login successful! Welcome, " + user.getUsername() + " (Role: " + user.getRole() + ")");

                    if (user.getRole().equals("Admin")) {
                        adminMenu(scanner, adminDAO, userDAO);
                    } else if (user.getRole().equals("Team Member")) {
                        teamMemberMenu(user, scanner, taskDAO, notificationDAO);
                    } else if (user.getRole().equals("Assignee")) {
                        assignerMenu(user, scanner, taskDAO, notificationDAO, userDAO);
                    }
                } else {
                    System.out.println("Invalid credentials.");
                }
            } else if (choice == 3) {
                System.out.println("Exiting the program. Goodbye!");
                break;
            }
        }
    }

    private static void adminMenu(Scanner scanner, AdminDAO adminDAO, UserDAO userDAO) {
        while (true) {
            System.out.println("\nAdmin Panel:");
            System.out.println("1. Remove User");
            System.out.println("2. Clear Task");
            System.out.println("3. Clear All Tasks");
            System.out.println("4. Return to Main Menu");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                // ✅ Fetch all users before allowing removal
                List<User> users = userDAO.getAllUsers();
                if (users.isEmpty()) {
                    System.out.println("No users found in the system.");
                    return;
                }

                // ✅ Display the list of users
                System.out.println("\nList of Users:");
                for (User user : users) {
                    System.out.println("User ID: " + user.getUserId() + " - " + user.getUsername() + " (" + user.getRole() + ")");
                }

                System.out.print("\nEnter User ID to remove: ");
                int userId = scanner.nextInt();
                scanner.nextLine();

                // ✅ Validate if User ID exists before attempting to delete
                boolean userExists = users.stream().anyMatch(u -> u.getUserId() == userId);
                if (!userExists) {
                    System.out.println("Error: User ID not found. Removal canceled.");
                    return;
                }

                if (adminDAO.removeUser(userId)) {
                    System.out.println("User removed successfully.");
                } else {
                    System.out.println("Failed to remove user.");
                }

            } else if (choice == 2) {
                System.out.print("Enter Task ID to clear: ");
                int taskId = scanner.nextInt();
                scanner.nextLine();

                if (adminDAO.clearTask(taskId)) {
                    System.out.println("Task cleared successfully.");
                } else {
                    System.out.println("Failed to clear task.");
                }

            } else if (choice == 3) {
                System.out.print("Are you sure you want to clear ALL tasks? (yes/no): ");
                String confirm = scanner.nextLine().toLowerCase();

                if (confirm.equals("yes")) {
                    if (adminDAO.clearAllTasks()) {
                        System.out.println("All tasks cleared successfully.");
                    } else {
                        System.out.println("Failed to clear all tasks.");
                    }
                } else {
                    System.out.println("Action canceled.");
                }

            } else if (choice == 4) {
                return; // ✅ Return to Main Menu
            }
        }
    }



    private static void teamMemberMenu(User user, Scanner scanner, TaskDAO taskDAO, NotificationDAO notificationDAO) {
        while (true) {
            System.out.println("\n1. Create Task\n2. View Tasks\n3. Complete a Task\n4. View Notifications\n5. Clear Notifications\n6. Return to Main Menu");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter task title: ");
                String title = scanner.nextLine();
                System.out.print("Enter task description: ");
                String description = scanner.nextLine();
                System.out.print("Enter priority (Low, Medium, High): ");
                String priority = scanner.nextLine();
                System.out.print("Enter due date (YYYY-MM-DD): ");
                String dueDate = scanner.nextLine();

                if (taskDAO.createTask(title, description, user.getUserId(), user.getUserId(), priority, dueDate)) {
                    System.out.println("Task created successfully!");
                } else {
                    System.out.println("Task creation failed.");
                }
            } else if (choice == 2) {
                List<Task> tasks = taskDAO.getUserTasks(user.getUserId());
                System.out.println("\nYour Tasks:");
                for (Task task : tasks) {
                    System.out.println(task.getTaskId() + ". " + task.getTitle() + " [" + task.getStatus() + "] - Due: " + task.getDueDate() + " - Assigned by: " + task.getAssignedBy());
                }
            } else if (choice == 3) { // ✅ Show assigned tasks before completing one
                List<Task> tasks = taskDAO.getUserTasks(user.getUserId());
                if (tasks.isEmpty()) {
                    System.out.println("No tasks assigned to you.");
                } else {
                    System.out.println("\nYour Assigned Tasks:");
                    for (Task task : tasks) {
                        System.out.println(task.getTaskId() + ". " + task.getTitle() + " [" + task.getStatus() + "] - Due: " + task.getDueDate() + " - Assigned by: " + task.getAssignedBy());
                    }

                    System.out.print("\nEnter Task ID to mark as completed: ");
                    int taskId = scanner.nextInt();
                    scanner.nextLine();

                    if (taskDAO.completeTask(taskId)) {
                        System.out.println("Task marked as completed!");
                    } else {
                        System.out.println("Failed to complete task.");
                    }
                }


        } else if (choice == 4) {
                List<String> notifications = notificationDAO.getUnreadNotifications(user.getUserId());
                System.out.println("\nYour Notifications:");
                for (String notification : notifications) {
                    System.out.println(notification);
                }
            } else if (choice == 5) {
                if (notificationDAO.clearNotifications(user.getUserId())) {
                    System.out.println("All notifications cleared.");
                } else {
                    System.out.println("No notifications to clear.");
                }
            } else if (choice == 6) {
                return; // ✅ Return to Main Menu
            }
        }
    }

    private static void assignerMenu(User user, Scanner scanner, TaskDAO taskDAO, NotificationDAO notificationDAO, UserDAO userDAO) {
        while (true) {
            System.out.println("\n1. Assign Task\n2. View Notifications\n3. Clear Notifications\n4. Return to Main Menu");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) { // ✅ Assign Task (Check if User ID Exists)
                List<User> teamMembers = userDAO.getTeamMembers();

                if (teamMembers.isEmpty()) {
                    System.out.println("No team members found. Cannot assign a task.");
                    continue;
                }

                System.out.println("\nSelect a Team Member to assign a task:");
                for (User member : teamMembers) {
                    System.out.println(member.getUserId() + ": " + member.getUsername());
                }

                System.out.print("\nEnter Team Member ID: ");
                int assignedTo = scanner.nextInt();
                scanner.nextLine();

                // ✅ Ensure the selected ID exists in the database
                boolean isValidUser = false;
                for (User member : teamMembers) {
                    if (member.getUserId() == assignedTo) {
                        isValidUser = true;
                        break;
                    }
                }

                if (!isValidUser) {
                    System.out.println("Error: Selected User ID does not exist. Task not assigned.");
                } else {
                    System.out.print("Enter task title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter task description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter priority (Low, Medium, High): ");
                    String priority = scanner.nextLine();
                    System.out.print("Enter due date (YYYY-MM-DD): ");
                    String dueDate = scanner.nextLine();

                    if (taskDAO.createTask(title, description, assignedTo, user.getUserId(), priority, dueDate)) {
                        System.out.println("Task assigned successfully!");
                    } else {
                        System.out.println("Task assignment failed.");
                    }
                }
            } else if (choice == 2) {
                List<String> notifications = notificationDAO.getUnreadNotifications(user.getUserId());
                System.out.println("\nYour Notifications:");
                for (String notification : notifications) {
                    System.out.println(notification);
                }
            } else if (choice == 3) {
                if (notificationDAO.clearNotifications(user.getUserId())) {
                    System.out.println("All notifications cleared.");
                } else {
                    System.out.println("No notifications to clear.");
                }
            } else if (choice == 4) {
                return; // ✅ Return to Main Menu
            }
        }
    }
}
