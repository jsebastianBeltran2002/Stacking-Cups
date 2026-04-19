package tower;
import shapes.*;

/**
 * Tapa miedosa que depende de su taza companera.
 * No entra a la torre si su taza companera no esta, y no sale si esta tapandola.
 * Se distingue visualmente con un rectangulo rojo encima.
 */
public class FearfulLid extends Lid {

    private int companionSize;
    private Rectangle marker;

    /**
     * Crea una tapa miedosa asociada a una taza companera.
     * @param i tamanio de la tapa.
     * @param color color de la taza.
     * @param companionSize tamanio de la taza companera.
     */
    public FearfulLid(int i, String color, int companionSize) {
        super(i, color);
        this.companionSize = companionSize;
        marker = new Rectangle();
        marker.changeSize(scale, scale);
        marker.changeColor("red");
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
     * Retorna el tamanio de la taza companera.
     * @return tamanio de la companera.
     */
    @Override
    public int getCompanionSize() { 
        return companionSize;
    }

    /**
     * Solo puede entrar si su taza companera esta en la torre.
     * @param companionPresent true si la companera esta en la torre.
     * @return true si la companera esta presente.
     */
    @Override
    public boolean canEnter(boolean companionPresent) { 
        return companionPresent; 
    }

    /**
     * No puede salir si esta tapando a su taza companera.
     * @param isCoveringCompanion true si esta tapando a su companera.
     * @return false si esta tapando a su companera.
     */
    @Override
    public boolean canExit(boolean isCoveringCompanion) { 
        return !isCoveringCompanion; 
    }

    /**
     * Retorna mensaje indicando que no puede salir mientras tapa a su companera.
     * @return mensaje de error.
     */
    @Override
    public String exitMessage() {
        return "La tapa no puede salir mientras tapa a su companera";
    }   
}
