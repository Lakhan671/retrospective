# Project API Documentation

This document outlines the usage of the RESTful API for the "sis" project, which manages retrospectives. The API provides functionality for adding retrospectives, adding feedback to retrospectives, updating feedback, and searching for retrospectives based on specific criteria.

## Retrospectives

### Add Retrospective

- **URL:** `http://localhost:8080/api/retrospectives`
- **Method:** POST
- **Description:** Add retrospectives
- **Request:**
  - Headers:
    - `Content-Type: application/json`
  - Body:
    ```json
    {
      "name": "Sprint2",
      "summary": "Successful completion of the first sprint",
      "date": "2024-01-10",
      "participants": ["Alice", "Bob", "Charlie"]
    }
    ```

### Add Feedback to Retrospective

- **URL:** `http://localhost:8080/api/retrospectives/1/feedback`
- **Method:** POST
- **Description:** Add feedback
- **Request:**
  - Headers:
    - `Content-Type: application/json`
  - Body:
    ```json
    {
      "feedbackProvider": "Lakhan Singh",
      "body": "Great collaboration within the team",
      "feedbackType": "POSITIVE"
    }
    ```

### Update Feedback

- **URL:** `http://localhost:8080/api/retrospectives/feedback/8`
- **Method:** PUT
- **Description:** Update feedback
- **Request:**
  - Headers:
    - `Content-Type: application/json`
  - Body:
    - `body: Improved communication among team members`
    - `feedbackType: NEGATIVE`


### Search Retrospectives

- **URL:** `http://localhost:8080/api/retrospectives/search`
- **Method:** GET
- **Description:** Search retrospectives
- **Request:**
  - Query Parameters:
    - `date: 2024-01-10`


### Search Retrospectives with Pagination

- **URL:** `http://localhost:8080/api/retrospectives/search`
- **Method:** GET
- **Description:** Search retrospectives with pagination
- **Request:**
  - Query Parameters:
    - `date: 2024-01-09`
    - `pageSize: 5`
    - `page: 0`


---
