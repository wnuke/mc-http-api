package dev.wnuke.mchttpapi.mixins;

import dev.wnuke.mchttpapi.HeadlessAPI;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class MixinDeathScreen {
    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        if (HeadlessAPI.compatLayer.playerNotNull()) {
            HeadlessAPI.compatLayer.respawn();
        }
    }
}
