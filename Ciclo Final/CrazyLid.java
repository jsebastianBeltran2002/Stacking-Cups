package tower;
import shapes.*;

/**
 * Tapa loca que en lugar de tapar a su taza, se ubica como base de la torre.
 * Se distingue visualmente con un rectangulo azul encima.
 */
public class CrazyLid extends Lid {

    private Rectangle marker;

    /**
     * Crea una tapa loca.
     * @param i tamanio de la tapa.
     * @param color color de la taza.
     */
    public CrazyLid(int i, String color) {
        super(i, color);
        marker = new Rectangle();
        marker.changeSize(scale, scale);
        marker.changeColor("blue");
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
     * Retorna true porque esta tapa es de tipo crazy.
     * @return true siempre.
     */
    @Override
    public boolean isCrazy() { 
        return true; 
    }
}
