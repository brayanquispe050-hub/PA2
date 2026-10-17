package com.isil.edu.pe.spedidos.client;

import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;

/**
 * Contrato para solicitar a inventario-service la devolución de stock.
 *
 * <p>La capa de negocio depende de esta abstracción y no de HTTP, lo que permite sustituir la
 * implementación por un mock en las pruebas unitarias.</p>
 */
public interface DevolucionStockClient {

  /**
   * Devuelve {@code cantidad} unidades al producto identificado por {@code codigo}.
   *
   * @throws InventarioClientException si inventario-service responde HTTP 4xx/5xx
   * @throws InventarioNoDisponibleException si no hay conexión o se excede el timeout
   */
  DevolucionStockResponse devolverStock(String codigo, int cantidad);
}
