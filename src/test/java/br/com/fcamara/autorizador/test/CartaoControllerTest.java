package br.com.fcamara.autorizador.test;

import br.com.fcamara.autorizador.model.Cartao;
import br.com.fcamara.autorizador.repository.CartaoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort; // Atualize este import
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartaoControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CartaoRepository cartaoRepository;

    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        restTemplate = new TestRestTemplate("username", "password");

        cartaoRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        cartaoRepository.deleteAll();
    }

    @Test
    public void testCriarCartao() {
        Map<String, String> request = new HashMap<>();
        request.put("numeroCartao", "6549873025634501");
        request.put("senha", "1234");

        ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:" + port + "/cartoes", request, Map.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(cartaoRepository.findByNumeroCartao("6549873025634501")).isPresent();
    }

    @Test
    public void testObterSaldoCartaoExistente() {
        // Criando um cartão manualmente
        Cartao cartao = new Cartao();
        cartao.setNumeroCartao("6549873025634501");
        cartao.setSenha("1234");
        cartao.setSaldo(new BigDecimal("500.00"));
        cartaoRepository.save(cartao);

        ResponseEntity<BigDecimal> response = restTemplate.getForEntity("http://localhost:" + port + "/cartoes/6549873025634501", BigDecimal.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    public void testObterSaldoCartaoNaoExistente() {
        ResponseEntity<Void> response = restTemplate.getForEntity("http://localhost:" + port + "/cartoes/1234567890123456", Void.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
}
