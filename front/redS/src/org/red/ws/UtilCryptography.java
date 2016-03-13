package org.red.ws;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class UtilCryptography {
	private static byte[] SALT_BYTES = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8,
			(byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };
	private static int ITERATION_COUNT = 19;
	private final static String CLAVE = "MiClave.com";

	public static String encriptar(String str) throws Exception {
		return encriptar(CLAVE, str);
	}

	public static String encriptar(String passPhrase, String str)throws Exception {
		Cipher ecipher = null;
		Cipher dcipher = null;
		// Crear la key
		KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), SALT_BYTES,	ITERATION_COUNT);
		SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
		ecipher = Cipher.getInstance(key.getAlgorithm());
		dcipher = Cipher.getInstance(key.getAlgorithm());
		// Preparar los parametros para los ciphers
		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT_BYTES,	ITERATION_COUNT);
		// Crear los ciphers
		ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
		dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		// Encodear la cadena a bytes usando utf-8
		byte[] utf8 = str.getBytes("UTF8");
		// Encriptar
		byte[] enc = ecipher.doFinal(utf8);
		// Encodear bytes a base64 para obtener cadena
		return new sun.misc.BASE64Encoder().encode(enc);
	}

	public static String desencriptar(String passPhrase, String str)throws Exception {
		Cipher ecipher = null;
		Cipher dcipher = null;
		// Crear la key
		KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), SALT_BYTES,ITERATION_COUNT);
		SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
				.generateSecret(keySpec);
		ecipher = Cipher.getInstance(key.getAlgorithm());
		dcipher = Cipher.getInstance(key.getAlgorithm());
		// Preparar los parametros para los ciphers
		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT_BYTES,	ITERATION_COUNT);
		// Crear los ciphers
		ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
		dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		// Decodear base64 y obtener bytes
		byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
		// Desencriptar
		byte[] utf8 = dcipher.doFinal(dec);
		// Decodear usando utf-8
		return new String(utf8, "UTF8");
	}

	public static String desencriptar(String str) throws Exception {
		return desencriptar(CLAVE, str);
	}

}
