package Testy; // Upewnij się, że pakiet jest zgodny z Twoją strukturą folderów

import StrukturyDanych.Grid;
import StrukturyDanych.Node;
import Logika.Dijkstra;
import Logika.aStar;
import Logika.MetodaPotencjalow;

import java.util.List;
import java.util.Random;

public class TestyStatystyczne {
    
    private Grid grid;
    private Random random = new Random();
    
    public TestyStatystyczne(Grid grid) {
        this.grid = grid;
    }
    
    public void uruchomTestyLosowe(int liczbaProb) {
        System.out.println("=== ROZPOCZYNAM TESTY STATYSTYCZNE (" + liczbaProb + " losowych tras) ===");
        
        // Zmienne do sumowania
        double sumaCzasDijkstra = 0, sumaKosztDijkstra = 0;
        long sumaWezlyDijkstra = 0;
        
        double sumaCzasAStar = 0, sumaKosztAStar = 0;
        long sumaWezlyAStar = 0;
        
        double sumaCzasPotencjaly = 0, sumaKosztPotencjaly = 0;
        long sumaWezlyPotencjaly = 0;
        int sukcesyPotencjaly = 0;
        
        for (int i = 0; i < liczbaProb; i++) {
            Node start, end;
            do
            {
                start = grid.getNode(random.nextInt(grid.getWidth()), random.nextInt(grid.getHeight()));
                end = grid.getNode(random.nextInt(grid.getWidth()), random.nextInt(grid.getHeight()));
            } while (start == end);
            
            // 2. DIJKSTRA
            long t1 = System.nanoTime();
            List<Node> pathD = Dijkstra.znajdzTrase(grid, start, end);
            long t2 = System.nanoTime();
            
            if (pathD != null && !pathD.isEmpty()) {
                sumaCzasDijkstra += (t2 - t1) / 1_000_000.0;
                sumaKosztDijkstra += obliczKoszt(pathD);
                sumaWezlyDijkstra += Dijkstra.liczbaOdwiedzonych; // Czytamy licznik ze zmiennej statycznej
            }
            
            // 3. A* (A Star)
            t1 = System.nanoTime();
            List<Node> pathA = aStar.znajdzTrase(grid, start, end);
            t2 = System.nanoTime();
            
            if (pathA != null && !pathA.isEmpty()) {
                sumaCzasAStar += (t2 - t1) / 1_000_000.0;
                sumaKosztAStar += obliczKoszt(pathA);
                sumaWezlyAStar += aStar.liczbaOdwiedzonych;
            }
            
            // 4. POTENCJAŁY
            t1 = System.nanoTime();
            List<Node> pathP = MetodaPotencjalow.znajdzTrase(grid, start, end);
            t2 = System.nanoTime();
            
            if (pathP != null && !pathP.isEmpty()) {
                sumaCzasPotencjaly += (t2 - t1) / 1_000_000.0;
                sumaKosztPotencjaly += obliczKoszt(pathP);
                sumaWezlyPotencjaly += MetodaPotencjalow.liczbaOdwiedzonych;
                sukcesyPotencjaly++;
            }
            
            // Opcjonalnie: Postęp w konsoli
            if (i % 10 == 0) System.out.print(".");
        }
        System.out.println("\nZakończono.");
        
        // === WYPISYWANIE WYNIKÓW ===
        wypiszWynik("DIJKSTRA", sumaCzasDijkstra, sumaWezlyDijkstra, sumaKosztDijkstra, liczbaProb);
        wypiszWynik("A*", sumaCzasAStar, sumaWezlyAStar, sumaKosztAStar, liczbaProb);
        
        // Dla potencjałów dzielimy przez liczbę sukcesów (bo czasami nie znajdują drogi)
        int dzielnik = (sukcesyPotencjaly > 0) ? sukcesyPotencjaly : 1;
        System.out.println("\nMETODA POTENCJAŁÓW (Sukces: " + sukcesyPotencjaly + "/" + liczbaProb + ")");
        System.out.printf("Średni Czas: %.4f ms\n", sumaCzasPotencjaly / dzielnik);
        System.out.println("Śr. Węzły: " + (sumaWezlyPotencjaly / dzielnik));
        System.out.printf("Śr. Koszt: %.2f\n", sumaKosztPotencjaly / dzielnik);
    }
    
    private void wypiszWynik(String nazwa, double czas, long wezly, double koszt, int n) {
        System.out.println("\n" + nazwa + ":");
        System.out.printf("Średni Czas: %.4f ms\n", czas / n);
        System.out.println("Śr. Węzły: " + (wezly / n));
        System.out.printf("Śr. Koszt: %.2f\n", koszt / n);
    }
    
    private double obliczKoszt(List<Node> path) {
        if (path == null || path.isEmpty()) return 0;
        return path.get(path.size() - 1).gScore;
    }
}