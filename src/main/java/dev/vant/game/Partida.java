package dev.vant.game;

public final class Partida {
    private final String jogador;
    private final Dificuldade dificuldade;
    private final int segredo;
    private int tentativasUsadas;
    private boolean encerrada;
    private boolean venceu;

    public Partida(String jogador, Dificuldade dificuldade, int segredo) {
        if (jogador == null || jogador.isBlank()) {
            throw new IllegalArgumentException("O nome do jogador e obrigatorio.");
        }
        if (dificuldade == null) {
            throw new IllegalArgumentException("A dificuldade e obrigatoria.");
        }
        if (!dificuldade.aceita(segredo)) {
            throw new IllegalArgumentException("O segredo esta fora do intervalo da dificuldade.");
        }
        this.jogador = jogador;
        this.dificuldade = dificuldade;
        this.segredo = segredo;
    }

    public Dica palpite(int numero) {
        if (encerrada) {
            throw new IllegalStateException("A partida ja foi encerrada.");
        }
        if (!dificuldade.aceita(numero)) {
            throw new IllegalArgumentException("O palpite esta fora do intervalo da dificuldade.");
        }

        tentativasUsadas++;
        if (numero == segredo) {
            encerrada = true;
            venceu = true;
            return Dica.ACERTOU;
        }
        if (tentativasUsadas >= dificuldade.tentativas()) {
            encerrada = true;
        }
        return numero < segredo ? Dica.MAIOR : Dica.MENOR;
    }

    public Resultado resultado(long segundos) {
        if (!encerrada) {
            throw new IllegalStateException("A partida ainda esta em andamento.");
        }
        int pontos = venceu ? dificuldade.pontos(tentativasUsadas, segundos) : 0;
        return new Resultado(jogador, dificuldade, venceu, tentativasUsadas, segredo, segundos, pontos);
    }

    public String jogador() {
        return jogador;
    }

    public Dificuldade dificuldade() {
        return dificuldade;
    }

    public int segredo() {
        return segredo;
    }

    public int tentativasUsadas() {
        return tentativasUsadas;
    }

    public int tentativasRestantes() {
        return dificuldade.tentativas() - tentativasUsadas;
    }

    public boolean encerrada() {
        return encerrada;
    }

    public boolean venceu() {
        return venceu;
    }
}
