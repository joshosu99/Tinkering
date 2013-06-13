package java.com.mycompany.app;

public class Name {
	String firstName;
	String lastName;

	public Name(String x, String y) {
        this.firstName = x;
        this.lastName = y;
    }
    
    public void getFirstName() {
        //return this.firstName;
    }
    
    public String getLastName() {
    	return this.lastName;
    }
}