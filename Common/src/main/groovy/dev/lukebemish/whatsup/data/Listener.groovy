package dev.lukebemish.whatsup.data

import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import io.github.groovymc.cgl.api.transform.codec.CodecSerializable

@TupleConstructor
@CodecSerializable
@CompileStatic
@ToString
class Listener {
    final String endpoint
    final List<Action> actions
    final int frequency
}
