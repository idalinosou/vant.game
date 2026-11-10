package dev.vant.game;

public record Resultado(
        String jogador,
        Dificuldade dificuldade,
        boolean venceu,
        int tentativasUsadas,
        int segredo,
        long segundos,
        int pontos) {

    public String linhaCsv() {
        return String.join(";",
                jogador.replace(';', ' '),
                dificuldade.name(),
                Boolean.toString(venceu),
                Integer.toString(tentativasUsadas),
                Integer.toString(segredo),
                Long.toString(segundos),
                Integer.toString(pontos));
    }

    public static Resultado deLinhaCsv(String linha) {
        String[] campos = linha.split(";");
        if (campos.length != 7) {
            throw new IllegalArgumentException("Linha de ranking invalida: " + linha);
        }
        return new Resultado(
                campos[0],
                Dificuldade.valueOf(campos[1]),
                Boolean.parseBoolean(campos[2]),
                Integer.parseInt(campos[3]),
                Integer.parseInt(campos[4]),
                Long.parseLong(campos[5]),
                Integer.parseInt(campos[6]));
    }
}
