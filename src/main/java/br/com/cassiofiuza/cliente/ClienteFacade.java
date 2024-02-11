package br.com.cassiofiuza.cliente;

import br.com.cassiofiuza.cliente.records.Credito;
import br.com.cassiofiuza.cliente.records.Extrato;
import br.com.cassiofiuza.cliente.records.NovaTransacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ClienteFacade {
  @Inject
  ClienteDao repository;

  public Extrato buscarExtratoCliente(Integer id) {
    return this.repository.buscarExtratoCliente(id);
  }

  @Transactional
  public Credito novaTransacao(Integer id, NovaTransacao novaTransacao) {
    novaTransacao.check();
    Credito credito = this.repository.buscarSaldoCliente(id);
    char tipoTransacao = Character.toLowerCase(novaTransacao.tipo().charAt(0));
    Credito novoCredito = credito.aplicaOperacao(tipoTransacao, novaTransacao.valor());
    this.repository.novaTransacao(id, novaTransacao);
    this.repository.atualizarSaldoCliente(id, novoCredito.saldo());
    return novoCredito;
  }
  
}
