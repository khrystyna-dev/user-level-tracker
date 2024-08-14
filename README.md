# USER-LEVEL-TRACKER 

![img.png](img.png)

### ⚡️ Project description
This is a web application that allows to set user results, get the top 20 user results, and get the top 20 users and 
their results at a selected level. The database is not used, all data is stored in memory. The application works 
correctly in a multithreaded environment. The program is covered by tests. Program logging is also provided. Interaction 
with the program happens through the Postman. 

### 🎯 Endpoints
The web app provides the following endpoints:
- <b>GET:</b> `/userinfo/{user_id}` - returns the top 20 user results at all levels in descending order result, 
level_id (JSON format).
- <b>GET:</b> `/levelinfo/{level_id}` - returns the top 20 users and their results at the selected level in descending 
order of result, user_id (JSON format).
- <b>PUT:</b> `/setinfo` - accepts 3 parameters in JSON format (user_id, level_id, result) sets the result.

### 🔥 Getting Started
To get started with the project follow these steps:
1. Clone the repository: git clone https://github.com/khrystyna-dev/user-level-tracker.git
2. Install Postman for sending requests.
3. Run the application.
4. Use this URL in Postman to test the app: http://localhost:8080/
5. Program execution logs are recorded in the `logs` folder at the root of the project.

