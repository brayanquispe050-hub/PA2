# Inventario Service — Jakarta EE 11 + SQL Server Express 2025

Proyecto académico complementario de `sistema-pedidos-jsp` para la Sesión 4: **Integración de aplicativos web con componentes empresariales**.

## Stack

- Java 21
- Maven
- Jakarta EE 11
- WildFly 41
- Jakarta REST
- Jakarta Enterprise Beans (EJB)
- Jakarta Persistence 3.2
- Jakarta Transactions (JTA)
- Bean Validation
- Microsoft JDBC Driver for SQL Server 13.6
- SQL Server Express 2025

## Arquitectura

```text
Sistema de Pedidos / Postman
          |
          | HTTP + JSON
          v
ProductoResource (Jakarta REST)
          |
          | @EJB
          v
InventarioService (@Stateless)
          |
          | @PersistenceContext
          v
EntityManager / InventarioPU
          |
          | java:app/jdbc/InventarioDS
          v
SQL Server Express 2025
```

## Preparación rápida

1. Habilitar TCP/IP en SQL Server Express y configurar un puerto fijo (la guía usa 1433).
2. Habilitar autenticación SQL Server/Mixed Mode para el usuario académico.
3. Ejecutar `database/01_crear_inventario.sql` en SSMS.
4. Verificar JDK 21 y Maven.
5. Ejecutar:

```bash
mvn clean test
mvn clean wildfly:run
```

El plugin usa `-Djboss.socket.binding.port-offset=2`, por lo que WildFly escucha en HTTP **8082** y Management **9992**. Esto permite ejecutar a la vez el `sistema-pedidos` existente (HTTP 8081, Management 9990).

Abrir:

```text
http://localhost:8082/inventario-service/
```

API principal:

```text
GET  /inventario-service/api/inventario/productos
GET  /inventario-service/api/inventario/productos/1/stock
POST /inventario-service/api/inventario/productos/1/reservas
```

Body de reserva:

```json
{
  "cantidad": 2
}
```

También existen endpoints por `codigo` (`LAP-001`, `MON-001`, `TEC-001`) para mostrar un contrato menos acoplado a las claves internas de base de datos.

## PA2 — API de devolución de stock

Nuevo endpoint (no existía en el proyecto inicial):

| Elemento | Valor |
|---|---|
| Método / ruta | `POST /inventario-service/api/inventario/productos/codigo/{codigo}/devoluciones` |
| Solicitud | `{ "cantidad": 2 }` |
| Respuesta 200 | `{ "codigo": "LAP-001", "cantidadDevuelta": 2, "stockRestante": 10, "version": 3 }` |
| 400 | `cantidad` ausente, `0` o negativa (`{ "cantidad": 0 }`) |
| 404 | `codigo` inexistente |
| 409 | conflicto de concurrencia (`@Version`) |

Capas: `ProductoResource` → `InventarioService` (`@Stateless`, `@TransactionAttribute(REQUIRED)`) →
`ProductoInventario.devolverStock(int)` (JPA) → SQL Server. El `UPDATE` conserva el control
optimista `@Version`.

Ejemplo ejecutable (con WildFly en el puerto 8082; ver `requests/inventario-service.http`):

```bash
curl -i -X POST \
  http://localhost:8082/inventario-service/api/inventario/productos/codigo/LAP-001/devoluciones \
  -H "Content-Type: application/json" -d '{"cantidad": 2}'
```

### Pruebas unitarias nuevas (PA2)

Clase `src/test/java/pe/edu/isil/inventario/domain/ProductoInventarioDevolucionTest.java`:

- `devolucionValidaIncrementaStockExactamenteEnLaCantidadDevuelta` (8 + 2 = 10)
- `devolucionInvalidaDeCeroONegativoSeRechazaYNoCambiaElStock`

```bash
mvn test        # desde la raíz de inventario-service; no requiere WildFly ni SQL Server
```

### Limitaciones

La devolución es una transacción local de Inventario. Si el llamador (Spring Boot) falla después,
la devolución remota no se revierte automáticamente: no existe transacción distribuida.
Tampoco se reintenta el `POST`, porque no es idempotente.

## Material de clase

- `docs/GUIA_IMPLEMENTACION_PASO_A_PASO.md`
- `docs/INTEGRACION_CON_SISTEMA_PEDIDOS.md`
- `requests/inventario-service.http`
- `openapi/inventario-service.yaml`
- `database/02_reset_stock_demo.sql`

## Nota académica de seguridad

`@DataSourceDefinition` contiene usuario/contraseña para reducir pasos durante el laboratorio. En producción las credenciales no deben almacenarse en código; deben externalizarse en un DataSource administrado por WildFly, Credential Store o un mecanismo de secretos. `trustServerCertificate=true` también se usa solo para el laboratorio local.
