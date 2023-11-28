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
  "password":"#Password",
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

### Authorization by role
The user you're about to login has rights to perform some changes in the data, below you can find the explanation of each.
This authorization by role is performed with an endpoint pre-authorization check.

|       | POST | GET | PATCH | DELETE | 
|-------|------|-----|-------|--------|
| USER  |      | X   |       |        |
| ADMIN |      | X   | X     |        |     
| ROOT  | X    | X   | X     | X      |

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
- **Status Code**: 200 OK

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

With pagination - `GET /api/applications?size=1`:
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

#### `GET /api/applications/:applicationId`  -- READ
Allows to obtain information of only one application.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.

##### Response
- **Status Code**: 200 OK
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
- **Status Code**: 200 OK

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

With pagination - `GET /api/permissions?size=1`:
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

#### `GET /api/permissions/:permissionId`  -- READ
Allows to obtain information of only one permission.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.

##### Response
- **Status Code**: 200 OK
```json
{
  "id": 3,
  "name": "User",
  "description": "Read only access",
  "keycode": "USER"
}
```

### Profiles
#### `GET /api/profiles`  -- READ
Allows to obtain information of all profiles. This endpoint can have queries, so as respond with pagination if needed.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.
- **Queries available**:
  - name: `/api/profiles?name=Root`
  - size: `/api/profiles?size=1`
  - page: `/api/profiles?page=0`
  - sort: `/api/profiles?sort=id,asc`

##### Response
- **Status Code**: 200 OK

Without pagination:
```json
{
    "data": [
        {
            "id": 1,
            "name": "Developer",
            "description": "Software Developer",
            "keycode": "DEV1"
        },
        {
            "id": 2,
            "name": "Tester",
            "description": "Software Tester",
            "keycode": "TEST1"
        },
        {
            "id": 3,
            "name": "Designer",
            "description": "UX/UI Designer",
            "keycode": "DES1"
        },
        {
            "id": 4,
            "name": "Project Manager",
            "description": "Project Manager",
            "keycode": "PM1"
        },
        {
            "id": 5,
            "name": "Developer",
            "description": "Software Developer",
            "keycode": "DEV2"
        },
        {
            "id": 6,
            "name": "Tester",
            "description": "Software Tester",
            "keycode": "TEST2"
        },
        {
            "id": 7,
            "name": "Designer",
            "description": "UX/UI Designer",
            "keycode": "DES2"
        },
        {
            "id": 8,
            "name": "Project Manager",
            "description": "Project Manager",
            "keycode": "PM2"
        },
        {
            "id": 9,
            "name": "Account Manager",
            "description": "Account Manager",
            "keycode": "AM1"
        },
        {
            "id": 10,
            "name": "Account Manager",
            "description": "Account Manager",
            "keycode": "AM2"
        }
    ]
}
```

With pagination - `GET /api/users?size=1`:
```json
{
    "totalItems": 10,
    "data": [
        {
            "id": 1,
            "name": "Developer",
            "description": "Software Developer",
            "keycode": "DEV1"
        }
    ],
    "totalPages": 10,
    "currentPage": 0
}
```

#### `GET /api/profiles/:profileId` -- READ
Allows to obtain information of only one profile.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.

##### Response
- **Status Code**: 200 OK
```json
{
  "id": 10,
  "name": "Account Manager",
  "description": "Account Manager",
  "keycode": "AM2"
}
```

### Users
#### `GET /api/users`  -- READ
Allows to obtain information of all users. This endpoint can have queries, so as respond with pagination if needed.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.
- **Queries available**:
  - name: `/api/users?name=Root`
  - size: `/api/users?size=1`
  - page: `/api/users?page=0`
  - sort: `/api/users?sort=id,asc`

##### Response
- **Status Code**: 200 OK

