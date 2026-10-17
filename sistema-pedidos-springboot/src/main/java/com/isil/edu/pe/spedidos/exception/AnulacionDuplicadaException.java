package com.isil.edu.pe.spedidos.exception;

/** El pedido ya fue anulado o no está en estado REGISTRADO (se traduce a HTTP 409). */
public class AnulacionDuplicadaException extends PedidoException {

  public AnulacionDuplicadaException(Long pedidoId) {
    super("El pedido " + pedidoId + " ya fue anulado o no está en estado REGISTRADO.");
  }
}
