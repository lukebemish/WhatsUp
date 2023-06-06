/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup

import dev.lukebemish.whatsup.data.Action
import dev.lukebemish.whatsup.data.Listener
import dev.lukebemish.whatsup.data.ResponsePredicate
import dev.lukebemish.whatsup.data.predicates.*
import groovy.transform.CompileStatic
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerFunctionManager
import net.minecraft.world.level.Level

import java.util.concurrent.ConcurrentLinkedDeque

@CompileStatic
final class WhatsUpCommon {
    private WhatsUpCommon() {}

    static void init() {
        ResponsePredicate.CODECS[new ResourceLocation(Constants.MOD_ID, "equals_string")] = EqualsStringPredicate.$CODEC
        ResponsePredicate.CODECS[new ResourceLocation(Constants.MOD_ID, "equals_json")] = EqualsJsonPredicate.$CODEC
        ResponsePredicate.CODECS[new ResourceLocation(Constants.MOD_ID, "contains_string")] = ContainsStringPredicate.$CODEC
        ResponsePredicate.CODECS[new ResourceLocation(Constants.MOD_ID, "contains_json_children")] = ContainsJsonChildrenPredicate.$CODEC
        ResponsePredicate.CODECS[new ResourceLocation(Constants.MOD_ID, "groovy_json")] = GroovyJsonPredicate.$CODEC
        ResponsePredicate.CODECS[new ResourceLocation(Constants.MOD_ID, "groovy_string")] = GroovyStringPredicate.$CODEC
    }

    static final Deque<LevelConsumer> ACTIONS = new ConcurrentLinkedDeque<>()

    static void onTickServer(MinecraftServer server) {
        if (!ACTIONS.empty) {
            ACTIONS.removeFirst().action(server)
        }
        int tick = server.tickCount
        for (WhatsUpListener.Listeners listeners : WhatsUpListener.LISTENERS) {
            if (tick % listeners.frequency == 0) {
                for (WhatsUpListener.ListenerData data : listeners.listeners) {
                    Listener listener = data.listener
                    URL url = new URL(listener.endpoint)
                    Runnable runnable = {->
                        try {
                            String text = url.getText('UTF-8', readTimeout: listeners.frequency/20*1000)
                            for (Action action : listener.actions) {
                                if (action.predicate.test(text)) {
                                    LevelConsumer consumer = { MinecraftServer s ->
                                        for (ResourceLocation key : action.levels) {
                                            var levelKey = ResourceKey.create(Registries.DIMENSION, key)
                                            Level l = s.getLevel(levelKey)
                                            ServerFunctionManager manager = s.functions
                                            manager.get(action.function).ifPresent {
                                                final CommandSourceStack commandSource = manager.getGameLoopSender()
                                                    .withLevel(l).withPermission(4).withSuppressedOutput()
                                                manager.execute(it, commandSource)
                                                return
                                            }
                                        }
                                        return
                                    }
                                    ACTIONS.add(consumer)
                                }
                            }
                        } catch (IOException e) {
                            Constants.LOGGER.warn("Issue reaching endpoint ${listener.endpoint}: $e")
                        }
                    }
                    Thread thread = new Thread(runnable)
                    thread.start()
                }
            }
        }
    }

    @FunctionalInterface
    static interface LevelConsumer {
        void action(MinecraftServer server)
    }
}
