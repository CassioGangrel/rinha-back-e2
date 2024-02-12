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
    if (exception instanceof BadRequestException) {
      return Response.status(422).build();
    }
    if (exception instanceof NotFoundException) {
      return Response.status(404).build();
    }
    return Response.status(500).build();
  }
  
}
