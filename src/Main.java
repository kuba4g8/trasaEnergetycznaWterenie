import java.util.List;
import Logika.Dijkstra;
import Logika.aStar;
import Logika.MetodaPotencjalow;
import StrukturyDanych.*;

public class Main {
    public static void main(String[] args) {
        // 1. Definicja terenu (4x4 z przeszkodą na środku)
        double[][] mapData = {
                // 0    1    2    3    4    5    6    7    8    9   (oś Y)
                {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, // x=0
                {  0,  50,  50,  50,  50,  50,  50,  50,  50,   0}, // x=1  <-- GÓRY
                {  0,  50,   0,   0,   0,   0,   0,   0,  50,   0}, // x=2
                {  0,  50,   0,  80,  80,  80,  80,   0,  50,   10000}, // x=3  <-- MUR
                {  100,   30,   0,  80,   0,   0,  80,   0,   0,   0}, // x=4  <-- PRZEJŚCIE (4,4), (4,5)
                {  0,  50,   0,  80,   0,   0,  80,   0,  50,   0}, // x=5
                {  0,  50,   0,  80,  80,  80,  80,   0,  50,   0}, // x=6
                {  0,  50,   0,   0,   0,   0,   0,   0,  50,   0}, // x=7
                {  0,  50,  50,  50,  50,  50,  50,  50,  50,   0}, // x=8
                {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}  // x=9
        };
        
        Grid terrain = new Grid(10, 10, mapData);
        Node start = terrain.getNode(0, 0);
        Node target = terrain.getNode(9, 9);
        
        
        // --- TEST DIJKSTRA ---
        System.out.println("=== TEST ALGORYTMU DIJKSTRY ===");
        List<Node> dijkstraPath = Dijkstra.znajdzTrase(terrain, start, target);
        printResults(dijkstraPath, target);
        
        // --- TEST A* ---
        // Pamiętaj: grid zostanie zresetowany wewnątrz metody aStar.znajdzTrase
        System.out.println("\n=== TEST ALGORYTMU A* ===");
        List<Node> aStarPath = aStar.znajdzTrase(terrain, start, target);
        printResults(aStarPath, target);
        
        // --- TEST METODY POTENCJAŁÓW ---
        System.out.println("\n=== TEST METODY POTENCJAŁÓW ===");
        List<Node> potentialPath = MetodaPotencjalow.znajdzTrase(terrain, start, target);
        if (potentialPath.size() <= 1 || potentialPath.get(potentialPath.size()-1) != target) {
            System.out.println("Metoda potencjałów nie dotarła do celu (utknęła)!");
        } else {
            printResults(potentialPath, target);
        }
    }
    
    private static void printResults(List<Node> path, Node target) {
        if (path.isEmpty()) {
            System.out.println("Nie znaleziono trasy!");
        } else {
            System.out.println("Całkowity koszt energii: " + String.format("%.2f", target.gScore));
            System.out.print("Przebieg trasy: ");
            for (int i = 0; i < path.size(); i++) {
                Node n = path.get(i);
                System.out.print("(" + n.elements.x + "," + n.elements.y + ")");
                if (i < path.size() - 1) System.out.print(" -> ");
            }
            System.out.println();
        }
    }
}