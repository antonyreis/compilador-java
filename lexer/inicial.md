# Analisador Léxico para Compilador Java
## Documentação Técnica Completa

### Versão: 1.0
### Data: Agosto 2025
### Autor: Documentação Técnica

---

## Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura do Sistema](#arquitetura-do-sistema)
3. [Estruturas de Dados](#estruturas-de-dados)
4. [Implementação Detalhada](#implementação-detalhada)
5. [Algoritmos e Fluxos](#algoritmos-e-fluxos)
6. [Tratamento de Erros](#tratamento-de-erros)
7. [Exemplos de Uso](#exemplos-de-uso)
8. [Testes e Validação](#testes-e-validação)
9. [Otimizações](#otimizações)
10. [Referências](#referências)

---

## Visão Geral

### Propósito
O Analisador Léxico (Lexer) é o primeiro componente de um compilador Java, responsável por converter o código fonte em uma sequência de tokens (unidades léxicas). Este documento detalha sua implementação, funcionamento e uso.

### Funcionalidades Principais
- **Tokenização**: Conversão de caracteres em tokens significativos
- **Reconhecimento de Padrões**: Identificação de palavras-chave, operadores, literais
- **Controle de Posição**: Rastreamento de linha e coluna para relatórios de erro
- **Tratamento de Comentários**: Processamento e descarte de comentários
- **Validação Léxica**: Detecção de caracteres inválidos

### Linguagens Suportadas
- **Linguagem Alvo**: Subconjunto da linguagem Java
- **Tokens Suportados**: Palavras-chave, identificadores, literais, operadores, delimitadores

---

## Arquitetura do Sistema

### Componentes Principais

```
┌─────────────────────────────────────────┐
│            Código Fonte                 │
│        (String de caracteres)           │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│         Analisador Léxico               │
│  ┌─────────────┐  ┌─────────────────┐   │
│  │   Scanner   │  │ Reconhecedor de │   │
│  │             │  │    Padrões      │   │
│  └─────────────┘  └─────────────────┘   │
│  ┌─────────────┐  ┌─────────────────┐   │
│  │ Controlador │  │ Gerador de      │   │
│  │ Posição     │  │    Tokens       │   │
│  └─────────────┘  └─────────────────┘   │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│         Sequência de Tokens             │
│     (Lista de objetos Token)            │
└─────────────────────────────────────────┘
```

### Padrão de Design Utilizado
- **State Machine**: Para reconhecimento de diferentes tipos de tokens
- **Factory Pattern**: Para criação de tokens específicos
- **Iterator Pattern**: Para navegação sequencial através do código fonte

---

## Estruturas de Dados

### Enumeração TipoToken

```java
public enum TipoToken {
    // Palavras-chave Java
    PUBLIC("public"), PRIVATE("private"), PROTECTED("protected"),
    CLASS("class"), INTERFACE("interface"), EXTENDS("extends"),
    IMPLEMENTS("implements"), STATIC("static"), FINAL("final"),
    ABSTRACT("abstract"), VOID("void"), RETURN("return"),
    
    // Tipos primitivos
    INT("int"), DOUBLE("double"), FLOAT("float"), BOOLEAN("boolean"),
    CHAR("char"), BYTE("byte"), SHORT("short"), LONG("long"),
    STRING("String"),
    
    // Estruturas de controle
    IF("if"), ELSE("else"), WHILE("while"), FOR("for"),
    DO("do"), SWITCH("switch"), CASE("case"), DEFAULT("default"),
    BREAK("break"), CONTINUE("continue"),
    
    // Literais e identificadores
    IDENTIFICADOR, NUMERO_INTEIRO, NUMERO_DECIMAL,
    STRING_LITERAL, CHAR_LITERAL, BOOLEAN_LITERAL,
    NULL_LITERAL,
    
    // Operadores aritméticos
    MAIS("+"), MENOS("-"), MULTIPLICACAO("*"), DIVISAO("/"),
    MODULO("%"), INCREMENTO("++"), DECREMENTO("--"),
    
    // Operadores de atribuição
    ATRIBUICAO("="), MAIS_IGUAL("+="), MENOS_IGUAL("-="),
    MULT_IGUAL("*="), DIV_IGUAL("/="), MOD_IGUAL("%="),
    
    // Operadores relacionais
    IGUAL("=="), DIFERENTE("!="), MENOR("<"), MAIOR(">"),
    MENOR_IGUAL("<="), MAIOR_IGUAL(">="),
    
    // Operadores lógicos
    E_LOGICO("&&"), OU_LOGICO("||"), NEGACAO("!"),
    
    // Operadores bitwise
    E_BITWISE("&"), OU_BITWISE("|"), XOR("^"),
    COMPLEMENTO("~"), SHIFT_LEFT("<<"), SHIFT_RIGHT(">>"),
    
    // Delimitadores
    ABRE_PARENTESE("("), FECHA_PARENTESE(")"),
    ABRE_CHAVE("{"), FECHA_CHAVE("}"),
    ABRE_COLCHETE("["), FECHA_COLCHETE("]"),
    PONTO_VIRGULA(";"), VIRGULA(","), PONTO("."),
    DOIS_PONTOS(":"),
    
    // Especiais
    WHITESPACE, COMENTARIO_LINHA, COMENTARIO_BLOCO,
    EOF, ERRO, DESCONHECIDO;
    
    private final String valor;
    
    TipoToken() {
        this.valor = null;
    }
    
    TipoToken(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}
```

### Classe Token

```java
/**
 * Representa um token identificado pelo analisador léxico.
 * Contém informações sobre tipo, valor e posição no código fonte.
 */
public class Token {
    private final TipoToken tipo;
    private final String valor;
    private final int linha;
    private final int coluna;
    private final int posicaoInicial;
    private final int posicaoFinal;
    
    /**
     * Construtor para criação de token
     * @param tipo Tipo do token identificado
     * @param valor Valor literal do token
     * @param linha Número da linha no código fonte
     * @param coluna Número da coluna no código fonte
     * @param posicaoInicial Posição inicial no código fonte
     * @param posicaoFinal Posição final no código fonte
     */
    public Token(TipoToken tipo, String valor, int linha, int coluna, 
                 int posicaoInicial, int posicaoFinal) {
        this.tipo = tipo;
        this.valor = valor;
        this.linha = linha;
        this.coluna = coluna;
        this.posicaoInicial = posicaoInicial;
        this.posicaoFinal = posicaoFinal;
    }
    
    // Getters
    public TipoToken getTipo() { return tipo; }
    public String getValor() { return valor; }
    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }
    public int getPosicaoInicial() { return posicaoInicial; }
    public int getPosicaoFinal() { return posicaoFinal; }
    
    @Override
    public String toString() {
        return String.format("Token{tipo=%s, valor='%s', linha=%d, coluna=%d}", 
                           tipo, valor, linha, coluna);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Token token = (Token) obj;
        return Objects.equals(tipo, token.tipo) && 
               Objects.equals(valor, token.valor);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tipo, valor);
    }
}
```

---

## Implementação Detalhada

### Classe Principal: AnalisadorLexico

```java
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
    
    /**
     * Construtor do analisador léxico
     * @param codigoFonte Código fonte a ser analisado
     */
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
    
    /**
     * Inicializa o mapa de palavras-chave Java
     */
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
}
```

### Métodos de Controle de Posição

```java
/**
 * Retorna o caractere na posição atual sem avançar
 * @return Caractere atual ou '\0' se fim do arquivo
 */
private char caracterAtual() {
    if (posicao >= codigoFonte.length()) {
        return '\0';
    }
    return codigoFonte.charAt(posicao);
}

/**
 * Retorna o próximo caractere sem avançar a posição
 * @return Próximo caractere ou '\0' se fim do arquivo
 */
private char proximoCaracter() {
    if (posicao + 1 >= codigoFonte.length()) {
        return '\0';
    }
    return codigoFonte.charAt(posicao + 1);
}

/**
 * Avança para o próximo caractere e atualiza posição
 * @return Caractere que foi consumido
 */
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

/**
 * Verifica se chegou ao fim do código fonte
 * @return true se não há mais caracteres
 */
private boolean fimDoArquivo() {
    return posicao >= codigoFonte.length();
}
```

### Reconhecimento de Padrões

#### Números

```java
/**
 * Reconhece e cria token para números (inteiros e decimais)
 * @return Token do tipo NUMERO_INTEIRO ou NUMERO_DECIMAL
 */
private Token lerNumero() {
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
    
    // Verifica notação científica (1e10, 2.5E-3)
    if (caracterAtual() == 'e' || caracterAtual() == 'E') {
        numero.append(consumirCaracter());
        
        if (caracterAtual() == '+' || caracterAtual() == '-') {
            numero.append(consumirCaracter());
        }
        
        if (!Character.isDigit(caracterAtual())) {
            // Erro: esperava dígito após expoente
            adicionarErro("Dígito esperado após expoente", linha, coluna);
            return criarTokenErro(numero.toString(), linhaInicial, 
                                colunaInicial, posicaoInicial);
        }
        
        while (Character.isDigit(caracterAtual())) {
            numero.append(consumirCaracter());
        }
        temPonto = true; // Notação científica é sempre decimal
    }
    
    // Verifica sufixos (f, F, d, D, l, L)
    char sufixo = caracterAtual();
    if (sufixo == 'f' || sufixo == 'F' || sufixo == 'd' || sufixo == 'D' ||
        sufixo == 'l' || sufixo == 'L') {
        numero.append(consumirCaracter());
        temPonto = (sufixo == 'f' || sufixo == 'F' || 
                   sufixo == 'd' || sufixo == 'D');
    }
    
    TipoToken tipo = temPonto ? TipoToken.NUMERO_DECIMAL : TipoToken.NUMERO_INTEIRO;
    
    return new Token(tipo, numero.toString(), linhaInicial, colunaInicial,
                    posicaoInicial, posicao - 1);
}
```

#### Identificadores e Palavras-chave

```java
/**
 * Reconhece identificadores e palavras-chave
 * @return Token do tipo apropriado
 */
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
```

#### Strings

```java
/**
 * Reconhece literais string
 * @return Token do tipo STRING_LITERAL
 */
private Token lerString() {
    int linhaInicial = linha;
    int colunaInicial = coluna;
    int posicaoInicial = posicao;
    
    StringBuilder string = new StringBuilder();
    
    // Consome aspas inicial
    char aspas = consumirCaracter(); // " ou '
    
    while (caracterAtual() != aspas && !fimDoArquivo()) {
        char c = caracterAtual();
        
        if (c == '\\') {
            // Caracteres de escape
            consumirCaracter(); // consome backslash
            char escape = consumirCaracter();
            
            switch (escape) {
                case 'n': string.append('\n'); break;
                case 't': string.append('\t'); break;
                case 'r': string.append('\r'); break;
                case 'b': string.append('\b'); break;
                case 'f': string.append('\f'); break;
                case '\'': string.append('\''); break;
                case '\"': string.append('\"'); break;
                case '\\': string.append('\\'); break;
                case '0': string.append('\0'); break;
                case 'u':
                    // Unicode escape sequence
                    string.append(lerEscapeUnicode());
                    break;
                default:
                    // Escape inválido
                    adicionarErro("Sequência de escape inválida: \\" + escape, 
                                linha, coluna);
                    string.append(escape);
            }
        } else if (c == '\n') {
            // String não pode quebrar linha sem escape
            adicionarErro("String não terminada", linha, coluna);
            break;
        } else {
            string.append(consumirCaracter());
        }
    }
    
    // Verifica se string foi fechada
    if (caracterAtual() == aspas) {
        consumirCaracter(); // consome aspas final
    } else {
        adicionarErro("String não terminada", linha, coluna);
    }
    
    TipoToken tipo = (aspas == '\'') ? TipoToken.CHAR_LITERAL : TipoToken.STRING_LITERAL;
    
    return new Token(tipo, string.toString(), linhaInicial, colunaInicial,
                    posicaoInicial, posicao - 1);
}

/**
 * Lê sequência de escape Unicode (\uXXXX)
 * @return Caractere Unicode correspondente
 */
private char lerEscapeUnicode() {
    int codigo = 0;
    
    for (int i = 0; i < 4; i++) {
        char c = consumirCaracter();
        if (Character.isDigit(c)) {
            codigo = codigo * 16 + (c - '0');
        } else if (c >= 'a' && c <= 'f') {
            codigo = codigo * 16 + (c - 'a' + 10);
        } else if (c >= 'A' && c <= 'F') {
            codigo = codigo * 16 + (c - 'A' + 10);
        } else {
            adicionarErro("Dígito hexadecimal esperado em escape Unicode", 
                        linha, coluna);
            return '?';
        }
    }
    
    return (char) codigo;
}
```

### Tratamento de Comentários

```java
/**
 * Processa e opcionalmente ignora comentários
 * @return Token de comentário ou null se ignorado
 */
private Token processarComentario() {
    int linhaInicial = linha;
    int colunaInicial = coluna;
    int posicaoInicial = posicao;
    
    consumirCaracter(); // consome primeira '/'
    
    if (caracterAtual() == '/') {
        // Comentário de linha
        consumirCaracter(); // consome segunda '/'
        StringBuilder comentario = new StringBuilder("//");
        
        while (caracterAtual() != '\n' && !fimDoArquivo()) {
            comentario.append(consumirCaracter());
        }
        
        if (!ignorarComentarios) {
            return new Token(TipoToken.COMENTARIO_LINHA, comentario.toString(),
                           linhaInicial, colunaInicial, posicaoInicial, posicao - 1);
        }
        
    } else if (caracterAtual() == '*') {
        // Comentário de bloco
        consumirCaracter(); // consome '*'
        StringBuilder comentario = new StringBuilder("/*");
        
        boolean terminado = false;
        while (!fimDoArquivo() && !terminado) {
            char c = consumirCaracter();
            comentario.append(c);
            
            if (c == '*' && caracterAtual() == '/') {
                comentario.append(consumirCaracter());
                terminado = true;
            }
        }
        
        if (!terminado) {
            adicionarErro("Comentário de bloco não terminado", linhaInicial, colunaInicial);
        }
        
        if (!ignorarComentarios) {
            return new Token(TipoToken.COMENTARIO_BLOCO, comentario.toString(),
                           linhaInicial, colunaInicial, posicaoInicial, posicao - 1);
        }
    } else {
        // Não é comentário, volta uma posição
        posicao--;
        coluna--;
        return lerOperador(); // Processa como operador de divisão
    }
    
    return null; // Comentário ignorado
}
```

### Reconhecimento de Operadores

```java
/**
 * Reconhece operadores (simples e compostos)
 * @return Token do operador correspondente
 */
private Token lerOperador() {
    int linhaInicial = linha;
    int colunaInicial = coluna;
    int posicaoInicial = posicao;
    
    char c = consumirCaracter();
    
    switch (c) {
        // Operadores simples
        case '+':
            if (caracterAtual() == '+') {
                consumirCaracter();
                return criarToken(TipoToken.INCREMENTO, "++", linhaInicial, 
                                colunaInicial, posicaoInicial);
            } else if (caracterAtual() == '=') {
                consumirCaracter();
                return criarToken(TipoToken.MAIS_IGUAL, "+=", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.MAIS, "+", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        case '-':
            if (caracterAtual() == '-') {
                consumirCaracter();
                return criarToken(TipoToken.DECREMENTO, "--", linhaInicial, 
                                colunaInicial, posicaoInicial);
            } else if (caracterAtual() == '=') {
                consumirCaracter();
                return criarToken(TipoToken.MENOS_IGUAL, "-=", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.MENOS, "-", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        case '*':
            if (caracterAtual() == '=') {
                consumirCaracter();
                return criarToken(TipoToken.MULT_IGUAL, "*=", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.MULTIPLICACAO, "*", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        case '/':
            if (caracterAtual() == '=') {
                consumirCaracter();
                return criarToken(TipoToken.DIV_IGUAL, "/=", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.DIVISAO, "/", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        case '%':
            if (caracterAtual() == '=') {
                consumirCaracter();
                return criarToken(TipoToken.MOD_IGUAL, "%=", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.MODULO, "%", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        // Operadores de comparação
        case '=':
            if (caracterAtual() == '=') {
                consumirCaracter();
                return criarToken(TipoToken.IGUAL, "==", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.ATRIBUICAO, "=", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        case '!':
            if (caracterAtual() == '=') {
                consumirCaracter();
                return criarToken(TipoToken.DIFERENTE, "!=", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.NEGACAO, "!", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        case '<':
            if (caracterAtual() == '=') {
                consumirCaracter();
                return criarToken(TipoToken.MENOR_IGUAL, "<=", linhaInicial, 
                                colunaInicial, posicaoInicial);
            } else if (caracterAtual() == '<') {
                consumirCaracter();
                return criarToken(TipoToken.SHIFT_LEFT, "<<", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.MENOR, "<", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        case '>':
            if (caracterAtual() == '=') {
                consumirCaracter();
                return criarToken(TipoToken.MAIOR_IGUAL, ">=", linhaInicial, 
                                colunaInicial, posicaoInicial);
            } else if (caracterAtual() == '>') {
                consumirCaracter();
                return criarToken(TipoToken.SHIFT_RIGHT, ">>", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.MAIOR, ">", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        // Operadores lógicos
        case '&':
            if (caracterAtual() == '&') {
                consumirCaracter();
                return criarToken(TipoToken.E_LOGICO, "&&", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.E_BITWISE, "&", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        case '|':
            if (caracterAtual() == '|') {
                consumirCaracter();
                return criarToken(TipoToken.OU_LOGICO, "||", linhaInicial, 
                                colunaInicial, posicaoInicial);
            }
            return criarToken(TipoToken.OU_BITWISE, "|", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        // Outros operadores
        case '^':
            return criarToken(TipoToken.XOR, "^", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        case '~':
            return criarToken(TipoToken.COMPLEMENTO, "~", linhaInicial, 
                            colunaInicial, posicaoInicial);
            
        default:
            // Operador não reconhecido
            adicionarErro("Operador desconhecido: " + c, linhaInicial, colunaInicial);
            return criarTokenErro(String.valueOf(c), linhaInicial, 
                                colunaInicial, posicaoInicial);
    }
}

/**
 * Reconhece delimitadores
 * @return Token do delimitador correspondente
 */
private Token lerDelimitador() {
    int linhaInicial = linha;
    int colunaInicial = coluna;
    int posicaoInicial = posicao;
    
    char c = consumirCaracter();
    
    switch (c) {
        case '(':
            return criarToken(TipoToken.ABRE_PARENTESE, "(", linhaInicial, 
                            colunaInicial, posicaoInicial);
        case ')':
            return criarToken(TipoToken.FECHA_PARENTESE, ")", linhaInicial, 
                            colunaInicial, posicaoInicial);
        case '{':
            return criarToken(TipoToken.ABRE_CHAVE, "{", linhaInicial, 
                            colunaInicial, posicaoInicial);
        case '}':
            return criarToken(TipoToken.FECHA_CHAVE, "}", linhaInicial, 
                            colunaInicial, posicaoInicial);
        case '[':
            return criarToken(TipoToken.ABRE_COLCHETE, "[", linhaInicial, 
                            colunaInicial, posicaoInicial);
        case ']':
            return criarToken(TipoToken.FECHA_COLCHETE, "]", linhaInicial, 
                            colunaInicial, posicaoInicial);
        case ';':
            return criarToken(TipoToken.PONTO_VIRGULA, ";", linhaInicial, 
                            colunaInicial, posicaoInicial);
        case ',':
            return criarToken(TipoToken.VIRGULA, ",", linhaInicial, 
                            colunaInicial, posicaoInicial);
        case '.':
            return criarToken(TipoToken.PONTO, ".", linhaInicial, 
                            colunaInicial, posicaoInicial);
        case ':':
            return criarToken(TipoToken.DOIS_PONTOS, ":", linhaInicial, 
                            colunaInicial, posicaoInicial);
        default:
            // Delimitador não reconhecido
            adicionarErro("Delimitador desconhecido: " + c, linhaInicial, colunaInicial);
            return criarTokenErro(String.valueOf(c), linhaInicial, 
                                colunaInicial, posicaoInicial);
    }
}
```

### Método Principal de Análise

```java
/**
 * Método principal que retorna o próximo token da entrada
 * @return Próximo token identificado
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
        // Se comentário foi ignorado, continua para próximo token
        return proximoToken();
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
 * Pula espaços em branco e comentários se configurado
 */
private void pularEspacosEComentarios() {
    while (!fimDoArquivo()) {
        char c = caracterAtual();
        
        if (Character.isWhitespace(c)) {
            if (!ignorarEspacos) {
                break; // Mantém espaços se não devem ser ignorados
            }
            consumirCaracter();
        } else if (c == '/' && (proximoCaracter() == '/' || proximoCaracter() == '*')) {
            if (!ignorarComentarios) {
                break; // Mantém comentários se não devem ser ignorados
            }
            Token comentario = processarComentario();
            // Comentário foi processado e ignorado
        } else {
            break; // Não é espaço nem comentário
        }
    }
}

/**
 * Analisa todo o código fonte e retorna lista de tokens
 * @return Lista completa de tokens
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
```

### Métodos Auxiliares

```java
/**
 * Verifica se caractere é um operador
 * @param c Caractere a verificar
 * @return true se é operador
 */
private boolean isOperador(char c) {
    return "+-*/%=!<>&|^~".indexOf(c) != -1;
}

/**
 * Verifica se caractere é um delimitador
 * @param c Caractere a verificar
 * @return true se é delimitador
 */
private boolean isDelimitador(char c) {
    return "(){}[];,.:|".indexOf(c) != -1;
}

/**
 * Cria um token com informações completas
 */
private Token criarToken(TipoToken tipo, String valor, int linha, 
                        int coluna, int posicaoInicial) {
    return new Token(tipo, valor, linha, coluna, posicaoInicial, posicao - 1);
}

/**
 * Cria um token de erro
 */
private Token criarTokenErro(String valor, int linha, int coluna, int posicaoInicial) {
    return new Token(TipoToken.ERRO, valor, linha, coluna, posicaoInicial, posicao - 1);
}

/**
 * Adiciona erro à lista de erros
 */
private void adicionarErro(String mensagem, int linha, int coluna) {
    erros.add(new ErroLexico(mensagem, linha, coluna, posicao));
}
```

---

## Algoritmos e Fluxos

### Fluxograma Principal

```
┌─────────────────┐
│    Início       │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐
│ Ler próximo     │
│ caractere       │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐      Sim     ┌─────────────────┐
│ Fim do arquivo? ├──────────────►│ Retornar EOF    │
└─────────┬───────┘              └─────────────────┘
          │ Não
          ▼
┌─────────────────┐
│ Pular espaços   │
│ e comentários   │
└─────────┬───────┘
          │
          ▼
┌─────────────────┐      Sim     ┌─────────────────┐
│ É dígito?       ├──────────────►│ Ler número      │
└─────────┬───────┘              └─────────────────┘
          │ Não
          ▼
┌─────────────────┐      Sim     ┌─────────────────┐
│ É letra ou '_'? ├──────────────►│ Ler identificador│
└─────────┬───────┘              └─────────────────┘
          │ Não
          ▼
┌─────────────────┐      Sim     ┌─────────────────┐
│ É aspas?        ├──────────────►│ Ler string      │
└─────────┬───────┘              └─────────────────┘
          │ Não
          ▼
┌─────────────────┐      Sim     ┌─────────────────┐
│ É operador?     ├──────────────►│ Ler operador    │
└─────────┬───────┘              └─────────────────┘
          │ Não
          ▼
┌─────────────────┐      Sim     ┌─────────────────┐
│ É delimitador?  ├──────────────►│ Ler delimitador │
└─────────┬───────┘              └─────────────────┘
          │ Não
          ▼
┌─────────────────┐
│ Caractere       │
│ não reconhecido │
│ (Erro)          │
└─────────────────┘
```

### Algoritmo de Reconhecimento de Números

```
Estado Inicial
│
▼
┌─────────────┐
│ Ler dígitos │
│ inteiros    │
└─────┬───────┘
      │
      ▼
┌─────────────┐      Não     ┌─────────────┐
│ Encontrou   ├──────────────►│ É inteiro   │
│ ponto '.'?  │              └─────────────┘
└─────┬───────┘
      │ Sim
      ▼
┌─────────────┐
│ Ler dígitos │
│ decimais    │
└─────┬───────┘
      │
      ▼
┌─────────────┐      Não     ┌─────────────┐
│ Encontrou   ├──────────────►│ É decimal   │
│ 'e' ou 'E'? │              └─────────────┘
└─────┬───────┘
      │ Sim
      ▼
┌─────────────┐
│ Ler sinal   │
│ opcional    │
└─────┬───────┘
      │
      ▼
┌─────────────┐
│ Ler dígitos │
│ expoente    │
└─────┬───────┘
      │
      ▼
┌─────────────┐
│ Notação     │
│ científica  │
└─────────────┘
```

---

## Tratamento de Erros

### Classe ErroLexico

```java
/**
 * Representa um erro encontrado durante a análise léxica
 */
public class ErroLexico {
    private final String mensagem;
    private final int linha;
    private final int coluna;
    private final int posicao;
    private final TipoErro tipo;
    
    public enum TipoErro {
        CARACTERE_INVALIDO,
        STRING_NAO_TERMINADA,
        COMENTARIO_NAO_TERMINADO,
        ESCAPE_INVALIDO,
        NUMERO_MALFORMADO,
        UNICODE_INVALIDO
    }
    
    public ErroLexico(String mensagem, int linha, int coluna, int posicao) {
        this.mensagem = mensagem;
        this.linha = linha;
        this.coluna = coluna;
        this.posicao = posicao;
        this.tipo = determinarTipo(mensagem);
    }
    
    private TipoErro determinarTipo(String mensagem) {
        if (mensagem.contains("String não terminada")) {
            return TipoErro.STRING_NAO_TERMINADA;
        } else if (mensagem.contains("Comentário não terminado")) {
            return TipoErro.COMENTARIO_NAO_TERMINADO;
        } else if (mensagem.contains("escape")) {
            return TipoErro.ESCAPE_INVALIDO;
        } else if (mensagem.contains("Unicode")) {
            return TipoErro.UNICODE_INVALIDO;
        } else if (mensagem.contains("número") || mensagem.contains("dígito")) {
            return TipoErro.NUMERO_MALFORMADO;
        }
        return TipoErro.CARACTERE_INVALIDO;
    }
    
    @Override
    public String toString() {
        return String.format("Erro léxico na linha %d, coluna %d: %s", 
                           linha, coluna, mensagem);
    }
    
    // Getters...
}
```

### Estratégias de Recuperação de Erros

```java
/**
 * Estratégias para recuperação de erros durante análise léxica
 */
public class RecuperadorErros {
    
    /**
     * Recupera de erro em string não terminada
     */
    public void recuperarStringNaoTerminada(AnalisadorLexico lexer) {
        // Pula até encontrar aspas de fechamento ou fim da linha
        while (!lexer.fimDoArquivo() && 
               lexer.caracterAtual() != '"' && 
               lexer.caracterAtual() != '\n') {
            lexer.consumirCaracter();
        }
        
        if (lexer.caracterAtual() == '"') {
            lexer.consumirCaracter(); // Consome aspas de fechamento
        }
    }
    
    /**
     * Recupera de comentário não terminado
     */
    public void recuperarComentarioNaoTerminado(AnalisadorLexico lexer) {
        // Para comentário de bloco, pula até encontrar */ ou fim do arquivo
        while (!lexer.fimDoArquivo()) {
            if (lexer.caracterAtual() == '*' && 
                lexer.proximoCaracter() == '/') {
                lexer.consumirCaracter(); // Consome '*'
                lexer.consumirCaracter(); // Consome '/'
                break;
            }
            lexer.consumirCaracter();
        }
    }
    
    /**
     * Recupera de caractere inválido
     */
    public void recuperarCaractereInvalido(AnalisadorLexico lexer) {
        // Simplesmente pula o caractere inválido
        lexer.consumirCaracter();
    }
}
```

---

## Exemplos de Uso

### Uso Básico

```java
/**
 * Exemplo básico de uso do analisador léxico
 */
public class ExemploUsoBasico {
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
        
        Token token;
        System.out.println("=== ANÁLISE LÉXICA ===");
        
        do {
            token = lexer.proximoToken();
            System.out.printf("%-20s | %-15s | Linha: %2d | Coluna: %2d%n",
                            token.getTipo(),
                            token.getValor(),
                            token.getLinha(),
                            token.getColuna());
        } while (token.getTipo() != TipoToken.EOF);
        
        // Exibe erros se houver
        if (!lexer.getErros().isEmpty()) {
            System.out.println("\n=== ERROS ENCONTRADOS ===");
            for (ErroLexico erro : lexer.getErros()) {
                System.out.println(erro);
            }
        }
    }
}
```

**Saída Esperada:**
```
=== ANÁLISE LÉXICA ===
PUBLIC               | public          | Linha:  1 | Coluna:  1
CLASS                | class           | Linha:  1 | Coluna:  8
IDENTIFICADOR        | HelloWorld      | Linha:  1 | Coluna: 14
ABRE_CHAVE          | {               | Linha:  1 | Coluna: 25
PUBLIC               | public          | Linha:  2 | Coluna:  5
STATIC               | static          | Linha:  2 | Coluna: 12
VOID                 | void            | Linha:  2 | Coluna: 19
IDENTIFICADOR        | main            | Linha:  2 | Coluna: 24
ABRE_PARENTESE      | (               | Linha:  2 | Coluna: 28
STRING               | String          | Linha:  2 | Coluna: 29
ABRE_COLCHETE       | [               | Linha:  2 | Coluna: 35
FECHA_COLCHETE      | ]               | Linha:  2 | Coluna: 36
IDENTIFICADOR        | args            | Linha:  2 | Coluna: 38
FECHA_PARENTESE     | )               | Linha:  2 | Coluna: 42
...
```

### Análise com Configurações Personalizadas

```java
/**
 * Exemplo com configurações personalizadas
 */
public class ExemploConfiguracaoPersonalizada {
    public static void main(String[] args) {
        String codigo = """
            // Este é um comentário
            int x = 10; /* comentário de bloco */
            """;
        
        // Criar lexer que preserva comentários
        AnalisadorLexico lexer = new AnalisadorLexico(codigo);
        lexer.setIgnorarComentarios(false);
        lexer.setIgnorarEspacos(false);
        
        // Analisar todos os tokens
        List<Token> tokens = lexer.analisarTodos();
        
        // Filtrar apenas comentários
        List<Token> comentarios = tokens.stream()
            .filter(t -> t.getTipo() == TipoToken.COMENTARIO_LINHA || 
                        t.getTipo() == TipoToken.COMENTARIO_BLOCO)
            .collect(Collectors.toList());
        
        System.out.println("Comentários encontrados:");
        comentarios.forEach(System.out::println);
        
        // Estatísticas
        Map<TipoToken, Long> estatisticas = tokens.stream()
            .collect(Collectors.groupingBy(Token::getTipo, 
                                         Collectors.counting()));
        
        System.out.println("\nEstatísticas de tokens:");
        estatisticas.entrySet().stream()
            .sorted(Map.Entry.<TipoToken, Long>comparingByValue().reversed())
            .forEach(entry -> 
                System.out.printf("%-20s: %d%n", entry.getKey(), entry.getValue()));
    }
}
```

### Validação de Sintaxe Léxica

```java
/**
 * Exemplo de validação de sintaxe léxica
 */
public class ValidadorSintaxeLexi {
    public static boolean validarSintaxeLexica(String codigo) {
        AnalisadorLexico lexer = new AnalisadorLexico(codigo);
        List<Token> tokens = lexer.analisarTodos();
        
        // Verifica se há tokens de erro
        boolean temErros = tokens.stream()
            .anyMatch(t -> t.getTipo() == TipoToken.ERRO);
        
        // Verifica se há erros reportados
        boolean temErrosReportados = !lexer.getErros().isEmpty();
        
        if (temErros || temErrosReportados) {
            System.out.println("Erros léxicos encontrados:");
            
            // Mostra tokens de erro
            tokens.stream()
                .filter(t -> t.getTipo() == TipoToken.ERRO)
                .forEach(t -> System.out.printf("Token inválido '%s' na linha %d%n",
                                              t.getValor(), t.getLinha()));
            
            // Mostra erros reportados
            lexer.getErros().forEach(System.out::println);
            
            return false;
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        // Código válido
        String codigoValido = "int x = 10;";
        System.out.println("Código válido: " + 
                         validarSintaxeLexica(codigoValido));
        
        // Código com erro
        String codigoInvalido = "int x = 10.5.3;"; // Número malformado
        System.out.println("Código inválido: " + 
                         validarSintaxeLexica(codigoInvalido));
    }
}
```

---

## Testes e Validação

### Suite de Testes Unitários

```java
/**
 * Testes unitários para o analisador léxico
 */
public class AnalisadorLexicoTest {
    
    private AnalisadorLexico lexer;
    
    @BeforeEach
    void setUp() {
        lexer = new AnalisadorLexico("");
    }
    
    @Test
    void testPalavrasChave() {
        lexer = new AnalisadorLexico("public class int if");
        
        assertEquals(TipoToken.PUBLIC, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.CLASS, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.INT, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.IF, lexer.proximoToken().getTipo());
    }
    
    @Test
    void testNumeros() {
        // Inteiros
        lexer = new AnalisadorLexico("42 0 1234567890");
        Token token1 = lexer.proximoToken();
        assertEquals(TipoToken.NUMERO_INTEIRO, token1.getTipo());
        assertEquals("42", token1.getValor());
        
        // Decimais
        lexer = new AnalisadorLexico("3.14 0.5 123.456");
        Token token2 = lexer.proximoToken();
        assertEquals(TipoToken.NUMERO_DECIMAL, token2.getTipo());
        assertEquals("3.14", token2.getValor());
        
        // Notação científica
        lexer = new AnalisadorLexico("1e10 2.5E-3");
        Token token3 = lexer.proximoToken();
        assertEquals(TipoToken.NUMERO_DECIMAL, token3.getTipo());
        assertEquals("1e10", token3.getValor());
    }
    
    @Test
    void testStrings() {
        lexer = new AnalisadorLexico("\"Hello World\" \"\\n\\t\\\"\"");
        
        Token token1 = lexer.proximoToken();
        assertEquals(TipoToken.STRING_LITERAL, token1.getTipo());
        assertEquals("Hello World", token1.getValor());
        
        Token token2 = lexer.proximoToken();
        assertEquals(TipoToken.STRING_LITERAL, token2.getTipo());
        assertEquals("\n\t\"", token2.getValor());
    }
    
    @Test
    void testOperadores() {
        lexer = new AnalisadorLexico("+ - * / == != <= >= && || ++ --");
        
        assertEquals(TipoToken.MAIS, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.MENOS, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.MULTIPLICACAO, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.DIVISAO, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.IGUAL, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.DIFERENTE, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.MENOR_IGUAL, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.MAIOR_IGUAL, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.E_LOGICO, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.OU_LOGICO, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.INCREMENTO, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.DECREMENTO, lexer.proximoToken().getTipo());
    }
    
    @Test
    void testComentarios() {
        lexer = new AnalisadorLexico("""
            // Comentário de linha
            /* Comentário
               de bloco */
            int x = 10;
            """);
        
        lexer.setIgnorarComentarios(false);
        
        assertEquals(TipoToken.COMENTARIO_LINHA, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.COMENTARIO_BLOCO, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.INT, lexer.proximoToken().getTipo());
    }
    
    @Test
    void testErros() {
        lexer = new AnalisadorLexico("int x = \"string não terminada");
        
        // Deve encontrar tokens válidos
        assertEquals(TipoToken.INT, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.IDENTIFICADOR, lexer.proximoToken().getTipo());
        assertEquals(TipoToken.ATRIBUICAO, lexer.proximoToken().getTipo());
        
        // Deve reportar erro na string
        Token stringToken = lexer.proximoToken();
        assertEquals(TipoToken.STRING_LITERAL, stringToken.getTipo());
        
        // Deve haver erro reportado
        assertFalse(lexer.getErros().isEmpty());
    }
    
    @Test
    void testPosicionamento() {
        lexer = new AnalisadorLexico("""
            int x;
            double y;
            """);
        
        Token token1 = lexer.proximoToken(); // int
        assertEquals(1, token1.getLinha());
        assertEquals(1, token1.getColuna());
        
        Token token2 = lexer.proximoToken(); // x
        assertEquals(1, token2.getLinha());
        assertEquals(5, token2.getColuna());
        
        Token token3 = lexer.proximoToken(); // ;
        assertEquals(1, token3.getLinha());
        assertEquals(6, token3.getColuna());
        
        Token token4 = lexer.proximoToken(); // double
        assertEquals(2, token4.getLinha());
        assertEquals(1, token4.getColuna());
    }
}
```

### Testes de Performance

```java
/**
 * Testes de performance do analisador léxico
 */
public class TestePerformance {
    
    @Test
    void testPerformanceArquivoGrande() {
        // Gera código Java grande
        StringBuilder codigoGrande = new StringBuilder();
        
        for (int i = 0; i < 10000; i++) {
            codigoGrande.append(String.format(
                "public class Classe%d {%n" +
                "    private int campo%d = %d;%n" +
                "    public void metodo%d() {%n" +
                "        System.out.println(\"Teste %d\");%n" +
                "    }%n" +
                "}%n", i, i, i, i, i));
        }
        
        long inicio = System.currentTimeMillis();
        
        AnalisadorLexico lexer = new AnalisadorLexico(codigoGrande.toString());
        List<Token> tokens = lexer.analisarTodos();
        
        long fim = System.currentTimeMillis();
        long tempoExecucao = fim - inicio;
        
        System.out.printf("Analisados %d tokens em %d ms%n", 
                         tokens.size(), tempoExecucao);
        System.out.printf("Velocidade: %.2f tokens/ms%n", 
                         (double) tokens.size() / tempoExecucao);
        
        // Verifica se não houve erros
        assertTrue(lexer.getErros().isEmpty());
        
        // Performance deve ser razoável (menos de 1s para 10k classes)
        assertTrue(tempoExecucao < 1000, 
                  "Análise muito lenta: " + tempoExecucao + "ms");
    }
    
    @Test
    void testMemoria() {
        String codigo = "int x = 10;".repeat(100000);
        
        Runtime runtime = Runtime.getRuntime();
        long memoriaInicial = runtime.totalMemory() - runtime.freeMemory();
        
        AnalisadorLexico lexer = new AnalisadorLexico(codigo);
        List<Token> tokens = lexer.analisarTodos();
        
        long memoriaFinal = runtime.totalMemory() - runtime.freeMemory();
        long usoMemoria = memoriaFinal - memoriaInicial;
        
        System.out.printf("Uso de memória: %.2f MB para %d tokens%n",
                         usoMemoria / (1024.0 * 1024.0), tokens.size());
        
        // Uso de memória deve ser proporcional ao tamanho da entrada
        double razaoMemoria = (double) usoMemoria / codigo.length();
        assertTrue(razaoMemoria < 10.0, 
                  "Uso excessivo de memória: " + razaoMemoria);
    }
}
```

---

## Otimizações

### Otimização de Performance

#### 1. Tabela de Símbolos com Cache

```java
/**
 * Tabela de símbolos otimizada com cache LRU
 */
public class TabelaSimbolosOtimizada {
    private final Map<String, Integer> tabelaSimbolos;
    private final LRUCache<String, TipoToken> cachePalavrasChave;
    private int proximoId;
    
    public TabelaSimbolosOtimizada(int tamanhoCache) {
        this.tabelaSimbolos = new HashMap<>();
        this.cachePalavrasChave = new LRUCache<>(tamanhoCache);
        this.proximoId = 1;
    }
    
    public int obterOuCriarId(String identificador) {
        return tabelaSimbolos.computeIfAbsent(identificador, 
            k -> proximoId++);
    }
    
    public TipoToken verificarPalavraChave(String palavra) {
        return cachePalavrasChave.computeIfAbsent(palavra, 
            this::buscarPalavraChave);
    }
    
    private TipoToken buscarPalavraChave(String palavra) {
        // Busca otimizada por hash
        return switch (palavra.hashCode()) {
            case 3357030 -> "if".equals(palavra) ? TipoToken.IF : TipoToken.IDENTIFICADOR;
            case 3315118 -> "for".equals(palavra) ? TipoToken.FOR : TipoToken.IDENTIFICADOR;
            case 94842723 -> "class".equals(palavra) ? TipoToken.CLASS : TipoToken.IDENTIFICADOR;
            case -982503830 -> "public".equals(palavra) ? TipoToken.PUBLIC : TipoToken.IDENTIFICADOR;
            // ... outros casos
            default -> TipoToken.IDENTIFICADOR;
        };
    }
}
```

#### 2. Buffer Circular para Entrada Grande

```java
/**
 * Buffer circular para otimizar leitura de arquivos grandes
 */
public class BufferCircular {
    private final char[] buffer;
    private final int tamanhoBuffer;
    private int posicaoAtual;
    private int posicaoFinal;
    private final Reader entrada;
    
    public BufferCircular(Reader entrada, int tamanhoBuffer) {
        this.entrada = entrada;
        this.tamanhoBuffer = tamanhoBuffer;
        this.buffer = new char[tamanhoBuffer];
        this.posicaoAtual = 0;
        this.posicaoFinal = 0;
        preencherBuffer();
    }
    
    public char proximoCaracter() {
        if (posicaoAtual >= posicaoFinal && !preencherBuffer()) {
            return '\0'; // EOF
        }
        
        return buffer[posicaoAtual++ % tamanhoBuffer];
    }
    
    private boolean preencherBuffer() {
        try {
            int bytesLidos = entrada.read(buffer, posicaoFinal % tamanhoBuffer, 
                                        tamanhoBuffer - (posicaoFinal % tamanhoBuffer));
            if (bytesLidos > 0) {
                posicaoFinal += bytesLidos;
                return true;
            }
        } catch (IOException e) {
            // Log error
        }
        return false;
    }
}
```

#### 3. Automato Finito Determinístico

```java
/**
 * Implementação com automato finito determinístico para tokens
 */
public class AutomatoFinito {
    private static final int[][] tabelaTransicao = {
        // Estados: INICIAL, NUMERO, DECIMAL, IDENTIFICADOR, STRING, etc.
        /*INICIAL*/      {1, 2, 0, 3, 4, 0, 0, 0},
        /*NUMERO*/       {0, 1, 2, 0, 0, 0, 0, 0},
        /*DECIMAL*/      {0, 2, 0, 0, 0, 0, 0, 0},
        /*IDENTIFICADOR*/{0, 0, 0, 3, 0, 0, 0, 0},
        /*STRING*/       {0, 0, 0, 0, 4, 0, 5, 0},
        /*STRING_ESC*/   {0, 0, 0, 0, 4, 0, 0, 0},
        // ... mais estados
    };
    
    private static final boolean[] estadosFinais = {
        false, true, true, true, false, false, true
    };
    
    public Token reconhecerToken(String entrada, int posicao) {
        int estado = 0;
        int inicio = posicao;
        int ultimoEstadoFinal = -1;
        int ultimaPosicaoFinal = inicio;
        
        while (posicao < entrada.length()) {
            char c = entrada.charAt(posicao);
            int simbolo = classificarCaracter(c);
            
            estado = tabelaTransicao[estado][simbolo];
            
            if (estado == 0) break; // Estado de erro
            
            posicao++;
            
            if (estadosFinais[estado]) {
                ultimoEstadoFinal = estado;
                ultimaPosicaoFinal = posicao;
            }
        }
        
        if (ultimoEstadoFinal != -1) {
            String valor = entrada.substring(inicio, ultimaPosicaoFinal);
            TipoToken tipo = determinarTipoToken(ultimoEstadoFinal, valor);
            return new Token(tipo, valor, 0, 0, inicio, ultimaPosicaoFinal - 1);
        }
        
        return null; // Token não reconhecido
    }
    
    private int classificarCaracter(char c) {
        if (Character.isDigit(c)) return 1;
        if (c == '.') return 2;
        if (Character.isLetter(c) || c == '_') return 3;
        if (c == '"') return 4;
        if (c == '\\') return 6;
        return 0;
    }
}
```

### Otimização de Memória

#### 1. Pool de Objetos Token

```java
/**
 * Pool de objetos Token para reduzir alocações
 */
public class TokenPool {
    private final Queue<Token> poolTokens;
    private final AtomicInteger contadorUso;
    
    public TokenPool(int tamanhoInicial) {
        this.poolTokens = new ConcurrentLinkedQueue<>();
        this.contadorUso = new AtomicInteger(0);
        
        // Pré-aloca tokens
        for (int i = 0; i < tamanhoInicial; i++) {
            poolTokens.offer(new Token(null, null, 0, 0, 0, 0));
        }
    }
    
    public Token obterToken(TipoToken tipo, String valor, int linha, int coluna,
                           int posInicial, int posFinal) {
        Token token = poolTokens.poll();
        
        if (token == null) {
            token = new Token(tipo, valor, linha, coluna, posInicial, posFinal);
            contadorUso.incrementAndGet();
        } else {
            token.reutilizar(tipo, valor, linha, coluna, posInicial, posFinal);
        }
        
        return token;
    }
    
    public void retornarToken(Token token) {
        if (poolTokens.size() < 1000) { // Limite do pool
            token.limpar();
            poolTokens.offer(token);
        }
    }
    
    public int getTamanhoPool() {
        return poolTokens.size();
    }
    
    public int getContadorUso() {
        return contadorUso.get();
    }
}

/**
 * Token modificado para suportar reutilização
 */
public class Token {
    private TipoToken tipo;
    private String valor;
    private int linha;
    private int coluna;
    private int posicaoInicial;
    private int posicaoFinal;
    
    public void reutilizar(TipoToken tipo, String valor, int linha, int coluna,
                          int posInicial, int posFinal) {
        this.tipo = tipo;
        this.valor = valor;
        this.linha = linha;
        this.coluna = coluna;
        this.posicaoInicial = posInicial;
        this.posicaoFinal = posFinal;
    }
    
    public void limpar() {
        this.tipo = null;
        this.valor = null;
        this.linha = 0;
        this.coluna = 0;
        this.posicaoInicial = 0;
        this.posicaoFinal = 0;
    }
    
    // ... resto da implementação
}
```

#### 2. Interning de Strings

```java
/**
 * Cache inteligente para strings comuns
 */
public class StringInterner {
    private final Map<String, String> cache;
    private final Set<String> stringsComuns;
    
    public StringInterner() {
        this.cache = new ConcurrentHashMap<>();
        this.stringsComuns = Set.of(
            "public", "private", "protected", "static", "final",
            "class", "interface", "extends", "implements",
            "int", "double", "String", "boolean", "void",
            "if", "else", "while", "for", "return",
            "+", "-", "*", "/", "=", "==", "!=",
            "{", "}", "(", ")", "[", "]", ";", ","
        );
    }
    
    public String intern(String str) {
        if (str == null || str.length() > 50) {
            return str; // Não interna strings muito grandes
        }
        
        if (stringsComuns.contains(str)) {
            return cache.computeIfAbsent(str, Function.identity());
        }
        
        return str;
    }
    
    public void limparCache() {
        cache.clear();
    }
    
    public int getTamanhoCache() {
        return cache.size();
    }
}
```

### Técnicas Avançadas

#### 1. Análise Paralela para Arquivos Grandes

```java
/**
 * Analisador léxico paralelo para arquivos grandes
 */
public class AnalisadorLexicoParalelo {
    private final ExecutorService executorService;
    private final int numeroThreads;
    
    public AnalisadorLexicoParalelo(int numeroThreads) {
        this.numeroThreads = numeroThreads;
        this.executorService = Executors.newFixedThreadPool(numeroThreads);
    }
    
    public List<Token> analisarParalelo(String codigoFonte) throws Exception {
        int tamanhoBloco = codigoFonte.length() / numeroThreads;
        List<Future<List<Token>>> futuros = new ArrayList<>();
        
        for (int i = 0; i < numeroThreads; i++) {
            int inicio = i * tamanhoBloco;
            int fim = (i == numeroThreads - 1) ? codigoFonte.length() 
                                               : (i + 1) * tamanhoBloco;
            
            // Ajusta limites para não quebrar tokens
            if (i > 0) {
                inicio = ajustarInicioBloco(codigoFonte, inicio);
            }
            if (i < numeroThreads - 1) {
                fim = ajustarFimBloco(codigoFonte, fim);
            }
            
            final int inicioFinal = inicio;
            final int fimFinal = fim;
            
            Future<List<Token>> futuro = executorService.submit(() -> {
                String bloco = codigoFonte.substring(inicioFinal, fimFinal);
                AnalisadorLexico lexer = new AnalisadorLexico(bloco);
                return lexer.analisarTodos();
            });
            
            futuros.add(futuro);
        }
        
        // Coleta resultados
        List<Token> todosTokens = new ArrayList<>();
        for (Future<List<Token>> futuro : futuros) {
            List<Token> tokens = futuro.get();
            todosTokens.addAll(tokens);
        }
        
        return todosTokens;
    }
    
    private int ajustarInicioBloco(String codigo, int posicao) {
        // Move para trás até encontrar um separador seguro
        while (posicao > 0 && !Character.isWhitespace(codigo.charAt(posicao - 1))) {
            posicao--;
        }
        return posicao;
    }
    
    private int ajustarFimBloco(String codigo, int posicao) {
        // Move para frente até encontrar um separador seguro
        while (posicao < codigo.length() && 
               !Character.isWhitespace(codigo.charAt(posicao))) {
            posicao++;
        }
        return posicao;
    }
    
    public void shutdown() {
        executorService.shutdown();
    }
}
```

#### 2. Cache de Resultados

```java
/**
 * Cache inteligente de tokens para códigos similares
 */
public class CacheTokens {
    private final Map<String, List<Token>> cache;
    private final LRUCache<String, String> cacheHash;
    private final int tamanhoMaximo;
    
    public CacheTokens(int tamanhoMaximo) {
        this.tamanhoMaximo = tamanhoMaximo;
        this.cache = new LinkedHashMap<String, List<Token>>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, List<Token>> eldest) {
                return size() > tamanhoMaximo;
            }
        };
        this.cacheHash = new LRUCache<>(tamanhoMaximo * 2);
    }
    
    public List<Token> obterTokensCacheados(String codigo) {
        String hash = calcularHash(codigo);
        return cache.get(hash);
    }
    
    public void armazenarTokens(String codigo, List<Token> tokens) {
        if (codigo.length() < 10000) { // Só cacheia códigos pequenos/médios
            String hash = calcularHash(codigo);
            cache.put(hash, new ArrayList<>(tokens));
        }
    }
    
    private String calcularHash(String codigo) {
        return cacheHash.computeIfAbsent(codigo, k -> {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(k.getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(digest);
            } catch (Exception e) {
                return String.valueOf(k.hashCode());
            }
        });
    }
    
    public void limparCache() {
        cache.clear();
        cacheHash.clear();
    }
    
    public double getTaxaAcerto() {
        return cache.size() > 0 ? 
               cacheHash.getHits() / (double) cacheHash.getRequests() : 0.0;
    }
}
```

---

## Integração com IDE e Ferramentas

### Suporte para Language Server Protocol (LSP)

```java
/**
 * Integração com Language Server Protocol
 */
public class JavaLanguageServer {
    private final AnalisadorLexico lexer;
    private final Map<String, List<Token>> documentos;
    
    public JavaLanguageServer() {
        this.lexer = new AnalisadorLexico("");
        this.documentos = new ConcurrentHashMap<>();
    }
    
    public void didOpenTextDocument(String uri, String conteudo) {
        List<Token> tokens = analisarDocumento(conteudo);
        documentos.put(uri, tokens);
        
        // Envia diagnósticos
        List<Diagnostic> diagnosticos = criarDiagnosticos(tokens, lexer.getErros());
        enviarDiagnosticos(uri, diagnosticos);
    }
    
    public void didChangeTextDocument(String uri, String conteudo) {
        didOpenTextDocument(uri, conteudo); // Re-analisa
    }
    
    private List<Token> analisarDocumento(String conteudo) {
        AnalisadorLexico lexerDoc = new AnalisadorLexico(conteudo);
        return lexerDoc.analisarTodos();
    }
    
    private List<Diagnostic> criarDiagnosticos(List<Token> tokens, 
                                              List<ErroLexico> erros) {
        List<Diagnostic> diagnosticos = new ArrayList<>();
        
        // Converte erros léxicos em diagnósticos LSP
        for (ErroLexico erro : erros) {
            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setRange(new Range(
                new Position(erro.getLinha() - 1, erro.getColuna() - 1),
                new Position(erro.getLinha() - 1, erro.getColuna())
            ));
            diagnostic.setSeverity(DiagnosticSeverity.Error);
            diagnostic.setMessage(erro.getMensagem());
            diagnostic.setSource("java-lexer");
            
            diagnosticos.add(diagnostic);
        }
        
        // Adiciona warnings para práticas questionáveis
        for (Token token : tokens) {
            if (token.getTipo() == TipoToken.IDENTIFICADOR) {
                String valor = token.getValor();
                
                // Warning para variáveis com uma letra só
                if (valor.length() == 1 && !valor.equals("i") && !valor.equals("j")) {
                    Diagnostic warning = new Diagnostic();
                    warning.setRange(criarRange(token));
                    warning.setSeverity(DiagnosticSeverity.Warning);
                    warning.setMessage("Nome de variável muito curto: " + valor);
                    warning.setSource("java-lexer");
                    diagnosticos.add(warning);
                }
            }
        }
        
        return diagnosticos;
    }
    
    public CompletionList completion(String uri, Position posicao) {
        List<Token> tokens = documentos.get(uri);
        if (tokens == null) return new CompletionList();
        
        // Encontra contexto atual
        Token tokenAnterior = encontrarTokenAnterior(tokens, posicao);
        
        List<CompletionItem> itens = new ArrayList<>();
        
        if (tokenAnterior != null && tokenAnterior.getTipo() == TipoToken.PONTO) {
            // Completar métodos/campos
            itens.addAll(criarCompletionMetodos());
        } else {
            // Completar palavras-chave
            itens.addAll(criarCompletionPalavrasChave());
        }
        
        return new CompletionList(itens);
    }
    
    private List<CompletionItem> criarCompletionPalavrasChave() {
        return Arrays.stream(TipoToken.values())
            .filter(tipo -> tipo.getValor() != null)
            .map(tipo -> {
                CompletionItem item = new CompletionItem();
                item.setLabel(tipo.getValor());
                item.setKind(CompletionItemKind.Keyword);
                item.setDetail("Palavra-chave Java");
                return item;
            })
            .collect(Collectors.toList());
    }
}
```

### Plugin para IntelliJ IDEA

```java
/**
 * Plugin para IntelliJ IDEA
 */
public class JavaLexerPlugin extends AbstractProjectComponent {
    
    public JavaLexerPlugin(Project project) {
        super(project);
    }
    
    @Override
    public void initComponent() {
        // Registra highlight personalizado
        EditorColorsManager.getInstance().addColorsScheme(
            new JavaLexerColorScheme()
        );
        
        // Registra inspeções
        InspectionManager.getInstance(myProject).registerInspection(
            new JavaLexerInspection()
        );
    }
    
    /**
     * Inspeção customizada baseada no lexer
     */
    private static class JavaLexerInspection extends LocalInspectionTool {
        
        @Override
        public ProblemDescriptor[] checkFile(@NotNull PsiFile file,
                                           @NotNull InspectionManager manager,
                                           boolean isOnTheFly) {
            
            if (!(file instanceof PsiJavaFile)) {
                return ProblemDescriptor.EMPTY_ARRAY;
            }
            
            String texto = file.getText();
            AnalisadorLexico lexer = new AnalisadorLexico(texto);
            List<Token> tokens = lexer.analisarTodos();
            List<ProblemDescriptor> problemas = new ArrayList<>();
            
            // Verifica problemas específicos
            for (ErroLexico erro : lexer.getErros()) {
                TextRange range = new TextRange(
                    erro.getPosicao(), 
                    erro.getPosicao() + 1
                );
                
                ProblemDescriptor problema = manager.createProblemDescriptor(
                    file,
                    range,
                    erro.getMensagem(),
                    ProblemHighlightType.ERROR,
                    isOnTheFly
                );
                
                problemas.add(problema);
            }
            
            return problemas.toArray(new ProblemDescriptor[0]);
        }
        
        @Override
        @NotNull
        public String getShortName() {
            return "JavaLexerInspection";
        }
    }
    
    /**
     * Esquema de cores personalizado
     */
    private static class JavaLexerColorScheme extends DefaultColorsScheme {
        
        public JavaLexerColorScheme() {
            super(null);
            initFontsAndColors();
        }
        
        private void initFontsAndColors() {
            // Define cores para diferentes tipos de token
            TextAttributes keywordAttrs = new TextAttributes();
            keywordAttrs.setForegroundColor(new Color(127, 0, 85));
            keywordAttrs.setFontType(Font.BOLD);
            
            setAttributes(JavaHighlightingColors.KEYWORD, keywordAttrs);
            
            TextAttributes stringAttrs = new TextAttributes();
            stringAttrs.setForegroundColor(new Color(0, 128, 0));
            
            setAttributes(JavaHighlightingColors.STRING, stringAttrs);
        }
        
        @Override
        @NotNull
        public String getName() {
            return "Java Lexer Enhanced";
        }
    }
}
```

---

## Métricas e Monitoramento

### Sistema de Métricas

```java
/**
 * Sistema de métricas para monitoramento do lexer
 */
public class MetricasLexer {
    private final Timer tempoAnalise;
    private final Counter tokensProcessados;
    private final Counter errosEncontrados;
    private final Histogram tamanhoArquivos;
    private final Gauge cacheHitRate;
    
    public MetricasLexer() {
        MeterRegistry registry = Metrics.globalRegistry;
        
        this.tempoAnalise = Timer.builder("lexer.tempo.analise")
            .description("Tempo de análise léxica")
            .register(registry);
            
        this.tokensProcessados = Counter.builder("lexer.tokens.processados")
            .description("Total de tokens processados")
            .register(registry);
            
        this.errosEncontrados = Counter.builder("lexer.erros.encontrados")
            .description("Total de erros léxicos encontrados")
            .register(registry);
            
        this.tamanhoArquivos = Histogram.builder("lexer.arquivo.tamanho")
            .description("Distribuição do tamanho dos arquivos analisados")
            .register(registry);
            
        this.cacheHitRate = Gauge.builder("lexer.cache.hit.rate")
            .description("Taxa de acerto do cache")
            .register(registry, this, MetricasLexer::calcularTaxaAcertoCache);
    }
    
    public void registrarAnalise(String codigo, List<Token> tokens, 
                                List<ErroLexico> erros, long tempoMs) {
        tempoAnalise.record(tempoMs, TimeUnit.MILLISECONDS);
        tokensProcessados.increment(tokens.size());
        errosEncontrados.increment(erros.size());
        tamanhoArquivos.record(codigo.length());
    }
    
    private double calcularTaxaAcertoCache() {
        // Implementação específica baseada no cache usado
        return 0.85; // Exemplo
    }
    
    public void gerarRelatorio() {
        System.out.println("=== RELATÓRIO DE MÉTRICAS ===");
        System.out.printf("Tokens processados: %.0f%n", 
                         tokensProcessados.count());
        System.out.printf("Erros encontrados: %.0f%n", 
                         errosEncontrados.count());
        System.out.printf("Tempo médio de análise: %.2f ms%n", 
                         tempoAnalise.mean(TimeUnit.MILLISECONDS));
        System.out.printf("Taxa de acerto do cache: %.2f%%%n", 
                         cacheHitRate.value() * 100);
    }
}
```

### Profiling e Debug

```java
/**
 * Ferramentas de profiling para otimização
 */
public class ProfilerLexer {
    private final Map<String, Long> temposOperacao;
    private final Map<TipoToken, Integer> distribuicaoTokens;
    private long tempoTotal;
    
    public ProfilerLexer() {
        this.temposOperacao = new HashMap<>();
        this.distribuicaoTokens = new EnumMap<>(TipoToken.class);
        this.tempoTotal = 0;
    }
    
    public void iniciarProfiler() {
        temposOperacao.clear();
        distribuicaoTokens.clear();
        tempoTotal = System.nanoTime();
    }
    
    public void registrarOperacao(String operacao, long tempoNs) {
        temposOperacao.merge(operacao, tempoNs, Long::sum);
    }
    
    public void registrarToken(TipoToken tipo) {
        distribuicaoTokens.merge(tipo, 1, Integer::sum);
    }
    
    public void finalizarProfiler() {
        tempoTotal = System.nanoTime() - tempoTotal;
    }
    
    public void imprimirRelatorio() {
        System.out.println("=== PROFILING REPORT ===");
        System.out.printf("Tempo total: %.2f ms%n", tempoTotal / 1_000_000.0);
        
        System.out.println("\nTempo por operação:");
        temposOperacao.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> 
                System.out.printf("  %s: %.2f ms (%.1f%%)%n",
                    entry.getKey(),
                    entry.getValue() / 1_000_000.0,
                    (entry.getValue() * 100.0) / tempoTotal));
        
        System.out.println("\nDistribuição de tokens:");
        distribuicaoTokens.entrySet().stream()
            .sorted(Map.Entry.<TipoToken, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry ->
                System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue()));
    }
}
```

---

## Extensibilidade e Modularidade

### Interface para Extensões

```java
/**
 * Interface para extensões do analisador léxico
 */
public interface ExtensaoLexer {
    
    /**
     * Processa token antes da criação final
     * @param tipo Tipo do token
     * @param valor Valor do token
     * @param contexto Contexto atual
     * @return Token modificado ou null para descartar
     */
    Token processarToken(TipoToken tipo, String valor, ContextoAnalise contexto);
    
    /**
     * Adiciona novos tipos de token
     * @return Mapa de padrões para novos tipos
     */
    Map<Pattern, TipoToken> novosTokens();
    
    /**
     * Processa caracteres especiais não reconhecidos
     * @param caractere Caractere não reconhecido
     * @param contexto Contexto atual
     * @return Token criado ou null se não processado
     */
    Token processarCaractereEspecial(char caractere, ContextoAnalise contexto);
    
    /**
     * Valida se extensão está ativa
     * @param contexto Contexto atual
     * @return true se deve ser aplicada
     */
    boolean isAtiva(ContextoAnalise contexto);
    
    /**
     * Prioridade da extensão (maior = executa primeiro)
     */
    int getPrioridade();
}

/**
 * Contexto compartilhado durante análise
 */
public class ContextoAnalise {
    private final String codigoFonte;
    private final int posicaoAtual;
    private final int linha;
    private final int coluna;
    private final List<Token> tokensAnteriores;
    
    // Construtor e getters...
    
    public char caracterAtual() {
        return posicaoAtual < codigoFonte.length() ? 
               codigoFonte.charAt(posicaoAtual) : '\0';
    }
    
    public String substring(int inicio, int fim) {
        return codigoFonte.substring(
            Math.max(0, inicio), 
            Math.min(codigoFonte.length(), fim)
        );
    }
    
    public Token ultimoToken() {
        return tokensAnteriores.isEmpty() ? null : 
               tokensAnteriores.get(tokensAnteriores.size() - 1);
    }
}
```

