package marcschweikert.com.security;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import android.util.Log;

/**
 * This is the class we use to perform our encryption, decryption and hashing
 * operations. It is a little more on the complex side, but I will attempt to
 * explain what it is doing here.
 */
public final class CryptoEngine {
	/** encryption cipher */
	private static Cipher ecipher;
	/** decryption cipher */
	private static Cipher dcipher;
	/** buffer */
	private byte[] buf = new byte[1024];

	/**
	 * It requires a SecretKey to be passed to it which is simply the Java-based
	 * construction of the encryption key. Most important thing to see in this
	 * method is it is creating the ciphers for encryption and decryption using
	 * AES. Implementations can vary based on platform.
	 * 
	 * @param key secret key
	 */
	public CryptoEngine(final SecretKey key) {
		byte[] iv = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d,
				0x0e, 0x0f };

		final AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		try {
			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// CBC requires an initialization vector
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (final Exception e) {
			Log.e(getClass().getSimpleName(), "Unable to load algorithm");
		}
	}

	/**
	 * The method getHash will require a String that will be hashed, and it will
	 * return a byte[]. This is not terribly useful to see, so we will convert
	 * it to hex in the next method.
	 * 
	 * Notice in this method that it is using SHA-1. This is a downlevel
	 * algorithm, but older versions of Android do not support stronger
	 * algorithms.
	 * 
	 * @param password password
	 * @return encrypted bytes
	 */
	public byte[] getHash(final String password) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			Log.e(getClass().getSimpleName(), "No such algorithm");
		}

		byte[] input = null;
		if (null != digest) {
			digest.reset();

			try {
				input = digest.digest(password.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Log.e(getClass().getSimpleName(), "Encoding exception");
			}
		}
		return input;
	}

	/**
	 * The method byteToHex allows us to represent the hashed value as a hex
	 * string. This is easier to store for future use. The method has to do a
	 * bunch of ANDing and masking to essentially split each byte into two and
	 * convert to a hexadecimal value.
	 * 
	 * @param _passphrase passphrase
	 * @return encrypted string
	 */
	public static String byteToHex(final byte[] _passphrase) {
		final StringBuffer buf = new StringBuffer();

		for (int i = 0; i < _passphrase.length; i++) {
			int firsthalf = (_passphrase[i] >>> 4) & 0x0F;

			if ((0 <= firsthalf) && (firsthalf <= 9)) {
				buf.append((char) ('0' + firsthalf));
			} else {
				buf.append((char) ('a' + (firsthalf - 10)));
			}

			final int secondhalf = _passphrase[i] & 0x0F;
			if ((0 <= secondhalf) && (secondhalf <= 9)) {
				buf.append((char) ('0' + secondhalf));
			} else {
				buf.append((char) ('a' + (secondhalf - 10)));
			}
		}

		return new String(buf);
	}

	/**
	 * The method getRawKey(byte[] seed) will use the passphrase you enter to
	 * create a SecretKey object that is used for encryption and decryption. You
	 * could also store this file (in a secret place) to use with other
	 * programs.
	 * 
	 * @param seed seed
	 * @return secret key
	 * @throws Exception failure
	 */
	public static SecretKey getRawKey(final byte[] seed) throws Exception {
		final KeyGenerator kgen = KeyGenerator.getInstance("AES");
		final SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr);
		final SecretKey skey = kgen.generateKey();
		return skey;
	}

	/**
	 * If you do wish to save the SecretKey to file, here is a method to do it.
	 * You would just have to read the file and retrieve the contents into a
	 * SecretKey later on.
	 * 
	 * @param dir dir
	 * @param _key key
	 * @throws IOException exception
	 * @throws FileNotFoundException exception
	 */
	public static void createKeyFile(final String dir, final SecretKey _key) throws IOException, FileNotFoundException {
		final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dir + "/" + "keyfile"));
		oos.writeObject(_key);
		oos.flush();
		oos.close();
	}

	/**
	 * The method encryptFile will require the file you are encrypting in the
	 * form of an InputStream, and it will create the new file in the form of an
	 * OutputStream.
	 * 
	 * @param is input
	 * @param out output
	 */
	public void encryptFile(final InputStream is, final OutputStream out) {
		try {
			final OutputStream cipher_out = new CipherOutputStream(out, ecipher);
			int numRead = 0;
			while ((numRead = is.read(buf)) >= 0) {
				cipher_out.write(buf, 0, numRead);
			}
			cipher_out.close();
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "IO Exception for file");
		}
	}

	/**
	 * The method decryptFile will do the same thing as encryptFile, but it will
	 * apply the decryption Cipher.
	 * 
	 * @param is input
	 * @param out output
	 * @return status
	 */
	public int decryptFile(final InputStream is, final OutputStream out) {
		try {
			// Bytes read from in will be decrypted
			final InputStream cipher_is = new CipherInputStream(is, dcipher);

			// Read in the decrypted bytes and write the cleartext to out
			int numRead = 0;
			while ((numRead = cipher_is.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			cipher_is.close();
			return 1;
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "IO Exception for file");
			return -1;
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Unhandled Exception for file");
			return -1;
		}
	}
}
