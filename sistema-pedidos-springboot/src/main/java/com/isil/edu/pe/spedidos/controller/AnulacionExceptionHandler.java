package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.controller.dto.ApiErrorResponse;
import com.isil.edu.pe.spedidos.exception.AnulacionDuplicadaException;
import com.isil.edu.pe.spedidos.exception.PedidoException;
import com.isil.edu.pe.spedidos.exception.PedidoNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce excepciones de negocio a HTTP solo para {@link AnulacionPedidoController}
 * (no altera el comportamiento de los controladores JSP existentes).
 *
 * <p>404 pedido inexistente · 409 anulación duplicada · 400 datos/regla de negocio ·
 * 503 inventario-service no disponible.</p>
 */
@RestControllerAdvice(assignableTypes = AnulacionPedidoController.class)
public class AnulacionExceptionHandler {

  @ExceptionHandler(PedidoNoEncontradoException.class)
  public ResponseEntity<ApiErrorResponse> noEncontrado(
      PedidoNoEncontradoException e, HttpServletRequest request) {
    return respuesta(HttpStatus.NOT_FOUND, e.getMessage(), request);
  }

  @ExceptionHandler(AnulacionDuplicadaException.class)
  public ResponseEntity<ApiErrorResponse> duplicada(
      AnulacionDuplicadaException e, HttpServletRequest request) {
    return respuesta(HttpStatus.CONFLICT, e.getMessage(), request);
  }

  @ExceptionHandler(PedidoException.class)
  public ResponseEntity<ApiErrorResponse> negocio(PedidoException e, HttpServletRequest request) {
    return respuesta(HttpStatus.BAD_REQUEST, e.getMessage(), request);
  }

  @ExceptionHandler(InventarioNoDisponibleException.class)
  public ResponseEntity<ApiErrorResponse> noDisponible(
      InventarioNoDisponibleException e, HttpServletRequest request) {
    return respuesta(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), request);
  }

  private static ResponseEntity<ApiErrorResponse> respuesta(
      HttpStatus status, String mensaje, HttpServletRequest request) {
    ApiErrorResponse body = new ApiErrorResponse(
        status.value(),
        status.getReasonPhrase(),
        mensaje,
        request.getRequestURI(),
        LocalDateTime.now()
    );
    return ResponseEntity.status(status).body(body);
  }
}
