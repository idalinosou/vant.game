package dev.vant.game;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public final class VantGame {
    private static final int TOP_RANKING = 5;
    private static final Path ARQUIVO_RANKING = Path.of("dados", "ranking.csv");

    public static void main(String[] args) {
        Console console = new Console();
        Ranking ranking = new Ranking(ARQUIVO_RANKING);

        try {
            mostrarTitulo();
            String nome = console.texto("Qual e o seu nome? ");
            if (nome.isBlank()) {
                nome = "jogador";
            }

            boolean sair = false;
            while (!sair) {
                mostrarMenu();
                int opcao = console.inteiro("Opcao: ", 0, 2);
                switch (opcao) {
                    case 1 -> jogarPartida(console, ranking, nome, args);
                    case 2 -> mostrarRanking(ranking);
                    default -> sair = true;
                }
            }
            System.out.println("Ate a proxima, " + nome + "!");
        } catch (EntradaEncerradaException e) {
            System.out.println();
            System.out.println("Entrada encerrada. Ate a proxima!");
        }
    }

    private static void jogarPartida(Console console, Ranking ranking, String nome, String[] args) {
        Dificuldade dificuldade = escolherDificuldade(console);
        Partida partida = new Partida(nome, dificuldade, sortearSegredo(dificuldade, args));

        System.out.println();
        System.out.println("Pensei em um numero entre " + dificuldade.minimo() + " e " + dificuldade.maximo() + ".");
        System.out.println("Voce tem " + dificuldade.tentativas() + " tentativas.");

        long inicio = System.nanoTime();
        while (!partida.encerrada()) {
            int palpite = console.inteiro(
                    "Tentativa " + (partida.tentativasUsadas() + 1) + "/" + dificuldade.tentativas()
                            + " - seu palpite: ",
                    dificuldade.minimo(), dificuldade.maximo());

            Dica dica = partida.palpite(palpite);
            if (dica == Dica.MAIOR) {
                System.out.println("O numero e MAIOR que " + palpite + ".");
            } else if (dica == Dica.MENOR) {
                System.out.println("O numero e MENOR que " + palpite + ".");
            }
        }
        long segundos = Math.max(1, (System.nanoTime() - inicio) / 1_000_000_000L);

        Resultado resultado = partida.resultado(segundos);
        ranking.registrar(resultado);

        if (resultado.venceu()) {
            System.out.println("Acertou, " + nome + "! O numero era " + resultado.segredo() + ".");
            System.out.println("Tempo: " + segundos + "s | Tentativas: " + resultado.tentativasUsadas()
                    + " | Pontos: " + resultado.pontos());
        } else {
            System.out.println("Suas tentativas acabaram. O numero era " + resultado.segredo() + ".");
        }
        System.out.println();
    }

    private static Dificuldade escolherDificuldade(Console console) {
        System.out.println("Escolha a dificuldade:");
        Dificuldade[] opcoes = Dificuldade.values();
        for (int i = 0; i < opcoes.length; i++) {
            Dificuldade d = opcoes[i];
            System.out.println((i + 1) + " - " + d.rotulo() + " (" + d.minimo() + " a " + d.maximo()
                    + ", " + d.tentativas() + " tentativas)");
        }
        int escolha = console.inteiro("Dificuldade: ", 1, opcoes.length);
        return opcoes[escolha - 1];
    }

    private static void mostrarRanking(Ranking ranking) {
        List<Resultado> melhores = ranking.melhores(TOP_RANKING);
        System.out.println();
        if (melhores.isEmpty()) {
            System.out.println("Ranking vazio. Jogue uma partida para entrar!");
            System.out.println();
            return;
        }
        System.out.println("--- TOP " + TOP_RANKING + " ---");
        for (int i = 0; i < melhores.size(); i++) {
            Resultado r = melhores.get(i);
            System.out.printf("%d. %-12s %-7s %3ds %4d pts%n",
                    i + 1, r.jogador(), r.dificuldade().rotulo(), r.segundos(), r.pontos());
        }
        System.out.println("Partidas registradas: " + ranking.totalPartidas());
        System.out.println();
    }

    private static void mostrarTitulo() {
        System.out.println("================================");
        System.out.println("         VANT.GAME  v2.0");
        System.out.println("  Adivinhe o numero que eu pensei");
        System.out.println("================================");
    }

    private static void mostrarMenu() {
        System.out.println("--------------------------------");
        System.out.println("1 - Jogar");
        System.out.println("2 - Ranking");
        System.out.println("0 - Sair");
    }

    // Permite fixar o numero para testes e demonstracoes: java -jar vant-game.jar 42
    private static int sortearSegredo(Dificuldade dificuldade, String[] args) {
        if (args.length > 0) {
            try {
                int fixo = Integer.parseInt(args[0]);
                if (dificuldade.aceita(fixo)) {
                    return fixo;
                }
            } catch (NumberFormatException ignorada) {
            }
        }
        int intervalo = dificuldade.maximo() - dificuldade.minimo() + 1;
        return new Random().nextInt(intervalo) + dificuldade.minimo();
    }
}
