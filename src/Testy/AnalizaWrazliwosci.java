package Testy;

import Logika.Dijkstra;
import Logika.MetodaPotencjalow;
import Logika.aStar;
import StrukturyDanych.Grid;
import StrukturyDanych.Node;
import java.util.ArrayList;
import java.util.List;

public class AnalizaWrazliwosci
{
    // jak duza mapa to dac mniejsze
    private static final int LICZBA_POWTORZEN = 10;
    
    // 0 -> Dijkstra, 1 -> A*, 2 -> Potencjaly
    public static void uruchom(double[][] teren, int algorytm)
    {
        String algorytmNazwa = "";
        if (algorytm == 0) algorytmNazwa = "Dijkstry";
        if (algorytm == 1) algorytmNazwa = "A*";
        if (algorytm == 2) algorytmNazwa = "Metody Potencjalow";
        
        System.out.println("\n=== START ANALIZY WRAŻLIWOŚCI (WIELOKROTNE POWTARZANIE) ===");
        System.out.println("Algorytm: " + algorytmNazwa + " | Mapa: " + teren.length + "x" + teren.length);
        System.out.println("Liczba powtórzeń dla każdego pomiaru: " + LICZBA_POWTORZEN);
        
        // Tworzymy grid raz
        Grid grid = new Grid(teren.length, teren.length, teren, false);
        Node start = grid.getNode(0, 0);
        Node koniec = grid.getNode(teren.length - 1, teren.length - 1);
        
        double[] testowaneWartosci = {0.0, 0.1, 0.5, 1.0, 2.0, 5.0, 10.0, 50.0};
        
        // Nowy nagłówek z kolumnami statystycznymi (Średnia + Odchylenie)
        System.out.println(String.format("%-10s | %-15s | %-15s | %-15s | %-15s | %-15s",
                "Alpha", "Długość (kroki)", "Koszt Energii", "Śr. Czas (ms)", "Odchylenie (ms)", "Odwiedzone (N)"));
        System.out.println("-------------------------------------------------------------------------------------------------------------------");
        
        for (double alpha : testowaneWartosci) {
            grid.wspolczynnikWysokosci = alpha;
            
            // Zmienne do statystyk
            List<Double> czasyPomiarow = new ArrayList<>();
            List<Node> przykladowaTrasa = null;
            int ostatnieOdwiedzone = 0;
            
            // --- PĘTLA POWTARZAJĄCA EKSPERYMENT ---
            for (int i = 0; i < LICZBA_POWTORZEN; i++) {
                long tStart = System.nanoTime();
                
                // Wybór algorytmu
                if (algorytm == 0) przykladowaTrasa = Dijkstra.znajdzTrase(grid, start, koniec);
                else if (algorytm == 1) przykladowaTrasa = aStar.znajdzTrase(grid, start, koniec);
                else if (algorytm == 2) przykladowaTrasa = MetodaPotencjalow.znajdzTrase(grid, start, koniec);
                
                long tStop = System.nanoTime();
                double czasMs = (tStop - tStart) / 1_000_000.0;
                czasyPomiarow.add(czasMs);
                
                // Pobieramy liczbę odwiedzonych węzłów (zwykle jest stała dla tego samego algorytmu i mapy)
                ostatnieOdwiedzone = ZlozonoscObliczeniowa.liczbaOdwiedzonych;
            }
            
            // Obliczenia statystyczne
            double sredniCzas = obliczSrednia(czasyPomiarow);
            double odchylenie = obliczOdchylenieStandardowe(czasyPomiarow, sredniCzas);
            
            double koszt = obliczCalkowityKoszt(grid, przykladowaTrasa);
            int dlugosc = (przykladowaTrasa != null) ? przykladowaTrasa.size() : 0;
            
            System.out.println(String.format("%-10.1f | %-15d | %-15.2f | %-15.3f | %-15.3f | %-15d",
                    alpha, dlugosc, koszt, sredniCzas, odchylenie, ostatnieOdwiedzone));
        }
        System.out.println("===================================================================================================================\n");
    }
    
    // --- METODY POMOCNICZE (Żeby nie było spaghetti w głównej pętli) ---
    
    private static double obliczSrednia(List<Double> czasy) {
        double suma = 0.0;
        for (Double czas : czasy) {
            suma += czas;
        }
        return suma / czasy.size();
    }
    
    private static double obliczOdchylenieStandardowe(List<Double> czasy, double srednia) {
        double sumaKwadratowRoznic = 0.0;
        for (Double czas : czasy) {
            sumaKwadratowRoznic += Math.pow(czas - srednia, 2);
        }
        // Wzór na odchylenie standardowe
        return Math.sqrt(sumaKwadratowRoznic / czasy.size());
    }
    
    private static double obliczCalkowityKoszt(Grid grid, List<Node> trasa) {
        if (trasa == null || trasa.isEmpty()) return 0.0;
        double suma = 0.0;
        for (int i = 0; i < trasa.size() - 1; i++) {
            suma += grid.obliczEnergie(trasa.get(i), trasa.get(i+1));
        }
        return suma;
    }
}