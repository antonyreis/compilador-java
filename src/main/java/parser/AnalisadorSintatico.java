package parser;

import lexer.AnalisadorLexico;
import lexer.Token;
import lexer.TipoToken;
import semantic.ErroSemantico;
import semantic.TabelaSimbolos;
import java.util.ArrayList;
import java.util.List;

/**
 * Analisador Sintático com Análise Semântica e Geração de Código MEPA
 * Responsável por:
 * - Verificar a estrutura sintática do programa
 * - Validar semântica (variáveis declaradas, sem duplicação)
 * - Gerar código intermediário MEPA
 */
public class AnalisadorSintatico {
    private final AnalisadorLexico lexer;
    private Token tokenAtual;
    private TabelaSimbolos tabelaSimbolos;
    private List<String> codigoMepa;
    private int contadorRotulo;

    public AnalisadorSintatico(AnalisadorLexico lexer) {
        this.lexer = lexer;
        this.tokenAtual = lexer.proximoToken();
        this.tabelaSimbolos = new TabelaSimbolos();
        this.codigoMepa = new ArrayList<>();
        this.contadorRotulo = 1;
    }

    /**
     * Gera próximo número de rótulo para desvios
     */
    private int proximoRotulo() {
        return contadorRotulo++;
    }

    /**
     * Emite uma instrução MEPA
     */
    private void emitir(String instrucao) {
        codigoMepa.add(instrucao);
    }

    /**
     * Avança se for o token esperado, senão lança erro
     */
    private void consome(TipoToken esperado) throws ErroSintatico, ErroSemantico {
        if (tokenAtual.getTipo() == esperado) {
            tokenAtual = lexer.proximoToken();
        } else {
            throw new ErroSintatico(
                "Erro sintático na linha " + tokenAtual.getLinha() +
                ": Esperado [" + esperado + "], encontrado [" +
                tokenAtual.getTipo() + " - " + tokenAtual.getValor() + "]"
            );
        }
    }

    // ====== GRAMÁTICA COM GERAÇÃO DE CÓDIGO ======

    /**
     * programa → public class IDENTIFICADOR { metodoMain }
     */
    public void programa() throws ErroSintatico, ErroSemantico {
        // Início do programa
        emitir("INPP");
        
        consome(TipoToken.PUBLIC);
        consome(TipoToken.CLASS);
        consome(TipoToken.IDENTIFICADOR);
        consome(TipoToken.ABRE_CHAVE);
        metodoMain();
        consome(TipoToken.FECHA_CHAVE);

        // Fim do programa
        emitir("PARA");

        if (tokenAtual.getTipo() != TipoToken.EOF) {
            throw new ErroSintatico("Tokens extras após fim do programa");
        }

        // Exibir resultados
        System.out.println("\n✅ Programa sintaticamente válido!");
        System.out.println("📊 Variáveis declaradas: " + tabelaSimbolos.getTamanho());
        
        tabelaSimbolos.imprimir();
        
        System.out.println("\n=== CÓDIGO MEPA GERADO ===");
        for (String instrucao : codigoMepa) {
            System.out.println(instrucao);
        }
    }

    /**
     * metodoMain → public static void main(String[] args) { bloco }
     */
    private void metodoMain() throws ErroSintatico, ErroSemantico {
        consome(TipoToken.PUBLIC);
        consome(TipoToken.STATIC);
        consome(TipoToken.VOID);

        if (!tokenAtual.getValor().equals("main")) {
            throw new ErroSintatico("Esperado 'main', encontrado " + tokenAtual.getValor());
        }
        consome(TipoToken.IDENTIFICADOR);

        consome(TipoToken.ABRE_PARENTESE);
        consome(TipoToken.STRING);
        consome(TipoToken.ABRE_COLCHETE);
        consome(TipoToken.FECHA_COLCHETE);
        consome(TipoToken.IDENTIFICADOR); // args
        consome(TipoToken.FECHA_PARENTESE);

        consome(TipoToken.ABRE_CHAVE);
        
        bloco();
        
        // Alocar memória para variáveis (inserir após INPP)
        if (tabelaSimbolos.getTamanho() > 0) {
            codigoMepa.add(1, "AMEM " + tabelaSimbolos.getTamanho());
        }
        
        consome(TipoToken.FECHA_CHAVE);
    }

