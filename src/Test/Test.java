package Test;

public class Test {
    private int number;
    private String location;

    public void runTest() {
        System.out.println("Running test...");
    }

    public Test(int number, String location) {
        this.number = number;
        this.location = location;
    }

    // the constructor can have lesser parameters
    public Test(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    
    // Use constructor to initialise the properties when declared
    // use getter setters to let other classes access the private val
}
