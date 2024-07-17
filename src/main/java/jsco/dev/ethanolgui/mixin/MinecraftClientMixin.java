package jsco.dev.ethanolgui.mixin;

import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.event.WindowResizeListener;
import jsco.dev.ethanolgui.render.EthanolHud;
import jsco.dev.ethanolgui.render.RenderText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Final private Window window;
    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;
    @Unique private long lastTime;
    @Unique private boolean firstFrame;

    @Inject(method = "<init>", at = @At("TAIL"))
    void postWindowInit(RunArgs args, CallbackInfo ci) {
        RenderText.initFonts();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo info) {
        long time = System.currentTimeMillis();

        if (firstFrame) {
            lastTime = time;
            firstFrame = false;
        }

        EthanolGUI.ETHANOL_HUD.setFrameTime((time - lastTime) / 1000.0);
        lastTime = time;
    }

    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    private void captureResize(CallbackInfo ci) {
        EthanolGUI.EVENT_BUS.call(WindowResizeListener.WindowResizeEvent.ID, new WindowResizeListener.WindowResizeEvent(this.window));
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void doAttack(CallbackInfoReturnable<Boolean> cir) {
        if (EthanolGUI.ETHANOL_HUD.isEnabled()) cir.setReturnValue(false);
    }
}
