-- ============================================================
-- FinCore — Inicialización de bases de datos
-- Se ejecuta automáticamente al crear el contenedor de PostgreSQL
-- ============================================================

-- auth_db ya existe (creada por POSTGRES_DB en docker-compose)

-- Crear las otras 3 databases
CREATE DATABASE account_db;
CREATE DATABASE transaction_db;
CREATE DATABASE audit_db;

-- Crear usuarios por servicio con permisos mínimos
-- Cada servicio solo accede a su propia database (principio de mínimo privilegio)

-- Usuario para Auth Service
CREATE USER auth_user WITH PASSWORD 'auth_pass_123';
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;

-- Usuario para Account Service
CREATE USER account_user WITH PASSWORD 'account_pass_123';
GRANT ALL PRIVILEGES ON DATABASE account_db TO account_user;

-- Usuario para Transaction Service
CREATE USER transaction_user WITH PASSWORD 'transaction_pass_123';
GRANT ALL PRIVILEGES ON DATABASE transaction_db TO transaction_user;

-- Usuario para Audit Service
CREATE USER audit_user WITH PASSWORD 'audit_pass_123';
GRANT ALL PRIVILEGES ON DATABASE audit_db TO audit_user;

-- Permisos de schema para cada usuario en su database
\c auth_db
GRANT ALL ON SCHEMA public TO auth_user;

\c account_db
GRANT ALL ON SCHEMA public TO account_user;

\c transaction_db
GRANT ALL ON SCHEMA public TO transaction_user;

\c audit_db
GRANT ALL ON SCHEMA public TO audit_user;