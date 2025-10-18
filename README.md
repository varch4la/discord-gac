# Discord Global ACtivity
This project allows you to share your Discord activity using a REST endpoint.

## Prerequisites
- Java 21+
- Apache Maven

## Running

You can run the project using `exec-maven-plugin`.  
You can control the port the app listens on with the `PORT` environment variable.

```bash
env PORT=8080 mvn clean compile exec:java
```

## Endpoints

- `/presence/{userId}`  
   Displays information about the user's status in JSON format.
- `/presence/{userId}/card`  
   Creates an SVG card with your activity  
   <img width="378" height="173" alt="card" src="https://github.com/user-attachments/assets/b1521b8a-8117-4975-a6a0-6f9f63168641"/>  
   *An example of a status card*

## Opting in
All users have to opt in with the `/share-activity` Discord command for their activity to be visible.

## Notes
This repository is a Coding Jam competition submission.
