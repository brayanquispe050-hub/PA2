package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.isil.edu.pe.spedidos.client.DevolucionStockClient;
import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.entity.PedidoEstado;
import com.isil.edu.pe.spedidos.exception.AnulacionDuplicadaException;
import com.isil.edu.pe.spedidos.exception.PedidoNoEncontradoException;
import com.isil.edu.pe.spedidos.repository.AnulacionPedidoRepository;
import com.isil.edu.pe.spedidos.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias NUEVAS de la PA2 sobre {@link AnulacionPedidoServiceImpl}.
 * Sin Spring ni inventario-service: todas las dependencias son mocks (Constructor Injection).
 */
class AnulacionPedidoServiceImplTest {

  private PedidoRepository pedidoRepository;
  private AnulacionPedidoRepository anulacionPedidoRepository;
  private DevolucionStockClient devolucionStockClient;
  private AnulacionPedidoServiceImpl service;

  @BeforeEach
  void setUp() {
    pedidoRepository = mock(PedidoRepository.class);
    anulacionPedidoRepository = mock(AnulacionPedidoRepository.class);
    devolucionStockClient = mock(DevolucionStockClient.class);
    service = new AnulacionPedidoServiceImpl(
        pedidoRepository, anulacionPedidoRepository, devolucionStockClient);
  }

  @Test
  void anulacionCorrectaInvocaUnaSolaDevolucionMarcaAnuladoYGuardaElRegistro() {
    // Preparación
    Pedido pedido = pedidoRegistrado(1L, 2);
    when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
    when(anulacionPedidoRepository.existsByPedidoId(1L)).thenReturn(false);
    when(devolucionStockClient.devolverStock("LAP-001", 2)).thenReturn(devolucion("LAP-001", 2, 10));
    when(anulacionPedidoRepository.saveAndFlush(any(AnulacionPedido.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Ejecución
    AnulacionPedido resultado = service.anularPedido(1L, "Solicitud del cliente");

    // Verificación
    assertEquals(PedidoEstado.ANULADO, pedido.getEstado());
    assertNotNull(resultado);
    assertEquals(1L, resultado.getPedidoId());
    assertEquals("LAP-001", resultado.getProductoCodigo());
    assertEquals(2, resultado.getCantidadDevuelta());
    assertEquals("Solicitud del cliente", resultado.getMotivo());
    verify(devolucionStockClient, times(1)).devolverStock("LAP-001", 2);
    verify(pedidoRepository).save(pedido);
    verify(anulacionPedidoRepository).saveAndFlush(any(AnulacionPedido.class));
  }

  @Test
  void anulacionDuplicadaSeRechazaSinNuevaDevolucionNiRegistroAdicional() {
    // Preparación: el pedido ya fue anulado
    Pedido pedido = pedidoRegistrado(1L, 2);
    pedido.setEstado(PedidoEstado.ANULADO);
    when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
    when(anulacionPedidoRepository.existsByPedidoId(1L)).thenReturn(true);

    // Ejecución + verificación
    assertThrows(AnulacionDuplicadaException.class, () -> service.anularPedido(1L, "Otra vez"));

    verify(devolucionStockClient, never()).devolverStock(anyString(), anyInt());
    verify(anulacionPedidoRepository, never()).saveAndFlush(any(AnulacionPedido.class));
    verify(anulacionPedidoRepository, never()).save(any(AnulacionPedido.class));
    verify(pedidoRepository, never()).save(any(Pedido.class));
  }

  @Test
  void fallaDeInventarioNoConfirmaCambiosLocalesNiRegistraAnulacion() {
    // Preparación: el cliente remoto lanza excepción (inventario detenido)
    Pedido pedido = pedidoRegistrado(1L, 2);
    when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
    when(anulacionPedidoRepository.existsByPedidoId(1L)).thenReturn(false);
    when(devolucionStockClient.devolverStock("LAP-001", 2))
        .thenThrow(new InventarioNoDisponibleException("Inventario no disponible."));

    // Ejecución + verificación
    assertThrows(InventarioNoDisponibleException.class, () -> service.anularPedido(1L, null));

    assertEquals(PedidoEstado.REGISTRADO, pedido.getEstado());
    verify(pedidoRepository, never()).save(any(Pedido.class));
    verify(anulacionPedidoRepository, never()).saveAndFlush(any(AnulacionPedido.class));
    verify(anulacionPedidoRepository, never()).save(any(AnulacionPedido.class));
  }

  @Test
  void pedidoInexistenteSeRechazaSinTocarInventario() {
    when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(PedidoNoEncontradoException.class, () -> service.anularPedido(99L, null));

    verifyNoInteractions(devolucionStockClient, anulacionPedidoRepository);
  }

  private static Pedido pedidoRegistrado(Long id, int cantidad) {
    Pedido pedido = new Pedido("Ana Torres", "LAP-001", "Laptop",
        new BigDecimal("2500.00"), cantidad, new BigDecimal("5000.00"));
    pedido.setId(id);
    return pedido;
  }

  private static DevolucionStockResponse devolucion(String codigo, int devuelta, int restante) {
    DevolucionStockResponse r = new DevolucionStockResponse();
    r.setCodigo(codigo);
    r.setCantidadDevuelta(devuelta);
    r.setStockRestante(restante);
    return r;
  }
}
