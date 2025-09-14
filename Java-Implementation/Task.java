package todoapp;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private static int nextId = 1;
    private int id;
    private String title;
    private String description;
    private boolean completed;
    private Date dueDate;
    private User assignedUser;
    private Category category;

    public Task(String title, String description, Date dueDate, Category category) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.completed = false;
        this.dueDate = dueDate;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Task #" + id + ": " + title + " (" + (completed ? "Completed" : "Pending") + ")" +
                " - Category: " + category.getName() +
                " - Assigned to: " + (assignedUser != null ? assignedUser.getUsername() : "Unassigned");
    }
}