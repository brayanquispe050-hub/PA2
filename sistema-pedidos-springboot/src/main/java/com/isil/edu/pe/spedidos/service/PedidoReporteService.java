package com.isil.edu.pe.spedidos.service;

import com.isil.edu.pe.spedidos.controller.dto.ClienteFrecuenteDto;
import com.isil.edu.pe.spedidos.entity.Pedido;

import java.util.List;

public interface PedidoReporteService {
    List<Pedido> buscarPedidosPorClienteYTotalMinimo(String cliente, Double totalMinimo);
    List<ClienteFrecuenteDto> listarClientesFrecuentes(Integer minPedidos);
}
