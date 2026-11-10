package dev.vant.game;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class Ranking {
    private final Path arquivo;
    private final List<Resultado> resultados = new ArrayList<>();

    public Ranking(Path arquivo) {
        this.arquivo = arquivo;
        carregar();
    }

    public void registrar(Resultado resultado) {
        resultados.add(resultado);
        salvar();
    }

    public List<Resultado> melhores(int limite) {
        return resultados.stream()
                .filter(Resultado::venceu)
                .sorted(Comparator.comparingInt(Resultado::pontos).reversed()
                        .thenComparingLong(Resultado::segundos))
                .limit(limite)
                .toList();
    }

    public int totalPartidas() {
        return resultados.size();
    }

    private void carregar() {
        if (!Files.exists(arquivo)) {
            return;
        }
        try (Stream<String> linhas = Files.lines(arquivo, StandardCharsets.UTF_8)) {
            linhas.map(String::trim)
                    .filter(linha -> !linha.isEmpty())
                    .forEach(linha -> {
                        try {
                            resultados.add(Resultado.deLinhaCsv(linha));
                        } catch (RuntimeException ignorada) {
                        }
                    });
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler o ranking em " + arquivo, e);
        }
    }

    private void salvar() {
        try {
            Path pasta = arquivo.getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }
            List<String> linhas = resultados.stream()
                    .map(Resultado::linhaCsv)
                    .toList();
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar o ranking em " + arquivo, e);
        }
    }
}
