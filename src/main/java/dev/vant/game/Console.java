package dev.vant.game;

import java.util.Scanner;

public final class Console {
    private final Scanner scanner = new Scanner(System.in);

    public String texto(String mensagem) {
        System.out.print(mensagem);
        return proximaLinha();
    }

    public int inteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            String entrada = texto(mensagem);
            try {
                int valor = Integer.parseInt(entrada);
                if (valor < minimo || valor > maximo) {
                    System.out.println("Digite um numero entre " + minimo + " e " + maximo + ".");
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Isso nao e um numero. Tente de novo.");
            }
        }
    }

    private String proximaLinha() {
        if (!scanner.hasNextLine()) {
            throw new EntradaEncerradaException();
        }
        return scanner.nextLine().trim();
    }
}
