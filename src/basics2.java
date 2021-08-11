
import java.util.Scanner;

public class basics2 {
    public static void main(String[] args) {
        String[] letter = {"A", "B", "C"}; // must initialise at the start
        String[] name = new String[3]; // declare array to hold 3 strings
        int[] number_list = new int[5];

        name[0] = "Alfred";
        name[1] = "Billie";
        name[2] = "Cathy";
        number_list[0] = 10;

        for (int i = 0; i < 3; i++) {
            System.out.println(name[i]);
            System.out.println(letter[i]);
            System.out.println(number_list[i]);
        }

        System.out.println("Choose your favourite name: ");
        Scanner scanner = new Scanner(System.in);
        String user_name = scanner.next();
        
        for (int i = 0; i < name.length; i++) {
            if (name[i].equalsIgnoreCase(user_name)) {  // cannot use ==
                System.out.println("It is position " + i);
            }
        }


    } 
}
