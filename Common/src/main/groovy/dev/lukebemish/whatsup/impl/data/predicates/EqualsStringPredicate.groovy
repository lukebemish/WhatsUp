/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.data.predicates

import com.mojang.serialization.Codec
import dev.lukebemish.whatsup.api.ResponsePredicate
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import io.github.groovymc.cgl.api.transform.codec.CodecSerializable

@TupleConstructor
@CodecSerializable
@CompileStatic
@ToString
class EqualsStringPredicate implements ResponsePredicate {
    final String target

    @Override
    Codec<? extends ResponsePredicate> getCodec() {
        return $CODEC
    }

    @Override
    boolean test(String s) {
        return s == target
    }
}
