package semantic;

import lexer.TipoToken;

/**
 * Representa uma entrada na tabela de símbolos
 * Armazena informações sobre variáveis declaradas no programa
 */
public class Simbolo {
    private String identificador;
    private TipoToken tipo;
    private int endereco;
    
    /**
     * Construtor do símbolo
     * @param identificador Nome da variável
     * @param tipo Tipo da variável (INT, DOUBLE, STRING, etc)
     * @param endereco Endereço de memória (posição na ordem de declaração)
     */
    public Simbolo(String identificador, TipoToken tipo, int endereco) {
        this.identificador = identificador;
        this.tipo = tipo;
        this.endereco = endereco;
    }
    
    public String getIdentificador() { 
        return identificador; 
    }
    
    public TipoToken getTipo() { 
        return tipo; 
    }
    
    public int getEndereco() { 
        return endereco; 
    }
    
    @Override
    public String toString() {
        return String.format("Simbolo{id='%s', tipo=%s, end=%d}", 
                           identificador, tipo, endereco);
    }
}