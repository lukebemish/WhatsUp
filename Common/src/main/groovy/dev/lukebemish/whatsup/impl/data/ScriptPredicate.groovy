/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.data


import dev.lukebemish.whatsup.impl.Constants
import dev.lukebemish.whatsup.impl.WhatsUpCommon
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovy.transform.TupleConstructor
import net.minecraft.resources.ResourceLocation

@TupleConstructor
@CompileStatic
class ScriptPredicate {

    final ResourceLocation script

    boolean test(String s) {
        Closure closure = WhatsUpCommon.SCRIPT_PROVIDER.getResource(script)
        if (closure == null) {
            Constants.LOGGER.warn "Failed to find script at ${script}"
            return false
        }
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
