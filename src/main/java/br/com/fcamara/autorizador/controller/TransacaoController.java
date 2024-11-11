package br.com.fcamara.autorizador.controller;

import br.com.fcamara.autorizador.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Operation(summary = "Realiza uma transação com o cartão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação realizada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Regras de autorização falharam (saldo insuficiente, senha inválida ou cartão inexistente)"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação")
    })
    @PostMapping
    public ResponseEntity<String> realizarTransacao(@RequestBody Map<String, Object> request) {
        try {
            String numeroCartao = (String) request.get("numeroCartao");
            String senhaCartao = (String) request.get("senhaCartao");
            BigDecimal valor = new BigDecimal(request.get("valor").toString());

            String resultado = transacaoService.realizarTransacao(numeroCartao, senhaCartao, valor);
            return new ResponseEntity<>(resultado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
