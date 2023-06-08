/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.data.predicates

import com.mojang.serialization.Codec
import dev.lukebemish.scriptresources.api.ScriptResources
import dev.lukebemish.whatsup.api.ResponsePredicate
import dev.lukebemish.whatsup.impl.Constants
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovy.transform.TupleConstructor
import io.github.groovymc.cgl.api.transform.codec.CodecSerializable
import net.minecraft.resources.ResourceLocation

@TupleConstructor
@CompileStatic
@CodecSerializable
class ScriptPredicate implements ResponsePredicate {
    public static final ScriptResources.ScriptProvider SCRIPT_PROVIDER = ScriptResources.registerPrefix(new ResourceLocation(Constants.MOD_ID, "predicates"), ResponseData)

    ResourceLocation script

    @Override
    Codec<? extends ResponsePredicate> getCodec() {
        return $CODEC
    }

    @Override
    boolean test(String s) {
        Closure closure = SCRIPT_PROVIDER.getResource(script)
        try {
            ResponseData data = new ResponseData(s)
            return closure.call(data) as boolean
        } catch (Exception e) {
            Constants.LOGGER.warn "Failed to test ${s} against script at ${script}", e
            return false
        }
    }

    @CompileStatic
    @TupleConstructor
    static class ResponseData {
        final String text

        @Memoized
        Map getJson() {
            Object object = Constants.JSON_SLURPER.parseText(text)
            if (object instanceof Map)
                return object
            return null
        }
    }
}
