package com.isil.edu.pe.spedidos.compensacion;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Solo para pruebas: lanza un fallo local simulado. Requiere la propiedad en {@code true}. */
@Component
@ConditionalOnProperty(name = "pedidos.test.simular-fallo-local", havingValue = "true")
public class FalloLocalSimulado implements ControlFalloLocal {

  @Override
  public void despuesDeGuardar() {
    throw new FalloLocalSimuladoException("Fallo local simulado antes del commit en H2.");
  }
}
