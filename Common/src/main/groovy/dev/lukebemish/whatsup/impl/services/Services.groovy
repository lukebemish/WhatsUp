/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl.services

import groovy.transform.AutoFinal
import dev.lukebemish.whatsup.impl.Constants
import groovy.transform.CompileStatic

@AutoFinal
@CompileStatic
class Services {
    static final PlatformHelper PLATFORM = load(PlatformHelper.class)

    static <T> T load(Class<T> clazz) {
        T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow {new NullPointerException("Failed to load service for ${clazz.getName()}")}
        Constants.LOGGER.debug("Loaded ${loadedService} for service ${clazz}")
        return loadedService
    }
}
