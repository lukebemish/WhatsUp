/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

ModsDotGroovy.make {
    modLoader = 'gml'
    loaderVersion = '[1,)'

    license = 'LGPL-3.0-or-later'
    issueTrackerUrl = 'https://github.com/lukebemish/WhatsUp/issues'

    mod {
        modId = this.buildProperties['mod_id']
        displayName = this.buildProperties['mod_name']
        version = this.version
        group = this.group
        onQuilt {
            intermediate_mappings = 'net.fabricmc:intermediary'
        }
        displayUrl = 'https://github.com/lukebemish/WhatsUp'

        description = "Make API calls and modify the world based on the results"
        authors = [this.buildProperties['mod_author'] as String]

        dependencies {
            minecraft = this.minecraftVersionRange

            forge {
                versionRange = ">=${this.forgeVersion}"
            }

            onForge {
                mod('gml') {
                    versionRange = ">=${this.libs.versions.gml}"
                }
            }

            onQuilt {
                mod('groovyduvet') {
                    versionRange = ">=${this.libs.versions.groovyduvet.all}"
                }
            }

            quiltLoader {
                versionRange = ">=${this.quiltLoaderVersion}"
            }

            mod('scriptresources') {
                versionRange = ">=${this.libs.versions.scriptresources}"
            }
        }

        entrypoints {
            init = [
                    adapted {
                        adapter = 'groovyduvet'
                        value = 'dev.lukebemish.whatsup.impl.quilt.WhatsUpQuilt'
                    }
            ]
        }
    }
}
