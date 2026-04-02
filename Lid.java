package tower;

import shapes.*;
import java.util.Random;

/**
 * Representa una tapa para una taza, con visualización gráfica.
 */
public class Lid extends Stackable {

    private Rectangle rect;

    /**
     * Crea una tapa del mismo tamaño que la taza indicada y con color aleatorio.
     * @param i tamaño de la taza a tapar.
     * @param color color original de la taza.
     */
    public Lid(int i, String color) {
        super(2 * i - 1, 1);
        String[] colors = {"yellow"};
        Random random = new Random();
        this.color = colors[random.nextInt(colors.length)];
        rect = new Rectangle();
        rect.changeSize(height * scale, width * scale);
        rect.changeColor(this.color);
    }

    /**
     * Mueve la tapa a la posición indicada en el canvas.
     * @param newX nueva posición horizontal.
     * @param newY nueva posición vertical.
     */
    public void setPosition(int newX, int newY) {
        rect.moveHorizontal(newX - x);
        rect.moveVertical(newY - y);
        x = newX;
        y = newY;
    }

    /** Hace visible la tapa en el canvas. */
    public void makeVisible() { 
        rect.makeVisible(); 
    }

    /** Oculta la tapa del canvas. */
    public void makeInvisible() { 
        rect.makeInvisible(); 
    }

    /** Retorna el tamaño de la tapa. */
    public int size() {
        return (width + 1) / 2; 
    }

    /** Retorna false porque este objeto es una tapa, no una taza. */
    public boolean isCup() { 
        return false; 
    }

    /**
     * Cambia el color de la tapa visualmente.
     * @param newColor nuevo color.
     */
    protected void changeColor(String newColor) {
        this.color = newColor;
        rect.changeColor(newColor);
    }

    /**
     * Retorna el tamaño de la taza companera. Por defecto -1 (no tiene companera).
     * @return -1.
     */
    public int getCompanionSize() { 
        return -1; 
    }

    /**
     * Retorna true si esta tapa puede entrar a la torre.
     * @param companionPresent indica si la taza companera esta en la torre.
     * @return true por defecto.
     */
    public boolean canEnter(boolean companionPresent) { 
        return true;
    }

    /**
     * Retorna true si esta tapa puede salir de la torre.
     * @param isCoveringCompanion indica si esta tapando a su taza companera.
     * @return true por defecto.
     */
    public boolean canExit(boolean isCoveringCompanion) { 
        return true;
    }
    public String exitMessage() {
        return "La tapa no puede salir";
    }
    public boolean isCrazy() { 
        return false;
    }
}