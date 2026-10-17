package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.controller.dto.AnulacionRequest;
import com.isil.edu.pe.spedidos.controller.dto.AnulacionResponse;
import com.isil.edu.pe.spedidos.service.AnulacionPedidoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/anulaciones")
public class AnulacionPedidoController {

  private final AnulacionPedidoService anulacionPedidoService;

  public AnulacionPedidoController(AnulacionPedidoService anulacionPedidoService) {
    this.anulacionPedidoService = anulacionPedidoService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AnulacionResponse anular(@RequestBody AnulacionRequest request) {
    return AnulacionResponse.from(
        anulacionPedidoService.anularPedido(request.pedidoId(), request.motivo())
    );
  }

  @GetMapping
  public List<AnulacionResponse> listar() {
    return anulacionPedidoService.listarAnulaciones()
        .stream()
        .map(AnulacionResponse::from)
        .toList();
  }
}
