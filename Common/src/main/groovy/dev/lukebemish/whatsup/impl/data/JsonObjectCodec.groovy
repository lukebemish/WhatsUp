/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import com.mojang.serialization.MapLike
import groovy.transform.CompileStatic

@CompileStatic
@Singleton
class JsonObjectCodec implements Codec<JsonObject> {
    @Override
    <T> DataResult<Pair<JsonObject, T>> decode(DynamicOps<T> ops, T input) {
        try {
            JsonElement jsonElement = ops.convertTo(JsonOps.INSTANCE, input)
            if (jsonElement instanceof JsonObject)
                return DataResult.success(new Pair<>(jsonElement, input))
        } catch (Exception e) {
            return DataResult.error {->e.message}
        }
        return DataResult.error {->"Not a json object"}
    }

    @Override
    <T> DataResult<T> encode(JsonObject input, DynamicOps<T> ops, T prefix) {
        try {
            T obj = JsonOps.INSTANCE.convertTo(ops, input)
            DataResult<T> out = ops.mergeToPrimitive(prefix, obj)
            if (out.error().present)
                out = ops.mergeToList(prefix, obj)
            if (out.error().present) {
                DataResult<MapLike<T>> map = ops.getMap(prefix)
                if (!map.error().present) {
                    out = ops.mergeToMap(prefix, map.getOrThrow(false, {}))
                }
            }
            return out
        } catch (Exception e) {
            return DataResult.error {->e.message}
        }
    }
}
