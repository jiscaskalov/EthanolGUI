package jsco.dev.ethanolgui.client;

import org.lwjgl.glfw.GLFW;

public class Input {
    private static final boolean[] buttons = new boolean[16];

    public static void setButtonState(int button, boolean pressed) {
        if (button >= 0 && button < buttons.length) buttons[button] = pressed;
    }

    public enum KeyAction {
        Press,
        Repeat,
        Release;

        public static Input.KeyAction get(int action) {
            return switch (action) {
                case GLFW.GLFW_PRESS -> Press;
                case GLFW.GLFW_RELEASE -> Release;
                default -> Repeat;
            };
        }
    }
}
