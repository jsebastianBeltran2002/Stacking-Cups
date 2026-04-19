package tower;
import shapes.*;
import java.util.*;

/**
 * Resuelve y simula el problema de la maratón Stacking Cups (ICPC 2025 Problem J).
 * La altura de la torre es la distancia del punto más bajo al más alto.
 * Taza i tiene altura (2i-1) cm y su base tiene grosor 1 cm.
 */
public class TowerContest {

    /**
     * Resuelve el problema: dado n tazas y altura deseada h,
     * retorna el orden de alturas en que colocar las tazas,
     * o "impossible" si no es alcanzable.
     * Usa algoritmo greedy similar al problema de cambio de monedas.
     * @param n número de tazas
     * @param h altura deseada
     * @return String con las alturas en orden, o "impossible"
     */
    public static String solve(int n, long h) {
        if (n == 1) return h == 1 ? "1" : "impossible";
        long minH = 2L * n - 1;
        long maxH = (long) n * n;
        if (h < minH || h > maxH) 
        return "impossible";

        boolean[] before = new boolean[n + 1];
        long rem1 = h - minH;
        for (int i = n - 1; i >= 1 && rem1 > 0; i--) {
            long coin = 2L * i - 1;
            if (coin <= rem1) { 
                before[i] = true; rem1 -= coin; 
            }
        }
        if (rem1 == 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < n; i++) 
            if (before[i]) sb.append(2*i-1).append(" ");
            sb.append(2*n-1);
            for (int i = n-1; i >= 1; i--) 
            if (!before[i]) sb.append(" ").append(2*i-1);
            return sb.toString().trim();
        }

        boolean[] inside = new boolean[n + 1];
        long rem2 = h - 1;
        for (int i = n - 1; i >= 1 && rem2 > 0; i--) {
            long coin = 2L * i - 1;
            if (coin <= rem2) { 
                inside[i] = true; rem2 -= coin; 
            }
        }
        if (rem2 == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(2*n-1);
            for (int i = 1; i < n; i++)
            if (inside[i]) sb.append(" ").append(2*i-1);
            for (int i = n-1; i >= 1; i--) 
            if (!inside[i]) sb.append(" ").append(2*i-1);
                return sb.toString().trim();
        }

        return "impossible";
    }

    /**
     * Simula la solución usando la clase Tower para visualizarla gráficamente.
     * La entrada y salida corresponde a lo definido en el problema de la maratón.
     * Si la solución existe y es posible graficarla, muestra la torre.
     * En caso contrario, presenta un mensaje indicándolo.
     * @param n número de tazas
     * @param h altura deseada
     */
    public static void simulate(int n, int h) {
        String sol = solve(n, h);
        if (sol.equals("impossible")) { 
            System.out.println("impossible"); 
            return; 
        }
        System.out.println(sol);
        String[] parts = sol.split(" ");
        Tower tower = new Tower(2*n-1, h+2);
    
        for (String p : parts) {
            int size = (Integer.parseInt(p)+1)/2;
            tower.pushCup(size);
        }
        tower.makeVisible();
    }
}