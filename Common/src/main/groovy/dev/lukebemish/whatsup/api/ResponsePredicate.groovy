package dev.lukebemish.whatsup.api

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import dev.lukebemish.whatsup.impl.data.DispatchMapCodec
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import io.github.groovymc.cgl.api.transform.codec.ExposeCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

import java.util.function.Predicate

@CompileStatic
interface ResponsePredicate extends Predicate<String> {
    @PackageScope
    static final BiMap<ResourceLocation, Codec<? extends ResponsePredicate>> CODECS = HashBiMap.create()

    @ExposeCodec
    Codec<ResponsePredicate> CODEC = ExtraCodecs.lazyInitializedCodec({ -> new DispatchMapCodec(CODECS) })
        .dispatch({ ResponsePredicate it -> it.getCodec() }, { it })

    Codec<? extends ResponsePredicate> getCodec();

}
