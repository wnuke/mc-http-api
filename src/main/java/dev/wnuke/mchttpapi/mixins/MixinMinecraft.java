package dev.wnuke.mchttpapi.mixins;

import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow
    private static Minecraft instance;

    @Inject(method = "run", at = @At("HEAD"))
    public void run(CallbackInfo ci) {
        new MinecraftCompatLayer(instance);
    }
}
