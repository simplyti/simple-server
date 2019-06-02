package com.simplyti.service.commons.ssl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Base64;

import sun.security.util.DerValue;
import sun.security.util.DerInputStream;

public class PrivateKeyUtils {
	
	// PKCS#8 format
    private static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
    private static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";
	
	// PKCS#1 format
    private static final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
    private static final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";
		
    public static PrivateKey read(String value) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		final KeySpec keySpec;
		if (value.indexOf(PEM_PRIVATE_START) != -1) {
			String privateKeyPem = value.replace(PEM_PRIVATE_START, "")
					.replace(PEM_PRIVATE_END, "").replaceAll("\\s", "");
				
			byte[] keyBuf = Base64.getDecoder().decode(privateKeyPem);
			keySpec = new PKCS8EncodedKeySpec(keyBuf);
		}else {
			String privateKeyPem = value.replace(PEM_RSA_PRIVATE_START, "").replace(PEM_RSA_PRIVATE_END, "").replaceAll("\\s", "");
			DerInputStream derReader = new DerInputStream(Base64.getDecoder().decode(privateKeyPem));
	        DerValue[] seq = derReader.getSequence(0);

	        BigInteger modulus = seq[1].getBigInteger();
	        BigInteger publicExp = seq[2].getBigInteger();
	        BigInteger privateExp = seq[3].getBigInteger();
	        BigInteger prime1 = seq[4].getBigInteger();
	        BigInteger prime2 = seq[5].getBigInteger();
	        BigInteger exp1 = seq[6].getBigInteger();
	        BigInteger exp2 = seq[7].getBigInteger();
	        BigInteger crtCoef = seq[8].getBigInteger();

	        keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
		}
		
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePrivate(keySpec);
	}

}
