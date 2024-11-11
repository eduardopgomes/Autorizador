package br.com.fcamara.autorizador.controller;

import br.com.fcamara.autorizador.model.Cartao;
import br.com.fcamara.autorizador.service.CartaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    @Operation(summary = "Cria um novo cartão com saldo inicial de R$500,00")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cartão criado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Cartão já existe")
    })
    @PostMapping
    public ResponseEntity<Map<String, String>> criarCartao(@RequestBody Map<String, String> request) {
        String numeroCartao = request.get("numeroCartao");
        String senha = request.get("senha");

        try {
            Cartao cartao = cartaoService.criarCartao(numeroCartao, senha);
            Map<String, String> response = new HashMap<>();
            response.put("numeroCartao", cartao.getNumeroCartao());
            response.put("senha", cartao.getSenha());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Operation(summary = "Obtém o saldo de um cartão existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo obtido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado")
    })
    @GetMapping("/{numeroCartao}")
    public ResponseEntity<Object> obterSaldo(@PathVariable String numeroCartao) {
        try {
            BigDecimal saldo = cartaoService.obterSaldo(numeroCartao);
            return new ResponseEntity<>(saldo, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
