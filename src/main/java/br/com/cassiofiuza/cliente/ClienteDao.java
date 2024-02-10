package br.com.cassiofiuza.cliente;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import br.com.cassiofiuza.cliente.records.Credito;
import br.com.cassiofiuza.cliente.records.Extrato;
import br.com.cassiofiuza.cliente.records.NovaTransacao;
import br.com.cassiofiuza.cliente.records.Transacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;

@ApplicationScoped
class ClienteDao {
  @Inject
  DataSource ds;

  Extrato buscarExtratoCliente(Integer id) {
    try (Connection connection = ds.getConnection()) {
      try (PreparedStatement ps = connection.prepareStatement(
          BuscarExtradoClietne.QUERY_TEMPLATE,
          TYPE_SCROLL_SENSITIVE,
          CONCUR_UPDATABLE)) {
        ps.setInt(BuscarExtradoClietne.ID_CLIENTE, id);
        try (ResultSet rs = ps.executeQuery()) {
          if (!rs.first()) {
            throw new BadRequestException();
          }
          Extrato cliente = new Extrato(
              rs.getInt("c_id"),
              rs.getString("c_nome"),
              rs.getInt("c_limite"),
              rs.getInt("s_valor"),
              new ArrayList<>());
          Callable<Optional<Transacao>> construirTransacao = () -> {
            Integer idTransacao = rs.getInt("t_id");
            if (idTransacao != 0) {
              Transacao transacao = new Transacao(
                  idTransacao,
                  rs.getInt("t_valor"),
                  rs.getString("t_tipo").charAt(0),
                  rs.getString("t_descricao"),
                  rs.getTimestamp("t_realizada_em").toInstant());
              return Optional.of(transacao);
            }
            return Optional.empty();
          };
          construirTransacao.call().ifPresent(cliente.ultimasTransacoes()::add);
          while (rs.next()) {
            construirTransacao.call().ifPresent(cliente.ultimasTransacoes()::add);
          }
          return cliente;
        }
      }
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }

  int novaTransacao(Integer id, NovaTransacao novaTransacao) {
    try (Connection conn = this.ds.getConnection()) {
      try (PreparedStatement prst = conn.prepareStatement(InsereNovaTransacao.QUERY_TEMPLATE,
          Statement.RETURN_GENERATED_KEYS)) {
        prst.setInt(InsereNovaTransacao.ID_CLIENTE, id);
        prst.setInt(InsereNovaTransacao.VALOR, novaTransacao.valor());
        prst.setString(InsereNovaTransacao.TIPO, novaTransacao.tipo());
        prst.setString(InsereNovaTransacao.DESCRICAO, novaTransacao.descricao());
        prst.executeUpdate();
        try (ResultSet generatedKeys = prst.getGeneratedKeys()) {
          generatedKeys.next();
          return generatedKeys.getInt("id");
        }
      }
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }

  boolean atualizarSaldoCliente(Integer id, int novoSaldo) {
    try (Connection conn = this.ds.getConnection()) {
      try (PreparedStatement prst = conn.prepareStatement(
          AtualizarSaldoCliente.QUERY_TEMPLATE, TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE)) {
        prst.setInt(AtualizarSaldoCliente.VALOR, novoSaldo);
        prst.setInt(AtualizarSaldoCliente.ID_CLIENTE, id);
        return prst.executeUpdate() != 0;
      }
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }

  Credito buscarSaldoCliente(Integer id) {
    try (Connection conn = this.ds.getConnection()) {
      try (PreparedStatement prst = conn.prepareStatement(
          BuscarSaldoCliente.QUERY_TEMPLATE, TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE)) {
        prst.setInt(BuscarSaldoCliente.ID_CLIENTE, id);
        try (ResultSet rs = prst.executeQuery()) {
          rs.next();
          return new Credito(rs.getInt(1), rs.getInt(2));
        }
      }
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }

  private final static class BuscarExtradoClietne {
    final static String QUERY_TEMPLATE = """
        select
          c.id as c_id,
          c.nome as c_nome,
          c.limite as c_limite,
          t.id as t_id,
          t.valor as t_valor,
          t.tipo as t_tipo,
          t.descricao as t_descricao,
          t.realizada_em as t_realizada_em,
          s.valor as s_valor
        from
          clientes c
        left join transacoes t on
          t.cliente_id = c.id
        inner join saldos s on
          s.cliente_id = c.id
        where
          c.id = ?
        order by t.realizada_em desc
        limit 10;
        """;

    final static int ID_CLIENTE = 1;
  }

  private static final class InsereNovaTransacao {
    final static String QUERY_TEMPLATE = """
        insert into transacoes
            (cliente_id,valor,tipo,descricao)
        values
            (?,?,?,?);
        """;

    final static int ID_CLIENTE = 1;
    final static int VALOR = 2;
    final static int TIPO = 3;
    final static int DESCRICAO = 4;

  }

  private final static class BuscarSaldoCliente {
    static final String QUERY_TEMPLATE = """
        select
          s.valor as saldo,
          c.limite as limite
        from
          saldos s
        inner join clientes c
          on c.id = s.cliente_id
        where
          s.cliente_id = ?;
        """;

    static final int ID_CLIENTE = 1;
  }

  private final static class AtualizarSaldoCliente {
    static final String QUERY_TEMPLATE = """
        update saldos s
        set valor=?
        where
          s.cliente_id = ?;
        """;

    static final int VALOR = 1;
    static final int ID_CLIENTE = 2;
  }
}
