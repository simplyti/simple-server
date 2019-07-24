package com.simplyti.service.api.serializer.json;

import io.netty.buffer.ByteBuf;

public interface Json {

	<T> T deserialize(ByteBuf content, TypeLiteral<T> type);

	void serialize(Object obj, ByteBuf buffer);

}
