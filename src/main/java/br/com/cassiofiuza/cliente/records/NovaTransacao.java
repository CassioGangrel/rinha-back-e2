package br.com.cassiofiuza.cliente.records;

public record NovaTransacao(
    int valor,
    String tipo,
    String descricao) {
}
