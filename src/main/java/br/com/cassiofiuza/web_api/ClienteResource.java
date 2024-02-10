package br.com.cassiofiuza.web_api;

import br.com.cassiofiuza.cliente.ClienteFacade;
import br.com.cassiofiuza.cliente.records.Extrato;
import br.com.cassiofiuza.cliente.records.NovaTransacao;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cliente")
public class ClienteResource {
    @Inject
    ClienteFacade clienteFacade;

    @GET
    @Path("{id}/extrato")
    @Produces(MediaType.APPLICATION_JSON)
    public Response visualizarExtrato(@PathParam("id") Integer id) {
        Extrato extrato = this.clienteFacade.buscarExtratoCliente(id);
        return Response.ok().entity(extrato).build();
    }

    @POST
    @Path("{id}/transacoes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response novaTransacao(@PathParam("id") Integer id, NovaTransacao novaTransacao) {
        var resultado = this.clienteFacade.novaTransacao(id, novaTransacao);
        return Response.ok(resultado).build();
    }
}
