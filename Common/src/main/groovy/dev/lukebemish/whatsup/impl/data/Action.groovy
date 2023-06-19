/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.data


import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.groovymc.cgl.api.transform.codec.CodecSerializable
import org.groovymc.cgl.api.transform.codec.WithCodec
import net.minecraft.resources.ResourceLocation

@TupleConstructor
@CodecSerializable
@CompileStatic
@ToString
class Action {
    final ResourceLocation function
    @WithCodec({ ->
        ResourceLocation.CODEC.<ScriptPredicate>xmap(
            { new ScriptPredicate(it) },
            { it.script }
        )
    })
    final ScriptPredicate predicate
    final List<ResourceLocation> levels = [new ResourceLocation("overworld")]
    final List<ResourceLocation> then = []
}
