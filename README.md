# SoleTrack

SoleTrack is a small demo API, focused project for tracking personal bank account balances, transactions

## Key features
- Connect to Enable Banking API 
- Connect to Plaid (Todo)
- Allow user query the balance
- Allow user query the transactions (Todo)

## Technology
- Language / framework: Java Spring Boot 4
- Database: (Todo)

## Getting started
1. Install prerequisites: 
    1.1 Sign Up Enable Banking (https://enablebanking.com/)
    1.2 Create API applications , environment:Sandbox, 2 Generate in the browser, Application name:soletrack, redirect url:http://localhost:8080/callback
    1.3 Register
    1.4 get app-id, pem file should be downloaded automatically
2. Clone the repo and open the project folder.
3. Put the pem file to soletrack\src\main\resources\enablebanking\sandbox\sandbox_private.pem
4. Configure application.properties , update enable-banking.app-id

## API endpoint 
- swagger-ui - http://localhost:8080/swagger-ui/index.html
- GET /login_to_bank: redirects user to the selected bank's OAuth login page.
GET /callback?code=...: exchanges code for a session and returns a simplified account list (uid and name).
- GET /getAuthUrl: returns JSON {authUrl: ...} useful for client-side redirection.
- GET /accounts/{id}/balance: returns account balances for the given account id.

## Development
- Code style: follow https://google.github.io/styleguide/javaguide.html

## Todo
- Create Docker file
- application.properties should be application-[profile].properties
- add transactions end point