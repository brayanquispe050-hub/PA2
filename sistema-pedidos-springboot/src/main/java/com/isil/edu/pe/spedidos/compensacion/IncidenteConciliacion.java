package com.isil.edu.pe.spedidos.compensacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Incidente pendiente de conciliación manual: la reserva remota se confirmó, falló el guardado
 * local y además falló la devolución compensatoria.
 */
@Entity
@Table(name = "incidente_conciliacion")
public class IncidenteConciliacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "producto_codigo", nullable = false, length = 30)
  private String productoCodigo;

  @Column(nullable = false)
  private int cantidad;

  @Column(nullable = false, length = 500)
  private String detalle;

  @Column(nullable = false, length = 20)
  private String estado = "PENDIENTE";

  @Column(nullable = false)
  private LocalDateTime fecha = LocalDateTime.now();

  public IncidenteConciliacion() {
  }

  public IncidenteConciliacion(String productoCodigo, int cantidad, String detalle) {
    this.productoCodigo = productoCodigo;
    this.cantidad = cantidad;
    this.detalle = detalle != null && detalle.length() > 500 ? detalle.substring(0, 500) : detalle;
  }

  public Long getId() {
    return id;
  }

  public String getProductoCodigo() {
    return productoCodigo;
  }

  public int getCantidad() {
    return cantidad;
  }

  public String getDetalle() {
    return detalle;
  }

  public String getEstado() {
    return estado;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }
}
