/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.forge.platform

import com.google.auto.service.AutoService
import dev.lukebemish.whatsup.services.PlatformHelper
import groovy.transform.CompileStatic
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.loading.FMLLoader
import net.minecraftforge.fml.loading.FMLPaths
import org.codehaus.groovy.control.CompilerConfiguration

import java.nio.file.Path

@AutoService(PlatformHelper)
@CompileStatic
class PlatformHelperImpl implements PlatformHelper {

    @Override
    boolean isDevelopmentEnvironment() {
        return !FMLLoader.production
    }

    @Override
    boolean isClient() {
        return FMLLoader.dist == Dist.CLIENT
    }

    @Override
    Path getConfigFolder() {
        return FMLPaths.CONFIGDIR.get()
    }

    @Override
    Platform getPlatform() {
        return Platform.FORGE
    }

    @Override
    void customize(CompilerConfiguration compilerConfiguration) {
        // Does nothing
    }
}
