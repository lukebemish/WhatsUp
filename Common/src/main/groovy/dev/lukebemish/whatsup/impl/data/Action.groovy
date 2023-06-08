/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.data

import dev.lukebemish.whatsup.api.ResponsePredicate
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import io.github.groovymc.cgl.api.transform.codec.CodecSerializable
import net.minecraft.resources.ResourceLocation

@TupleConstructor
@CodecSerializable
@CompileStatic
@ToString
class Action {
    final ResourceLocation function
    final ResponsePredicate predicate
    final List<ResourceLocation> levels = [new ResourceLocation("overworld")]
}
