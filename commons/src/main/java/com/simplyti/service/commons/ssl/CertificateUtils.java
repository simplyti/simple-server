package com.simplyti.service.commons.ssl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;


public class CertificateUtils {

	public static List<X509Certificate> read(String value) throws CertificateException {
		List<X509Certificate> certificates = new ArrayList<>();
		try(Scanner scanner = new Scanner(value)){
			StringBuilder builder = new StringBuilder();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line.equals("-----BEGIN CERTIFICATE-----")) {
					builder.delete(0, builder.length());
				}else if(line.equals("-----END CERTIFICATE-----")) {
					byte[] data = Base64.getDecoder().decode(builder.toString());
					certificates.add(buildCertificate(new ByteArrayInputStream(data)));
				}else {
					builder.append(line);
				}
			}
		}
		return certificates;
	}
	
	private static X509Certificate buildCertificate(InputStream input) throws CertificateException {
		return (X509Certificate) CertificateFactory.getInstance("X.509")
					.generateCertificate(input);
	}

}
