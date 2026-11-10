package dev.vant.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartidaTest {

    @Test
    void acertaNaPrimeiraTentativa() {
        Partida partida = new Partida("Victor", Dificuldade.MEDIO, 42);

        assertEquals(Dica.ACERTOU, partida.palpite(42));
        assertTrue(partida.venceu());
        assertTrue(partida.encerrada());
        assertEquals(1, partida.tentativasUsadas());
    }

    @Test
    void daDicaDeMaiorEMenor() {
        Partida partida = new Partida("Victor", Dificuldade.MEDIO, 42);

        assertEquals(Dica.MAIOR, partida.palpite(10));
        assertEquals(Dica.MENOR, partida.palpite(90));
        assertFalse(partida.encerrada());
        assertEquals(2, partida.tentativasUsadas());
    }

    @Test
    void encerraAposEsgotarTentativas() {
        Partida partida = new Partida("Ana", Dificuldade.FACIL, 50);

        for (int i = 0; i < Dificuldade.FACIL.tentativas(); i++) {
            partida.palpite(1);
        }

        assertTrue(partida.encerrada());
        assertFalse(partida.venceu());
        assertEquals(0, partida.tentativasRestantes());
    }

    @Test
    void recusaPalpiteForaDoIntervalo() {
        Partida partida = new Partida("Bia", Dificuldade.MEDIO, 50);

        assertThrows(IllegalArgumentException.class, () -> partida.palpite(0));
        assertThrows(IllegalArgumentException.class, () -> partida.palpite(101));
        assertEquals(0, partida.tentativasUsadas());
    }

    @Test
    void recusaPalpiteDepoisDeEncerrada() {
        Partida partida = new Partida("Caio", Dificuldade.MEDIO, 50);
        partida.palpite(50);

        assertThrows(IllegalStateException.class, () -> partida.palpite(10));
    }

    @Test
    void geraResultadoComPontosApenasNaVitoria() {
        Partida vitoria = new Partida("Duda", Dificuldade.MEDIO, 42);
        vitoria.palpite(42);
        Resultado ganhou = vitoria.resultado(5);
        assertTrue(ganhou.venceu());
        assertTrue(ganhou.pontos() > 0);

        Partida derrota = new Partida("Duda", Dificuldade.FACIL, 50);
        for (int i = 0; i < Dificuldade.FACIL.tentativas(); i++) {
            derrota.palpite(1);
        }
        assertEquals(0, derrota.resultado(5).pontos());
    }

    @Test
    void exigePartidaEncerradaParaGerarResultado() {
        Partida partida = new Partida("Eva", Dificuldade.MEDIO, 42);

        assertThrows(IllegalStateException.class, () -> partida.resultado(1));
    }

    @Test
    void recusaSegredoForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class, () -> new Partida("Eva", Dificuldade.MEDIO, 500));
    }
}
