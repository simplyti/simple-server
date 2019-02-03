package com.simplyti.service.clients.k8s.secrets.domain;

import java.nio.charset.Charset;

import io.netty.util.CharsetUtil;
import lombok.Getter;

@Getter
public class SecretData {
	
	private final byte[] data;
	
	private SecretData(byte[] data) {
		this.data=data;
	}

	public static SecretData of(String strData,Charset charset){
		return new SecretData(strData.getBytes(charset));
	}
	
	public static SecretData of(String str){
		return of(str,CharsetUtil.UTF_8);
	}

	public static SecretData of(byte[] data) {
		return new SecretData(data);
	}

	public String asString(Charset charset) {
		return new String(data,charset);
	}


}