Without pagination:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Eli Bernal",
      "email": "ebernal@bwl.com.mx",
      "username": "ebernal"
    },
    {
      "id": 2,
      "name": "Walter Bernal",
      "email": "wbernal@bwl.com.mx",
      "username": "cantseeme2"
    },
    {
      "id": 3,
      "name": "John Spartan",
      "email": "emontero@bwl.com.mx",
      "username": "john117"
    },
    {
      "id": 4,
      "name": "Walter Bernal",
      "email": "wbernal@bwl.com.mx",
      "username": "wbernal"
    }
  ]
}
```

With pagination - `GET /api/users?size=1`:
```json
{
  "totalItems": 4,
  "data": [
    {
      "id": 1,
      "name": "Eli Bernal",
      "email": "ebernal@bwl.com.mx",
      "username": "ebernal"
    }
  ],
  "totalPages": 4,
  "currentPage": 0
}
```

#### `GET /api/users/:userId`  -- READ
Allows to obtain information of only one user.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.

##### Response
- **Status Code**: 200 OK
```json
{
  "id": 2,
  "name": "Walter Bernal",
  "email": "wbernal@bwl.com.mx",
  "username": "cantseeme2"
}
```

#### `POST /api/users` -- CREATE
Allows to create one user.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.
```json
{
    "email":"test@bwl.com.mx",
    "name":"test user",
    "username":"testuser",
    "profileId": 1,
    "applicationId": 1,
    "password":"#Password"
}
```

##### Response
- **Status Code**: 201 Created
```json
{
    "data": {
        "idProfile": 5,
        "idApplication": 2,
        "user": {
            "id": 5,
            "name": "delete user",
            "email": "delete@bwl.com.mx",
            "username": "cantsefgerbeteme"
        }
    }
}
```

### `PATCH /api/users/:userId` -- UPDATE
Allows the update and modification of a user.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.
```json
{
    "username": "cantseeme2"
}
```

##### Response
- **Status Code**: 200 OK
```json
{
  "data": {
    "user": {
      "id": 2,
      "name": "Walter Bernal",
      "email": "wbernal@bwl.com.mx",
      "username": "cantseeme2"
    }
  }
}
```

### `DELETE /api/users/:userId` -- DELETE
Allows the deletion of a user.
##### Request
- **Authorization Header**: Include an `Authorization` header with a valid Authentication Bearer Token (provided at login) to authenticate the request.
##### Response
- **Status Code**: 204 No Content

### Clients
#### `GET /api/clients` -- READ
Allows to obtain information of all clients. This endpoint can have queries, so as respond with pagination if needed.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.
- **Queries available**:
  - name: `/api/users?name=Root`
  - size: `/api/users?size=1`
  - page: `/api/users?page=0`
  - sort: `/api/users?sort=id,asc`
##### Response
- **Status Code**: 200 OK

Without pagination:
```json
{
    "data": [
        {
            "id": 1,
            "name": "General Motors",
            "shortName": "GM",
            "enabled": true
        },
        {
            "id": 2,
            "name": "Mercedes Benz GmbH",
            "shortName": "MB",
            "enabled": true
        },
        {
            "id": 3,
            "name": "Volkswagen Group",
            "shortName": "VW",
            "enabled": true
        }
    ]
}
```

With pagination - `GET /api/clients?size=1`:
```json
{
    "totalItems": 3,
    "data": [
        {
            "id": 1,
            "name": "General Motors",
            "shortName": "GM",
            "enabled": true
        }
    ],
    "totalPages": 3,
    "currentPage": 0
}
```

#### `GET /api/clients/:clientId` -- READ
Allows to obtain information of only one client.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.

##### Response
- **Status Code**: 200 OK
```json
{
  "id": 3,
  "name": "Volkswagen Group",
  "shortName": "VW",
  "enabled": true
}
```

#### `POST /api/clients` -- CREATE
Allows to create one user.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.
```json
{
  "name":"Honda Automotive Group Japan",
  "shortName": "HONDA"
}
```

##### Response
- **Status Code**: 201 Created
```json
{
  "data": {
    "client": {
      "id": 4, 
      "name": "Honda Automotive Group Japan",
      "shortName": "HONDA", 
      "enabled": true
    }
  }
}
```

#### `PATCH /api/clients/:clientId` -- UPDATE
Allows the update and modification of a user.
##### Request
- **Authorization Header**: Include an `Authorization` header with valid Authentication Bearer Token (provided at login) to authenticate the request.
```json
{
  "name": "Mazda Automotive",
  "shortName": "MAZDA",
  "enabled": true
}
```

##### Response
- **Status Code**: 200 OK
```json
{
    "data": {
        "client": {
            "id": 6,
            "name": "Mazda Automotive",
            "shortName": "MAZDA",
            "enabled": true
        }
    }
}
```

#### `DELETE /api/clients/:clientId` -- DELETE
Allows the deletion of a client (only allowed if not related with any project).
##### Request
- **Authorization Header**: Include an `Authorization` header with a valid Authentication Bearer Token (provided at login) to authenticate the request.
##### Response
- **Status Code**: 204 No Content