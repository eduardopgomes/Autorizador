package br.com.fcamara.autorizador.service;

import br.com.fcamara.autorizador.model.Cartao;
import br.com.fcamara.autorizador.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class TransacaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Transactional
    public String realizarTransacao(String numeroCartao, String senhaCartao, BigDecimal valor) {
        Cartao cartao = cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new IllegalArgumentException("CARTAO_INEXISTENTE"));

        if (!cartao.getSenha().equals(senhaCartao)) {
            throw new IllegalArgumentException("SENHA_INVALIDA");
        }

        if (cartao.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("SALDO_INSUFICIENTE");
        }

        cartao.setSaldo(cartao.getSaldo().subtract(valor));
        cartaoRepository.save(cartao);

        return "OK";
    }
}
