package br.com.cassiofiuza;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.empty;

import java.util.Collection;
import java.util.Collections;

@QuarkusTest
class ClienteResourceTest {
    @Test
    void deveBuscarExtratoCliente() {
        given()
                .when().get("/cliente/2/extrato")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("limit", equalTo(80000))
                .body("nome", equalTo("zan corp ltda"))
                .body("saldo", equalTo(0))
                .body("ultimas_transacoes", empty());
    }

    @Test
    void deveCriarNovaTransacaoCreditoCliente() {
        var dadosCriacaoTrasacao = """
                {"valor":1000,"tipo":"c","descricao":"teste"}
                """;
        given()
                .contentType(ContentType.JSON)
                .body(dadosCriacaoTrasacao)
                .when().post("/cliente/1/transacoes")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("limit", equalTo(100000))
                .body("saldo", equalTo(1000));
    }
}