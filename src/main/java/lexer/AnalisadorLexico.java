package lexer;

import java.util.*;

/**
 * Analisador Léxico para compilador Java
 * Responsável pela primeira fase da compilação: análise léxica
 */
public class AnalisadorLexico {
    
    // Atributos de controle
    private final String codigoFonte;
    private int posicao;
    private int linha;
    private int coluna;
    private int posicaoLinha;
    
    // Estruturas auxiliares
    private final Map<String, TipoToken> palavrasChave;
    private final List<Token> tokens;
    private final List<ErroLexico> erros;
    
    // Configurações
    private boolean ignorarComentarios;
    private boolean ignorarEspacos;
    
    public AnalisadorLexico(String codigoFonte) {
        this.codigoFonte = codigoFonte != null ? codigoFonte : "";
        this.posicao = 0;
        this.linha = 1;
        this.coluna = 1;
        this.posicaoLinha = 0;
        
        this.palavrasChave = new HashMap<>();
        this.tokens = new ArrayList<>();
        this.erros = new ArrayList<>();
        
        this.ignorarComentarios = true;
        this.ignorarEspacos = true;
        
        inicializarPalavrasChave();
    }
    
    private void inicializarPalavrasChave() {
        // Modificadores de acesso
        palavrasChave.put("public", TipoToken.PUBLIC);
        palavrasChave.put("private", TipoToken.PRIVATE);
        palavrasChave.put("protected", TipoToken.PROTECTED);
        palavrasChave.put("static", TipoToken.STATIC);
        palavrasChave.put("final", TipoToken.FINAL);
        palavrasChave.put("abstract", TipoToken.ABSTRACT);
        
        // Palavras-chave de classe
        palavrasChave.put("class", TipoToken.CLASS);
        palavrasChave.put("interface", TipoToken.INTERFACE);
        palavrasChave.put("extends", TipoToken.EXTENDS);
        palavrasChave.put("implements", TipoToken.IMPLEMENTS);
        
        // Tipos primitivos
        palavrasChave.put("int", TipoToken.INT);
        palavrasChave.put("double", TipoToken.DOUBLE);
        palavrasChave.put("float", TipoToken.FLOAT);
        palavrasChave.put("boolean", TipoToken.BOOLEAN);
        palavrasChave.put("char", TipoToken.CHAR);
        palavrasChave.put("byte", TipoToken.BYTE);
        palavrasChave.put("short", TipoToken.SHORT);
        palavrasChave.put("long", TipoToken.LONG);
        palavrasChave.put("void", TipoToken.VOID);
        palavrasChave.put("String", TipoToken.STRING);
        
        // Estruturas de controle
        palavrasChave.put("if", TipoToken.IF);
        palavrasChave.put("else", TipoToken.ELSE);
        palavrasChave.put("while", TipoToken.WHILE);
        palavrasChave.put("for", TipoToken.FOR);
        palavrasChave.put("do", TipoToken.DO);
        palavrasChave.put("switch", TipoToken.SWITCH);
        palavrasChave.put("case", TipoToken.CASE);
        palavrasChave.put("default", TipoToken.DEFAULT);
        palavrasChave.put("break", TipoToken.BREAK);
        palavrasChave.put("continue", TipoToken.CONTINUE);
        palavrasChave.put("return", TipoToken.RETURN);
        
        // Literais especiais
        palavrasChave.put("true", TipoToken.BOOLEAN_LITERAL);
        palavrasChave.put("false", TipoToken.BOOLEAN_LITERAL);
        palavrasChave.put("null", TipoToken.NULL_LITERAL);
    }
    
    /**
     * Retorna o próximo token da entrada
     */
    public Token proximoToken() {
        pularEspacosEComentarios();
        
        if (fimDoArquivo()) {
            return new Token(TipoToken.EOF, "", linha, coluna, posicao, posicao);
        }
        
        char c = caracterAtual();
        
        // Números (inteiros e decimais)
        if (Character.isDigit(c)) {
            return lerNumero();
        }
        
        // Identificadores e palavras-chave
        if (Character.isLetter(c) || c == '_') {
            return lerIdentificador();
        }
        
        // Strings e caracteres
        if (c == '"' || c == '\'') {
            return lerString();
        }
        
        // Comentários
        if (c == '/' && (proximoCaracter() == '/' || proximoCaracter() == '*')) {
            Token comentario = processarComentario();
            if (comentario != null) {
                return comentario;
            }
            return proximoToken(); // Continue para próximo token
        }
        
        // Operadores
        if (isOperador(c)) {
            return lerOperador();
        }
        
        // Delimitadores
        if (isDelimitador(c)) {
            return lerDelimitador();
        }
        
        // Caractere não reconhecido
        int linhaInicial = linha;
        int colunaInicial = coluna;
        int posicaoInicial = posicao;
        
        consumirCaracter();
        adicionarErro("Caractere não reconhecido: " + c, linhaInicial, colunaInicial);
        
        return criarTokenErro(String.valueOf(c), linhaInicial, colunaInicial, posicaoInicial);
    }
    
