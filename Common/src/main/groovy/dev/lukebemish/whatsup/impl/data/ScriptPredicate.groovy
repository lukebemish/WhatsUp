/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.data

import dev.lukebemish.whatsup.impl.Constants
import dev.lukebemish.whatsup.impl.WhatsUpCommon
import groovy.json.JsonException
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovy.transform.TupleConstructor
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.CommandStorage
import org.groovymc.cgl.api.codec.ObjectOps

import java.util.function.BiConsumer
import java.util.function.Function

@TupleConstructor
@CompileStatic
class ScriptPredicate {

    final ResourceLocation script

    boolean test(String s, CommandStorage storage, Map<String, ResourceLocation> storageNames) {
        Closure closure = WhatsUpCommon.SCRIPT_PROVIDER.getResource(script)
        if (closure == null) {
            Constants.LOGGER.warn "Failed to find script at ${script}"
            return false
        }
        try {
            Map<ResourceLocation, Object> storageQueue = [:]
            ResponseData data = new ResponseData(s, { String key ->
                var location = storageNames[key]
                if (location == null) {
                    Constants.LOGGER.warn "Failed to find storage location for ${key}"
                    return null
                }
                CompoundTag tag = storage.get(location)
                var object = NbtOps.INSTANCE.convertTo(ObjectOps.instance, tag)
                storageQueue[location] = object
                return object
            } as Function<String, Object>, { String key, Object value ->
                var location = storageNames[key]
                if (location == null) {
                    Constants.LOGGER.warn "Failed to find storage location for ${key}"
                    return
                }
                storageQueue[location] = value
            } as BiConsumer<String, Object>)
            closure.delegate = data
            boolean out = closure.call(data) as boolean
            storageQueue.each {location, value ->
                Tag tag = ObjectOps.instance.convertTo(NbtOps.INSTANCE, value)
                if (tag instanceof CompoundTag) {
                    storage.set(location, tag)
                    return
                }
                Constants.LOGGER.warn "Failed to set ${location} to ${value}"
            }
            return out
        } catch (Exception e) {
            Constants.LOGGER.warn "Failed to test ${s} against script at ${script}", e
            return false
        }
    }

    @CompileStatic
    static class ResponseData {
        final String text
        private final Function<String, Object> dataGetter
        private final BiConsumer<String, Object> dataSetter

        ResponseData(String text, Function<String, Object> dataGetter, BiConsumer<String, Object> dataSetter) {
            this.text = text
            this.dataGetter = dataGetter
            this.dataSetter = dataSetter
        }

        final Object storage = new Object() {
            Object getAt(String key) {
                return dataGetter.apply(key)
            }

            void putAt(String key, Object value) {
                dataSetter.accept(key, value)
            }

            Object getProperty(String key) {
                return dataGetter.apply(key)
            }

            void setProperty(String key, Object value) {
                dataSetter.accept(key, value)
            }
        }

        @Memoized
        Map getJson() {
            try {
                Object object = Constants.JSON_SLURPER.parseText(text)
                if (object instanceof Map)
                    return object
            } catch (JsonException ignored) {}
            return null
        }
    }
}
