package com.isil.edu.pe.spedidos.compensacion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Guarda el incidente en una transacción NUEVA e independiente (REQUIRES_NEW). Es necesario
 * porque la transacción del registro de pedido va a hacer rollback: sin REQUIRES_NEW el
 * incidente se perdería junto con ella.
 */
@Service
public class ConciliacionServiceImpl implements ConciliacionService {

  private final IncidenteConciliacionRepository repository;

  public ConciliacionServiceImpl(IncidenteConciliacionRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void registrarPendiente(String productoCodigo, int cantidad, String detalle) {
    repository.save(new IncidenteConciliacion(productoCodigo, cantidad, detalle));
  }
}
