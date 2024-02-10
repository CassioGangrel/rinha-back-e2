package br.com.cassiofiuza.cliente.records;

import java.time.LocalDateTime;

public record Transacao (
  Integer id,
  Integer value,
  Character tipo,
  String descricao,
  LocalDateTime realizadaEm
){}
