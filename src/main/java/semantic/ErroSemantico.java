package semantic;

/**
 * Exceção para erros semânticos encontrados durante a compilação
 * Exemplos: variável não declarada, variável duplicada, tipo incompatível
 */
public class ErroSemantico extends Exception {
    private int linha;
    private int coluna;
    
    /**
     * Construtor do erro semântico
     * @param mensagem Descrição do erro
     * @param linha Linha onde ocorreu o erro
     * @param coluna Coluna onde ocorreu o erro
     */
    public ErroSemantico(String mensagem, int linha, int coluna) {
        super(String.format("Erro semântico na linha %d, coluna %d: %s", 
                           linha, coluna, mensagem));
        this.linha = linha;
        this.coluna = coluna;
    }
    
    public int getLinha() { 
        return linha; 
    }
    
    public int getColuna() { 
        return coluna; 
    }
}