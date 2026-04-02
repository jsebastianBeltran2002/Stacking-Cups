package tower;

import shapes.*;

/**
 * Tapa loca que en lugar de tapar a su taza, se ubica como base de la torre.
 */
public class CrazyLid extends Lid {

    /**
     * Crea una tapa loca.
     * @param i tamanio de la tapa.
     * @param color color de la taza.
     */
    public CrazyLid(int i, String color) {
        super(i, color);
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
