package com.cmatch.support.code;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class KeyValueShapedSerializer extends StdSerializer<KeyValueShapedCodeContainer> {

    private static final long serialVersionUID = 4663034866896145189L;

    public KeyValueShapedSerializer() {
        super(KeyValueShapedCodeContainer.class);
    }

    @Override
    public void serialize(KeyValueShapedCodeContainer msgType, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();

        for (KeyValueShapedCode e : msgType.getKeyValueShapedCode()) {
            gen.writeFieldName(e.getName());
            gen.writeString(e.getCode());
        }

        gen.writeEndObject();
    }
}
