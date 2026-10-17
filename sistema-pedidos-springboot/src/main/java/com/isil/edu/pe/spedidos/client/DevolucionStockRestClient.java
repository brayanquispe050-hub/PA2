package com.isil.edu.pe.spedidos.client;

import com.isil.edu.pe.spedidos.client.dto.ApiError;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockRequest;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Implementación HTTP/JSON de {@link DevolucionStockClient}.
 *
 * <p>Reutiliza el {@link RestClient} {@code inventarioHttpClient} (con sus timeouts de conexión
 * y lectura) y la URL base {@code inventario.api.base-url} de {@code application.properties}.
 * Recibe ambos por Constructor Injection. No realiza reintentos: un POST de devolución no es
 * idempotente y reintentarlo podría devolver el stock dos veces.</p>
 */
@Component
public class DevolucionStockRestClient implements DevolucionStockClient {

  private final RestClient restClient;
  private final String baseUrl;

  public DevolucionStockRestClient(
      @Qualifier("inventarioHttpClient") RestClient restClient,
      @Value("${inventario.api.base-url}") String baseUrl) {
    this.restClient = restClient;
    this.baseUrl = baseUrl;
  }

  @Override
  public DevolucionStockResponse devolverStock(String codigo, int cantidad) {
    DevolucionStockRequest request = new DevolucionStockRequest(cantidad);
    try {
      return restClient
          .post()
          .uri(baseUrl + "/productos/codigo/{codigo}/devoluciones", codigo)
          .body(request)
          .retrieve()
          .body(DevolucionStockResponse.class);
    } catch (RestClientResponseException e) {
      // Hubo respuesta HTTP, pero fue 4xx/5xx.
      throw convertirError(e);
    } catch (ResourceAccessException e) {
      // Conexión rechazada, servicio detenido o timeout.
      throw new InventarioNoDisponibleException(
          "El servicio de inventario no se encuentra disponible o excedió el tiempo de espera.",
          e
      );
    }
  }

  private InventarioClientException convertirError(RestClientResponseException e) {
    String mensaje = "inventario-service respondió HTTP " + e.getStatusCode().value() + ".";
    try {
      ApiError error = e.getResponseBodyAs(ApiError.class);
      if (error != null && error.getMessage() != null && !error.getMessage().isBlank()) {
        mensaje = error.getMessage();
      }
    } catch (RuntimeException ignored) {
      // Cuerpo con formato inesperado: se conserva el mensaje genérico.
    }
    return new InventarioClientException(e.getStatusCode().value(), mensaje);
  }
}
