/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.forge

import com.matyrobbrt.gml.GMod
import dev.lukebemish.whatsup.Constants
import dev.lukebemish.whatsup.WhatsUpCommon
import groovy.transform.CompileStatic

@GMod(Constants.MOD_ID)
@CompileStatic
class WhatsUpForge {
    WhatsUpForge() {
        WhatsUpCommon.init()
    }
}
