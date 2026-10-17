package pe.edu.isil.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Solicitud de devolución de stock.
 *
 * <p>Se define como DTO propio (y no se reutiliza {@link ReservaStockRequest}) para que el
 * contrato de devoluciones pueda evolucionar de forma independiente al de reservas.</p>
 */
public class DevolucionStockRequest {

  @NotNull(message = "La cantidad es obligatoria.")
  @Positive(message = "La cantidad debe ser mayor que cero.")
  private Integer cantidad;

  public DevolucionStockRequest() {
  }

  public DevolucionStockRequest(Integer cantidad) {
    this.cantidad = cantidad;
  }

  public Integer getCantidad() {
    return cantidad;
  }

  public void setCantidad(Integer cantidad) {
    this.cantidad = cantidad;
  }
}
