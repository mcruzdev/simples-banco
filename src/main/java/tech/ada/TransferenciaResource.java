package tech.ada;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.math.BigDecimal;
import java.util.Map;

@Path("/transferencias")
public class TransferenciaResource {

    final TransferenciaService transferenciaService;

    public TransferenciaResource(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @POST
    public Response transferir(TransferenciaDTO transferenciaDTO) throws Exception {
        this.transferenciaService.transferir(transferenciaDTO.contaOrigem(), transferenciaDTO.contaDestino(), transferenciaDTO.valor());
        return Response.ok().build();
    }

    @ServerExceptionMapper
    public Response trataContaNaoEncontradaException(ContaNaoEncontradaException ex) {
        return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
    }

    @ServerExceptionMapper
    public Response trataIllegalArgumentException(IllegalArgumentException ex) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", ex.getMessage())).build();
    }

    @ServerExceptionMapper
    public Response trataIllegalStateException(IllegalStateException ex) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", ex.getMessage())).build();
    }

    public record TransferenciaDTO(String contaOrigem, String contaDestino, BigDecimal valor) {
    }
}
