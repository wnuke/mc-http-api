package dev.wnuke.mchttpapi.mixins;

import dev.wnuke.mchttpapi.HeadlessAPI;
import dev.wnuke.mchttpapi.utils.RunBooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft {
    private static final float TPS = 20.0F;
    private final RenderTickCounter renderTickCounter = new RenderTickCounter(TPS, 0L);
    @Shadow
    private ClientConnection connection;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(boolean tick, CallbackInfo ci) {
        MinecraftClient.getInstance().skipGameRender = true;
        if (null != MinecraftClient.getInstance().player && null != HeadlessAPI.compatLayer.connection) {
            if (1.0F > MinecraftClient.getInstance().player.getHealth() || MinecraftClient.getInstance().player.isDead()) {
                HeadlessAPI.compatLayer.respawn();
            }
        }
        int k = renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs());
        if (tick) {
            if (null != HeadlessAPI.compatLayer) {
                connection = HeadlessAPI.compatLayer.connection;
            }
            MinecraftClient.getInstance().runTasks(new RunBooleanSupplier());
            for (int j = 0; j < Math.min(10, k); ++j) {
                MinecraftClient.getInstance().tick();
            }
        }
    }

    @Inject(method = "isModded", at = @At("HEAD"), cancellable = true)
    public void setNotModded(CallbackInfoReturnable<? super Boolean> cir) {
        cir.setReturnValue(false);
    }
}
