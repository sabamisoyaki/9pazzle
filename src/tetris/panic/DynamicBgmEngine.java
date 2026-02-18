package tetris.panic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;

public class DynamicBgmEngine {

    private static final int BUFFER_SIZE = 2048;
    private static final int BUFFER_OVERLAP = 512;
    private static final double MIN_TEMPO = 1.00;
    private static final double MAX_TEMPO = 1.18;

    private final Path bgmPath;
    private final AtomicReference<Double> targetTempo = new AtomicReference<>(1.0);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final boolean debug;

    private volatile float volume = 0.35f;
    private volatile boolean muted = false;

    private Thread audioThread;
    private AudioDispatcher dispatcher;
    private SourceDataLine sourceLine;

    private byte[] pcmBytes;
    private AudioFormat pcmFormat;

    public DynamicBgmEngine(Path bgmPath) {
        this(bgmPath, Boolean.getBoolean("tetris.debug.audio"));
    }

    public DynamicBgmEngine(Path bgmPath, boolean debug) {
        this.bgmPath = bgmPath;
        this.debug = debug;
    }

    public synchronized void start() {
        if (running.get()) {
            return;
        }
        if (bgmPath == null || !Files.exists(bgmPath)) {
            System.out.println("[BGM] Not found: " + (bgmPath == null ? "(null)" : bgmPath.toAbsolutePath()));
            return;
        }

        try {
            ensurePcmLoaded();
        } catch (Exception e) {
            System.out.println("[BGM] Failed to decode: " + e.getMessage());
            return;
        }

        running.set(true);
        audioThread = new Thread(this::audioLoop, "dynamic-bgm-engine");
        audioThread.setDaemon(true);
        audioThread.start();
    }

    public synchronized void stop() {
        running.set(false);
        if (dispatcher != null) {
            dispatcher.stop();
        }
        if (audioThread != null) {
            try {
                audioThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        closeLine();
        audioThread = null;
        dispatcher = null;
    }

    public void setTempo(double tempo) {
        targetTempo.set(clamp(tempo, MIN_TEMPO, MAX_TEMPO));
    }

    public void setVolume(double volume) {
        this.volume = (float) clamp(volume, 0.0, 1.0);
    }

    public double getVolume() {
        return volume;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    private void audioLoop() {
        try {
            openLine();

            LoopingByteInputStream loopingInput = new LoopingByteInputStream(pcmBytes);
            AudioInputStream audioInputStream =
                    new AudioInputStream(loopingInput, pcmFormat, AudioSystem.NOT_SPECIFIED);
            JVMAudioInputStream jvmStream = new JVMAudioInputStream(audioInputStream);
            dispatcher = new AudioDispatcher(jvmStream, BUFFER_SIZE, BUFFER_OVERLAP);

            WaveformSimilarityBasedOverlapAdd wsola = createWsola(targetTempo.get());
            wsola.setDispatcher(dispatcher);

            dispatcher.addAudioProcessor(new TempoUpdateProcessor(wsola, pcmFormat.getSampleRate()));
            dispatcher.addAudioProcessor(wsola);
            dispatcher.addAudioProcessor(new LineWriterProcessor());

            if (debug) {
                System.out.println("[BGM] Dynamic engine started.");
            }
            dispatcher.run();
        } catch (Exception e) {
            System.out.println("[BGM] Playback error: " + e.getMessage());
        } finally {
            closeLine();
            running.set(false);
            if (debug) {
                System.out.println("[BGM] Dynamic engine stopped.");
            }
        }
    }

    private void ensurePcmLoaded() throws IOException, UnsupportedAudioFileException {
        if (pcmBytes != null && pcmFormat != null) {
            return;
        }

        try (AudioInputStream encoded = AudioSystem.getAudioInputStream(bgmPath.toFile())) {
            AudioFormat base = encoded.getFormat();
            AudioFormat decoded = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.getSampleRate(),
                    16,
                    base.getChannels(),
                    base.getChannels() * 2,
                    base.getSampleRate(),
                    false);

            try (AudioInputStream pcm = AudioSystem.getAudioInputStream(decoded, encoded)) {
                pcmBytes = readAllBytes(pcm);
                applyLoopCrossfade(pcmBytes, decoded.getChannels(), 128);
                pcmFormat = decoded;
            }
        }
    }

    private void openLine() throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, pcmFormat);
        sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(pcmFormat, BUFFER_SIZE * pcmFormat.getFrameSize() * 2);
        sourceLine.start();
    }

    private void closeLine() {
        if (sourceLine != null) {
            try {
                sourceLine.flush();
                sourceLine.stop();
            } finally {
                sourceLine.close();
                sourceLine = null;
            }
        }
    }

    private WaveformSimilarityBasedOverlapAdd createWsola(double tempo) {
        WaveformSimilarityBasedOverlapAdd.Parameters params =
                WaveformSimilarityBasedOverlapAdd.Parameters.musicDefaults(tempo, pcmFormat.getSampleRate());
        return new WaveformSimilarityBasedOverlapAdd(params);
    }

