package dev.wnuke.mchttpapi.mixins;

import dev.wnuke.mchttpapi.HeadlessAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void postInit(CallbackInfo ci) {
        HeadlessAPI.startAPIServer();
    }

    @Shadow @Nullable private NetworkManager networkManager;

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;networkManager:Lnet/minecraft/network/NetworkManager;", ordinal = 1))
    private void runTickUpdateNetwork(CallbackInfo ci) {
        if (networkManager != null) {
            networkManager.tick();
        }
    }

    @Inject(method = "runTick", at = @At("HEAD"), cancellable = true)
    public void runTickCancel(CallbackInfo ci) {
        ci.cancel();
    }
}
