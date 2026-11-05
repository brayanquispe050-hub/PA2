package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.service.PedidoReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reportes")
public class PedidoReporteWebController {

    private final PedidoReporteService pedidoReporteService;

    public PedidoReporteWebController(PedidoReporteService pedidoReporteService) {
        this.pedidoReporteService = pedidoReporteService;
    }

    @GetMapping("/pedidos")
    public String mostrarReportes(
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false, defaultValue = "0") Double totalMinimo,
            @RequestParam(required = false, defaultValue = "1") Integer minPedidos,
            Model model) {

        if (cliente != null && !cliente.trim().isEmpty()) {
            model.addAttribute("pedidosBusqueda",
                    pedidoReporteService.buscarPedidosPorClienteYTotalMinimo(cliente, totalMinimo));
        }

        model.addAttribute("clientesFrecuentes",
                pedidoReporteService.listarClientesFrecuentes(minPedidos));
        model.addAttribute("clienteFiltro", cliente);
        model.addAttribute("totalMinimoFiltro", totalMinimo);
        model.addAttribute("minPedidosFiltro", minPedidos);

        return "reportes-pedidos";
    }
}