    private byte[] readAllBytes(AudioInputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = stream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    private void applyLoopCrossfade(byte[] pcm, int channels, int fadeSamples) {
        int bytesPerSample = 2;
        int frameSize = channels * bytesPerSample;
        int totalFrames = pcm.length / frameSize;
        if (totalFrames <= fadeSamples + 1) {
            return;
        }

        for (int i = 0; i < fadeSamples; i++) {
            double t = (double) i / (fadeSamples - 1);
            double inGain = t;
            double outGain = 1.0 - t;

            int headFrame = i;
            int tailFrame = totalFrames - fadeSamples + i;

            for (int ch = 0; ch < channels; ch++) {
                int headIndex = (headFrame * channels + ch) * bytesPerSample;
                int tailIndex = (tailFrame * channels + ch) * bytesPerSample;

                short head = littleEndianToShort(pcm[headIndex], pcm[headIndex + 1]);
                short tail = littleEndianToShort(pcm[tailIndex], pcm[tailIndex + 1]);

                int mixed = (int) Math.round(head * inGain + tail * outGain);
                short clipped = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, mixed));
                pcm[headIndex] = (byte) (clipped & 0xFF);
                pcm[headIndex + 1] = (byte) ((clipped >>> 8) & 0xFF);
            }
        }
    }

    private short littleEndianToShort(byte lo, byte hi) {
        return (short) (((hi & 0xFF) << 8) | (lo & 0xFF));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private class TempoUpdateProcessor implements AudioProcessor {
        private final WaveformSimilarityBasedOverlapAdd wsola;
        private final float sampleRate;
        private double appliedTempo = 1.0;

        private TempoUpdateProcessor(WaveformSimilarityBasedOverlapAdd wsola, float sampleRate) {
            this.wsola = wsola;
            this.sampleRate = sampleRate;
        }

        @Override
        public boolean process(AudioEvent audioEvent) {
            double wantedTempo = targetTempo.get();
            if (Math.abs(wantedTempo - appliedTempo) >= 0.002) {
                applyTempo(wsola, wantedTempo, sampleRate);
                appliedTempo = wantedTempo;
                if (debug) {
                    System.out.printf("[BGM-TEMPO] %.3f%n", wantedTempo);
                }
            }
            return true;
        }

        @Override
        public void processingFinished() {
            // no-op
        }

        private void applyTempo(WaveformSimilarityBasedOverlapAdd target,
                                double tempo,
                                float sampleRate) {
            try {
                Method setTempo = target.getClass().getMethod("setTempo", double.class);
                setTempo.invoke(target, tempo);
                return;
            } catch (Exception ignored) {
                // fallback below
            }
            try {
                WaveformSimilarityBasedOverlapAdd.Parameters params =
                        WaveformSimilarityBasedOverlapAdd.Parameters.musicDefaults(tempo, sampleRate);
                Method setParams = target.getClass().getMethod("setParameters",
                        WaveformSimilarityBasedOverlapAdd.Parameters.class);
                setParams.invoke(target, params);
            } catch (Exception e) {
                if (debug) {
                    System.out.println("[BGM] WSOLA tempo update failed: " + e.getMessage());
                }
            }
        }
    }

    private class LineWriterProcessor implements AudioProcessor {

        @Override
        public boolean process(AudioEvent audioEvent) {
            if (sourceLine == null) {
                return false;
            }

            float[] buffer = audioEvent.getFloatBuffer();
            int channels = pcmFormat.getChannels();
            byte[] out = new byte[buffer.length * 2];

            float gain = muted ? 0.0f : volume;
            int outIndex = 0;
            for (float sample : buffer) {
                int pcm = Math.round(sample * gain * 32767.0f);
                if (pcm > Short.MAX_VALUE) {
                    pcm = Short.MAX_VALUE;
                } else if (pcm < Short.MIN_VALUE) {
                    pcm = Short.MIN_VALUE;
                }

                out[outIndex++] = (byte) (pcm & 0xFF);
                out[outIndex++] = (byte) ((pcm >>> 8) & 0xFF);
            }

            int writableBytes = (buffer.length / channels) * pcmFormat.getFrameSize();
            int toWrite = Math.min(writableBytes, out.length);
            sourceLine.write(out, 0, toWrite);
            return true;
        }

        @Override
        public void processingFinished() {
            // no-op
        }
    }

    private static class LoopingByteInputStream extends InputStream {
        private final byte[] data;
        private int pos = 0;

        private LoopingByteInputStream(byte[] data) {
            this.data = data;
        }

        @Override
        public int read() {
            if (data.length == 0) {
                return -1;
            }
            int value = data[pos] & 0xFF;
            pos = (pos + 1) % data.length;
            return value;
        }

        @Override
        public int read(byte[] b, int off, int len) {
            if (data.length == 0) {
                return -1;
            }
            for (int i = 0; i < len; i++) {
                b[off + i] = data[pos];
                pos = (pos + 1) % data.length;
            }
            return len;
        }
    }
}
