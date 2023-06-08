/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.data

import com.google.common.collect.BiMap
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import dev.lukebemish.whatsup.api.ResponsePredicate
import groovy.transform.TupleConstructor
import net.minecraft.resources.ResourceLocation

@TupleConstructor
class DispatchMapCodec implements Codec<Codec<? extends ResponsePredicate>> {
    final BiMap<ResourceLocation, Codec<? extends ResponsePredicate>> codecs

    @Override
    <T> DataResult<Pair<Codec<? extends ResponsePredicate>, T>> decode(DynamicOps<T> ops, T input) {
        return ResourceLocation.CODEC.decode(ops, input).<Pair<Codec<? extends ResponsePredicate>, T>>flatMap {
            if (codecs.containsKey(it.getFirst()))
                return DataResult.success(it.mapFirst { codecs.get(it) })
            DataResult.error(() -> "Unknown response predicate type: ${it.getFirst()}")
        }
    }

    @Override
    <T> DataResult<T> encode(Codec<? extends ResponsePredicate> input, DynamicOps<T> ops, T prefix) {
        ResourceLocation key = codecs.inverse().get(input)
        if (key == null) {
            return DataResult.error(() -> "Unregistered response predicate type: " + input)
        }
        T toMerge = ops.createString(key.toString())
        return ops.mergeToPrimitive(prefix, toMerge)
    }
}
