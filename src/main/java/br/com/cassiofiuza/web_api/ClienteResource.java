package br.com.cassiofiuza.web_api;

import br.com.cassiofiuza.cliente.ClienteFacade;
import br.com.cassiofiuza.cliente.records.Extrato;
import br.com.cassiofiuza.cliente.records.NovaTransacao;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
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
        JsonObject saldo = new JsonObject();
        saldo.put("total", extrato.saldo());
        saldo.put("data_extrato", extrato.saldo());
        saldo.put("limite", extrato.limite());
        JsonArray ultimasTransacoes = extrato.ultimasTransacoes().stream().map(transacao -> {
            JsonObject transacoes = new JsonObject();
            transacoes.put("valor", transacao.valor());
            transacoes.put("tipo", transacao.tipo());
            transacoes.put("descricao", transacao.descricao());
            transacoes.put("realizada_em", transacao.realizadaEm());
            return transacoes;
        }).reduce(new JsonArray(), (acc, next) -> acc.add(next), (acc, next) -> acc);
        JsonObject response = new JsonObject();
        response.put("saldo", saldo);
        response.put("ultimas_transacoes", ultimasTransacoes);
        return Response.ok().entity(response).build();
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
