package tower;
import shapes.*;
import java.util.*;

/**
 * Taza que al entrar a la torre elimina todas las tapas que le impiden el paso.
 */
public class OpenerCup extends Cup {
    /**
     * Crea una taza opener de tamanio i.
     * @param i tamanio de la taza.
     */
    public OpenerCup(int i) {
        super(i);
    }
    /**
     * Al entrar a la torre elimina todas las tapas de tazas mas grandes que ella.
     * @param cups lista de elementos de la torre.
     */
    @Override
    public void onPush(ArrayList<Stackable> cups) {
        for (Stackable s : cups) {
            if (s.isCup() && s.size() > size() && s.hasLid()) {
                s.removeLid();
            }
        }
    }
}
