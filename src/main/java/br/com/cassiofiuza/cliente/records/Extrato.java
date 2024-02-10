package br.com.cassiofiuza.cliente.records;

import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;

public record Extrato (
  Integer id,
  String nome,
  Integer limite,
  Integer saldo,
  @JsonbProperty(value = "ultimas_transacoes")
  List<Transacao> ultimasTransacoes
){}
