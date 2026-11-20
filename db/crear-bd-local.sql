-- Base de datos unica para desarrollo local (sin Docker).
-- Ejecutar en pgAdmin: Query Tool sobre la BD "postgres" (o clic derecho en Databases > Create > Database, nombre: uber).
-- Las tablas las crea cada microservicio al arrancar (ddl-auto: update).
CREATE DATABASE uber;
