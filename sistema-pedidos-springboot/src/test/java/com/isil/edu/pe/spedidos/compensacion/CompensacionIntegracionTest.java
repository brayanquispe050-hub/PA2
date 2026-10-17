package com.isil.edu.pe.spedidos.compensacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.isil.edu.pe.spedidos.client.DevolucionStockClient;
import com.isil.edu.pe.spedidos.client.InventarioClient;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import com.isil.edu.pe.spedidos.client.dto.ProductoInventarioResponse;
import com.isil.edu.pe.spedidos.client.dto.ReservaStockResponse;
import com.isil.edu.pe.spedidos.repository.PedidoRepository;
import com.isil.edu.pe.spedidos.service.PedidoService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


@SpringBootTest(properties = "pedidos.test.simular-fallo-local=true")
class CompensacionIntegracionTest {


  static class InventarioFalso implements InventarioClient, DevolucionStockClient {

    int stock = 10;

    @Override
    public List<ProductoInventarioResponse> listarProductos() {
      return List.of();
    }

    @Override
    public ReservaStockResponse reservarStock(String codigo, int cantidad) {
      stock -= cantidad;
      ReservaStockResponse r = new ReservaStockResponse();
      r.setCodigo(codigo);
      r.setNombre("Laptop");
      r.setPrecio(new BigDecimal("2500.00"));
      r.setCantidadReservada(cantidad);
      r.setStockRestante(stock);
      return r;
    }

    @Override
    public DevolucionStockResponse devolverStock(String codigo, int cantidad) {
      stock += cantidad;
      DevolucionStockResponse r = new DevolucionStockResponse();
      r.setCodigo(codigo);
      r.setCantidadDevuelta(cantidad);
      r.setStockRestante(stock);
      return r;
    }
  }

  @TestConfiguration
  static class Config {

    @Bean
    @Primary
    InventarioFalso inventarioFalso() {
      return new InventarioFalso();
    }
  }

  @Autowired
  private PedidoService pedidoService;

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private InventarioFalso inventario;

  @Test
  void compensaLaReservaYNoDejaPedidoConfirmadoEnH2() {
    assertEquals(10, inventario.stock);
    assertEquals(0, pedidoRepository.count());

    assertThrows(FalloLocalSimuladoException.class,
        () -> pedidoService.registrarPedido("Ana Torres", "LAP-001", 2));

    // Después: Inventario restituido (10 -> 8 -> 10) y H2 sin pedido confirmado.
    assertEquals(10, inventario.stock);
    assertEquals(0, pedidoRepository.count());
  }
}
