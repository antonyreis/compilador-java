package lexer;

import java.util.Objects;

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