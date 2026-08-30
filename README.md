# OnlineExamPortal

The project is split into three folders:

- `backend`: Spring Boot Java API
- `frontend`: HTML, CSS, and JavaScript client
- `database`: MySQL scripts

## How the project works

1. A user creates an account from the React frontend in `frontend/src`.
2. The frontend sends API requests to the Spring Boot backend in `backend`.
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

## Run the frontend

1. Install Node.js 18+.
2. From `frontend`, run `npm install` the first time.
3. Start the dev server with `npm run dev`.
4. Open `http://localhost:5173`.

You can also build the frontend with `npm run build` and serve the generated `frontend/dist` files through the backend static resources.

## Notes

- `POST /api/auth/signup`
- `POST /api/auth/signin`

New users receive `PENDING` approval status until an administrator approves them.
