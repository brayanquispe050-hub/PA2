package com.isil.edu.pe.spedidos.compensacion;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Implementación por defecto: no hace nada. */
@Component
@ConditionalOnProperty(
    name = "pedidos.test.simular-fallo-local",
    havingValue = "false",
    matchIfMissing = true
)
public class FalloLocalDesactivado implements ControlFalloLocal {

  @Override
  public void despuesDeGuardar() {
    // Sin efecto.
  }
}
