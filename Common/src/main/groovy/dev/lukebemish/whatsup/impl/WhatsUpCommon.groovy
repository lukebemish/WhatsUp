/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl

import com.mojang.brigadier.CommandDispatcher
import dev.lukebemish.scriptresources.api.ScriptResources
import dev.lukebemish.whatsup.impl.data.Action
import dev.lukebemish.whatsup.impl.data.Listener
import dev.lukebemish.whatsup.impl.data.ScriptPredicate
import groovy.transform.CompileStatic
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerFunctionManager
import net.minecraft.world.level.Level

import java.util.concurrent.ConcurrentLinkedDeque

@CompileStatic
final class WhatsUpCommon {
    public static final ScriptResources.ScriptProvider SCRIPT_PROVIDER = ScriptResources.registerPrefix(new ResourceLocation(Constants.MOD_ID, "predicates"), ScriptPredicate.ResponseData)

    private WhatsUpCommon() {}

    static void init() {
    }

    static final Deque<LevelConsumer> ACTIONS = new ConcurrentLinkedDeque<>()

    static void onTickServer(MinecraftServer server) {
        if (!ACTIONS.empty) {
            ACTIONS.removeFirst().action(server)
        }
        int tick = server.tickCount
        if (tick % Constants.FREQUENCY_CONVERSION !== 0)
            return
        for (WhatsUpListener.Listeners listeners : WhatsUpListener.LISTENERS) {
            if (tick % listeners.frequency === 0) {
                for (WhatsUpListener.ListenerData data : listeners.listeners) {
                    Listener listener = data.listener
                    runListener(listener, listeners.frequency * 50) // 50 = 1000 / 20
                }
            }
        }
    }

    private static void runListener(Listener listener, int timeout) {
        URL url = new URL(listener.endpoint)
        Runnable runnable = { ->
            try {
                String text = url.getText('UTF-8', readTimeout: timeout)
                for (Action outerAction : listener.actions) {
                    if (outerAction.predicate.test(text)) {
                        final action = outerAction
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

    static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Constants.MOD_ID)
            .requires { it.hasPermission(4) }
            .then(Commands.argument('listener', ResourceLocationArgument.id())
                .suggests { ctx, builder ->
                    return SharedSuggestionProvider.suggestResource(WhatsUpListener.LISTENER_MAP.keySet().stream(), builder)
                }.executes {
                    ResourceLocation location = ResourceLocationArgument.getId(it, 'listener')
                    Listener listener = WhatsUpListener.LISTENER_MAP.get(location)
                    if (listener !== null) {
                        runListener(listener, 10000) // 10 second timeout
                    }
                }
            )
        )
    }

    @FunctionalInterface
    static interface LevelConsumer {
        void action(MinecraftServer server)
    }
}
