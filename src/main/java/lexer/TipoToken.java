package lexer;

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