package Test;

public class Main {
    public static void main(String[] args) {
        Test test1 = new Test(1, "NTU");
        Test test2 = new Test(2);   // only 1 parameter

        System.out.println(test1.getLocation());
        System.out.println(test2.getNumber());
        test1.runTest();

        int s = 10;
        System.out.println(s);
        
        float [] nums= {1.1f, 2.2f, 3.3f};
        for (int i =0; i < 3; i++) System.out.println(nums[i]);

    }
}
