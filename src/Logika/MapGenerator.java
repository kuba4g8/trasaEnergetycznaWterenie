package Logika;

import java.util.Random;

public class MapGenerator {
    
    private static final Random random = new Random();
    
    /**
     * Główna metoda sterująca procesem generowania mapy.
     */
    public static double[][] generateRandomMap(int width, int height, double minWysokosc, double maxWysokosc) {
        
        double[][] map = initializeMap(width, height);
        
        nalozenieWysokosci(map, width, height);
        
        normalizacjaWysokosciMapy(map, minWysokosc, maxWysokosc);
        
        return map;
    }
    
    // -----------------------------------------------------------------
    // SEKCJA 1: Logika nakładania terenu
    // -----------------------------------------------------------------
    
    private static void nalozenieWysokosci(double[][] map, int width, int height) {
        int numberOfHills = calculateHillCount(width, height);
        double maxRadius = calculateMaxHillRadius(width, height);
        
        for (int i = 0; i < numberOfHills; i++) {
            addRandomHill(map, width, height, maxRadius);
        }

        // Dodajemy drobny szum dla mikrozróżnicowania
        addNoise(map);
    }

    private static void addNoise(double[][] map) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] += (random.nextDouble() - 0.5) * 0.1;
            }
        }
    }
    
    private static void addRandomHill(double[][] map, int width, int height, double maxRadius) {
        // Losowanie parametrów pojedynczej górki
        double centerX = (random.nextDouble() * (width + 2 * maxRadius)) - maxRadius;
        double centerY = (random.nextDouble() * (height + 2 * maxRadius)) - maxRadius;
        
        // Zróżnicowanie promienia: niektóre górki bardzo małe, inne duże
        double radiusPower = random.nextDouble();
        double radius = Math.pow(radiusPower, 2) * maxRadius + 2.0;
        
        // Peak height może być ujemny (tworzenie dolin)
        double peakHeight = (random.nextDouble() * 2.0 - 0.7); // Więcej dodatnich niż ujemnych
        
        depositHillMaterial(map, centerX, centerY, radius, peakHeight);
    }
    
    private static void depositHillMaterial(double[][] map, double cx, double cy, double r, double h) {
        int width = map.length;
        int height = map[0].length;
        
        // Wyznaczenie granic rysowania (bounding box)
        int startX = Math.max(0, (int) (cx - r));
        int endX = Math.min(width, (int) (cx + r + 1));
        int startY = Math.max(0, (int) (cy - r));
        int endY = Math.min(height, (int) (cy + r + 1));
        
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                
                double elevation = obliczenieWysokosci(x, y, cx, cy, r, h);
                
                if (elevation > 0) {
                    map[x][y] += elevation;
                }
            }
        }
    }
    
    private static double obliczenieWysokosci(int currentX, int currentY, double hillCenterX, double hillCenterY, double hillRadius, double hillPeakHeight) {
        double distanceFromCenterSquared = (currentX - hillCenterX) * (currentX - hillCenterX) + (currentY - hillCenterY) * (currentY - hillCenterY);
        double radiusSquared = hillRadius * hillRadius;
        
        if (distanceFromCenterSquared < radiusSquared)
        {
            return hillPeakHeight * (1.0 - distanceFromCenterSquared / radiusSquared);
        }
        
        return 0.0;
    }

    
    private static void normalizacjaWysokosciMapy(double[][] map, double minTarget, double maxTarget) {
        double currentMin = findMinValue(map);
        double currentMax = findMaxValue(map);
        double currentRange = currentMax - currentMin;
        
        // Zabezpieczenie przed dzieleniem przez zero (gdy mapa jest płaska)
        if (currentRange == 0) {
            currentRange = 1;
        }
        
        skalowanieDoTerenu(map, currentMin, currentRange, minTarget, maxTarget);
    }
    
    private static void skalowanieDoTerenu(double[][] map, double currentMin, double currentRange, double minTarget, double maxTarget) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                
                double normalized = (map[x][y] - currentMin) / currentRange;
                double scaled = minTarget + normalized * (maxTarget - minTarget);
                
                map[x][y] = Math.round(scaled);
            }
        }
    }
    

    private static double[][] initializeMap(int width, int height) {
        return new double[width][height];
    }
    
    private static int calculateHillCount(int width, int height) {
        // Skalujemy liczbę górek liniowo do obwodu, nie pola, dla bardzo dużych map
        // aby uniknąć nadmiernego obciążenia przy generowaniu.
        return (int) (Math.sqrt((long)width * height) * 2.5);
    }
    
    private static double calculateMaxHillRadius(int width, int height) {
        // Pozwalamy na większe zróżnicowanie promieni
        return Math.min(width, height) / 2.0;
    }
    
    private static double findMinValue(double[][] map) {
        double min = Double.MAX_VALUE;
        
        for (double[] row : map) {
            for (double val : row) {
                if (val < min) {
                    min = val;
                }
            }
        }
        return min;
    }
    
    private static double findMaxValue(double[][] map) {
        double max = -Double.MAX_VALUE;
        
        for (double[] row : map) {
            for (double val : row) {
                if (val > max) {
                    max = val;
                }
            }
        }
        return max;
    }
}