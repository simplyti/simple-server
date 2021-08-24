package com.simplyti.service.filter.priority;

import java.util.Comparator;
import java.util.Optional;

import javax.annotation.Priority;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Priorized implements Comparator<Object>{
	
	public static final Comparator<Object> PRIORITY_ANN_ORDER = new Priorized();

	@Override
	public int compare(Object o1, Object o2) {
		Integer o1Priority = Optional.ofNullable(o1.getClass().getAnnotation(Priority.class))
				.map(priority->priority.value())
				.orElse(Integer.MAX_VALUE);
			
			Integer o2Priority = Optional.ofNullable(o2.getClass().getAnnotation(Priority.class))
					.map(priority->priority.value())
					.orElse(Integer.MAX_VALUE);
			
			return o1Priority.compareTo(o2Priority);
	}

}
