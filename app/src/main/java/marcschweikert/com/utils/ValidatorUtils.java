package marcschweikert.com.utils;

/**
 * Created by Marc on 4/16/2015.
 */
public class ValidatorUtils {

    public static boolean isEmailValid(final String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(final String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
