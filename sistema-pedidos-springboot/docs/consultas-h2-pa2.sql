-- PA2 - Consultas para la consola H2 (http://localhost:8081/sistema-pedidos/h2-console)
-- JDBC URL: jdbc:h2:mem:pedidosdb   Usuario: admin   Contraseña: admin
-- Las tablas las crea Hibernate (ddl-auto=create-drop): NO hay que ejecutar scripts de creación.

-- Estado de los pedidos (nueva columna ESTADO: REGISTRADO / ANULADO)
SELECT id, cliente, producto_codigo, cantidad, total, estado FROM pedido ORDER BY id;

-- Auditoría de anulaciones (UNIQUE sobre pedido_id)
SELECT id, pedido_id, producto_codigo, cantidad_devuelta, fecha, motivo
FROM anulacion_pedido ORDER BY id;

-- Reto de compensación: incidentes pendientes de conciliación (normalmente vacío)
SELECT id, producto_codigo, cantidad, estado, detalle, fecha FROM incidente_conciliacion;
