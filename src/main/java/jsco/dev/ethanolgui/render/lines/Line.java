package jsco.dev.ethanolgui.render.lines;

public class Line {
    double vector;
    double x;
    double y;
    float speed;

    public Line(double x, double y, double vector, float speed) {
        this.x = x;
        this.y = y;
        this.vector = vector;
        this.speed = speed;
    }
}
