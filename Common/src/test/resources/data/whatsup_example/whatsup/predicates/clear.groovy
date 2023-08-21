/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

if ([0, 1, 2, 3].contains(json.current_weather?.weathercode)) {
    storage.weatherType.type = 'clear'
    return true
}

return false
