public class TesteFatorial {
    public static void main(String[] args) {
        int fat;
        int num;
        int cont;
        
        num = 5;
        fat = 1;
        cont = 2;
        
        while (cont <= num) {
            fat = fat * cont;
            cont = cont + 1;
        }
        
        System.out.println(fat);
    }
}