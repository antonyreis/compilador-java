package parser;

import lexer.AnalisadorLexico;
import lexer.Token;
import lexer.TipoToken;

public class AnalisadorSintatico {
    private final AnalisadorLexico lexer;
    private Token tokenAtual;

    public AnalisadorSintatico(AnalisadorLexico lexer) {
        this.lexer = lexer;
        this.tokenAtual = lexer.proximoToken(); // primeiro token
    }

    // Avança se for o esperado, senão erro
    private void consome(TipoToken esperado) throws ErroSintatico {
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

    // ====== GRAMÁTICA SIMPLIFICADA ======

    public void programa() throws ErroSintatico {
        consome(TipoToken.PUBLIC); // opcional em Java real, mas aqui simplificamos
        consome(TipoToken.CLASS);
        consome(TipoToken.IDENTIFICADOR);
        consome(TipoToken.ABRE_CHAVE);
        metodoMain();
        consome(TipoToken.FECHA_CHAVE);

        if (tokenAtual.getTipo() != TipoToken.EOF) {
            throw new ErroSintatico("Tokens extras após fim do programa");
        }

        System.out.println("Programa sintaticamente válido!");
    }

    private void metodoMain() throws ErroSintatico {
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
        consome(TipoToken.FECHA_CHAVE);
    }

    private void bloco() throws ErroSintatico {
        while (tokenAtual.getTipo() != TipoToken.FECHA_CHAVE &&
               tokenAtual.getTipo() != TipoToken.EOF) {
            comando();
        }
    }

    private void comando() throws ErroSintatico {
        switch (tokenAtual.getTipo()) {
            case INT:
            case DOUBLE:
            case STRING:
                declaracaoVariavel();
                break;

            case IDENTIFICADOR:
                // Pode ser atribuição ou System.out.println
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

    private void declaracaoVariavel() throws ErroSintatico {
        // tipo
        consome(tokenAtual.getTipo()); // INT, DOUBLE ou STRING
        consome(TipoToken.IDENTIFICADOR);

        // pode ter inicialização
        if (tokenAtual.getTipo() == TipoToken.ATRIBUICAO) {
            consome(TipoToken.ATRIBUICAO);
            expressao();
        }

        consome(TipoToken.PONTO_VIRGULA);
    }

    private void atribuicao() throws ErroSintatico {
        consome(TipoToken.IDENTIFICADOR);
        consome(TipoToken.ATRIBUICAO);
        expressao();
        consome(TipoToken.PONTO_VIRGULA);
    }

    private void comandoIf() throws ErroSintatico {
        consome(TipoToken.IF);
        consome(TipoToken.ABRE_PARENTESE);
        expressao();
        consome(TipoToken.FECHA_PARENTESE);
        consome(TipoToken.ABRE_CHAVE);
        bloco();
        consome(TipoToken.FECHA_CHAVE);

        if (tokenAtual.getTipo() == TipoToken.ELSE) {
            consome(TipoToken.ELSE);
            consome(TipoToken.ABRE_CHAVE);
            bloco();
            consome(TipoToken.FECHA_CHAVE);
        }
    }

    private void comandoWhile() throws ErroSintatico {
        consome(TipoToken.WHILE);
        consome(TipoToken.ABRE_PARENTESE);
        expressao();
        consome(TipoToken.FECHA_PARENTESE);
        consome(TipoToken.ABRE_CHAVE);
        bloco();
        consome(TipoToken.FECHA_CHAVE);
    }

    private void comandoPrint() throws ErroSintatico {
        // System . out . println ( expr ) ;
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
        consome(TipoToken.PONTO_VIRGULA);
    }

    // EXPRESSÕES
    private void expressao() throws ErroSintatico {
        termo();
        while (tokenAtual.getTipo() == TipoToken.MAIS ||
               tokenAtual.getTipo() == TipoToken.MENOS ||
               tokenAtual.getTipo() == TipoToken.OU_LOGICO ||
               tokenAtual.getTipo() == TipoToken.IGUAL ||
               tokenAtual.getTipo() == TipoToken.DIFERENTE ||
               tokenAtual.getTipo() == TipoToken.MAIOR ||
               tokenAtual.getTipo() == TipoToken.MENOR ||
               tokenAtual.getTipo() == TipoToken.MAIOR_IGUAL ||
               tokenAtual.getTipo() == TipoToken.MENOR_IGUAL) {
            consome(tokenAtual.getTipo());
            termo();
        }
    }

    private void termo() throws ErroSintatico {
        fator();
        while (tokenAtual.getTipo() == TipoToken.MULTIPLICACAO ||
               tokenAtual.getTipo() == TipoToken.DIVISAO ||
               tokenAtual.getTipo() == TipoToken.E_LOGICO) {
            consome(tokenAtual.getTipo());
            fator();
        }
    }

    private void fator() throws ErroSintatico {
        switch (tokenAtual.getTipo()) {
            case IDENTIFICADOR:
                consome(TipoToken.IDENTIFICADOR);
                break;
            case NUMERO_INTEIRO:
            case NUMERO_DECIMAL:
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
}
