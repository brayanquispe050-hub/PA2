package com.isil.edu.pe.spedidos.service.impl;

import com.isil.edu.pe.spedidos.controller.dto.ClienteFrecuenteDto;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.repository.PedidoReporteJdbcRepository;
import com.isil.edu.pe.spedidos.service.PedidoReporteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoReporteServiceImpl implements PedidoReporteService {

    private final PedidoReporteJdbcRepository reporteJdbcRepository;

    public PedidoReporteServiceImpl(PedidoReporteJdbcRepository reporteJdbcRepository) {
        this.reporteJdbcRepository = reporteJdbcRepository;
    }

    @Override
    public List<Pedido> buscarPedidosPorClienteYTotalMinimo(String cliente, Double totalMinimo) {
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }
        if (totalMinimo == null || totalMinimo < 0) {
            throw new IllegalArgumentException("El total mínimo debe ser mayor o igual a 0");
        }

        return reporteJdbcRepository.buscarPedidosPorClienteYTotalMinimo(cliente.trim(), totalMinimo);
    }

    @Override
    public List<ClienteFrecuenteDto> listarClientesFrecuentes(Integer minPedidos) {
        if (minPedidos == null || minPedidos < 1) {
            throw new IllegalArgumentException("El número mínimo de pedidos debe ser al menos 1");
        }

        return reporteJdbcRepository.listarClientesFrecuentes(minPedidos);
    }
}
