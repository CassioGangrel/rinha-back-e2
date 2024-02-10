package br.com.cassiofiuza.cliente.records;

import java.util.List;

public record Extrato (
  Integer id,
  String nome,
  Integer limite,
  Integer saldo,
  List<Transacao> ultimasTransacoes
){}
