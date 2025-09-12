package main;

import lexer.*;
import parser.*;

public class Main {
    public static void main(String[] args) {
        String codigoJava = """
            public class HelloWorld {
                public static void main(String[] args) {
                    int numero = 42;
                    double valor = 3.14159;
                    String mensagem = "Olá, mundo!";
                    
                    if (numero > 0) {
                        System.out.println(mensagem);
                    }
                }
            }
            """;

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
            lexer = new AnalisadorLexico(codigoJava); // reinicia para o parser
            AnalisadorSintatico parser = new AnalisadorSintatico(lexer);
            parser.programa();
        } catch (ErroSintatico e) {
            System.err.println(e.getMessage());
        }
    }
}
