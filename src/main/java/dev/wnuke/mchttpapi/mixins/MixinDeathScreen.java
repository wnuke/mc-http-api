package dev.wnuke.mchttpapi.mixins;

import dev.wnuke.mchttpapi.HeadlessAPI;
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class MixinDeathScreen {
    @Inject(method = "<init>", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        if (MinecraftCompatLayer.getMinecraft().player != null) {
            HeadlessAPI.compatLayer.respawn();
        }
    }
}
