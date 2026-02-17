package tetris.panic;

public class BgmPanicFeatureOrchestrator {

    private final DangerMeter dangerMeter;
    private final PanicTempoMapper tempoMapper;
    private final DynamicBgmEngine bgmEngine;
    private final double smoothingK;
    private final double updateIntervalSec;
    private final boolean debug;

    private double updateAccumulatorSec = 0.0;
    private double smoothedTempo = 1.0;

    public BgmPanicFeatureOrchestrator(DangerMeter dangerMeter,
                                       PanicTempoMapper tempoMapper,
                                       DynamicBgmEngine bgmEngine) {
        this(dangerMeter, tempoMapper, bgmEngine, 6.0, 0.08, Boolean.getBoolean("tetris.debug.panic"));
    }

    public BgmPanicFeatureOrchestrator(DangerMeter dangerMeter,
                                       PanicTempoMapper tempoMapper,
                                       DynamicBgmEngine bgmEngine,
                                       double smoothingK,
                                       double updateIntervalSec,
                                       boolean debug) {
        this.dangerMeter = dangerMeter;
        this.tempoMapper = tempoMapper;
        this.bgmEngine = bgmEngine;
        this.smoothingK = smoothingK;
        this.updateIntervalSec = updateIntervalSec;
        this.debug = debug;
    }

    public void reset() {
        updateAccumulatorSec = 0.0;
        smoothedTempo = 1.0;
        if (bgmEngine != null) {
            bgmEngine.setTempo(1.0);
        }
    }

    public void update(double deltaTimeSec) {
        if (dangerMeter == null || tempoMapper == null || bgmEngine == null) {
            return;
        }

        updateAccumulatorSec += Math.max(0.0, deltaTimeSec);
        if (updateAccumulatorSec < updateIntervalSec) {
            return;
        }

        double dt = updateAccumulatorSec;
        updateAccumulatorSec = 0.0;

        double danger = dangerMeter.computeDanger();
        double targetTempo = tempoMapper.mapDangerToTempo(danger);
        double alpha = 1.0 - Math.exp(-smoothingK * dt);
        smoothedTempo += (targetTempo - smoothedTempo) * alpha;
        smoothedTempo = clamp(smoothedTempo, 1.0, 1.18);

        bgmEngine.setTempo(smoothedTempo);

        if (debug) {
            System.out.printf("[PANIC-TEMPO] danger=%.1f target=%.3f tempo=%.3f%n",
                    danger, targetTempo, smoothedTempo);
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
