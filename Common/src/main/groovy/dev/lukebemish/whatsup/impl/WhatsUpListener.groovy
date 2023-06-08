/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl

import com.google.gson.JsonElement
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.JsonOps
import dev.lukebemish.whatsup.impl.data.Listener
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller

@CompileStatic
class WhatsUpListener extends SimpleJsonResourceReloadListener {
    public static final String DIRECTORY = "whatsup/listeners"
    public static Listeners[] LISTENERS = new Listeners[] {}

    WhatsUpListener() {
        super(Constants.GSON, DIRECTORY)
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Int2ObjectMap<List<ListenerData>> building = new Int2ObjectOpenHashMap<>()
        object.each {rl, json ->
            DataResult<Listener> result = ((Decoder<Listener>) Listener.$CODEC).parse(JsonOps.INSTANCE, json)
            if (result.result().isPresent()) {
                Listener listener = result.result().get()
                if (!building.containsKey(listener.frequency))
                    building[listener.frequency] = []
                building[listener.frequency].add(new ListenerData(rl, listener))
            } else {
                DataResult.PartialResult<?> partial = result.error().get()
                Constants.LOGGER.error("Failed to parse listener at {}: {}", rl, partial.message())
            }
        }
        LISTENERS = new Listeners[building.size()]
        int i = 0
        building.each {frequency, listeners ->
            ListenerData[] data = new ListenerData[listeners.size()]
            for (int j = 0; j < listeners.size(); j++)
                data[j] = listeners.get(j)
            LISTENERS[i] = new Listeners(frequency*Constants.FREQUENCY_CONVERSION, data)
            i++
        }
        Constants.LOGGER.info("WhatsUp loaded ${LISTENERS.length} listeners!")
    }

    @TupleConstructor
    static class Listeners {
        final int frequency
        final ListenerData[] listeners
    }

    @TupleConstructor
    static class ListenerData {
        final ResourceLocation location
        final Listener listener
    }
}
