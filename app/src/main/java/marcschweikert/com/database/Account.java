package marcschweikert.com.database;

import java.io.Serializable;

/**
 * Created by Marc on 4/17/2015.
 */
public class Account implements Serializable {
    private Integer myID;
    private String myFirstName;
    private String myLastName;
    private String myEmail;
    private String myHashedPassword;

    public Account(final String firstName,
                   final String lastName,
                   final String email,
                   final String hashedPassword) {
        myFirstName = firstName;
        myLastName = lastName;
        myEmail = email;
        myHashedPassword = hashedPassword;
    }

    public Integer getID() {
        return myID;
    }

    public void setID(final Integer id) {
        myID = id;
    }

    public String getFirstName() {
        return myFirstName;
    }

    public String getLastName() {
        return myLastName;
    }

    public String getEmail() {
        return myEmail;
    }

    public String getHashedPassword() {
        return myHashedPassword;
    }
}
