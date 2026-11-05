package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.controller.dto.ClienteFrecuenteDto;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.service.PedidoReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoReporteRestController {

    private final PedidoReporteService pedidoReporteService;

    public PedidoReporteRestController(PedidoReporteService pedidoReporteService) {
        this.pedidoReporteService = pedidoReporteService;
    }

    @GetMapping("/busqueda-avanzada")
    public ResponseEntity<List<Pedido>> buscarPedidos(
            @RequestParam String cliente,
            @RequestParam Double totalMinimo) {
        List<Pedido> resultados = pedidoReporteService.buscarPedidosPorClienteYTotalMinimo(cliente, totalMinimo);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/clientes-frecuentes")
    public ResponseEntity<List<ClienteFrecuenteDto>> listarClientesFrecuentes(
            @RequestParam Integer minPedidos) {
        List<ClienteFrecuenteDto> resultados = pedidoReporteService.listarClientesFrecuentes(minPedidos);
        return ResponseEntity.ok(resultados);
    }
}
