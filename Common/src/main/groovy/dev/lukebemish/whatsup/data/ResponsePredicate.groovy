package dev.lukebemish.whatsup.data

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.transform.codec.ExposeCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

import java.util.function.Predicate

@CompileStatic
interface ResponsePredicate extends Predicate<String> {
    static final BiMap<ResourceLocation, Codec<? extends ResponsePredicate>> CODECS = HashBiMap.create()

    @ExposeCodec
    Codec<ResponsePredicate> CODEC = ExtraCodecs.lazyInitializedCodec({ -> new DispatchMapCodec() })
        .dispatch({ ResponsePredicate it -> it.getCodec() }, { it })

    Codec<? extends ResponsePredicate> getCodec();

    static class DispatchMapCodec implements Codec<Codec<? extends ResponsePredicate>> {
        @Override
        <T> DataResult<Pair<Codec<? extends ResponsePredicate>, T>> decode(DynamicOps<T> ops, T input) {
            return ResourceLocation.CODEC.decode(ops, input).<Pair<Codec<? extends ResponsePredicate>, T>>flatMap {
                if (CODECS.containsKey(it.getFirst()))
                    return DataResult.success(it.mapFirst { CODECS.get(it) })
                DataResult.error(() -> "Unknown response predicate type: ${it.getFirst()}")
            }
        }

        @Override
        <T> DataResult<T> encode(Codec<? extends ResponsePredicate> input, DynamicOps<T> ops, T prefix) {
            ResourceLocation key = CODECS.inverse().get(input)
            if (key == null) {
                return DataResult.error(() -> "Unregistered response predicate type: " + input);
            }
            T toMerge = ops.createString(key.toString())
            return ops.mergeToPrimitive(prefix, toMerge)
        }
    }
}
