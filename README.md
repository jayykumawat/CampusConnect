# CampusConnect
"Multi-institution event &amp; communication portal - Spring Boot"

## Authentication (session-based)

Public authentication endpoints are:

- `POST /api/auth/register` creates a `STUDENT` account. The request requires `name`, `email`, `password` (at least 8 characters), and an existing `institutionId`; optional fields are `classOrYear` and `department`. Any submitted `role` is ignored.
- `POST /api/auth/login` accepts `email` and `password`. Success returns `{"authenticated":true,"email":"student@example.com"}` and establishes an HTTP session.
- `GET /api/auth/csrf` returns the CSRF header name and a token.
- `POST /api/auth/logout` invalidates the session and requires a CSRF token.

For Postman, keep its cookie jar enabled so the `JSESSIONID` from `GET /api/auth/csrf` is sent with subsequent requests. Copy the returned `token` into the returned header name (currently `X-CSRF-TOKEN`) for every state-changing request, including registration and login. Login rotates the session ID and CSRF token; after login, call `GET /api/auth/csrf` again and use its fresh token for later POST/PUT/DELETE requests, including logout. A browser client should likewise retain the session cookie and send the CSRF token in the indicated header.

All other application APIs require authentication. User-management routes under `/api/users/**` require `SUPER_ADMIN`; fine-grained resource ownership and role policies for other modules are not yet implemented. Authentication does not establish authorization to every institution, event, registration, or announcement. WebSocket chat still accepts a client-supplied sender ID and does not yet authorize STOMP SEND/SUBSCRIBE destinations or event participation.

Session cookies are HttpOnly and SameSite=Lax. For HTTPS deployments, set `SESSION_COOKIE_SECURE=true`; the default is intended for local HTTP development. No administrator is created automatically. Provision the initial `SUPER_ADMIN` through a controlled operator-only process that uses the application’s BCrypt encoder; do not store or commit a plaintext bootstrap password.
