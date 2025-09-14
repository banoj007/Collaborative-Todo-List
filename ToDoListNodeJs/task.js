// task.js
class Task {
    constructor(id, title, description, dueDate, category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = false;
        this.dueDate = dueDate;
        this.category = category;
        this.assignedUser = null;
    }

    assignTo(user) {
        this.assignedUser = user;
    }

    markComplete() {
        this.completed = true;
    }

    toString() {
        return `Task #${this.id}: ${this.title} (${this.completed ? 'Completed' : 'Pending'})` +
               ` - Category: ${this.category}` +
               ` - Assigned to: ${this.assignedUser || 'Unassigned'}`;
    }
}

module.exports = Task;