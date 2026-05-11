package spiel1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random; // quelle ki.com und wenn das keine quelle ist dann kommt es aus meinem gehirn

public class Bluenoise {
    private static final Random random = new Random();

    /**
     * Generiert Blue Noise Punkte in einem definierten Rechteck.
     * @param linksOben Der Startpunkt (Min X, Min Y)
     * @param rechtsUnten Der Endpunkt (Max X, Max Y)
     * @return Eine ArrayList mit Vector2 Objekten
     */
    public static ArrayList<Vector2> generate(Vector2 linksOben, Vector2 rechtsUnten) {
        // Konfiguration: Mindestabstand (r) und Versuche (k)
        double r = 10.0;
        int k = 30;

        double width = rechtsUnten.x - linksOben.x;
        double height = rechtsUnten.y - linksOben.y;
        double cellSize = r / Math.sqrt(2);

        int cols = (int) Math.ceil(width / cellSize);
        int rows = (int) Math.ceil(height / cellSize);

        // Gitter zur schnellen Nachbarschaftsprüfung
        int[][] grid = new int[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) grid[i][j] = -1;
        }

        ArrayList<Vector2> points = new ArrayList<>();
        ArrayList<Vector2> activeList = new ArrayList<>();

        // Ersten Punkt im relativen Bereich setzen
        Vector2 first = new Vector2(linksOben.x + random.nextDouble() * width,
                linksOben.y + random.nextDouble() * height);
        addPoint(first, linksOben, points, activeList, grid, cellSize);

        while (!activeList.isEmpty()) {
            int randomIndex = random.nextInt(activeList.size());
            Vector2 center = activeList.get(randomIndex);
            boolean found = false;

            for (int i = 0; i < k; i++) {
                // Punkt im Ring zwischen r und 2r generieren
                double angle = 2 * Math.PI * random.nextDouble();
                double radius = r * (random.nextDouble() + 1);
                Vector2 candidate = new Vector2(center.x + radius * Math.cos(angle),
                        center.y + radius * Math.sin(angle));

                if (isValid(candidate, linksOben, rechtsUnten, r, grid, cellSize, points)) {
                    addPoint(candidate, linksOben, points, activeList, grid, cellSize);
                    found = true;
                    break;
                }
            }

            if (!found) {
                activeList.remove(randomIndex);
            }
        }
        return points;
    }

    private static void addPoint(Vector2 p, Vector2 start, List<Vector2> points, List<Vector2> active, int[][] grid, double cellSize) {
        points.add(p);
        active.add(p);
        int col = (int) ((p.x - start.x) / cellSize);
        int row = (int) ((p.y - start.y) / cellSize);
        grid[col][row] = points.size() - 1;
    }

    private static boolean isValid(Vector2 p, Vector2 min, Vector2 max, double r, int[][] grid, double cellSize, List<Vector2> points) {
        if (p.x < min.x || p.x >= max.x || p.y < min.y || p.y >= max.y) return false;

        int col = (int) ((p.x - min.x) / cellSize);
        int row = (int) ((p.y - min.y) / cellSize);

        for (int i = Math.max(0, col - 2); i <= Math.min(grid.length - 1, col + 2); i++) {
            for (int j = Math.max(0, row - 2); j <= Math.min(grid[0].length - 1, row + 2); j++) {
                int index = grid[i][j];
                if (index != -1) {
                    Vector2 other = points.get(index);
                    double dX = p.x - other.x;
                    double dY = p.y - other.y;
                    if (dX * dX + dY * dY < r * r) return false;
                }
            }
        }
        return true;
    }
}