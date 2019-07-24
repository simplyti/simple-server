package com.simplyti.service.api.serializer.json;

import static com.google.inject.internal.MoreTypes.canonicalize;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeLiteral<T> {

	private final Type type;
	
	protected TypeLiteral() {
		this.type = getSuperclassTypeParameter(getClass());
	}

	private TypeLiteral(Type type) {
		this.type=type;
	}

	public Type getType() {
		return type;
	}

	public static <I> TypeLiteral<I> create(Type type) {
		return new TypeLiteral<I>(type);
	}
	
	private static Type getSuperclassTypeParameter(Class<?> subclass) {
	    Type superclass = subclass.getGenericSuperclass();
	    if (superclass instanceof Class) {
	      throw new RuntimeException("Missing type parameter.");
	    }
	    ParameterizedType parameterized = (ParameterizedType) superclass;
	    return canonicalize(parameterized.getActualTypeArguments()[0]);
	  }

}
