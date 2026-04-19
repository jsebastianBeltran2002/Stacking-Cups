package tower;
import shapes.*;

/**
 * Tapa pegajosa que necesita dos intentos para salir de la torre.
 * Al primer intento muestra un mensaje y no sale.
 * Al segundo intento sale normalmente.
 * Se distingue visualmente con un rectangulo negro encima.
 */
public class StickyLid extends Lid {

    private int attempts;
    private Rectangle marker;

    /**
     * Crea una tapa pegajosa.
     * @param i tamanio de la tapa.
     * @param color color de la taza.
     */
    public StickyLid(int i, String color) {
        super(i, color);
        this.attempts = 0;
        marker = new Rectangle();
        marker.changeSize(scale, scale);
        marker.changeColor("black");
    }

    /**
     * Mueve la tapa y el marcador a la posicion indicada.
     * @param newX nueva posicion horizontal en pixeles.
     * @param newY nueva posicion vertical en pixeles.
     */
    @Override
    public void setPosition(int newX, int newY) {
        int dx = newX - x;
        int dy = newY - y;
        super.setPosition(newX, newY);
        marker.moveHorizontal(dx);
        marker.moveVertical(dy);
        if (visible) {
            marker.makeInvisible();
            marker.makeVisible();
        }
    }

    /**
     * Hace visible la tapa y su marcador.
     */
    @Override
    public void makeVisible() {
        super.makeVisible();
        marker.makeInvisible();
        marker.makeVisible();
    }

    /**
     * Oculta la tapa y su marcador.
     */
    @Override
    public void makeInvisible() {
        super.makeInvisible();
        marker.makeInvisible();
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
    @Override
    public String exitMessage() {
        return "La tapa esta pegada, intenta de nuevo!";
    }
}
