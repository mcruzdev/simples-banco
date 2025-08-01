package tech.ada;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@ApplicationScoped
public class TransferenciaService {

    final ContaRepository contaRepository;

    public TransferenciaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @Transactional
    public void transferir(String idContaOrigem, String idContaDestino, BigDecimal valor) throws Exception {

        var contaOrigem = contaRepository.findByIdOptional(idContaOrigem)
                .orElseThrow(() -> new ContaNaoEncontradaException(
                        "Conta com ID '%s' não existe".formatted(idContaOrigem)));

        var contaDestino = contaRepository.findByIdOptional(idContaDestino)
                .orElseThrow(() -> new ContaNaoEncontradaException(
                        "Conta com ID '%s' não existe".formatted(idContaDestino)));

        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo.");
        }
        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new IllegalStateException("Saldo insuficiente na conta de origem.");
        }

        // simulação de atraso para demonstrar concorrência
        // em um cenário real, isso não deve ser feito, pois pode causar problemas de performance
        // e não é uma prática recomendada em produção.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
    }
}