    /**
     * bloco → comando*
     */
    private void bloco() throws ErroSintatico, ErroSemantico {
        while (tokenAtual.getTipo() != TipoToken.FECHA_CHAVE &&
               tokenAtual.getTipo() != TipoToken.EOF) {
            comando();
        }
    }

    /**
     * comando → declaração | atribuição | if | while | print
     */
    private void comando() throws ErroSintatico, ErroSemantico {
        switch (tokenAtual.getTipo()) {
            case INT:
            case DOUBLE:
            case STRING:
                declaracaoVariavel();
                break;

            case IDENTIFICADOR:
                if (tokenAtual.getValor().equals("System")) {
                    comandoPrint();
                } else {
                    atribuicao();
                }
                break;

            case IF:
                comandoIf();
                break;

            case WHILE:
                comandoWhile();
                break;

            default:
                throw new ErroSintatico("Comando inesperado: " + tokenAtual.getTipo() +
                        " (" + tokenAtual.getValor() + ")");
        }
    }

    /**
     * declaracaoVariavel → tipo IDENTIFICADOR [= expressao] ;
     * Verifica se variável já foi declarada (análise semântica)
     */
    private void declaracaoVariavel() throws ErroSintatico, ErroSemantico {
        // Captura o tipo da variável
        TipoToken tipoVariavel = tokenAtual.getTipo();
        consome(tokenAtual.getTipo());
        
        // Captura o identificador
        String nomeVariavel = tokenAtual.getValor();
        int linha = tokenAtual.getLinha();
        int coluna = tokenAtual.getColuna();
        
        // VERIFICAÇÃO SEMÂNTICA: Variável já foi declarada?
        if (!tabelaSimbolos.adicionar(nomeVariavel, tipoVariavel)) {
            throw new ErroSemantico(
                "Variável '" + nomeVariavel + "' já foi declarada",
                linha, coluna
            );
        }
        
        consome(TipoToken.IDENTIFICADOR);

        // Pode ter inicialização
        if (tokenAtual.getTipo() == TipoToken.ATRIBUICAO) {
            consome(TipoToken.ATRIBUICAO);
            expressao();
            
            // GERAÇÃO DE CÓDIGO: Armazena valor inicial
            int endereco = tabelaSimbolos.buscar(nomeVariavel).getEndereco();
            emitir("\tARMZ " + endereco);
        }

        consome(TipoToken.PONTO_VIRGULA);
    }

    /**
     * atribuicao → IDENTIFICADOR = expressao ;
     * Verifica se variável foi declarada (análise semântica)
     */
    private void atribuicao() throws ErroSintatico, ErroSemantico {
        String nomeVar = tokenAtual.getValor();
        int linha = tokenAtual.getLinha();
        int coluna = tokenAtual.getColuna();
        
        // VERIFICAÇÃO SEMÂNTICA: Variável foi declarada?
        if (!tabelaSimbolos.existe(nomeVar)) {
            throw new ErroSemantico(
                "Variável '" + nomeVar + "' não foi declarada",
                linha, coluna
            );
        }
        
        consome(TipoToken.IDENTIFICADOR);
        consome(TipoToken.ATRIBUICAO);
        expressao();
        
        // GERAÇÃO DE CÓDIGO: Armazena resultado
        int endereco = tabelaSimbolos.buscar(nomeVar).getEndereco();
        emitir("\tARMZ " + endereco);
        
        consome(TipoToken.PONTO_VIRGULA);
    }

