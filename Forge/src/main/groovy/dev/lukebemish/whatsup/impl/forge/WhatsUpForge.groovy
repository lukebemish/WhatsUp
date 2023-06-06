/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.forge

import com.matyrobbrt.gml.GMod
import cpw.mods.modlauncher.Launcher
import cpw.mods.modlauncher.api.IModuleLayerManager
import dev.lukebemish.whatsup.impl.Constants
import dev.lukebemish.whatsup.impl.WhatsUpCommon
import dev.lukebemish.whatsup.impl.WhatsUpListener
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent

@GMod(Constants.MOD_ID)
@CompileStatic
class WhatsUpForge {
    WhatsUpForge() {
        WhatsUpCommon.init()
        properlyLoadGroovyJson()
        forgeBus.addListener(ServerTickEvent) { event ->
            if (event.@phase !== TickEvent.Phase.START)
                return
            WhatsUpCommon.onTickServer(event.server)
        }
        forgeBus.addListener(AddReloadListenerEvent) { event ->
            event.addListener(new WhatsUpListener())
        }
    }

    static void properlyLoadGroovyJson() {
        var oldLoader = Thread.currentThread().getContextClassLoader()
        var newLoader = Launcher.INSTANCE.findLayerManager().orElseThrow().getLayer(IModuleLayerManager.Layer.PLUGIN).orElseThrow().findLoader('org.apache.groovy.json')

        Thread.currentThread().setContextClassLoader(newLoader)
        JsonSlurper slurper = new JsonSlurper()
        slurper.parseText('{}')
        Thread.currentThread().setContextClassLoader(oldLoader)
    }
}
