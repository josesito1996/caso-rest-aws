package com.samy.service.app.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncriptadorAes {

	/**
	 * Crea la clave de encriptacion usada internamente
	 * 
	 * @param clave Clave que se usara para encriptar
	 * @return Clave de encriptacion
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	@Value("${aws.config.encryption-key}")
	private String encryptionKey;
	
	@Value("${aws.config.encryption-iv}")
	private String encryptionIv;

	/**
	 * AES CBC encryption
	 * 
	 * @param message String to be encrypted
	 * @param key     The key
	 * @param iv      IV, Need and key Same length
	 * @return Return encrypted ciphertext , Encoded as base64
	 */
	public String encryptCBC(String message) {
		final String cipherMode = "AES/CBC/PKCS5Padding";
		final String charsetName = "UTF-8";
		try {
			byte[] content = new byte[0];
			content = message.getBytes(charsetName);
//
			byte[] keyByte = this.encryptionKey.getBytes(charsetName);
			SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
//
			byte[] ivByte = this.encryptionIv.getBytes(charsetName);
			IvParameterSpec ivSpec = new IvParameterSpec(ivByte);
			Cipher cipher = Cipher.getInstance(cipherMode);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			byte[] data = cipher.doFinal(content);
			final Base64.Encoder encoder = Base64.getEncoder();
			final String result = encoder.encodeToString(data);
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * AES CBC Decrypt
	 * 
	 * @param messageBase64 Ciphertext ,base64 code
	 * @param key           The key , Same as when encrypting
	 * @param iv            IV, Need and key Same length
	 * @return Decrypted data
	 */
	public String decryptCBC(String messageBase64) {
		final String cipherMode = "AES/CBC/PKCS5Padding";
		final String charsetName = "UTF-8";
		try {
			final Base64.Decoder decoder = Base64.getDecoder();
			byte[] messageByte = decoder.decode(messageBase64);
//
			byte[] keyByte = this.encryptionKey.getBytes(charsetName);
			SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
//
			byte[] ivByte = this.encryptionIv.getBytes(charsetName);
			IvParameterSpec ivSpec = new IvParameterSpec(ivByte);
			Cipher cipher = Cipher.getInstance(cipherMode);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
			byte[] content = cipher.doFinal(messageByte);
			String result = new String(content, charsetName);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * AES ECB encryption
	 * 
	 * @param message String to be encrypted
	 * @param key     The key
	 * @return Return encrypted ciphertext , Encoded as base64
	 */
	public String encryptECB(String message, String key) {
		final String cipherMode = "AES/ECB/PKCS5Padding";
		final String charsetName = "UTF-8";
		try {
			byte[] content = new byte[0];
			content = message.getBytes(charsetName);
//
			byte[] keyByte = key.getBytes(charsetName);
			SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
			Cipher cipher = Cipher.getInstance(cipherMode);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] data = cipher.doFinal(content);
			final Base64.Encoder encoder = Base64.getEncoder();
			final String result = encoder.encodeToString(data);
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * AES ECB Decrypt
	 * 
	 * @param messageBase64 Ciphertext ,base64 code
	 * @param key           The key , Same as when encrypting
	 * @return Decrypted data
	 */
	public String decryptECB(String messageBase64, String key) {
		final String cipherMode = "AES/ECB/PKCS5Padding";
		final String charsetName = "UTF-8";
		try {
			final Base64.Decoder decoder = Base64.getDecoder();
			byte[] messageByte = decoder.decode(messageBase64);
//
			byte[] keyByte = key.getBytes(charsetName);
			SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
			Cipher cipher = Cipher.getInstance(cipherMode);
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] content = cipher.doFinal(messageByte);
			String result = new String(content, charsetName);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
