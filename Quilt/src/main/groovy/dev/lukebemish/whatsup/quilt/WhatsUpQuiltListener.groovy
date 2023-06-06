package dev.lukebemish.whatsup.quilt

import dev.lukebemish.whatsup.WhatsUpListener
import net.minecraft.resources.ResourceLocation
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader

class WhatsUpQuiltListener extends WhatsUpListener implements IdentifiableResourceReloader {
    public static final ResourceLocation QUILT_ID = new ResourceLocation("whatsup", "load_listeners")

    @Override
    ResourceLocation getQuiltId() {
        return QUILT_ID
    }
}
