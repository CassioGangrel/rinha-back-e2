package br.com.cassiofiuza.cliente.records;

import jakarta.ws.rs.BadRequestException;

public record NovaTransacao(
    int valor,
    String tipo,
    String descricao) {

    public NovaTransacao(String valor, String tipo, String descricao) {
        this(
            transfomarParaInteroSomenteSeForDecimal(valor),
            tipo,
            descricao
        );
    }

    public void check() {
        if (tipo == null || !(tipo.equalsIgnoreCase("c") || tipo.equalsIgnoreCase("d"))) {
            throw new BadRequestException("Tipo precisa ser 'c' ou 'd'");
        }
        if (descricao == null ||  descricao.length() > 10 || descricao.length() < 1) {
            throw new BadRequestException("Descrição deve ter 1 ou 10 caracteres.");
        }
    }

    private static int transfomarParaInteroSomenteSeForDecimal(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Valor deve ser estritamente um inteiro");
        }
    }
}
