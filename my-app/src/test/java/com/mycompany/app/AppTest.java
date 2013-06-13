package java.com.mycompany.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mockito.Mockito;
import java.com.mycompany.app.Person;


import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.core.classloader.annotations.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.exceptions.ConstructorNotFoundException;


import org.powermock.api.mockito.PowerMockito;
import java.io.IOException;

/**
 * Unit test for simple App.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest( { Name.class, Person.class })
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }




//     @Override
//     public void setUp() throws Exception {
//         super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
// //      noExitSecurityManagerInstaller = NoExitSecurityManagerInstaller.installNoExitSecurityManager();

//         Name mockName = PowerMockito.mock(Name.class);
//         Mockito.doThrow(new IOException("********************************************")).when(mockName).getFirstName();
//         //PowerMockito.when(mockName.getFirstName()).thenReturn("aaaaaaaaaa");





//         //PowerMockito.whenNew(Name.class).withArguments(Mockito.anyString(), Mockito.anyString()).thenReturn(mockName);
//         PowerMockito.whenNew(Name.class).withAnyArguments().thenReturn(mockName);
//     }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
//      noExitSecurityManagerInstaller.uninstall();

    }


    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertEquals (App.method1(), "method1");
    }
    
    /**
     * Rigorous Test2
     */
    public void testApp2() {
    	assertEquals (App.method2(), "method2");
    }

    public void testApp3() {
        Person x = new Person ("Josh", "Trick2");
        assertEquals ("Trick2", x.getLastName());
    }

    public void testApp4() {
        Person x = new Person ("Josh", "Trick");
        assertEquals (1, x.number("a"));

    }

    // public void testApp5() {
    //     Person x = new Person ("Josh", "Trick");
    //     //Mocking does nothing here
    //     Name mockedY = Mockito.mock(Name.class);
    //     Mockito.when(mockedY.getFirstName()).thenReturn("AAA");
    //     assertEquals(1, x.number("a"));

    // }


    public void testApp6() throws Exception {
        Person x = new Person ("Josh", "Trick");
        


        Name mockName = PowerMockito.mock(Name.class);
        Mockito.doThrow(new Error("************************************************")).when(mockName).getFirstName();
        //PowerMockito.when(mockName.getFirstName()).thenReturn("aaaaaaaaaa");





        //PowerMockito.whenNew(Name.class).withArguments(Mockito.anyString(), Mockito.anyString()).thenReturn(mockName);
        PowerMockito.whenNew(Name.class).withAnyArguments().thenReturn(mockName);



        //PowerMockito.verifyNew(Name.class).withArguments(Mockito.anyString(), Mockito.anyString());
        assertEquals(10, x.number("Josh"));
    }
}

// why below this can't use doReturn
//PowerMockito.when(mockName.getFirstName()).thenReturn("AAAAA");