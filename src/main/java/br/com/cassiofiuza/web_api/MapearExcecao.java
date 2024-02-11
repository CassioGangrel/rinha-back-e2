package br.com.cassiofiuza.web_api;

import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MapearExcecao implements ExceptionMapper<Exception> {

  @Override
  public Response toResponse(Exception exception) {
    JsonObject response = new JsonObject();
    if (exception instanceof BadRequestException badRequestException) {
      response.put("mensagem", badRequestException.getMessage());
      return Response.status(422).entity(response).build();
    }
    if (exception instanceof NotFoundException notFoundException) {
      response.put("mensagem", notFoundException.getMessage());
      return Response.status(404).entity(response).build();
    }
    response.put("mensagem", "Sinto muito mas não foi possivel atenter a sua solicação!");
    return Response.status(500).entity(response).build();
  }
  
}
