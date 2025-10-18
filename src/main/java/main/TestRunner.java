package main;

import lexer.*;
import parser.*;
import semantic.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * Executor de testes para o compilador
 * Roda todos os casos de teste das pastas sucesso e erro
 */
public class TestRunner {
    public static void main(String[] args) throws IOException {
        // AJUSTE ESTES CAMINHOS PARA O SEU AMBIENTE
        Path pastaSucesso = Paths.get("src/tests/sucesso");
        Path pastaErro = Paths.get("src/tests/erro");

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   COMPILADOR JAVA - EXECUÇÃO TESTES   ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        System.out.println("=== TESTES DE SUCESSO ===");
        rodarTestes(pastaSucesso);

        System.out.println("\n=== TESTES DE ERRO ===");
        rodarTestes(pastaErro);
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        EXECUÇÃO FINALIZADA             ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    /**
     * Roda todos os testes de uma pasta
     */
    private static void rodarTestes(Path pasta) throws IOException {
        if (!Files.exists(pasta)) {
            System.err.println("❌ Pasta não encontrada: " + pasta);
            return;
        }
        
        try (Stream<Path> arquivos = Files.list(pasta)) {
            arquivos
                .filter(Files::isRegularFile)
                .filter(f -> f.toString().endsWith(".java"))
                .sorted()
                .forEach(TestRunner::executarTeste);
        }
    }

    /**
     * Executa um teste individual
     */
    private static void executarTeste(Path arquivo) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📄 Arquivo: " + arquivo.getFileName());
        System.out.println("=".repeat(60));

        try {
            String codigoJava = Files.readString(arquivo);

            // Análise completa: Léxica + Sintática + Semântica + Geração de Código
            AnalisadorLexico lexer = new AnalisadorLexico(codigoJava);
            AnalisadorSintatico parser = new AnalisadorSintatico(lexer);
            
            // Executa análise
            parser.programa();

        } catch (ErroSemantico e) {
            System.err.println("\n❌ ERRO SEMÂNTICO:");
            System.err.println("   " + e.getMessage());
        } catch (ErroSintatico e) {
            System.err.println("\n❌ ERRO SINTÁTICO:");
            System.err.println("   " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\n❌ ERRO INESPERADO:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        }
    }
}