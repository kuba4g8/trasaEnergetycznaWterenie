import GUI.PathfindingGUI;
import StrukturyDanych.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Definicja terenu
        double[][] mapData = {
                // 0    1    2    3    4    5    6    7    8    9   (oś Y)
                {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, // x=0
                {  0,  50,  50,  50,  50,  50,  50,  50,  50,   0}, // x=1  <-- GÓRY
                {  0,  50,   120,   0,   0,   0,   0,   0,  50,   0}, // x=2
                {  0,  50,   0,  80,  80,  80,  80,   0,  50,   10000}, // x=3  <-- MUR
                {  100,   30,   0,  80,   0,   0,  80,   0,   0,   0}, // x=4  <-- PRZEJŚCIE (4,4), (4,5)
                {  0,  50,   200,  80,   0,   0,  80,   0,  50,   0}, // x=5
                {  110,  50,   0,  80,  80,  80,  80,   0,  50,   0}, // x=6
                {  0,  50,   0,   0,   0,   0,   600,   0,  50,   0}, // x=7
                {  0,  50,  50,  50,  50,  50,  50,  50,  50,   0}, // x=8
                {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}  // x=9
        };

        Grid terrain = new Grid(10, 10, mapData);
        Node start = terrain.getNode(0, 0);
        Node target = terrain.getNode(9, 9);

        // Uruchomienie GUI w wątku Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            PathfindingGUI gui = new PathfindingGUI(terrain, start, target);
            gui.setVisible(true);
        });
    }
}