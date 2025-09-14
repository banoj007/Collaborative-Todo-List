package todoapp;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TaskManager {
    private List<Task> tasks;
    private List<User> users;
    private List<Category> categories;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final String DATA_FILE = "tasks.dat";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public TaskManager() {
        tasks = new ArrayList<>();
        users = new ArrayList<>();
        categories = new ArrayList<>();
        loadData();
    }

    public void addUser(String username) {
        lock.writeLock().lock();
        try {
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    System.out.println("User already exists!");
                    return;
                }
            }
            users.add(new User(username));
            System.out.println("User added successfully!");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public User findUser(String username) {
        lock.readLock().lock();
        try {
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    return user;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addCategory(String name) {
        lock.writeLock().lock();
        try {
            for (Category category : categories) {
                if (category.getName().equals(name)) {
                    System.out.println("Category already exists!");
                    return;
                }
            }
            categories.add(new Category(name));
            System.out.println("Category added successfully!");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Category findCategory(String name) {
        lock.readLock().lock();
        try {
            for (Category category : categories) {
                if (category.getName().equals(name)) {
                    return category;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addTask(String title, String description, String dueDateStr, String categoryName, String assignedUsername) {
        lock.writeLock().lock();
        try {
            Date dueDate = null;
            try {
                dueDate = dateFormat.parse(dueDateStr);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd");
                return;
            }

            Category category = findCategory(categoryName);
            if (category == null) {
                System.out.println("Category not found!");
                return;
            }

            Task task = new Task(title, description, dueDate, category);
            if (assignedUsername != null && !assignedUsername.isEmpty()) {
                User user = findUser(assignedUsername);
                if (user == null) {
                    System.out.println("User not found!");
                    return;
                }
                task.setAssignedUser(user);
                user.assignTask(task);
            }

            tasks.add(task);
            category.addTask(task);
            System.out.println("Task added successfully!");
            saveData();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void markTaskAsComplete(int taskId) {
        lock.writeLock().lock();
        try {
            Task task = findTaskById(taskId);
            if (task != null) {
                task.setCompleted(true);
                System.out.println("Task marked as complete!");
                saveData();
            } else {
                System.out.println("Task not found!");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteTask(int taskId) {
        lock.writeLock().lock();
        try {
            Task task = findTaskById(taskId);
            if (task != null) {
                tasks.remove(task);
                task.getCategory().removeTask(task);
                if (task.getAssignedUser() != null) {
                    task.getAssignedUser().removeTask(task);
                }
                System.out.println("Task deleted successfully!");
                saveData();
            } else {
                System.out.println("Task not found!");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Task findTaskById(int taskId) {
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                return task;
            }
        }
        return null;
    }

    public List<Task> getAllTasks() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(tasks);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Task> getTasksByUser(String username) {
        lock.readLock().lock();
        try {
            User user = findUser(username);
            if (user != null) {
                return new ArrayList<>(user.getAssignedTasks());
            }
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Task> getTasksByCategory(String categoryName) {
        lock.readLock().lock();
        try {
            Category category = findCategory(categoryName);
            if (category != null) {
                return new ArrayList<>(category.getTasks());
            }
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<User> getAllUsers() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(users);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Category> getAllCategories() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(categories);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks);
            oos.writeObject(categories);
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No existing data found. Starting fresh.");
            initializeDefaultData();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            tasks = (List<Task>) ois.readObject();
            categories = (List<Category>) ois.readObject();
            
            // Reconstruct users from tasks
            Set<String> usernames = new HashSet<>();
            for (Task task : tasks) {
                if (task.getAssignedUser() != null) {
                    usernames.add(task.getAssignedUser().getUsername());
                }
            }
            
            for (String username : usernames) {
                users.add(new User(username));
            }
            
            System.out.println("Data loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
            initializeDefaultData();
        }
    }
    
    private void initializeDefaultData() {
        // Add default categories
        categories.add(new Category("Work"));
        categories.add(new Category("Personal"));
        categories.add(new Category("Study"));
        
        // Add default users
        users.add(new User("admin"));
        users.add(new User("user1"));
        
        System.out.println("Default data initialized.");
    }
}