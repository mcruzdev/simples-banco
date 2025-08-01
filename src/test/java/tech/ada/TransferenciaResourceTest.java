package tech.ada;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.ada.TransferenciaResource.TransferenciaDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@QuarkusTest
class TransferenciaResourceTest {

    @Inject
    ContaRepository contaRepository;

    @Test
    void deveTransferirCorretamente() {
        List<Conta> contas = contaRepository.listAll();

        Conta contaOrigem = contas.get(0);
        Conta contaDestino = contas.get(1);

        TransferenciaDTO body = new TransferenciaDTO(contaOrigem.getId(), contaDestino.getId(),
                new BigDecimal("100.00"));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .post("/transferencias")
                .then();

        Conta[] contasDepoisDaTransferencia = RestAssured.given()
                .accept(ContentType.JSON)
                .get("/contas")
                .then()
                .extract()
                .body()
                .as(Conta[].class);

        Conta contaOrigemDepoisDaTransferencia = Arrays.stream(contasDepoisDaTransferencia)
                .filter(conta -> conta.getId().equals(contaOrigem.getId()))
                .findAny()
                .get();

        Assertions.assertEquals(new BigDecimal("0.00"), contaOrigemDepoisDaTransferencia.getSaldo());

        Conta contaDestinoDepoisDaTransferencia = Arrays.stream(contasDepoisDaTransferencia)
                .filter(conta -> conta.getId().equals(contaDestino.getId()))
                .findAny()
                .get();

        Assertions.assertEquals(new BigDecimal("200.00"), contaDestinoDepoisDaTransferencia.getSaldo());
    }

    @Test
    void deveExecutar10TransferenciasConcorrentesDe50() throws Exception {
        // Lê as contas pelo repositório (mesmo padrão do teste existente)
        Conta[] contasAntesDaTransferencia = RestAssured.given()
                .accept(ContentType.JSON)
                .get("/contas")
                .then()
                .extract()
                .body()
                .as(Conta[].class);

        Conta origemAntes = contasAntesDaTransferencia[0];
        Conta destinoAntes = contasAntesDaTransferencia[1];

        // Corpo da transferência
        TransferenciaDTO body = new TransferenciaDTO(origemAntes.getId(), destinoAntes.getId(),
                new BigDecimal("50.00"));

        // Dispara 10 requisições concorrentes
        int totalTransferencias = 10;
        ExecutorService pool = Executors.newFixedThreadPool(totalTransferencias);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger sucessos = new AtomicInteger(0);

        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < totalTransferencias; i++) {
            futures.add(pool.submit(() -> {
                try {
                    // Todos começam juntos, aumentando a chance de disputa
                    start.await(5, TimeUnit.SECONDS);

                    var response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .accept(ContentType.JSON)
                            .body(body)
                            .post("/transferencias");

                    int status = response.getStatusCode();
                    if (status == 200) {
                        sucessos.incrementAndGet();
                    } else {
                        // Opcional: log para inspeção
                        // System.out.println("Transferência falhou com status: " + status + " -> " +
                        // response.getBody().asString());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        // Libera as threads ao mesmo tempo
        start.countDown();

        // Aguarda todas terminarem
        for (Future<?> f : futures) {
            f.get(15, TimeUnit.SECONDS);
        }
        pool.shutdown();
        pool.awaitTermination(15, TimeUnit.SECONDS);

        // Lê novamente as contas após as transferências
        Conta[] contasDepois = RestAssured.given()
                .accept(ContentType.JSON)
                .get("/contas")
                .then()
                .statusCode(200)
                .extract()
                .as(Conta[].class);

        Conta origemDepois = Arrays.stream(contasDepois)
                .filter(c -> c.getId().equals(origemAntes.getId()))
                .findAny().orElseThrow();

        Conta destinoDepois = Arrays.stream(contasDepois)
                .filter(c -> c.getId().equals(destinoAntes.getId()))
                .findAny().orElseThrow();

        Assertions.assertEquals(new BigDecimal("0.00"), origemDepois.getSaldo());
        Assertions.assertEquals(new BigDecimal("200.00"), destinoDepois.getSaldo());

    }

}