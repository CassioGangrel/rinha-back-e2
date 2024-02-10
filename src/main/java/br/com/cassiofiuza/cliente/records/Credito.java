package br.com.cassiofiuza.cliente.records;

import br.com.cassiofiuza.apoio.Resultado;
import jakarta.ws.rs.BadRequestException;

public record Credito(
    int saldo,
    int limit) {

  public Credito depositar(int valor) {
    return new Credito(this.saldo + valor, this.limit);
  }

  public Resultado<Credito, String> sacar(int valor) {
    var novoSaldo = this.saldo - valor;
    if (novoSaldo < (this.limit * -1)) {
      return Resultado.falha("Credito insuficiete");
    }
    return Resultado.sucesso(new Credito(novoSaldo, this.limit));
  }

  public Credito aplicaOperacao(char tipo, int valor) {
    switch (tipo) {
      case 'c':
        return this.depositar(valor);
      case 'd':
        Resultado<Credito, String> resultado = this.sacar(valor);
        if (resultado.temSucesso()) {
          return resultado.sucesso();
        }
        throw new BadRequestException("Credito insuficiente!");
      default:
        throw new BadRequestException("Tipo transacao invalido!");
    }
  }
}
