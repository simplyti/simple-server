package com.simplyti.service.transport.unix;

import com.simplyti.service.transport.Listener;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(of = "file")
@AllArgsConstructor
public class UnixDomainListener implements Listener{

	private final String file;
	private final boolean ssl;
	
}
