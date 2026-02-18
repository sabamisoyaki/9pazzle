package tetris.panic;

public class DangerMeter {

    private final BoardProvider board;
    private double previousDanger = 0.0;

    public DangerMeter(BoardProvider board) {
        this.board = board;
    }

    public double computeDanger() {
        int width = board.getWidth();
        int height = board.getHeight();
        if (width <= 0 || height <= 0) {
            previousDanger = 0.0;
            return 0.0;
        }

        int[] heights = new int[width];
        int holeCount = 0;

        for (int x = 0; x < width; x++) {
            int topMostRow = -1;
            boolean seenBlock = false;
            for (int y = 0; y < height; y++) {
                boolean occupied = board.isOccupied(x, y);
                if (occupied) {
                    if (topMostRow < 0) {
                        topMostRow = y;
                    }
                    seenBlock = true;
                } else if (seenBlock) {
                    holeCount++;
                }
            }
            heights[x] = (topMostRow < 0) ? 0 : (height - topMostRow);
        }

        int maxColumnHeight = 0;
        for (int h : heights) {
            if (h > maxColumnHeight) {
                maxColumnHeight = h;
            }
        }

        double heightRate = clamp(maxColumnHeight * 100.0 / height, 0.0, 100.0);
        double holeRate = Math.min(holeCount * 8.0, 100.0);

        int sumDiff = 0;
        for (int x = 0; x < width - 1; x++) {
            sumDiff += Math.abs(heights[x] - heights[x + 1]);
        }
        double maxBumpiness = Math.max(1.0, (width - 1) * (double) height);
        double bumpinessRate = clamp(sumDiff / maxBumpiness * 100.0, 0.0, 100.0);

        double dependencyRisk = computeDependencyRisk(heights);

        double danger = 0.4 * heightRate
                + 0.3 * holeRate
                + 0.15 * bumpinessRate
                + 0.15 * dependencyRisk;

        double clamped = clamp(danger, 0.0, 100.0);
        previousDanger = clamped;
        return clamped;
    }

    public double getPreviousDanger() {
        return previousDanger;
    }

    private double computeDependencyRisk(int[] heights) {
        if (heights.length < 3) {
            return 0.0;
        }

        double maxRisk = 0.0;
        for (int x = 1; x < heights.length - 1; x++) {
            int center = heights[x];
            int left = heights[x - 1];
            int right = heights[x + 1];

            int wellDepth = Math.min(left - center, right - center);
            if (wellDepth >= 8) {
                maxRisk = Math.max(maxRisk, 40.0);
            } else if (wellDepth >= 5) {
                maxRisk = Math.max(maxRisk, 25.0);
            }
        }
        return maxRisk;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
