package jsco.dev.ethanolgui.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.util.Window;

public interface WindowResizeListener {

    void onResize(WindowResizeEvent event);

    class WindowResizeEvent extends AbstractEvent<WindowResizeListener> {
        public static final int ID = 3;
        public final Window window;

        public WindowResizeEvent(final Window window) {
            this.window = window;
        }

        @Override
        public void call(WindowResizeListener listener) {
            listener.onResize(this);
        }
    }
}