    /**
     * Analisa todo o código fonte e retorna lista de tokens
     */
    public List<Token> analisarTodos() {
        tokens.clear();
        erros.clear();
        
        Token token;
        do {
            token = proximoToken();
            tokens.add(token);
        } while (token.getTipo() != TipoToken.EOF);
        
        return new ArrayList<>(tokens);
    }
    
    // Métodos auxiliares para controle de posição
    private char caracterAtual() {
        if (posicao >= codigoFonte.length()) {
            return '\0';
        }
        return codigoFonte.charAt(posicao);
    }
    
    private char proximoCaracter() {
        if (posicao + 1 >= codigoFonte.length()) {
            return '\0';
        }
        return codigoFonte.charAt(posicao + 1);
    }
    
    private char consumirCaracter() {
        if (posicao >= codigoFonte.length()) {
            return '\0';
        }
        
        char c = codigoFonte.charAt(posicao);
        posicao++;
        
        if (c == '\n') {
            linha++;
            posicaoLinha = posicao;
            coluna = 1;
        } else {
            coluna = posicao - posicaoLinha + 1;
        }
        
        return c;
    }
    
    private boolean fimDoArquivo() {
        return posicao >= codigoFonte.length();
    }
    
    // Implementação dos métodos de leitura (esqueleto básico)
    private Token lerNumero() {
        // Implementar conforme documentação
        int linhaInicial = linha;
        int colunaInicial = coluna;
        int posicaoInicial = posicao;
        
        StringBuilder numero = new StringBuilder();
        boolean temPonto = false;
        
        // Lê dígitos iniciais
        while (Character.isDigit(caracterAtual())) {
            numero.append(consumirCaracter());
        }
        
        // Verifica se é número decimal
        if (caracterAtual() == '.' && Character.isDigit(proximoCaracter())) {
            temPonto = true;
            numero.append(consumirCaracter()); // consome o ponto
            
            while (Character.isDigit(caracterAtual())) {
                numero.append(consumirCaracter());
            }
        }
        
        TipoToken tipo = temPonto ? TipoToken.NUMERO_DECIMAL : TipoToken.NUMERO_INTEIRO;
        
        return new Token(tipo, numero.toString(), linhaInicial, colunaInicial,
                        posicaoInicial, posicao - 1);
    }
    
    private Token lerIdentificador() {
        int linhaInicial = linha;
        int colunaInicial = coluna;
        int posicaoInicial = posicao;
        
        StringBuilder identificador = new StringBuilder();
        
        // Primeiro caractere deve ser letra ou underscore
        if (Character.isLetter(caracterAtual()) || caracterAtual() == '_') {
            identificador.append(consumirCaracter());
        }
        
        // Caracteres subsequentes podem ser letras, dígitos ou underscore
        while (Character.isLetterOrDigit(caracterAtual()) || caracterAtual() == '_') {
            identificador.append(consumirCaracter());
        }
        
        String valor = identificador.toString();
        TipoToken tipo = palavrasChave.getOrDefault(valor, TipoToken.IDENTIFICADOR);
        
        return new Token(tipo, valor, linhaInicial, colunaInicial,
                        posicaoInicial, posicao - 1);
    }
    
    private Token lerString() {
        // Implementar conforme documentação
        int linhaInicial = linha;
        int colunaInicial = coluna;
        int posicaoInicial = posicao;
        
        StringBuilder string = new StringBuilder();
        
        // Consome aspas inicial
        char aspas = consumirCaracter();
        
        while (caracterAtual() != aspas && !fimDoArquivo()) {
            char c = caracterAtual();
            
            if (c == '\\') {
                // Caracteres de escape - implementar
                consumirCaracter();
                char escape = consumirCaracter();
                switch (escape) {
                    case 'n': string.append('\n'); break;
                    case 't': string.append('\t'); break;
                    case 'r': string.append('\r'); break;
                    case '\"': string.append('\"'); break;
                    case '\'': string.append('\''); break;
                    case '\\': string.append('\\'); break;
                    default: string.append(escape); break;
                }
            } else if (c == '\n') {
                adicionarErro("String não terminada", linha, coluna);
                break;
            } else {
                string.append(consumirCaracter());
            }
        }
        
        if (caracterAtual() == aspas) {
            consumirCaracter();
        } else {
            adicionarErro("String não terminada", linha, coluna);
        }
        
        TipoToken tipo = (aspas == '\'') ? TipoToken.CHAR_LITERAL : TipoToken.STRING_LITERAL;
        
        return new Token(tipo, string.toString(), linhaInicial, colunaInicial,
                        posicaoInicial, posicao - 1);
    }
    
    private Token processarComentario() {
        // Implementar conforme documentação
        return null; // Por enquanto ignora comentários
    }
    
