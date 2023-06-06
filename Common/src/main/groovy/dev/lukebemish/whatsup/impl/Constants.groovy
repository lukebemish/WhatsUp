/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.whatsup.impl


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class Constants {
    private Constants() {}

    public static final String MOD_ID = "whatsup"
    public static final String MOD_NAME = "WhatsUp"
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME)
    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create()
    public static final JsonSlurper JSON_SLURPER = new JsonSlurper()
    public static final int FREQUENCY_CONVERSION = 60*20
}
