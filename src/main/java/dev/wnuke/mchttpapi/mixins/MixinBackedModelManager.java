package dev.wnuke.mchttpapi.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BakedModelManager.class)
public class MixinBackedModelManager {
    @Inject(method = "shouldRerender", at = @At("HEAD"), cancellable = true)
    public void setRenderFalse(BlockState from, BlockState to, CallbackInfoReturnable<? super Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
