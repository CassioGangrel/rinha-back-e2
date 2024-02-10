package br.com.cassiofiuza.apoio;

import static java.util.Objects.nonNull;

public record Resultado<T, E>(
    T sucesso,
    E falha) {

  public boolean temSucesso() {
    return nonNull(this.sucesso);
  }

  public boolean temFalha() {
    return nonNull(this.falha);
  }

  public static <T, E> Resultado<T, E> falha(E falha) {
    return new Resultado<>(null, falha);
  }

  public static <T, E> Resultado<T, E> sucesso(T sucesso) {
    return new Resultado<>(sucesso, null);
  }
}
