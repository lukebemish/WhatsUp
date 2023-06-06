package dev.lukebemish.whatsup.data.predicates

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import dev.lukebemish.whatsup.Constants
import dev.lukebemish.whatsup.data.JsonObjectCodec
import dev.lukebemish.whatsup.data.ResponsePredicate
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import io.github.groovymc.cgl.api.transform.codec.CodecSerializable
import io.github.groovymc.cgl.api.transform.codec.WithCodec

@CompileStatic
@TupleConstructor
@CodecSerializable
@ToString
class ContainsJsonChildrenPredicate implements ResponsePredicate {
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
            return jsonMatches(json, target)
        } catch (Exception ignored) {
            return false
        }
    }

    private static boolean jsonMatches(JsonElement full, JsonElement target) {
        if (full === null || target === null)
            return false
        if (full instanceof JsonObject && target instanceof JsonObject) {
            for (def entry : target.entrySet()) {
                String key = entry.getKey()
                JsonElement value = entry.getValue()
                if (!full.has(key))
                    return false
                if (!jsonMatches(full.get(key), value))
                    return false
            }
            return true
        }
        return full == target
    }
}
