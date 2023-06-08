/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.data.predicates

import com.mojang.serialization.Codec
import dev.lukebemish.whatsup.api.ResponsePredicate
import dev.lukebemish.whatsup.impl.services.Services
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.transform.codec.CodecSerializable
import org.codehaus.groovy.control.CompilerConfiguration

import java.util.function.Predicate

@CodecSerializable
@CompileStatic
class GroovyStringPredicate implements ResponsePredicate {
    public static final CompilerConfiguration COMPILER_CONFIGURATION = new CompilerConfiguration().tap {
        Services.PLATFORM.customize(it)
    }

    final String groovy
    final Predicate<String> predicate

    GroovyStringPredicate(String groovy) {
        this.groovy = groovy
        var shell = new GroovyShell(GroovyJsonPredicate.classLoader, COMPILER_CONFIGURATION)
        this.predicate = shell.evaluate("{ it -> ${groovy} }") as Predicate<String>
    }

    @Override
    Codec<? extends ResponsePredicate> getCodec() {
        return $CODEC
    }

    @Override
    boolean test(String s) {
        try {
            return predicate.test(s)
        } catch (Exception ignored) {}
        return false
    }
}
