package java.com.mycompany.app;

import java.com.mycompany.app.Name;

/**
 * Hello world!
 *
 */
public class Person
{

    Name name;

    public Person(String x, String y) {
        this.name = new Name(x, y);
    }
    
    // public String getFirstName() {
    //     return this.name.getFirstName();
    // }
    
    public String getLastName() {
    	return this.name.getLastName();
    }

    public int number(String x) {
        Name y = new Name (x, "Trick");
        y.getFirstName();
        return 5;
    }
}