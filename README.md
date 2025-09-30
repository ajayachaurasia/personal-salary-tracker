# Salary Tracker REST API

Spring Boot backend to manage personal salary history across financial years, companies, and currencies. Supports multi-sheet Excel upload, currency-normalized trends, company switches, hikes, comparisons, Google OAuth2 login, and role-based access (ROLE_USER, ROLE_ADMIN).

---

## Table of contents

- [Prerequisites](#prerequisites)
- [Quick start](#quick-start)
- [Configuration](#configuration)
- [Running the app](#running-the-app)
- [API endpoints](#api-endpoints)
- [Excel format](#excel-format)
- [Security](#security)
- [Testing](#testing)
- [Developer notes](#developer-notes)
- [Useful commands](#useful-commands)

---

## Prerequisites

- Java 17+ (installed and on PATH)  
- Maven 3.6+ (installed and on PATH)  
- Optional: IDE (Eclipse / IntelliJ), Postman or curl

---

## Quick start

1. Clone repository and open project root (contains `pom.xml`).

2. Build (skip tests for speed):  
   mvn clean package -DskipTests
   
---

## Configuration

Edit src/main/resources/application.yml for DB, OAuth and other settings.

- Google OAuth (replace client-id, client-secret with your credentials):
   
- Google Console Redirect URIs to add:

	http://localhost:8080/login/oauth2/code/google
   
- H2 console (dev): http://localhost:8080/h2-console

	JDBC URL: jdbc:h2:mem:salarydb
	User: sa Password: (empty)
   
---

## Running the app

- From terminal: mvn spring-boot:run
- From IDE: run com.example.salarytracker.SalaryTrackerApplication
   
---


## API endpoints
Base path: /api/salaries. Replace employeeEmail and baseCurrency as needed.

* Upload Salary Excel
    * POST api/salaries/uploads?employeeEmail=
    * multipart/form-data: file (xlsx), employeeEmail (string)

* Get by Financial Year
    * GET /api/salaries/{year}?employeeEmail=&baseCurrency=USD
    * Example: http://localhost:8080/api/salaries/2021-2022?employeeEmail=ajay@gmail.com&baseCurrency=AED

* Salary Growth Trend
    * GET /api/salaries/trend?employeeEmail=&baseCurrency=USD
    * Example: http://localhost:8080/api/salaries/trend?employeeEmail=ajay@gmail.com&baseCurrency=AED

* Latest Salary
    * GET /api/salaries/latest?employeeEmail=&baseCurrency=USD
    * Example: http://localhost:8080/api/salaries/latest?employeeEmail=ajay@gmail.com&baseCurrency=USD

* Update Salary Hike
    * PUT /api/salaries/2023-2024/hike?employeeEmail=&baseCurrency=&hikePercent=
    * Example: http://localhost:8080/api/salaries/2023-2024/hike?employeeEmail=ajay@gmail.com&baseCurrency=INR&hikePercent=10

* Update Company Switch
    * POST /api/salaries/switch?employeeEmail=
    * Example: http://localhost:8080/api/salaries/switch?employeeEmail=ajay@gmail.com
    * Request Data: 
    {
	  "company": "WalkingTree",
	  "currency": "INR",
	  "financialYear": "2024-2025",
	  "fixedCTC": 1800000.00,
	  "variable": 180000.00,
	  "deductions": 0.00
	}

* Compare Salary Across Companies
    * GET /api/salaries/compare?employeeEmail=&baseCurrency=
    * Example: http://localhost:8080/api/salaries/compare?employeeEmail=ajay@gmail.com&baseCurrency=USD

---

## Excel format
- Each sheet = one financial year (sheet name example: 2021-22).
- First row must include (case-insensitive) keywords: Company, Currency, Fixed, Variable, Deductions.
- Rows: Company | Currency | Fixed | Variable | Deductions.
- Sample file (used for testing) is added to /personal-salary-tracker/src/test/resources/sample-excel-template/sample-for-test.xlsx
   
---

## Security
* Google OAuth2 configured via application.yml; register client in Google Cloud Console and add redirect URIs listed above.
* Role-based authorization:
	* Endpoints require ROLE_USER or ROLE_ADMIN.
	* Upload Salary Excel require ROLE_USER.
	* Fetch Salary by Year require ROLE_USER or ROLE_ADMIN.
	* Salary Growth Trend require ROLE_USER or ROLE_ADMIN.
	* Latest Salary require ROLE_USER or ROLE_ADMIN.
	* Update Salary Hike require ROLE_USER.
	* Update Company Switch require ROLE_USER.
	* Compare Salary Across Companies require ROLE_USER or ROLE_ADMIN.
	
* Provisioning:
	* DataInitializer seeds ROLE_USER and ROLE_ADMIN initially for some email.
	* CustomJwtAuthenticationConverter is used to get the email from token which will be used to get the role from db

* Step to Get Google ID Token
	* Go to Google OAuth2 Playground: https://developers.google.com/oauthplayground
	* Select Google OAuth2 API v2 → Userinfo.email / Userinfo.profile.
	* Click Authorize APIs → choose your account → exchange code for tokens.
	* Copy the ID Token
	
---

## Testing
* Call Your API with the Token via Postman: 
	* Method: GET / POST (As per API)
	* URL: http://localhost:8080/api.. (As per API)
	* Headers:  → Authorization  → Bearer <ID_TOKEN>
		
* Swagger Json is attached in the /personal-salary-tracker/src/test/resources/WalkingTree.postman_collection.json
   
---

## Developer notes
- CurrencyService uses hardcoded rates for demo purpose.
   
---

## Useful commands
- Build: mvn clean package
- Run: mvn spring-boot:run
   
---

