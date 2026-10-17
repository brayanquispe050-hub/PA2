package com.isil.edu.pe.spedidos.service.impl;

import com.isil.edu.pe.spedidos.client.DevolucionStockClient;
import com.isil.edu.pe.spedidos.client.InventarioClientException;
import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.entity.PedidoEstado;
import com.isil.edu.pe.spedidos.exception.AnulacionDuplicadaException;
import com.isil.edu.pe.spedidos.exception.PedidoException;
import com.isil.edu.pe.spedidos.exception.PedidoNoEncontradoException;
import com.isil.edu.pe.spedidos.repository.AnulacionPedidoRepository;
import com.isil.edu.pe.spedidos.repository.PedidoRepository;
import java.util.List;

import com.isil.edu.pe.spedidos.service.AnulacionPedidoService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnulacionPedidoServiceImpl implements AnulacionPedidoService {

    private final PedidoRepository pedidoRepository;
    private final AnulacionPedidoRepository anulacionPedidoRepository;
    private final DevolucionStockClient devolucionStockClient;

    public AnulacionPedidoServiceImpl(
            PedidoRepository pedidoRepository,
            AnulacionPedidoRepository anulacionPedidoRepository,
            DevolucionStockClient devolucionStockClient) {
        this.pedidoRepository = pedidoRepository;
        this.anulacionPedidoRepository = anulacionPedidoRepository;
        this.devolucionStockClient = devolucionStockClient;
    }

    @Override
    @Transactional
    public AnulacionPedido anularPedido(Long pedidoId, String motivo) {
        if (pedidoId == null || pedidoId <= 0) {
            throw new PedidoException("El identificador del pedido es obligatorio.");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));

        if (pedido.getEstado() != PedidoEstado.REGISTRADO
                || anulacionPedidoRepository.existsByPedidoId(pedidoId)) {
            throw new AnulacionDuplicadaException(pedidoId);
        }

        final DevolucionStockResponse devolucion;
        try {
            devolucion = devolucionStockClient.devolverStock(
                    pedido.getProductoCodigo(),
                    pedido.getCantidad()
            );
        } catch (InventarioClientException e) {
            if (e.getStatus() >= 500) {
                throw new InventarioNoDisponibleException(
                        "El servicio de inventario presentó un error temporal (HTTP " + e.getStatus() + ").",
                        e
                );
            }
            throw new PedidoException(e.getMessage(), e);
        }

        if (devolucion == null) {
            throw new PedidoException("inventario-service devolvió una respuesta incompleta.");
        }

        AnulacionPedido anulacion = new AnulacionPedido(
                pedido.getId(),
                pedido.getProductoCodigo(),
                devolucion.getCantidadDevuelta() > 0 ? devolucion.getCantidadDevuelta() : pedido.getCantidad(),
                normalizar(motivo)
        );

        pedido.setEstado(PedidoEstado.ANULADO);
        pedidoRepository.save(pedido);

        try {
            return anulacionPedidoRepository.saveAndFlush(anulacion);
        } catch (DataIntegrityViolationException e) {
            throw new AnulacionDuplicadaException(pedidoId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnulacionPedido> listarAnulaciones() {
        return anulacionPedidoRepository.findAllByOrderByIdDesc();
    }

    private static String normalizar(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            return null;
        }
        String limpio = motivo.trim();
        return limpio.length() > 255 ? limpio.substring(0, 255) : limpio;
    }
}
