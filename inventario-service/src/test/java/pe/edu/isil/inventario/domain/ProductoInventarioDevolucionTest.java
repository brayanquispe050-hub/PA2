package pe.edu.isil.inventario.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias NUEVAS de la PA2: operación de dominio {@code devolverStock}.
 * No requieren WildFly ni SQL Server.
 */
class ProductoInventarioDevolucionTest {

  private static ProductoInventario productoConStock(int stock) {
    return new ProductoInventario("LAP-001", "Laptop", new BigDecimal("2500.00"), stock);
  }

  @Test
  void devolucionValidaIncrementaStockExactamenteEnLaCantidadDevuelta() {
    // Preparación: 10 unidades, reserva de 2 (stock 8)
    ProductoInventario producto = productoConStock(10);
    producto.reservarStock(2);
    assertEquals(8, producto.getStock());

    // Ejecución
    producto.devolverStock(2);

    // Verificación: 8 + 2 = 10
    assertEquals(10, producto.getStock());
  }

  @Test
  void devolucionInvalidaDeCeroONegativoSeRechazaYNoCambiaElStock() {
    ProductoInventario producto = productoConStock(8);

    assertThrows(IllegalArgumentException.class, () -> producto.devolverStock(0));
    assertEquals(8, producto.getStock());

    assertThrows(IllegalArgumentException.class, () -> producto.devolverStock(-3));
    assertEquals(8, producto.getStock());
  }
}
