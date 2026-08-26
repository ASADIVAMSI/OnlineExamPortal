# OnlineExamPortal

The project is split into three folders:

- `backend`: Spring Boot Java API
- `frontend`: HTML, CSS, and JavaScript client
- `database`: MySQL scripts

## How the project works

1. A user creates an account from `frontend/index.html`.
2. The frontend sends the form data to the Spring Boot API in `backend`.
3. The backend stores users, question sets, assignments, submissions, and answers in MySQL.
4. An admin approves users and assigns a question set.
5. An approved user answers the assigned questions and submits the assessment.
6. The admin can review the saved submission and answer results.

The main Java package is `com.kspiders.app.onlineexamportal`. The Maven `groupId` in
`backend/pom.xml` uses the same organization name.

## Run the backend

1. Install Java 17 and Maven.
2. Create the MySQL database and tables by running `database/schema.sql` in MySQL Workbench or the MySQL command line.
3. The current backend configuration connects to `online_exam` with the credentials in `backend/src/main/resources/application.properties`.
4. Update the datasource URL, username, and password in that properties file when your local MySQL settings differ.
5. From the project root, run `mvn -f backend/pom.xml spring-boot:run`.

The API runs at `http://localhost:8080`.

## Easy Windows start and stop

From the project folder, double-click `start.bat` to start the Java application. Open `http://localhost:8080/index.html` in your browser.

Double-click `stop.bat` to stop the application on port `8080`. The start script will not launch a second copy if the application is already running.

## Run the frontend

The Spring Boot backend serves the separate `frontend` folder, so no Python or Node server is required. Open `http://localhost:8080/index.html` after starting the backend.

Available endpoints:

- `POST /api/auth/signup`
- `POST /api/auth/signin`

New users receive `PENDING` approval status. Admin approval and assessment access will be added in the next feature slice.
