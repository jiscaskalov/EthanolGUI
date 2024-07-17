package jsco.dev.ethanolgui.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import lombok.Getter;

public interface MouseScrollListener {

    void onScroll(MouseScrollEvent event);

    @Getter class MouseScrollEvent extends AbstractEvent<MouseScrollListener> {
        public static final int ID = 2;
        public final long window;
        public final double horizontal, vertical;

        public MouseScrollEvent(final long window, final double horizontal, final double vertical) {
            this.window = window;
            this.horizontal = horizontal;
            this.vertical = vertical;
        }

        @Override
        public void call(MouseScrollListener listener) {
            listener.onScroll(this);
        }
    }
}
