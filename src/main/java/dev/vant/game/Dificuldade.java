package dev.vant.game;

public enum Dificuldade {
    FACIL("Facil", 1, 50, 12, 150),
    MEDIO("Medio", 1, 100, 8, 300),
    DIFICIL("Dificil", 1, 500, 9, 500);

    private final String rotulo;
    private final int minimo;
    private final int maximo;
    private final int tentativas;
    private final int pontosBase;

    Dificuldade(String rotulo, int minimo, int maximo, int tentativas, int pontosBase) {
        this.rotulo = rotulo;
        this.minimo = minimo;
        this.maximo = maximo;
        this.tentativas = tentativas;
        this.pontosBase = pontosBase;
    }

    public String rotulo() {
        return rotulo;
    }

    public int minimo() {
        return minimo;
    }

    public int maximo() {
        return maximo;
    }

    public int tentativas() {
        return tentativas;
    }

    public boolean aceita(int numero) {
        return numero >= minimo && numero <= maximo;
    }

    public int pontos(int tentativasUsadas, long segundos) {
        int bonusTentativas = Math.max(0, tentativas - tentativasUsadas) * 10;
        int bonusTempo = (int) Math.max(0, 30 - segundos);
        return Math.max(10, pontosBase + bonusTentativas + bonusTempo);
    }
}
