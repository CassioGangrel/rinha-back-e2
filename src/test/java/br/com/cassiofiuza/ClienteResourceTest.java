package br.com.cassiofiuza;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;;

@QuarkusTest
class ClienteResourceTest {
    @Test
    void deveBuscarExtratoCliente() {
        given()
                .when().get("/cliente/2/extrato")
                .then()
                .statusCode(200)
                .body("saldo.total", equalTo(0))
                .body("saldo.limite", equalTo(80000))
                .body("saldo.data_extrato", notNullValue())
                .body("ultimas_transacoes", empty());
    }

    @Test
    void deveCriarNovaTransacaoDepositoCliente() {
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

    @Test
    void deveCriarNovaTransacaoDebitoCliente() {
        var dadosCriacaoTrasacao = """
                {"valor":1000,"tipo":"d","descricao":"teste"}
                """;
        given()
                .contentType(ContentType.JSON)
                .body(dadosCriacaoTrasacao)
                .when().post("/cliente/3/transacoes")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("limit", equalTo(1000000))
                .body("saldo", equalTo(-1000));
    }

    @Test
    void NaoDevePermitirTransacaoDebitoAlemDoLimiteCliente() {
        var dadosCriacaoTrasacao = """
                {"valor":10000001,"tipo":"d","descricao":"teste"}
                """;
        given()
                .contentType(ContentType.JSON)
                .body(dadosCriacaoTrasacao)
                .when().post("/cliente/4/transacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void deveManterSaldoConsitenteAposVariasTransacoes() {
        realizarMultiplasOperacoesMantendoSaldoFinalIgualInicial();
        given()
                .when().get("/cliente/5/extrato")
                .then()
                .statusCode(200)
                .body("saldo.total", equalTo(0))
                .body("saldo.limite", equalTo(500000));
    }

    @Test
    void deveTrazerTransacoesOrdenasPorDataRealizacao() {
        realizarMultiplasOperacoesMantendoSaldoFinalIgualInicial();
        JsonObject response = new JsonObject(
                given()
                        .when().get("/cliente/5/extrato")
                        .getBody().asString());
        JsonArray ultimasTransacoes = response.getJsonArray("ultimas_transacoes");

        for (int i = 1; i < ultimasTransacoes.size(); i++) {
            JsonObject transacaoAnterior = ultimasTransacoes.getJsonObject(i - 1);
            JsonObject transasaoPosterior = ultimasTransacoes.getJsonObject(i);
            assertTrue(transacaoAnterior.getInstant("realizada_em")
                    .isAfter(transasaoPosterior.getInstant("realizada_em")));
        }
    }

    private final void realizarMultiplasOperacoesMantendoSaldoFinalIgualInicial() {
        var dadosCriacaoTrasacaoCredito = """
                {"valor":500,"tipo":"c","descricao":"Credito"}
                """;
        var dadosCriacaoTrasacaoDebito = """
                {"valor":500,"tipo":"d","descricao":"Debito"}
                """;
        for (int i = 0; i <= 13; i++) {
            if (i % 2 == 0) {
                given()
                        .contentType(ContentType.JSON)
                        .body(dadosCriacaoTrasacaoCredito)
                        .when().post("/cliente/5/transacoes")
                        .then()
                        .statusCode(200);
            } else {
                given()
                        .contentType(ContentType.JSON)
                        .body(dadosCriacaoTrasacaoDebito)
                        .when().post("/cliente/5/transacoes")
                        .then()
                        .statusCode(200);
            }
        }
    }
}