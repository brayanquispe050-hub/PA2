package com.isil.edu.pe.spedidos.controller.dto;

import java.math.BigDecimal;

public class ClienteFrecuenteDto {
    private String cliente;
    private Long totalPedidos;
    private Integer totalUnidades;
    private BigDecimal importeAcumulado;
    private BigDecimal ticketPromedio;

    public ClienteFrecuenteDto() {}

    public ClienteFrecuenteDto(String cliente, Long totalPedidos, Integer totalUnidades, BigDecimal importeAcumulado, BigDecimal ticketPromedio) {
        this.cliente = cliente;
        this.totalPedidos = totalPedidos;
        this.totalUnidades = totalUnidades;
        this.importeAcumulado = importeAcumulado;
        this.ticketPromedio = ticketPromedio;
    }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public Long getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(Long totalPedidos) { this.totalPedidos = totalPedidos; }

    public Integer getTotalUnidades() { return totalUnidades; }
    public void setTotalUnidades(Integer totalUnidades) { this.totalUnidades = totalUnidades; }

    public BigDecimal getImporteAcumulado() { return importeAcumulado; }
    public void setImporteAcumulado(BigDecimal importeAcumulado) { this.importeAcumulado = importeAcumulado; }

    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(BigDecimal ticketPromedio) { this.ticketPromedio = ticketPromedio; }
}
