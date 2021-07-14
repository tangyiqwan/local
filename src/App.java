import java.lang.Math;
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        
        long number = (long) Math.pow(2, 66);
        System.out.println(number);

        char fav = '\u0035';
        System.out.println(fav);

        String name = "yiqwan"; // string is a class, not primitive data type
        System.out.println(name);

        boolean isFat = true;
        System.out.println(isFat);

        int a = 5, b = 2;
        float result = a / b; // 2.0 because a / b is int
        float result2 = (float) a / b;
        System.out.println(result2);
        
    }
}
