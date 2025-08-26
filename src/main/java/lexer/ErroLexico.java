package lexer;

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
    
    // Getters
    public String getMensagem() { return mensagem; }
    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }
    public int getPosicao() { return posicao; }
    public TipoErro getTipo() { return tipo; }
    
    @Override
    public String toString() {
        return String.format("Erro léxico na linha %d, coluna %d: %s", 
                           linha, coluna, mensagem);
    }
}