    private Token lerOperador() {
        // Implementar conforme documentação
        int linhaInicial = linha;
        int colunaInicial = coluna;
        int posicaoInicial = posicao;
        
        char c = consumirCaracter();
        
        switch (c) {
            case '+':
                if (caracterAtual() == '+') {
                    consumirCaracter();
                    return criarToken(TipoToken.INCREMENTO, "++", linhaInicial, colunaInicial, posicaoInicial);
                } else if (caracterAtual() == '=') {
                    consumirCaracter();
                    return criarToken(TipoToken.MAIS_IGUAL, "+=", linhaInicial, colunaInicial, posicaoInicial);
                }
                return criarToken(TipoToken.MAIS, "+", linhaInicial, colunaInicial, posicaoInicial);
                
            case '-':
                if (caracterAtual() == '-') {
                    consumirCaracter();
                    return criarToken(TipoToken.DECREMENTO, "--", linhaInicial, colunaInicial, posicaoInicial);
                } else if (caracterAtual() == '=') {
                    consumirCaracter();
                    return criarToken(TipoToken.MENOS_IGUAL, "-=", linhaInicial, colunaInicial, posicaoInicial);
                }
                return criarToken(TipoToken.MENOS, "-", linhaInicial, colunaInicial, posicaoInicial);
                
            case '*':
                if (caracterAtual() == '=') {
                    consumirCaracter();
                    return criarToken(TipoToken.MULT_IGUAL, "*=", linhaInicial, colunaInicial, posicaoInicial);
                }
                return criarToken(TipoToken.MULTIPLICACAO, "*", linhaInicial, colunaInicial, posicaoInicial);
                
            case '=':
                if (caracterAtual() == '=') {
                    consumirCaracter();
                    return criarToken(TipoToken.IGUAL, "==", linhaInicial, colunaInicial, posicaoInicial);
                }
                return criarToken(TipoToken.ATRIBUICAO, "=", linhaInicial, colunaInicial, posicaoInicial);
                
            // Adicionar outros operadores conforme necessário
            default:
                return criarTokenErro(String.valueOf(c), linhaInicial, colunaInicial, posicaoInicial);
        }
    }
    
    private Token lerDelimitador() {
        int linhaInicial = linha;
        int colunaInicial = coluna;
        int posicaoInicial = posicao;
        
        char c = consumirCaracter();
        
        switch (c) {
            case '(': return criarToken(TipoToken.ABRE_PARENTESE, "(", linhaInicial, colunaInicial, posicaoInicial);
            case ')': return criarToken(TipoToken.FECHA_PARENTESE, ")", linhaInicial, colunaInicial, posicaoInicial);
            case '{': return criarToken(TipoToken.ABRE_CHAVE, "{", linhaInicial, colunaInicial, posicaoInicial);
            case '}': return criarToken(TipoToken.FECHA_CHAVE, "}", linhaInicial, colunaInicial, posicaoInicial);
            case '[': return criarToken(TipoToken.ABRE_COLCHETE, "[", linhaInicial, colunaInicial, posicaoInicial);
            case ']': return criarToken(TipoToken.FECHA_COLCHETE, "]", linhaInicial, colunaInicial, posicaoInicial);
            case ';': return criarToken(TipoToken.PONTO_VIRGULA, ";", linhaInicial, colunaInicial, posicaoInicial);
            case ',': return criarToken(TipoToken.VIRGULA, ",", linhaInicial, colunaInicial, posicaoInicial);
            case '.': return criarToken(TipoToken.PONTO, ".", linhaInicial, colunaInicial, posicaoInicial);
            default: return criarTokenErro(String.valueOf(c), linhaInicial, colunaInicial, posicaoInicial);
        }
    }
    
    // Métodos auxiliares
    private void pularEspacosEComentarios() {
        while (!fimDoArquivo()) {
            char c = caracterAtual();
            
            if (Character.isWhitespace(c)) {
                if (!ignorarEspacos) {
                    break;
                }
                consumirCaracter();
            } else {
                break;
            }
        }
    }
    
    private boolean isOperador(char c) {
        return "+-*/%=!<>&|^~".indexOf(c) != -1;
    }
    
    private boolean isDelimitador(char c) {
        return "(){}[];,.:|".indexOf(c) != -1;
    }
    
    private Token criarToken(TipoToken tipo, String valor, int linha, int coluna, int posicaoInicial) {
        return new Token(tipo, valor, linha, coluna, posicaoInicial, posicao - 1);
    }
    
    private Token criarTokenErro(String valor, int linha, int coluna, int posicaoInicial) {
        return new Token(TipoToken.ERRO, valor, linha, coluna, posicaoInicial, posicao - 1);
    }
    
    private void adicionarErro(String mensagem, int linha, int coluna) {
        erros.add(new ErroLexico(mensagem, linha, coluna, posicao));
    }
    
    // Getters e Setters
    public List<ErroLexico> getErros() { return new ArrayList<>(erros); }
    public void setIgnorarComentarios(boolean ignorar) { this.ignorarComentarios = ignorar; }
    public void setIgnorarEspacos(boolean ignorar) { this.ignorarEspacos = ignorar; }
}