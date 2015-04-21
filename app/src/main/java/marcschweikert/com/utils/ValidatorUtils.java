package marcschweikert.com.utils;

/**
 * Created by Marc on 4/16/2015.
 */
public class ValidatorUtils {

    public static boolean isEmailValid(final String email) {
        if (null == email) {
            return false;
        }

        return email.contains("@");
    }

    public static boolean isPasswordValid(final String password) {
        if (null == password) {
            return false;
        }

        return password.length() > 4;
    }

    public static boolean isNameValid(final String name) {
        if (null == name) {
            return false;
        }

        return !name.matches(".*\\d+.*");
    }
}
