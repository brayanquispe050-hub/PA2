package com.isil.edu.pe.spedidos.service.impl;

import com.isil.edu.pe.spedidos.client.DevolucionStockClient;
import com.isil.edu.pe.spedidos.client.InventarioClient;
import com.isil.edu.pe.spedidos.client.InventarioClientException;
import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import com.isil.edu.pe.spedidos.client.dto.ProductoInventarioResponse;
import com.isil.edu.pe.spedidos.client.dto.ReservaStockResponse;
import com.isil.edu.pe.spedidos.compensacion.ConciliacionService;
import com.isil.edu.pe.spedidos.compensacion.ControlFalloLocal;
import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.entity.PedidoEstado;
import com.isil.edu.pe.spedidos.exception.AnulacionDuplicadaException;
import com.isil.edu.pe.spedidos.exception.PedidoException;
import com.isil.edu.pe.spedidos.repository.AnulacionPedidoRepository;
import com.isil.edu.pe.spedidos.repository.PedidoRepository;
import com.isil.edu.pe.spedidos.service.PedidoService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de las operaciones de negocio definidas por {@link PedidoService}.
 *
 * <p>Esta clase contiene la lógica de negocio del Sistema de Pedidos y
 * coordina dos dependencias principales:
 * <ul>
 *   <li>{@link PedidoRepository}, encargado de la persistencia local de los pedidos.</li>
 *   <li>{@link InventarioClient}, encargado de comunicarse con {@code inventario-service}.</li>
 * </ul>
 *
 * @see PedidoService
 * @see PedidoRepository
 * @see InventarioClient
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidoRepository;
    private final InventarioClient inventarioClient;
    private final DevolucionStockClient devolucionStockClient;
    private final ControlFalloLocal controlFalloLocal;
    private final ConciliacionService conciliacionService;

    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            InventarioClient inventarioClient,
            DevolucionStockClient devolucionStockClient,
            ControlFalloLocal controlFalloLocal,
            ConciliacionService conciliacionService) {
        this.pedidoRepository = pedidoRepository;
        this.inventarioClient = inventarioClient;
        this.devolucionStockClient = devolucionStockClient;
        this.controlFalloLocal = controlFalloLocal;
        this.conciliacionService = conciliacionService;
    }

    @Override
    @Transactional
    public Pedido registrarPedido(String cliente, String productoCodigo, int cantidad) {
        validarDatos(cliente, productoCodigo, cantidad);

        final ReservaStockResponse reserva;

        try {
            reserva = inventarioClient.reservarStock(productoCodigo.trim(), cantidad);
        } catch (InventarioClientException e) {
            if (e.getStatus() >= 500) {
                throw new InventarioNoDisponibleException(
                        "El servicio de inventario presentó un error temporal (HTTP "
                                + e.getStatus()
                                + ").",
                        e
                );
            }

            throw new PedidoException(e.getMessage(), e);
        }

        try {
            validarReserva(reserva);

            BigDecimal total = reserva.getPrecio().multiply(BigDecimal.valueOf(cantidad));

            Pedido pedido = new Pedido(cliente.trim(), reserva.getCodigo(), reserva.getNombre(),
                    reserva.getPrecio(), cantidad, total);

            Pedido guardado = pedidoRepository.save(pedido);
            controlFalloLocal.despuesDeGuardar();
            return guardado;
        } catch (RuntimeException e) {
            compensarReserva(productoCodigo.trim(), cantidad, e);
            throw e;
        }
    }

    private void compensarReserva(String codigo, int cantidad, RuntimeException causaOriginal) {
        try {
            devolucionStockClient.devolverStock(codigo, cantidad);
            log.warn("Reserva compensada en Inventario: {} x{} (causa: {})",
                    codigo, cantidad, causaOriginal.getMessage());
        } catch (RuntimeException fallo) {
            log.error("Falló la compensación de {} x{}; incidente pendiente de conciliación.",
                    codigo, cantidad, fallo);
            causaOriginal.addSuppressed(fallo);
            conciliacionService.registrarPendiente(
                    codigo,
                    cantidad,
                    "Reserva confirmada sin pedido local; devolución compensatoria fallida: "
                            + fallo.getMessage()
            );
        }
    }

    @Override
    public List<ProductoInventarioResponse> listarProductos() {
        return inventarioClient.listarProductos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAllByOrderByIdDesc();
    }

    private void validarDatos(String cliente, String productoCodigo, int cantidad) {
        if (cliente == null || cliente.isBlank()) {
            throw new PedidoException("El cliente es obligatorio.");
        }

        if (productoCodigo == null || productoCodigo.isBlank()) {
            throw new PedidoException("Debe seleccionar un producto.");
        }

        if (cantidad <= 0) {
            throw new PedidoException("La cantidad debe ser mayor que cero.");
        }
    }

    private void validarReserva(ReservaStockResponse reserva) {
        if (reserva == null
                || reserva.getCodigo() == null
                || reserva.getCodigo().isBlank()
                || reserva.getNombre() == null
                || reserva.getNombre().isBlank()
                || reserva.getPrecio() == null) {
            throw new PedidoException("inventario-service devolvió una respuesta incompleta.");
        }
    }
}
