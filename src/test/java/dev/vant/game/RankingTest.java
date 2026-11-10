package dev.vant.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankingTest {

    @TempDir
    Path pasta;

    @Test
    void comecaVazio() {
        Ranking ranking = new Ranking(pasta.resolve("ranking.csv"));

        assertTrue(ranking.melhores(5).isEmpty());
        assertEquals(0, ranking.totalPartidas());
    }

    @Test
    void ordenaMelhoresPorPontos() {
        Ranking ranking = new Ranking(pasta.resolve("ranking.csv"));
        ranking.registrar(resultado("Ana", 100, 10));
        ranking.registrar(resultado("Bia", 300, 5));
        ranking.registrar(resultado("Caio", 200, 20));

        List<Resultado> melhores = ranking.melhores(3);

        assertEquals(List.of("Bia", "Caio", "Ana"),
                melhores.stream().map(Resultado::jogador).toList());
    }

    @Test
    void ignoraDerrotasNoRanking() {
        Ranking ranking = new Ranking(pasta.resolve("ranking.csv"));
        ranking.registrar(new Resultado("Ana", Dificuldade.MEDIO, false, 8, 50, 30, 0));

        assertTrue(ranking.melhores(5).isEmpty());
        assertEquals(1, ranking.totalPartidas());
    }

    @Test
    void persisteEntreInstancias() {
        Path arquivo = pasta.resolve("ranking.csv");
        Ranking primeiro = new Ranking(arquivo);
        primeiro.registrar(resultado("Duda", 250, 12));

        Ranking segundo = new Ranking(arquivo);

        assertEquals(1, segundo.totalPartidas());
        assertEquals("Duda", segundo.melhores(1).get(0).jogador());
    }

    @Test
    void ignoraLinhasCorrompidas() throws Exception {
        Path arquivo = pasta.resolve("ranking.csv");
        Files.writeString(arquivo,
                "linha quebrada\n" + resultado("Eva", 150, 7).linhaCsv() + "\n",
                StandardCharsets.UTF_8);

        Ranking ranking = new Ranking(arquivo);

        assertEquals(1, ranking.totalPartidas());
        assertEquals("Eva", ranking.melhores(1).get(0).jogador());
    }

    private static Resultado resultado(String jogador, int pontos, long segundos) {
        return new Resultado(jogador, Dificuldade.MEDIO, true, 3, 42, segundos, pontos);
    }
}
