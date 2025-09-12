package main;

import lexer.*;
import parser.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java Main <arquivo.java>");
            return;
        }

        String codigoJava = Files.readString(Path.of(args[0]));

        AnalisadorLexico lexer = new AnalisadorLexico(codigoJava);

        System.out.println("=== ANÁLISE LÉXICA ===");
        Token token;
        do {
            token = lexer.proximoToken();
            System.out.printf("%-20s | %-15s | Linha: %2d | Coluna: %2d%n",
                    token.getTipo(),
                    token.getValor(),
                    token.getLinha(),
                    token.getColuna());
        } while (token.getTipo() != TipoToken.EOF);

        if (!lexer.getErros().isEmpty()) {
            System.out.println("\n=== ERROS LÉXICOS ===");
            for (ErroLexico erro : lexer.getErros()) {
                System.out.println(erro);
            }
        }

        System.out.println("\n=== ANÁLISE SINTÁTICA ===");
        try {
            lexer = new AnalisadorLexico(codigoJava); // reinicia
            AnalisadorSintatico parser = new AnalisadorSintatico(lexer);
            parser.programa();
        } catch (ErroSintatico e) {
            System.err.println(e.getMessage());
        }
    }
}
