package com.isil.edu.pe.spedidos.controller.dto;

import java.time.LocalDateTime;

/** Estructura de error devuelta por la API de anulaciones. */
public record ApiErrorResponse(
    int status,
    String error,
    String message,
    String path,
    LocalDateTime timestamp
) {
}
