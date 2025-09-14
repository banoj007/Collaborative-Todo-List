package todoapp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static TaskManager taskManager = new TaskManager();
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static String currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== Collaborative To-Do List Application ===");
        login();
        
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    viewAllTasks();
                    break;
                case 2:
                    viewMyTasks();
                    break;
                case 3:
                    addTask();
                    break;
                case 4:
                    markTaskComplete();
                    break;
                case 5:
                    deleteTask();
                    break;
                case 6:
                    addCategory();
                    break;
                case 7:
                    addUser();
                    break;
                case 8:
                    switchUser();
                    break;
                case 9:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void login() {
        List<User> users = taskManager.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found. Creating default user 'admin'");
            taskManager.addUser("admin");
            currentUser = "admin";
            return;
        }
        
        System.out.println("Available users:");
        for (User user : users) {
            System.out.println("- " + user.getUsername());
        }
        
        while (true) {
            System.out.print("Enter username to login (or 'new' to create new user): ");
            String username = scanner.nextLine().trim();
            
            if (username.equalsIgnoreCase("new")) {
                System.out.print("Enter new username: ");
                String newUsername = scanner.nextLine().trim();
                taskManager.addUser(newUsername);
                currentUser = newUsername;
                break;
            } else {
                User user = taskManager.findUser(username);
                if (user != null) {
                    currentUser = username;
                    System.out.println("Logged in as " + username);
                    break;
                } else {
                    System.out.println("User not found. Please try again.");
                }
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Menu (Logged in as: " + currentUser + ") ===");
        System.out.println("1. View All Tasks");
        System.out.println("2. View My Tasks");
        System.out.println("3. Add New Task");
        System.out.println("4. Mark Task as Complete");
        System.out.println("5. Delete Task");
        System.out.println("6. Add New Category");
        System.out.println("7. Add New User");
        System.out.println("8. Switch User");
        System.out.println("9. Exit");
    }

    private static void viewAllTasks() {
        List<Task> tasks = taskManager.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            System.out.println("\n=== All Tasks ===");
            for (Task task : tasks) {
                System.out.println(task);
            }
        }
    }

    private static void viewMyTasks() {
        List<Task> tasks = taskManager.getTasksByUser(currentUser);
        if (tasks.isEmpty()) {
            System.out.println("You have no assigned tasks.");
        } else {
            System.out.println("\n=== My Tasks ===");
            for (Task task : tasks) {
                System.out.println(task);
            }
        }
    }

    private static void addTask() {
        System.out.println("\n=== Add New Task ===");
        
        System.out.print("Enter task title: ");
        String title = scanner.nextLine().trim();
        
        System.out.print("Enter task description: ");
        String description = scanner.nextLine().trim();
        
        System.out.print("Enter due date (yyyy-MM-dd): ");
        String dueDate = scanner.nextLine().trim();
        
        // Show available categories
        List<Category> categories = taskManager.getAllCategories();
        System.out.println("Available categories:");
        for (Category category : categories) {
            System.out.println("- " + category.getName());
        }
        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine().trim();
        
        // Show available users
        List<User> users = taskManager.getAllUsers();
        System.out.println("Available users:");
        for (User user : users) {
            System.out.println("- " + user.getUsername());
        }
        System.out.print("Assign to user (leave empty for no assignment): ");
        String assignedUser = scanner.nextLine().trim();
        
        taskManager.addTask(title, description, dueDate, categoryName, assignedUser);
    }

    private static void markTaskComplete() {
        System.out.println("\n=== Mark Task as Complete ===");
        int taskId = getIntInput("Enter task ID: ");
        taskManager.markTaskAsComplete(taskId);
    }

    private static void deleteTask() {
        System.out.println("\n=== Delete Task ===");
        int taskId = getIntInput("Enter task ID: ");
        taskManager.deleteTask(taskId);
    }

    private static void addCategory() {
        System.out.println("\n=== Add New Category ===");
        System.out.print("Enter category name: ");
        String name = scanner.nextLine().trim();
        taskManager.addCategory(name);
    }

    private static void addUser() {
        System.out.println("\n=== Add New User ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        taskManager.addUser(username);
    }

    private static void switchUser() {
        login();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}