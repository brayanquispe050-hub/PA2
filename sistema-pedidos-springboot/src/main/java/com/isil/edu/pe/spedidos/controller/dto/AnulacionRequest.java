package com.isil.edu.pe.spedidos.controller.dto;

/** Cuerpo de POST /api/v1/anulaciones. {@code motivo} es opcional. */
public record AnulacionRequest(Long pedidoId, String motivo) {
}
