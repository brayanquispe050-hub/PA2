package com.isil.edu.pe.spedidos.repository;

import com.isil.edu.pe.spedidos.controller.dto.ClienteFrecuenteDto;
import com.isil.edu.pe.spedidos.entity.Pedido;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PedidoReporteJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Pedido> pedidoRowMapper;

    public PedidoReporteJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.pedidoRowMapper = (rs, rowNum) -> {
            Pedido p = new Pedido();
            p.setId(rs.getLong("id"));
            p.setCliente(rs.getString("cliente"));
            p.setProductoCodigo(rs.getString("producto_codigo"));
            p.setProductoNombre(rs.getString("producto_nombre"));
            p.setCantidad(rs.getInt("cantidad"));
            p.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
            p.setTotal(rs.getBigDecimal("total"));
            return p;
        };
    }

    public List<Pedido> buscarPedidosPorClienteYTotalMinimo(String cliente, Double totalMinimo) {
        String sql = "SELECT id, cliente, producto_codigo, producto_nombre, cantidad, precio_unitario, total " +
                "FROM pedido " +
                "WHERE cliente LIKE :cliente AND total >= :totalMinimo " +
                "ORDER BY id DESC";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("cliente", "%" + cliente + "%")
                .addValue("totalMinimo", totalMinimo);

        return jdbcTemplate.query(sql, params, pedidoRowMapper);
    }

    public List<ClienteFrecuenteDto> listarClientesFrecuentes(Integer minPedidos) {
        String sql = "SELECT cliente, " +
                "COUNT(id) AS total_pedidos, " +
                "SUM(cantidad) AS total_unidades, " +
                "SUM(total) AS importe_acumulado, " +
                "AVG(total) AS ticket_promedio " +
                "FROM pedido " +
                "GROUP BY cliente " +
                "HAVING COUNT(id) >= :minPedidos " +
                "ORDER BY total_pedidos DESC";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("minPedidos", minPedidos);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new ClienteFrecuenteDto(
                rs.getString("cliente"),
                rs.getLong("total_pedidos"),
                rs.getInt("total_unidades"),
                rs.getBigDecimal("importe_acumulado"),
                rs.getBigDecimal("ticket_promedio")
        ));
    }
}
