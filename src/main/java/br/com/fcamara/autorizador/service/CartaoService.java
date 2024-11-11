package br.com.fcamara.autorizador.service;

import br.com.fcamara.autorizador.model.Cartao;
import br.com.fcamara.autorizador.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    public Cartao criarCartao(String numeroCartao, String senha) {
        Optional<Cartao> cartaoExistente = cartaoRepository.findByNumeroCartao(numeroCartao);
        if (cartaoExistente.isPresent()) {
            throw new IllegalArgumentException("Cartão já existe");
        }
        Cartao novoCartao = new Cartao();
        novoCartao.setNumeroCartao(numeroCartao);
        novoCartao.setSenha(senha);
        novoCartao.setSaldo(new BigDecimal("500.00"));
        return cartaoRepository.save(novoCartao);
    }

    public BigDecimal obterSaldo(String numeroCartao) {
        Cartao cartao = cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new IllegalArgumentException("Cartão não encontrado"));
        return cartao.getSaldo();
    }
}
