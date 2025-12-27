# Bookportal app

Created in order to learn Java (with Spring Boot and Postgre database). Using React for frontend

## App description

The goal is to create a portal where users can register and post book recommendations.

- Each user can post, edit and delete their book recommendations
- Each user will see a list of all the book recommendations
- Admins will be able to delete other users and posts

## Technical aspects

### UX/UI
I'm using very basic ux and ui strategies. No error handling in frontend actions (i leave the backend message as it is) and no role restrictions in frontend (some actions will produce "not allowed" response from backend).

### Security
jwt authentication with two authorization levels: user and admin.

Refresh token is implementend with a guard in frontend so it redirects to login when no token is found.

### Backend
Java with Spring. Swagger added (http://localhost:8080/swagger-ui). Book Controller is the one with annotations in swagger and using declarative security.

### Database access
Using Hibernate/JPA. QueryDSL can be used to mimic the LINQ deferred query representation used in C# to prevent N+1 issues. 

## Run the app
A makefile was created to run the app in local. 
- run the docker container app in development: 
```
make up  
```
- build the docker container app: 
```
make prod  
```
The app can be run in local without containers, starting the backend in localhost:8080 and the frontend in localhost:3000

Generate folder structure json with this command (Linux):
```
tree -J -I 'node_modules|.next|.git' > project-structure.json
```
