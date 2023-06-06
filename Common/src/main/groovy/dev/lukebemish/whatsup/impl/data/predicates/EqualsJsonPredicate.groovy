package dev.lukebemish.whatsup.impl.data.predicates

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import dev.lukebemish.whatsup.impl.Constants
import dev.lukebemish.whatsup.impl.data.JsonObjectCodec
import dev.lukebemish.whatsup.api.ResponsePredicate
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import io.github.groovymc.cgl.api.transform.codec.CodecSerializable
import io.github.groovymc.cgl.api.transform.codec.WithCodec

@TupleConstructor
@CodecSerializable
@CompileStatic
@ToString
class EqualsJsonPredicate implements ResponsePredicate {
    @WithCodec({ JsonObjectCodec.instance })
    final JsonObject target

    @Override
    Codec<? extends ResponsePredicate> getCodec() {
        return $CODEC
    }

    @Override
    boolean test(String s) {
        try {
            JsonObject json = Constants.GSON.fromJson(s, JsonObject)
            return json == target
        } catch (Exception ignored) {
            return false
        }
    }
}