    /**
     * comandoIf → if (expressao) { bloco } [else { bloco }]
     * Gera código com desvios condicionais
     */
    private void comandoIf() throws ErroSintatico, ErroSemantico {
        int L1 = proximoRotulo();
        int L2 = proximoRotulo();
        
        consome(TipoToken.IF);
        consome(TipoToken.ABRE_PARENTESE);
        expressao();
        consome(TipoToken.FECHA_PARENTESE);
        
        // GERAÇÃO DE CÓDIGO: Se falso, pula para L1
        emitir("\tDSVF L" + L1);
        
        consome(TipoToken.ABRE_CHAVE);
        bloco();
        consome(TipoToken.FECHA_CHAVE);

        if (tokenAtual.getTipo() == TipoToken.ELSE) {
            // GERAÇÃO DE CÓDIGO: Pula o else
            emitir("\tDSVS L" + L2);
            emitir("L" + L1 + ":\tNADA");
            
            consome(TipoToken.ELSE);
            consome(TipoToken.ABRE_CHAVE);
            bloco();
            consome(TipoToken.FECHA_CHAVE);
            
            emitir("L" + L2 + ":\tNADA");
        } else {
            emitir("L" + L1 + ":\tNADA");
        }
    }

    /**
     * comandoWhile → while (expressao) { bloco }
     * Gera código com loop
     */
    private void comandoWhile() throws ErroSintatico, ErroSemantico {
        int L1 = proximoRotulo();
        int L2 = proximoRotulo();
        
        consome(TipoToken.WHILE);
        
        // GERAÇÃO DE CÓDIGO: Início do loop
        emitir("L" + L1 + ":\tNADA");
        
        consome(TipoToken.ABRE_PARENTESE);
        expressao();
        consome(TipoToken.FECHA_PARENTESE);
        
        // GERAÇÃO DE CÓDIGO: Se falso, sai do loop
        emitir("\tDSVF L" + L2);
        
        consome(TipoToken.ABRE_CHAVE);
        bloco();
        consome(TipoToken.FECHA_CHAVE);
        
        // GERAÇÃO DE CÓDIGO: Volta para o início
        emitir("\tDSVS L" + L1);
        emitir("L" + L2 + ":\tNADA");
    }

    /**
     * comandoPrint → System.out.println(expressao);
     */
    private void comandoPrint() throws ErroSintatico, ErroSemantico {
        if (!tokenAtual.getValor().equals("System"))
            throw new ErroSintatico("Esperado 'System', encontrado " + tokenAtual.getValor());
        consome(TipoToken.IDENTIFICADOR);

        consome(TipoToken.PONTO);

        if (!tokenAtual.getValor().equals("out"))
            throw new ErroSintatico("Esperado 'out', encontrado " + tokenAtual.getValor());
        consome(TipoToken.IDENTIFICADOR);

        consome(TipoToken.PONTO);

        if (!tokenAtual.getValor().equals("println"))
            throw new ErroSintatico("Esperado 'println', encontrado " + tokenAtual.getValor());
        consome(TipoToken.IDENTIFICADOR);

        consome(TipoToken.ABRE_PARENTESE);
        expressao();
        consome(TipoToken.FECHA_PARENTESE);
        
        // GERAÇÃO DE CÓDIGO: Imprime valor do topo da pilha
        emitir("\tIMPR");
        
        consome(TipoToken.PONTO_VIRGULA);
    }

    // ====== EXPRESSÕES ======

