// app.js
const readline = require('readline');
const TaskManager = require('./taskManager');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

const taskManager = new TaskManager();
let currentUser = null;

// Handle events
taskManager.on('taskAdded', (task) => {
    console.log(`Event: New task added - ${task.title}`);
});

taskManager.on('taskCompleted', (task) => {
    console.log(`Event: Task completed - ${task.title}`);
});

taskManager.on('taskDeleted', (task) => {
    console.log(`Event: Task deleted - ${task.title}`);
});

function askQuestion(query) {
    return new Promise((resolve) => {
        rl.question(query, (answer) => resolve(answer.trim()));
    });
}

async function login() {
    try {
        const users = await taskManager.getAllUsers();
        if (users.length === 0) {
            console.log("No users found. Creating default user 'admin'");
            await taskManager.addUser('admin');
            currentUser = 'admin';
            return;
        }
        
        console.log("Available users:");
        users.forEach(user => {
            console.log(`- ${user}`);
        });
        
        while (true) {
            const username = await askQuestion("Enter username to login (or 'new' to create new user): ");
            
            if (username.toLowerCase() === 'new') {
                const newUsername = await askQuestion("Enter new username: ");
                try {
                    await taskManager.addUser(newUsername);
                    currentUser = newUsername;
                    break;
                } catch (error) {
                    console.log(error.message);
                }
            } else {
                if (users.includes(username)) {
                    currentUser = username;
                    console.log(`Logged in as ${username}`);
                    break;
                } else {
                    console.log("User not found. Please try again.");
                }
            }
        }
    } catch (error) {
        console.log(`Error during login: ${error.message}`);
    }
}

function displayMenu() {
    console.log(`\n=== Menu (Logged in as: ${currentUser}) ===`);
    console.log("1. View All Tasks");
    console.log("2. View My Tasks");
    console.log("3. Add New Task");
    console.log("4. Mark Task as Complete");
    console.log("5. Delete Task");
    console.log("6. Add New Category");
    console.log("7. Add New User");
    console.log("8. Switch User");
    console.log("9. Exit");
}

async function viewAllTasks() {
    try {
        const tasks = await taskManager.getAllTasks();
        if (tasks.length === 0) {
            console.log("No tasks found.");
        } else {
            console.log("\n=== All Tasks ===");
            tasks.forEach(task => {
                console.log(`Task #${task.id}: ${task.title} (${task.completed ? 'Completed' : 'Pending'})` +
                         ` - Category: ${task.category}` +
                         ` - Assigned to: ${task.assignedUser || 'Unassigned'}`);
            });
        }
    } catch (error) {
        console.log(`Error: ${error.message}`);
    }
}

async function viewMyTasks() {
    try {
        const tasks = await taskManager.getTasksByUser(currentUser);
        if (tasks.length === 0) {
            console.log("You have no assigned tasks.");
        } else {
            console.log("\n=== My Tasks ===");
            tasks.forEach(task => {
                console.log(`Task #${task.id}: ${task.title} (${task.completed ? 'Completed' : 'Pending'})` +
                         ` - Category: ${task.category}`);
            });
        }
    } catch (error) {
        console.log(`Error: ${error.message}`);
    }
}

async function addTask() {
    try {
        console.log("\n=== Add New Task ===");
        
        const title = await askQuestion("Enter task title: ");
        const description = await askQuestion("Enter task description: ");
        const dueDate = await askQuestion("Enter due date (yyyy-MM-dd): ");
        
        // Show available categories
        const categories = await taskManager.getAllCategories();
        console.log("Available categories:");
        categories.forEach(category => {
            console.log(`- ${category}`);
        });
        const categoryName = await askQuestion("Enter category name: ");
        
        // Show available users
        const users = await taskManager.getAllUsers();
        console.log("Available users:");
        users.forEach(user => {
            console.log(`- ${user}`);
        });
        const assignedUser = await askQuestion("Assign to user (leave empty for no assignment): ");
        
        await taskManager.addTask(title, description, dueDate, categoryName, assignedUser || null);
        console.log("Task added successfully!");
    } catch (error) {
        console.log(`Error: ${error.message}`);
    }
}

async function markTaskComplete() {
    try {
        console.log("\n=== Mark Task as Complete ===");
        const taskId = parseInt(await askQuestion("Enter task ID: "), 10);
        await taskManager.markTaskAsComplete(taskId);
        console.log("Task marked as complete!");
    } catch (error) {
        console.log(`Error: ${error.message}`);
    }
}

async function deleteTask() {
    try {
        console.log("\n=== Delete Task ===");
        const taskId = parseInt(await askQuestion("Enter task ID: "), 10);
        await taskManager.deleteTask(taskId);
        console.log("Task deleted successfully!");
    } catch (error) {
        console.log(`Error: ${error.message}`);
    }
}

async function addCategory() {
    try {
        console.log("\n=== Add New Category ===");
        const name = await askQuestion("Enter category name: ");
        await taskManager.addCategory(name);
        console.log("Category added successfully!");
    } catch (error) {
        console.log(`Error: ${error.message}`);
    }
}

async function addUser() {
    try {
        console.log("\n=== Add New User ===");
        const username = await askQuestion("Enter username: ");
        await taskManager.addUser(username);
        console.log("User added successfully!");
    } catch (error) {
        console.log(`Error: ${error.message}`);
    }
}

async function main() {
    console.log("=== Collaborative To-Do List Application ===");
    await taskManager.init();
    await login();
    
    let running = true;
    while (running) {
        displayMenu();
        const choice = parseInt(await askQuestion("Enter your choice: "), 10);
        
        switch (choice) {
            case 1:
                await viewAllTasks();
                break;
            case 2:
                await viewMyTasks();
                break;
            case 3:
                await addTask();
                break;
            case 4:
                await markTaskComplete();
                break;
            case 5:
                await deleteTask();
                break;
            case 6:
                await addCategory();
                break;
            case 7:
                await addUser();
                break;
            case 8:
                await login();
                break;
            case 9:
                running = false;
                console.log("Goodbye!");
                rl.close();
                break;
            default:
                console.log("Invalid choice. Please try again.");
        }
    }
}

main().catch(error => {
    console.error(`Fatal error: ${error.message}`);
    rl.close();
});