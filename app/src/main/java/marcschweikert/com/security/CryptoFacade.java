package marcschweikert.com.security;

import android.util.Log;

import javax.crypto.SecretKey;

/**
 * This is a Facade for the CryptoEngine class.
 * <p/>
 * It is implemented as a Singleton such that we only initialize the crypto engine with our secret
 * passphrase once.  We will store an instance of the CryptoEngine and then delegate to it
 */
public class CryptoFacade {
    /**
     * Our secret key.  Good thing this isn't a security course!
     */
    private static final String SECRET_KEY = "OOADisFun!";
    /**
     * Our Singleton instance
     */
    private static CryptoFacade theInstance;
    /**
     * CryptoEngine to delegate to
     */
    private CryptoEngine myCryptoEngine;

    /**
     * Initialize the CryptoEngine with our secret key
     */
    private CryptoFacade() {
        SecretKey key = null;
        Log.i(getClass().getSimpleName(), "Initializing crypto engine ...");
        try {
            key = CryptoEngine.getRawKey(SECRET_KEY.getBytes());
        } catch (final Exception e) {
            Log.e(getClass().getSimpleName(), "... Failed to initialize crypto engine!" + e.getMessage());
        }

        Log.i(getClass().getSimpleName(), "... crypto engine initialized!");
        myCryptoEngine = new CryptoEngine(key);
    }

    /**
     * @return CryptoFacade instance
     */
    public static CryptoFacade getInstance() {
        if (null == theInstance) {
            theInstance = new CryptoFacade();
        }
        return theInstance;
    }

    /**
     * @param clearPassword Cleartext password
     * @return Hashed password
     */
    public String hashPassword(final String clearPassword) {
        String hashedPassword = null;

        // don't bother encrypting invalid passwords
        if (null == clearPassword || clearPassword.equals("")) {
            return hashedPassword;
        }

        // create the hashed password
        hashedPassword = new String(myCryptoEngine.getHash(clearPassword));
        return hashedPassword;
    }
}