    /**
     * expressao → termo (op termo)*
     * Gera código para operações aritméticas e relacionais
     */
    private void expressao() throws ErroSintatico, ErroSemantico {
        termo();
        
        while (tokenAtual.getTipo() == TipoToken.MAIS ||
               tokenAtual.getTipo() == TipoToken.MENOS ||
               tokenAtual.getTipo() == TipoToken.OU_LOGICO) {
            
            TipoToken operador = tokenAtual.getTipo();
            consome(tokenAtual.getTipo());
            termo();
            
            // GERAÇÃO DE CÓDIGO: Operação aritmética
            if (operador == TipoToken.MAIS) {
                emitir("\tSOMA");
            } else if (operador == TipoToken.MENOS) {
                emitir("\tSUBT");
            }
        }
        
        // Operadores relacionais (para if/while)
        if (tokenAtual.getTipo() == TipoToken.IGUAL ||
            tokenAtual.getTipo() == TipoToken.DIFERENTE ||
            tokenAtual.getTipo() == TipoToken.MAIOR ||
            tokenAtual.getTipo() == TipoToken.MENOR ||
            tokenAtual.getTipo() == TipoToken.MAIOR_IGUAL ||
            tokenAtual.getTipo() == TipoToken.MENOR_IGUAL) {
            
            TipoToken operador = tokenAtual.getTipo();
            consome(tokenAtual.getTipo());
            termo();
            
            // GERAÇÃO DE CÓDIGO: Comparação
            switch (operador) {
                case MAIOR: emitir("\tCMMA"); break;
                case MENOR: emitir("\tCMME"); break;
                case MAIOR_IGUAL: emitir("\tCMAG"); break;
                case MENOR_IGUAL: emitir("\tCMEG"); break;
                case IGUAL: emitir("\tCMIG"); break;
                case DIFERENTE: emitir("\tCMDG"); break;
            }
        }
    }

    /**
     * termo → fator (op fator)*
     * Gera código para multiplicação e divisão
     */
    private void termo() throws ErroSintatico, ErroSemantico {
        fator();
        
        while (tokenAtual.getTipo() == TipoToken.MULTIPLICACAO ||
               tokenAtual.getTipo() == TipoToken.DIVISAO ||
               tokenAtual.getTipo() == TipoToken.E_LOGICO) {
            
            TipoToken operador = tokenAtual.getTipo();
            consome(tokenAtual.getTipo());
            fator();
            
            // GERAÇÃO DE CÓDIGO: Operação
            if (operador == TipoToken.MULTIPLICACAO) {
                emitir("\tMULT");
            } else if (operador == TipoToken.DIVISAO) {
                emitir("\tDIVI");
            }
        }
    }

    /**
     * fator → IDENTIFICADOR | NUMERO | (expressao)
     * Verifica se variável foi declarada e gera código para carregar valores
     */
    private void fator() throws ErroSintatico, ErroSemantico {
        switch (tokenAtual.getTipo()) {
            case IDENTIFICADOR:
                String nomeVar = tokenAtual.getValor();
                int linha = tokenAtual.getLinha();
                int coluna = tokenAtual.getColuna();
                
                // VERIFICAÇÃO SEMÂNTICA: Variável foi declarada?
                if (!tabelaSimbolos.existe(nomeVar)) {
                    throw new ErroSemantico(
                        "Variável '" + nomeVar + "' não foi declarada",
                        linha, coluna
                    );
                }
                
                // GERAÇÃO DE CÓDIGO: Carrega valor da variável
                int endereco = tabelaSimbolos.buscar(nomeVar).getEndereco();
                emitir("\tCRVL " + endereco);
                
                consome(TipoToken.IDENTIFICADOR);
                break;
                
            case NUMERO_INTEIRO:
            case NUMERO_DECIMAL:
                // GERAÇÃO DE CÓDIGO: Carrega constante
                emitir("\tCRCT " + tokenAtual.getValor());
                consome(tokenAtual.getTipo());
                break;
                
            case BOOLEAN_LITERAL:
                consome(TipoToken.BOOLEAN_LITERAL);
                break;
                
            case STRING_LITERAL:
                consome(TipoToken.STRING_LITERAL);
                break;
                
            case ABRE_PARENTESE:
                consome(TipoToken.ABRE_PARENTESE);
                expressao();
                consome(TipoToken.FECHA_PARENTESE);
                break;
                
            default:
                throw new ErroSintatico("Fator inesperado: " + tokenAtual.getTipo() +
                        " (" + tokenAtual.getValor() + ")");
        }
    }
    
    /**
     * Retorna lista de instruções MEPA geradas
     */
    public List<String> getCodigoMepa() {
        return new ArrayList<>(codigoMepa);
    }
}