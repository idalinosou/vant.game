package dev.vant.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DificuldadeTest {

    @Test
    void intervalosSaoValidos() {
        for (Dificuldade d : Dificuldade.values()) {
            assertTrue(d.minimo() < d.maximo());
            assertTrue(d.tentativas() > 0);
            assertTrue(d.pontos(1, 1) >= 10);
        }
    }

    @Test
    void aceitaSomenteDentroDoIntervalo() {
        Dificuldade medio = Dificuldade.MEDIO;

        assertTrue(medio.aceita(1));
        assertTrue(medio.aceita(100));
        assertFalse(medio.aceita(0));
        assertFalse(medio.aceita(101));
    }

    @Test
    void vitoriaRapidaValeMaisPontos() {
        int rapida = Dificuldade.MEDIO.pontos(1, 5);
        int lenta = Dificuldade.MEDIO.pontos(7, 40);

        assertTrue(rapida > lenta);
    }
}
