package br.com.cassiofiuza.cliente;

import jakarta.ws.rs.BadRequestException;

public enum TipoTransacao {
  CREDITO('c'),
  DEBITO('d');

  private char tipo;

  TipoTransacao(char tipo) {
    this.tipo = tipo;
  }

  public static TipoTransacao valueOf(char tipo) {
    for (TipoTransacao tipoTransacao :TipoTransacao.values()) {
      if (tipo == tipoTransacao.tipo) {
        return tipoTransacao;
      }
    }
    throw new BadRequestException("Tipo de transacao invalido!");
  }
}
