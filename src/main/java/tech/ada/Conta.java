package tech.ada;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "contas")
public class Conta {

    @Id
    private String id;
    private BigDecimal saldo;
    private String nome;

    protected Conta() {
    }

    public Conta(String nome) {
        this.id = UUID.randomUUID().toString();
        this.saldo = BigDecimal.ZERO;
        this.nome = Objects.requireNonNull(nome, "nome não deve ser nulo");
    }

    public String getId() {
        return id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getNome() {
        return nome;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "Conta{" +
                "id='" + id + '\'' +
                ", saldo=" + saldo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
