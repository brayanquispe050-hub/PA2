package com.isil.edu.pe.spedidos.service;

import com.isil.edu.pe.spedidos.repository.PedidoReporteJdbcRepository;
import com.isil.edu.pe.spedidos.service.impl.PedidoReporteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

class PedidoReporteServiceTest {

    private PedidoReporteJdbcRepository repository;
    private PedidoReporteServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PedidoReporteJdbcRepository.class);
        service = new PedidoReporteServiceImpl(repository);
    }

    @Test
    @DisplayName("Dado un cliente vacío, cuando se busca pedidos, debe lanzar IllegalArgumentException")
    void dadaClienteVacio_cuandoBuscar_entoncesLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                service.buscarPedidosPorClienteYTotalMinimo("", 100.0)
        );
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Dado un totalMinimo negativo, cuando se busca pedidos, debe lanzar IllegalArgumentException")
    void dadoTotalMinimoNegativo_cuandoBuscar_entoncesLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                service.buscarPedidosPorClienteYTotalMinimo("Juan", -10.0)
        );
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Dado un minPedidos menor a 1, cuando se lista clientes frecuentes, debe lanzar IllegalArgumentException")
    void dadoMinPedidosInvalido_cuandoListarClientesFrecuentes_entoncesLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                service.listarClientesFrecuentes(0)
        );
        verifyNoInteractions(repository);
    }
}
