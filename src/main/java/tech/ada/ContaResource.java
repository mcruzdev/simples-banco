package tech.ada;

import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/contas")
public class ContaResource {

    final ContaRepository contaRepository;

    public ContaResource(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @POST
    public Response criaConta() {
        Conta conta = new Conta("Matheus Cruz");

        QuarkusTransaction.requiringNew()
                .timeout(1)
                .run(() -> {
                    this.contaRepository.persist(conta);
                });

        return Response.created(URI.create("/contas/" + conta.getId())).build();
    }

    @GET
    public Response listaTodasAsContas() {
        return Response.ok(this.contaRepository.listAll()).build();
    }
}
