package tower;
import shapes.*;
import java.util.*;

/**
 * Taza que al entrar desplaza todos los objetos de menor tamanio.
 * Si llega al fondo de la torre, no se deja quitar.
 * Se distingue visualmente con la pared izquierda de color blanco.
 */
public class HierarchicalCup extends Cup {

    private boolean fixed;

    /**
     * Crea una taza hierarchical de tamanio i.
     * @param i tamanio de la taza.
     */
    public HierarchicalCup(int i) {
        super(i);
        fixed = false;
        changeLeftWallColor("white");
    }

    /**
     * Al entrar fija la taza y desplaza las mas pequenas.
     * @param cups lista de elementos de la torre.
     */
    @Override
    public void onPush(ArrayList<Stackable> cups) {
        fix();
    }

    /**
     * Se inserta al final de cups que equivale al fondo de la torre.
     * @param cups lista de elementos de la torre.
     */
    @Override
    public void insertIntoCups(ArrayList<Stackable> cups) {
        ArrayList<Stackable> smaller = new ArrayList<>();
        for (int i = cups.size() - 1; i >= 0; i--) {
            if (cups.get(i).size() < size()) {
                smaller.add(0, cups.remove(i));
            }
        }
        cups.add(this);
        for (Stackable s : smaller) {
            setInnerCup((Cup) s);
        }
    }

    /** Fija la taza al fondo de la torre. */
    public void fix() {
        fixed = true;
    }

    /**
     * Retorna true si la taza esta fija en el fondo.
     * @return true si esta fija.
     */
    @Override
    public boolean isFixed() {
        return fixed;
    }
}
