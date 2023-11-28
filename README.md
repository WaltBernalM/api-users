# API-USERS

## Objective
This project allows to manage the information that is shared in the applications for the control of activities inside an organization.

## Description
The API allows the connection between two applications to manage the activities related.
This two applications share information like users, clients and projects, the development of this API helps to have that information in one place and prevent duplication.
The API has authentication and authorization through several endpoints, also that includes roles of the user.

### API main endpoints:
```
/login
/logout
/api
```

## Installation
The API has built in database information, however, it is needed to have a database named bwl_users.
Then you'll need to run the ApiUsersApplication.java main class with:
```shell
$ ./mvnw spring-boot:run --quiet
```

## Access
To start working and exploring the endpoints, you can post a login with the following JSON body:

```json
{
  "username":"john117",
  "password":"#Bemw930628",
  "idApplication": 2
}
```
You will receive a response as follows, with a Json Web Token, that you'll have to send as Authorization token in the header of any further request.
```json
{
  "id": 3, 
  "username": "john117", 
  "name": "John Spartan", 
  "enabled": true, 
  "profileKeycodes": [
    "DES2",
    "AM2"
  ], 
  "profilePermissions": {
    "DES2": {
      "permissionKeyCode_3": "USER"
    }, 
    "AM2": {
      "permissionKeyCode_3": "USER", 
      "permissionKeyCode_2": "ADMIN",
      "permissionKeyCode_1": "ROOT"
    }
    },
    "token": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huMTE3IiwiZXhwIjoxNzAxMjAwNTYxLCJpZEFwcGxpY2F0aW9uIjoyLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIiwiUk9MRV9ST09UIiwiUk9MRV9JREFQUDIiXX0.yeR6txxJdm8jz2Jq-7iRVE1ljkGGZDq5BbGwH-u1-UU",
    "idApplication": 2
}
```

## API Endpoints
### Applications
#### `GET /api/applications`  -- READ
Allows to obtain information of all applications. This endpoint can have queries, so as respond with pagination if needed.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.
- **Queries available**:
  - name: `/api/applications?name=App 2`
  - size: `/api/applications?size=1`
  - page: `/api/applications?page=0`
  - sort: `/api/applications?sort=id,asc`

##### Response 
Without pagination:
```json
{
  "data": [
    {
      "id": 1,
      "name": "App 1",
      "description": "Application 1",
      "keycode": "APP1",
      "username": null,
      "shortName": null
    },
    {
      "id": 2,
      "name": "App 2",
      "description": "Application 2",
      "keycode": "APP2",
      "username": null,
      "shortName": null
    }
  ]
}
```

With pagination (size must be queried):
```json
{
  "totalItems": 2,
  "data": [
    {
      "id": 1,
      "name": "App 1",
      "description": "Application 1",
      "keycode": "APP1",
      "username": null,
      "shortName": null
    }
  ],
  "totalPages": 2,
  "currentPage": 0
}
```

#### `GET /api/applications/1`  -- READ
Allows to obtain information of only one application. This endpoint can have queries, so as respond with pagination if needed.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.

##### Response
```json
{
  "id": 2,
  "name": "App 2",
  "description": "Application 2",
  "keycode": "APP2"
}
```

### Permissions
#### `GET /api/permissions`  -- READ
Allows to obtain information of all permissions. This endpoint can have queries, so as respond with pagination if needed.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.
- **Queries available**:
  - name: `/api/permissions?name=Root`
  - size: `/api/permissions?size=1`
  - page: `/api/permissions?page=0`
  - sort: `/api/permissions?sort=id,asc`

##### Response
Without pagination:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Root",
      "description": "Full CRUD",
      "keycode": "ROOT"
    },
    {
      "id": 2,
      "name": "Administrator",
      "description": "Access to CRU",
      "keycode": "ADMIN"
    },
    {
      "id": 3,
      "name": "User",
      "description": "Read only access",
      "keycode": "USER"
    }
  ]
}
```

With pagination (size must be queried):
```json
{
  "totalItems": 3,
  "data": [
    {
      "id": 1,
      "name": "Root",
      "description": "Full CRUD",
      "keycode": "ROOT"
    }
  ],
  "totalPages": 3,
  "currentPage": 0
}
```

#### `GET /api/permissions/1`  -- READ
Allows to obtain information of only one permission. This endpoint can have queries, so as respond with pagination if needed.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.

##### Response
```json
{
  "id": 3,
  "name": "User",
  "description": "Read only access",
  "keycode": "USER"
}
```


