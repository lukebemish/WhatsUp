/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

if ([95, 96, 99].contains(json.current_weather?.weathercode)) {
    storage.weatherType.type = 'thunder'
    return true
}

return false
