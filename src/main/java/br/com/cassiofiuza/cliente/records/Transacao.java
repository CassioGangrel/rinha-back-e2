package br.com.cassiofiuza.cliente.records;

import java.time.Instant;

public record Transacao (
  Integer id,
  Integer valor,
  Character tipo,
  String descricao,
  Instant realizadaEm
){}
