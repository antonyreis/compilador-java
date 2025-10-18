package semantic;

import lexer.TipoToken;
import java.util.*;

/**
 * Tabela de símbolos para análise semântica
 * Armazena variáveis declaradas com seu tipo e endereço de memória
 * Permite verificar:
 * - Se uma variável já foi declarada (evita duplicação)
 * - Se uma variável existe antes de ser usada
 */
public class TabelaSimbolos {
    private Map<String, Simbolo> tabela;
    private int proximoEndereco;
    
    public TabelaSimbolos() {
        this.tabela = new LinkedHashMap<>(); // Mantém ordem de inserção
        this.proximoEndereco = 0;
    }
    
    /**
     * Adiciona uma variável na tabela
     * @param identificador Nome da variável
     * @param tipo Tipo da variável
     * @return true se adicionou com sucesso, false se já existe
     */
    public boolean adicionar(String identificador, TipoToken tipo) {
        if (tabela.containsKey(identificador)) {
            return false; // Variável já declarada
        }
        
        Simbolo simbolo = new Simbolo(identificador, tipo, proximoEndereco);
        tabela.put(identificador, simbolo);
        proximoEndereco++;
        return true;
    }
    
    /**
     * Busca uma variável na tabela
     * @param identificador Nome da variável
     * @return Simbolo encontrado ou null se não existe
     */
    public Simbolo buscar(String identificador) {
        return tabela.get(identificador);
    }
    
    /**
     * Verifica se variável existe na tabela
     * @param identificador Nome da variável
     * @return true se existe, false caso contrário
     */
    public boolean existe(String identificador) {
        return tabela.containsKey(identificador);
    }
    
    /**
     * Retorna quantidade de variáveis declaradas
     * @return Número de variáveis
     */
    public int getTamanho() {
        return tabela.size();
    }
    
    /**
     * Lista todas as variáveis da tabela
     * @return Coleção de símbolos
     */
    public Collection<Simbolo> listarTodas() {
        return tabela.values();
    }
    
    /**
     * Imprime tabela de símbolos formatada
     */
    public void imprimir() {
        System.out.println("\n=== TABELA DE SÍMBOLOS ===");
        System.out.printf("%-20s | %-15s | %-10s%n", "Identificador", "Tipo", "Endereço");
        System.out.println("-".repeat(50));
        
        for (Simbolo s : tabela.values()) {
            System.out.printf("%-20s | %-15s | %-10d%n", 
                            s.getIdentificador(), s.getTipo(), s.getEndereco());
        }
    }
    
    /**
     * Limpa toda a tabela de símbolos
     */
    public void limpar() {
        tabela.clear();
        proximoEndereco = 0;
    }
}