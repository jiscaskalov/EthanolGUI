package jsco.dev.ethanolgui.mixin;

import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.client.Input;
import jsco.dev.ethanolgui.event.MouseButtonListener;
import jsco.dev.ethanolgui.event.MouseScrollListener;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo info) {
        Input.setButtonState(button, action != GLFW_RELEASE);
        MouseButtonListener.MouseButtonEvent event = new MouseButtonListener.MouseButtonEvent(window, button, Input.KeyAction.get(action));
        EthanolGUI.EVENT_BUS.call(MouseButtonListener.MouseButtonEvent.ID, event);
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        MouseScrollListener.MouseScrollEvent event = new MouseScrollListener.MouseScrollEvent(window, horizontal, vertical);
        EthanolGUI.EVENT_BUS.call(MouseScrollListener.MouseScrollEvent.ID, event);
    }

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void lockCursor(CallbackInfo ci) {
        if (EthanolGUI.ETHANOL_HUD.isEnabled()) ci.cancel();
    }
}
