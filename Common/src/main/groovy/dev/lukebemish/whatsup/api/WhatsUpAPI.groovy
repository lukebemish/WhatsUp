/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.api

import com.mojang.serialization.Codec
import net.minecraft.resources.ResourceLocation

final class WhatsUpAPI {
    public static final ResponsePredicates RESPONSE_PREDICATES = new ResponsePredicates()

    private WhatsUpAPI() {}

    private static final class ResponsePredicates {
        void putAt(ResourceLocation location, Codec<? extends ResponsePredicate> predicateCodec) {
            if (ResponsePredicate.CODECS.containsKey(location))
                throw new IllegalArgumentException("Response predicate already registered: $location")
            ResponsePredicate.CODECS[location] = predicateCodec
        }

        Codec<? extends ResponsePredicate> getAt(ResourceLocation location) {
            return ResponsePredicate.CODECS[location]
        }
    }
}
