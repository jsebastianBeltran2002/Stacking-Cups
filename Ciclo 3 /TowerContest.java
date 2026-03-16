import java.util.*;
import javax.swing.*;

/**
 * Resuelve y simula el problema de ordenar tazas en una torre con altura objetivo.
 */
public class TowerContest {

    /**
     * Retorna el orden de las tazas que produce la altura h en una torre de n tazas.
     * Si no es posible, retorna "impossible".
     * @param n número de tazas disponibles.
     * @param h altura objetivo de la torre.
     * @return orden de alturas de tazas o "impossible".
     */
    public String solve(int n, long h) {
        long minHeight = 2 * n - 1;
        long maxHeight = (long) n * n + n;

        if (h < minHeight || h > maxHeight) {
            return "impossible";
        }

        long remaining = h - minHeight;
        ArrayList<Integer> outside = new ArrayList<>();
        ArrayList<Integer> inside = new ArrayList<>();

        for (int i = n; i >= 1; i--) {
            long increase = 2 * (i - 1);
            if (increase > remaining) {
                outside.add(2 * i - 1);
            } else {
                inside.add(2 * i - 1);
                remaining -= increase;
            }
        }

        StringBuilder result = new StringBuilder();
        for (int k = 0; k < Math.max(outside.size(), inside.size()); k++) {
            if (k < outside.size()) result.append(outside.get(k)).append(" ");
            if (k < inside.size()) result.append(inside.get(k)).append(" ");
        }
        return result.toString().trim();
    }

    /**
     * Muestra gráficamente la torre con altura h usando n tazas.
     * Si no tiene solución, muestra un mensaje con JOptionPane.
     * @param n número de tazas disponibles.
     * @param h altura objetivo de la torre.
     */
    public void simulate(int n, long h) {
        String result = solve(n, h);

        if (result.equals("impossible")) {
            JOptionPane.showMessageDialog(null, "impossible");
            return;
        }

        Tower tower = new Tower(2 * n - 1, n * n + n);

        ArrayList<Integer>[] lists = getLists(n, h);
        ArrayList<Integer> outside = lists[0];
        ArrayList<Integer> inside = lists[1];

        for (int i = 0; i < outside.size(); i++) {
            int size = (outside.get(i) + 1) / 2;
            tower.pushCup(size);
            if (i < inside.size()) {
                int innerSize = (inside.get(i) + 1) / 2;
                tower.pushCup(innerSize);
            }
        }
        for (int i = outside.size(); i < inside.size(); i++) {
            int size = (inside.get(i) + 1) / 2;
            tower.pushCup(size);
        }

        tower.makeVisible();
    }

    /** Calcula y retorna las listas de tazas outside e inside para n y h dados. */
    private ArrayList<Integer>[] getLists(int n, long h) {
        long remaining = h - (2 * n - 1);
        ArrayList<Integer> outside = new ArrayList<>();
        ArrayList<Integer> inside = new ArrayList<>();

        for (int i = n; i >= 1; i--) {
            long increase = 2 * (i - 1);
            if (increase > remaining) {
                outside.add(2 * i - 1);
            } else {
                inside.add(2 * i - 1);
                remaining -= increase;
            }
        }

        return new ArrayList[]{outside, inside};
    }
}
