/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.quilt

import dev.lukebemish.whatsup.impl.WhatsUpCommon
import groovy.transform.CompileStatic
import net.minecraft.server.packs.PackType
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents
import org.quiltmc.qsl.resource.loader.api.ResourceLoader

@CompileStatic
class WhatsUpQuilt implements ModInitializer {

    @Override
    void onInitialize(ModContainer mod) {
        WhatsUpCommon.init()
        ResourceLoader.get(PackType.SERVER_DATA).registerReloader(new WhatsUpQuiltListener())
        ServerTickEvents.START << (WhatsUpCommon::onTickServer as ServerTickEvents.Start)
    }
}
