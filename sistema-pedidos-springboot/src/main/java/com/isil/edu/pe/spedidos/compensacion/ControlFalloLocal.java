package com.isil.edu.pe.spedidos.compensacion;

/**
 * Punto de extensión para PROVOCAR un fallo local controlado después de que Inventario confirmó
 * la reserva y antes del commit de H2 (reto de compensación).
 *
 * <p>Por defecto está desactivado ({@link FalloLocalDesactivado}). Solo se activa con la
 * propiedad {@code pedidos.test.simular-fallo-local=true}, pensada para pruebas; no se expone
 * ningún endpoint para activarlo.</p>
 */
public interface ControlFalloLocal {

  /** Se invoca tras guardar el pedido en la sesión JPA y antes de terminar la transacción. */
  void despuesDeGuardar();
}
