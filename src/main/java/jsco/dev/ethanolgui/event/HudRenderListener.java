package jsco.dev.ethanolgui.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import jsco.dev.ethanolgui.client.EthanolGUI;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

public interface HudRenderListener {

    void onHudRender(HudRenderEvent event);

    @Getter class HudRenderEvent extends AbstractEvent<HudRenderListener> {
        public static final int ID = 0;
        public DrawContext drawContext;
        public int screenWidth, screenHeight;
        public double frameTime;
        public float tickDelta;

        public HudRenderEvent(final DrawContext drawContext, final int screenWidth, final int screenHeight, final float tickDelta) {
            this.drawContext = drawContext;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.frameTime = EthanolGUI.ETHANOL_HUD.getFrameTime();
            this.tickDelta = tickDelta;
        }

        @Override
        public void call(HudRenderListener listener) {
            listener.onHudRender(this);
        }
    }
}
