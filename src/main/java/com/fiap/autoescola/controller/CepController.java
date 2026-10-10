package com.fiap.autoescola.controller;

import com.fiap.autoescola.dto.ViaCepResponse;
import com.fiap.autoescola.service.CepService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/enderecos")
@Tag(name="API externa ViaCEP")
public class CepController {
    private final CepService service;
    public CepController(CepService service){this.service=service;}
    @GetMapping("/{cep}")
    @Operation(summary="Consultar endereço na API pública ViaCEP")
    public ViaCepResponse consultar(@PathVariable String cep){return service.consultar(cep);}
}
