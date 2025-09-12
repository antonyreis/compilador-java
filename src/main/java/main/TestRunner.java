package main;

import lexer.*;
import parser.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class TestRunner {
    public static void main(String[] args) throws IOException {
        Path pastaSucesso = Paths.get("C:\\projs\\impacta\\compilador-java\\src\\tests\\sucesso");
        Path pastaErro = Paths.get("C:\\projs\\impacta\\compilador-java\\src\\tests\\erro");

        System.out.println("=== TESTES DE SUCESSO ===");
        rodarTestes(pastaSucesso);

        System.out.println("\n=== TESTES DE ERRO ===");
        rodarTestes(pastaErro);
    }

    private static void rodarTestes(Path pasta) throws IOException {
        try (Stream<Path> arquivos = Files.list(pasta)) {
            arquivos
                .filter(Files::isRegularFile)
                .filter(f -> f.toString().endsWith(".java"))
                .forEach(TestRunner::executarTeste);
        }
    }

    private static void executarTeste(Path arquivo) {
        System.out.println("\n>> Rodando: " + arquivo);

        try {
            String codigoJava = Files.readString(arquivo);

            // Etapa léxica
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

            // Etapa sintática
            lexer = new AnalisadorLexico(codigoJava); 
            AnalisadorSintatico parser = new AnalisadorSintatico(lexer);
            parser.programa();

        } catch (ErroSintatico e) {
            System.err.println("Erro sintático: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + arquivo);
        }
    }
}
