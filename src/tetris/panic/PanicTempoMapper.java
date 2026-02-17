package tetris.panic;

public class PanicTempoMapper {

    private final double maxTempo;

    public PanicTempoMapper() {
        this(1.18);
    }

    public PanicTempoMapper(double maxTempo) {
        this.maxTempo = Math.max(1.0, maxTempo);
    }

    public double mapDangerToTempo(double danger) {
        double d = clamp(danger, 0.0, 100.0);
        if (d < 40.0) {
            return 1.00;
        }
        if (d <= 70.0) {
            double t = (d - 40.0) / 30.0;
            return lerp(1.00, 1.10, t);
        }
        double t = (d - 70.0) / 30.0;
        return clamp(lerp(1.10, maxTempo, t), 1.0, maxTempo);
    }

    private double lerp(double a, double b, double t) {
        return a + (b - a) * clamp(t, 0.0, 1.0);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
