package Testy;

public abstract class ZlozonoscObliczeniowa
{
    // Statyczny licznik dostępny dla wszystkich dziedziczących klas
    public static int liczbaOdwiedzonych = 0;
    
    // Metoda zwracająca sformatowany raport o złożoności
    public static String obliczZlozonoscObliczeniowa()
    {
        return "Złożoność (liczba odwiedzonych węzłów N): " + liczbaOdwiedzonych;
    }
    
    public static int getLiczbaOdwiedzonych()
    {
        return liczbaOdwiedzonych;
    }
    
    // Metoda pomocnicza do resetowania przed startem algorytmu
    protected static void resetLicznika()
    {
        liczbaOdwiedzonych = 0;
    }
}