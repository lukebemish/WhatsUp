package dev.lukebemish.whatsup.impl.data.predicates

import com.mojang.serialization.Codec
import dev.lukebemish.whatsup.impl.Constants
import dev.lukebemish.whatsup.api.ResponsePredicate
import dev.lukebemish.whatsup.impl.services.Services
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.transform.codec.CodecSerializable
import org.codehaus.groovy.control.CompilerConfiguration

import java.util.function.Predicate

@CodecSerializable
@CompileStatic
class GroovyJsonPredicate implements ResponsePredicate {
    public static final CompilerConfiguration COMPILER_CONFIGURATION = new CompilerConfiguration().tap {
        Services.PLATFORM.customize(it)
    }

    final String groovy
    final Predicate<Map> predicate

    GroovyJsonPredicate(String groovy) {
        this.groovy = groovy
        var shell = new GroovyShell(GroovyJsonPredicate.classLoader, COMPILER_CONFIGURATION)
        this.predicate = shell.evaluate("{ it ->  ${groovy} }") as Predicate<Map>
    }

    @Override
    Codec<? extends ResponsePredicate> getCodec() {
        return $CODEC
    }

    @Override
    boolean test(String s) {
        try {
            Object object = Constants.JSON_SLURPER.parseText(s)
            if (object instanceof Map)
                return predicate.test(object)
        } catch (Exception ignored) {}
        return false
    }
}
