package com.isil.edu.pe.spedidos.compensacion;

/** Registra incidentes que requieren conciliación manual entre Pedidos e Inventario. */
public interface ConciliacionService {

  void registrarPendiente(String productoCodigo, int cantidad, String detalle);
}
