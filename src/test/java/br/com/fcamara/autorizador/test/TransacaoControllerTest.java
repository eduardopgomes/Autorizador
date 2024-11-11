package br.com.fcamara.autorizador.test;

import br.com.fcamara.autorizador.model.Cartao;
import br.com.fcamara.autorizador.repository.CartaoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransacaoControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CartaoRepository cartaoRepository;

    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        restTemplate = new TestRestTemplate("username", "password");

        cartaoRepository.deleteAll();

        Cartao cartao = new Cartao();
        cartao.setNumeroCartao("6549873025634501");
        cartao.setSenha("1234");
        cartao.setSaldo(new BigDecimal("500.00"));
        cartaoRepository.save(cartao);
    }

    @AfterEach
    public void tearDown() {
        cartaoRepository.deleteAll();
    }

    @Test
    public void testRealizarTransacaoComSucesso() {
        Map<String, Object> request = new HashMap<>();
        request.put("numeroCartao", "6549873025634501");
        request.put("senhaCartao", "1234");
        request.put("valor", 100.00);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/transacoes", request, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo("OK");

        Cartao cartao = cartaoRepository.findByNumeroCartao("6549873025634501").orElseThrow();
        assertThat(cartao.getSaldo()).isEqualTo(new BigDecimal("400.00"));
    }

    @Test
    public void testRealizarTransacaoSaldoInsuficiente() {
        Map<String, Object> request = new HashMap<>();
        request.put("numeroCartao", "6549873025634501");
        request.put("senhaCartao", "1234");
        request.put("valor", 600.00);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/transacoes", request, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(422);
        assertThat(response.getBody()).isEqualTo("SALDO_INSUFICIENTE");
    }

    @Test
    public void testRealizarTransacaoSenhaInvalida() {
        Map<String, Object> request = new HashMap<>();
        request.put("numeroCartao", "6549873025634501");
        request.put("senhaCartao", "wrongpassword");
        request.put("valor", 100.00);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/transacoes", request, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(422);
        assertThat(response.getBody()).isEqualTo("SENHA_INVALIDA");
    }

    @Test
    public void testRealizarTransacaoCartaoInexistente() {
        Map<String, Object> request = new HashMap<>();
        request.put("numeroCartao", "1234567890123456");
        request.put("senhaCartao", "1234");
        request.put("valor", 100.00);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/transacoes", request, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(422);
        assertThat(response.getBody()).isEqualTo("CARTAO_INEXISTENTE");
    }
}
