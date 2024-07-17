package jsco.dev.ethanolgui.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import jsco.dev.ethanolgui.client.Input;
import lombok.Getter;

public interface MouseButtonListener {

    void onMouse(MouseButtonEvent event);

    @Getter class MouseButtonEvent extends AbstractEvent<MouseButtonListener> {
        public static final int ID = 1;
        public final long window;
        public final int button;
        public final Input.KeyAction action;

        public MouseButtonEvent(final long window, final int button, final Input.KeyAction action) {
            this.window = window;
            this.button = button;
            this.action = action;
        }

        @Override
        public void call(MouseButtonListener listener) {
            listener.onMouse(new MouseButtonEvent(window, button, action));
        }
    }
}
