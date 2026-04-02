package tower;

import shapes.*;

/**
 * Tapa pegajosa que necesita dos intentos para salir de la torre.
 * Al primer intento muestra un mensaje y no sale.
 * Al segundo intento sale normalmente.
 */
public class StickyLid extends Lid {

    private int attempts;

    /**
     * Crea una tapa pegajosa.
     * @param i tamanio de la tapa.
     * @param color color de la taza.
     */
    public StickyLid(int i, String color) {
        super(i, color);
        this.attempts = 0;
    }

    /**
     * Al primer intento no sale y muestra mensaje.
     * Al segundo intento sale normalmente.
     * @param isCoveringCompanion no se usa en esta tapa.
     * @return true si ya se intento antes, false si es el primer intento.
     */
    @Override
    public boolean canExit(boolean isCoveringCompanion) {
        attempts++;
        if (attempts < 2) {
            return false;
        }
        attempts = 0;
        return true;
    }

    /**
     * Retorna un mensaje indicando que la tapa esta pegada.
     * @return mensaje de tapa pegada.
     */
    public String exitMessage() {
        return "La tapa esta pegada, intenta de nuevo!";
    }
}