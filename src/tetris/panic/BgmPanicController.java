package tetris.panic;

import javafx.scene.media.MediaPlayer;

public class BgmPanicController {

    private enum PanicState {
        NORMAL,
        TENSION,
        CRISIS
    }

    private static final double MAX_RATE = 1.25;
    private static final double NORMAL_RATE = 1.00;
    private static final double TENSION_RATE = 1.10;
    private static final double CRISIS_BASE_RATE = 1.20;

    private static final double ENTER_TENSION = 40.0;
    private static final double EXIT_TENSION = 35.0;
    private static final double ENTER_CRISIS = 70.0;
    private static final double EXIT_CRISIS = 65.0;

    private static final double ACCENT_TRIGGER_DELTA = 15.0;
    private static final double ACCENT_DURATION_SEC = 0.30;
    private static final double ACCENT_MAX_BOOST = 0.03;

    private final MediaPlayer mediaPlayer;
    private final double smoothingK;
    private final boolean debugLog;

    private PanicState state = PanicState.NORMAL;
    private double currentRate = NORMAL_RATE;
    private double previousDanger = 0.0;
    private double accentTimeLeftSec = 0.0;

    public BgmPanicController(MediaPlayer mediaPlayer) {
        this(mediaPlayer, 6.0, Boolean.getBoolean("tetris.debug.panic"));
    }

    public BgmPanicController(MediaPlayer mediaPlayer, double smoothingK, boolean debugLog) {
        this.mediaPlayer = mediaPlayer;
        this.smoothingK = smoothingK;
        this.debugLog = debugLog;
        if (mediaPlayer != null) {
            this.currentRate = clamp(mediaPlayer.getRate(), NORMAL_RATE, MAX_RATE);
        }
    }

    public void update(double deltaTimeSec, double danger) {
        if (mediaPlayer == null) {
            previousDanger = danger;
            return;
        }

        updateStateWithHysteresis(danger);

        if (danger - previousDanger >= ACCENT_TRIGGER_DELTA) {
            accentTimeLeftSec = ACCENT_DURATION_SEC;
        }

        if (accentTimeLeftSec > 0.0) {
            accentTimeLeftSec = Math.max(0.0, accentTimeLeftSec - deltaTimeSec);
        }

        double targetRate = computeTargetRate(danger);
        targetRate += ACCENT_MAX_BOOST * (accentTimeLeftSec / ACCENT_DURATION_SEC);
        targetRate = clamp(targetRate, NORMAL_RATE, MAX_RATE);

        double alpha = 1.0 - Math.exp(-smoothingK * Math.max(deltaTimeSec, 0.0));
        currentRate += (targetRate - currentRate) * alpha;
        currentRate = clamp(currentRate, NORMAL_RATE, MAX_RATE);

        if (Math.abs(mediaPlayer.getRate() - currentRate) > 1e-4) {
            mediaPlayer.setRate(currentRate);
        }

        if (debugLog) {
            System.out.printf("[PANIC] danger=%.1f state=%s target=%.3f rate=%.3f%n",
                    danger, state, targetRate, currentRate);
        }

        previousDanger = danger;
    }

    private void updateStateWithHysteresis(double danger) {
        switch (state) {
            case NORMAL -> {
                if (danger >= ENTER_TENSION) {
                    state = PanicState.TENSION;
                }
            }
            case TENSION -> {
                if (danger >= ENTER_CRISIS) {
                    state = PanicState.CRISIS;
                } else if (danger < EXIT_TENSION) {
                    state = PanicState.NORMAL;
                }
            }
            case CRISIS -> {
                if (danger < EXIT_CRISIS) {
                    state = PanicState.TENSION;
                }
            }
        }
    }

    private double computeTargetRate(double danger) {
        return switch (state) {
            case NORMAL -> NORMAL_RATE;
            case TENSION -> TENSION_RATE;
            case CRISIS -> {
                double ratio = clamp((danger - ENTER_CRISIS) / (100.0 - ENTER_CRISIS), 0.0, 1.0);
                yield CRISIS_BASE_RATE + ratio * 0.05;
            }
        };
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
