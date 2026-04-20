from weasyprint import HTML

# Contenido del archivo MD con errores
md_content = """# ❌ User Stories – Iberia Auth + Destination API (VERSION WITH ERRORS)

## US1 – Gestión de Acceso

### Endpoint
POST https://ibisauth.iberia.com/api/auth/realms/commercial_platform/protocol/openid-connect/token

### Request
- Content-Type: application/x-www-form-urlencoded  
- Body: grant_type=client_credentials  
- Authorization: Basic <client_credentials>

### Acceptance Criteria
El sistema debe devolver un token si las credenciales son válidas. Si las credenciales no son válidas, debe dar un error 401. El token debe guardarse para usarse después.

---