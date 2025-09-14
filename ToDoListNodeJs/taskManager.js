// taskManager.js
const fs = require('fs').promises;
const path = require('path');
const Task = require('./task');
const EventEmitter = require('events');

const DATA_FILE = path.join(__dirname, 'tasks.json');

class TaskManager extends EventEmitter {
    constructor() {
        super();
        this.tasks = [];
        this.users = [];
        this.categories = [];
        this.nextTaskId = 1;
    }

    async init() {
        try {
            await this.loadData();
            console.log('Data loaded successfully!');
        } catch (error) {
            console.log('No existing data found or error loading data. Starting fresh.');
            this.initializeDefaultData();
        }
    }

    initializeDefaultData() {
        // Add default categories
        this.categories = ['Work', 'Personal', 'Study'];
        
        // Add default users
        this.users = ['admin', 'user1'];
        
        this.tasks = [];
        this.nextTaskId = 1;
        
        console.log('Default data initialized.');
    }

    async loadData() {
        const data = await fs.readFile(DATA_FILE, 'utf8');
        const parsedData = JSON.parse(data);
        
        this.tasks = parsedData.tasks;
        this.users = parsedData.users;
        this.categories = parsedData.categories;
        this.nextTaskId = parsedData.nextTaskId;
    }

    async saveData() {
        const data = {
            tasks: this.tasks,
            users: this.users,
            categories: this.categories,
            nextTaskId: this.nextTaskId
        };
        
        await fs.writeFile(DATA_FILE, JSON.stringify(data, null, 2), 'utf8');
        console.log('Data saved successfully!');
    }

    async addUser(username) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (this.users.includes(username)) {
                    reject(new Error('User already exists!'));
                    return;
                }
                
                this.users.push(username);
                this.saveData().then(() => {
                    this.emit('userAdded', username);
                    resolve(username);
                }).catch(reject);
            }, 100); // Simulate some async operation
        });
    }

    async addCategory(name) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (this.categories.includes(name)) {
                    reject(new Error('Category already exists!'));
                    return;
                }
                
                this.categories.push(name);
                this.saveData().then(() => {
                    this.emit('categoryAdded', name);
                    resolve(name);
                }).catch(reject);
            }, 100); // Simulate some async operation
        });
    }

    async addTask(title, description, dueDate, category, assignedUser = null) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (!this.categories.includes(category)) {
                    reject(new Error('Category not found!'));
                    return;
                }
                
                if (assignedUser && !this.users.includes(assignedUser)) {
                    reject(new Error('User not found!'));
                    return;
                }
                
                const task = {
                    id: this.nextTaskId++,
                    title,
                    description,
                    completed: false,
                    dueDate,
                    category,
                    assignedUser
                };
                
                this.tasks.push(task);
                this.saveData().then(() => {
                    this.emit('taskAdded', task);
                    resolve(task);
                }).catch(reject);
            }, 200); // Simulate some async operation
        });
    }

    async markTaskAsComplete(taskId) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                const task = this.tasks.find(t => t.id === taskId);
                if (!task) {
                    reject(new Error('Task not found!'));
                    return;
                }
                
                task.completed = true;
                this.saveData().then(() => {
                    this.emit('taskCompleted', task);
                    resolve(task);
                }).catch(reject);
            }, 150); // Simulate some async operation
        });
    }

    async deleteTask(taskId) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                const taskIndex = this.tasks.findIndex(t => t.id === taskId);
                if (taskIndex === -1) {
                    reject(new Error('Task not found!'));
                    return;
                }
                
                const task = this.tasks[taskIndex];
                this.tasks.splice(taskIndex, 1);
                this.saveData().then(() => {
                    this.emit('taskDeleted', task);
                    resolve(task);
                }).catch(reject);
            }, 150); // Simulate some async operation
        });
    }

    async getAllTasks() {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve([...this.tasks]);
            }, 100);
        });
    }

    async getTasksByUser(username) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (!this.users.includes(username)) {
                    reject(new Error('User not found!'));
                    return;
                }
                
                const userTasks = this.tasks.filter(task => task.assignedUser === username);
                resolve(userTasks);
            }, 100);
        });
    }

    async getTasksByCategory(categoryName) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (!this.categories.includes(categoryName)) {
                    reject(new Error('Category not found!'));
                    return;
                }
                
                const categoryTasks = this.tasks.filter(task => task.category === categoryName);
                resolve(categoryTasks);
            }, 100);
        });
    }

    async getAllUsers() {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve([...this.users]);
            }, 50);
        });
    }

    async getAllCategories() {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve([...this.categories]);
            }, 50);
        });
    }
}

module.exports = TaskManager;