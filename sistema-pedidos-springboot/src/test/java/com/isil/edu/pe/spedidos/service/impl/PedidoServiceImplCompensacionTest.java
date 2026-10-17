package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.isil.edu.pe.spedidos.client.DevolucionStockClient;
import com.isil.edu.pe.spedidos.client.InventarioClient;
import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.client.dto.ReservaStockResponse;
import com.isil.edu.pe.spedidos.compensacion.ConciliacionService;
import com.isil.edu.pe.spedidos.compensacion.ControlFalloLocal;
import com.isil.edu.pe.spedidos.compensacion.FalloLocalSimuladoException;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.repository.PedidoRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Reto PA2: compensación tras una reserva remota confirmada y un fallo local previo al commit. */
class PedidoServiceImplCompensacionTest {

  private PedidoRepository pedidoRepository;
  private InventarioClient inventarioClient;
  private DevolucionStockClient devolucionStockClient;
  private ConciliacionService conciliacionService;
  private PedidoServiceImpl service;

  @BeforeEach
  void setUp() {
    pedidoRepository = mock(PedidoRepository.class);
    inventarioClient = mock(InventarioClient.class);
    devolucionStockClient = mock(DevolucionStockClient.class);
    conciliacionService = mock(ConciliacionService.class);

    ControlFalloLocal fallo = () -> {
      throw new FalloLocalSimuladoException("fallo simulado");
    };
    service = new PedidoServiceImpl(
        pedidoRepository, inventarioClient, devolucionStockClient, fallo, conciliacionService);

    ReservaStockResponse reserva = new ReservaStockResponse();
    reserva.setCodigo("LAP-001");
    reserva.setNombre("Laptop");
    reserva.setPrecio(new BigDecimal("2500.00"));
    reserva.setCantidadReservada(2);
    reserva.setStockRestante(8);
    when(inventarioClient.reservarStock("LAP-001", 2)).thenReturn(reserva);
    when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void fallaLocalTrasReservaConfirmadaInvocaDevolucionCompensatoriaUnaVez() {
    assertThrows(FalloLocalSimuladoException.class,
        () -> service.registrarPedido("Ana Torres", "LAP-001", 2));

    verify(inventarioClient, times(1)).reservarStock("LAP-001", 2);
    verify(devolucionStockClient, times(1)).devolverStock("LAP-001", 2);
    verify(conciliacionService, never()).registrarPendiente(anyString(), anyInt(), anyString());
  }

  @Test
  void siFallaLaCompensacionSeRegistraIncidentePendienteYSePropagaElFalloOriginal() {
    when(devolucionStockClient.devolverStock("LAP-001", 2))
        .thenThrow(new InventarioNoDisponibleException("Inventario caído."));

    FalloLocalSimuladoException error = assertThrows(FalloLocalSimuladoException.class,
        () -> service.registrarPedido("Ana Torres", "LAP-001", 2));

    assertEquals(1, error.getSuppressed().length);
    // Un único intento: no se reintenta un POST no idempotente.
    verify(devolucionStockClient, times(1)).devolverStock("LAP-001", 2);
    verify(conciliacionService).registrarPendiente(eq("LAP-001"), eq(2), contains("Inventario caído"));
  }
}
