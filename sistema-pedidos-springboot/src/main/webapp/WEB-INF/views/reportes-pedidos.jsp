<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Módulo de Reportes - Sistema de Pedidos</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="container mt-4">
    <h2>Módulo de Consulta y Supervisión de Pedidos</h2>
    <hr/>

    <!-- Formulario Búsqueda Avanzada -->
    <div class="card mb-4">
        <div class="card-header font-weight-bold">Búsqueda Avanzada de Pedidos</div>
        <div class="card-body">
            <form action="/reportes/pedidos" method="GET" class="row g-3">
                <div class="col-md-5">
                    <input type="text" name="cliente" value="${clienteFiltro}" class="form-control" placeholder="Nombre del cliente" required>
                </div>
                <div class="col-md-4">
                    <input type="number" step="0.01" name="totalMinimo" value="${totalMinimoFiltro}" class="form-control" placeholder="Monto mínimo">
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-primary">Buscar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Resultados Búsqueda Avanzada -->
    <c:if test="${not empty pedidosBusqueda}">
        <h3>Resultados de Búsqueda</h3>
        <table class="table table-striped mb-4">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Precio Unitario</th>
                    <th>Total</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${pedidosBusqueda}">
                    <tr>
                        <td>${p.id}</td>
                        <td>${p.cliente}</td>
                        <td>${p.productoNombre}</td>
                        <td>${p.cantidad}</td>
                        <td>S/ ${p.precioUnitario}</td>
                        <td>S/ ${p.total}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <!-- Clientes Frecuentes -->
    <h3 class="mt-4">Clientes Frecuentes</h3>
    <table class="table table-bordered">
        <thead class="table-dark">
            <tr>
                <th>Cliente</th>
                <th>Total Pedidos</th>
                <th>Total Unidades</th>
                <th>Importe Acumulado</th>
                <th>Ticket Promedio</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="cf" items="${clientesFrecuentes}">
                <tr>
                    <td>${cf.cliente}</td>
                    <td>${cf.totalPedidos}</td>
                    <td>${cf.totalUnidades}</td>
                    <td>S/ ${cf.importeAcumulado}</td>
                    <td>S/ ${cf.ticketPromedio}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
