package com.simplyti.service.priority;

import java.util.Comparator;
import java.util.Optional;

import javax.annotation.Priority;

public class Priorized {
	
	public static final Comparator<Object> PRIORITY_ANN_ORDER = (o1,o2)-> {
		Integer o1Priority = Optional.ofNullable(o1.getClass().getAnnotation(Priority.class))
			.map(priority->priority.value())
			.orElse(Integer.MAX_VALUE);
		
		Integer o2Priority = Optional.ofNullable(o2.getClass().getAnnotation(Priority.class))
				.map(priority->priority.value())
				.orElse(Integer.MAX_VALUE);
		
		return o1Priority.compareTo(o2Priority);
	};

}
