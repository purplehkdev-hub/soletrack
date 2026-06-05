SoleTrack Open Banking integration.

Usage:
- GET /login_to_bank: redirects user to the selected bank's OAuth login page.
- GET /callback?code=...: exchanges code for a session and returns a simplified account list (uid and name).
- GET /getAuthUrl: returns JSON {authUrl: ...} useful for client-side redirection.
- GET /accounts/{id}/balance: returns account balances for the given account id.

Authentication: external provider-specific; this application generates client JWTs for the EnableBanking provider.

Examples:
1) Browser flow: call /login_to_bank -> user signs in at bank -> bank redirects to /callback with code.
2) Server flow: call /getAuthUrl to fetch provider URL and redirect the user from browser/client.