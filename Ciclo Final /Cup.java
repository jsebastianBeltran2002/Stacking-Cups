package tower;
import shapes.*;
import java.util.*;

/**
 * Representa una taza apilable con paredes, base, tapa opcional e inner cups.
 * Las tazas pueden anidarse unas dentro de otras si la exterior es más grande.
 */
public class Cup extends Stackable {

    private Rectangle base;
    private Rectangle leftWall;
    private Rectangle rightWall;
    private Lid lid;
    private ArrayList<Cup> innerCups;
    /**
     * Crea una taza de tamaño i con sus rectángulos visuales inicializados.
     * @param i tamaño de la taza, determina ancho y alto.
     */
    public Cup(int i) {
        super(2 * i - 1, 2 * i - 1);
        base = new Rectangle();
        leftWall = new Rectangle();
        rightWall = new Rectangle();
        base.changeSize(scale, width * scale);
        leftWall.changeSize((height - 1) * scale, scale);
        rightWall.changeSize((height - 1) * scale, scale);
        base.changeColor(color);
        leftWall.changeColor(color);
        rightWall.changeColor(color);
        base.moveVertical((height - 1) * scale);
        rightWall.moveHorizontal((width - 1) * scale);
        innerCups = new ArrayList<>();
    }
    /**
     * Agrega una cup anidada dentro de esta taza.
     * @param cup taza a anidar.
     */
    public void setInnerCup(Cup cup) { 
        innerCups.add(cup); 
    }
    /**
     * Agrega una tapa normal a la taza si no tiene una.
     */
    public void addLid() {
        if (lid == null) {
            lid = new Lid(size(), color);
            lid.setPosition(x, y - scale);
            if (visible) 
            lid.makeVisible();
        }
    }
    /**
     * Agrega la tapa indicada a la taza si no tiene una.
     * @param newLid tapa a agregar.
     */
    public void addFearfulLid(int companionSize) {
        if (lid == null) {
            lid = new FearfulLid(size(), color, companionSize);
            lid.setPosition(x, y - scale);
            if (visible) 
            lid.makeVisible();
        }
    }
    /**
     * Agrega una tapa crazy a la taza si no tiene una.
     * La tapa se ubicará debajo de la taza en lugar de encima.
     */
    public void addCrazyLid() {
        if (lid == null) {
            lid = new CrazyLid(size(), color);
            lid.setPosition(x, y - scale);
            if (visible) 
            lid.makeVisible();
        }
    }
    /** Agrega una tapa sticky a la taza si no tiene una. */
    public void addStickyLid() {
        if (lid == null) {
            lid = new StickyLid(size(), color);
            lid.setPosition(x, y - scale);
            if (visible) lid.makeVisible();
        }
    }
    /**
     * Elimina la tapa de la taza si tiene una.
     */
    public void removeLid() {
        if (lid != null) {
            lid.makeInvisible();
            lid = null;
        }
    }
     /**
     * Mueve la taza a la posición indicada actualizando sus rectángulos e inner cups.
     * @param newX nueva posición horizontal en píxeles.
     * @param newY nueva posición vertical en píxeles.
     */
    public void setPosition(int newX, int newY) {
        leftWall.moveHorizontal(newX - x);
        leftWall.moveVertical(newY - y);
        rightWall.moveHorizontal(newX + (width - 1) * scale - (x + (width - 1) * scale));
        rightWall.moveVertical(newY - y);
        base.moveHorizontal(newX - x);
        base.moveVertical(newY + (height - 1) * scale - (y + (height - 1) * scale));
        x = newX;
        y = newY;
        if (lid != null) lid.setPosition(x, y - scale);
        int innerAccumulated = 0;
        for (Cup inner : innerCups) {
            int innerX = x + ((width - inner.width()) / 2) * scale;
            int innerY = y + (height - 1) * scale - inner.height() * scale - innerAccumulated * scale;
            inner.setPosition(innerX, innerY);
            innerAccumulated += inner.height();
            if (inner.hasLid()) innerAccumulated += 1;
        }
    }
    /**
     * Hace visible la taza, su tapa y sus inner cups.
     */
    public void makeVisible() {
        visible = true;
        base.makeVisible();
        leftWall.makeVisible();
        rightWall.makeVisible();
        for (Cup inner : innerCups) 
        inner.makeVisible();
        if (lid != null) 
        lid.makeVisible();
    }
    /**
     * Oculta la taza, su tapa y sus inner cups.
     */
    public void makeInvisible() {
        visible = false;
        base.makeInvisible();
        leftWall.makeInvisible();
        rightWall.makeInvisible();
        for (Cup inner : innerCups)
        inner.makeInvisible();
        if (lid != null) 
        lid.makeInvisible();
    }
    /**
     * Agrega la tapa indicada a la taza si no tiene una.
     * @param newLid tapa a agregar.
     */ 
    public void addSpecificLid(Lid newLid) {
        if (lid == null) {
            lid = newLid;
            lid.setPosition(x, y - scale);
            if (visible) lid.makeVisible();
        }
    }
    /**
     * Cambia el color de la taza y sus rectángulos visuales.
     * @param newColor nuevo color.
     */
    public void changeColor(String newColor) {
        this.color = newColor;
        base.changeColor(newColor);
        leftWall.changeColor(newColor);
        rightWall.changeColor(newColor);
    }
    /**
     * Retorna la inner cup más reciente, o null si no hay ninguna.
     * @return última inner cup o null.
     */
    public Cup getInnerCup() { 
        return innerCups.isEmpty() ? null : innerCups.get(innerCups.size() - 1);
    }
    /**
     * Retorna la lista de inner cups de esta taza.
     * @return lista de inner cups.
     */
    public ArrayList<Cup> getInnerCups() { 
        return innerCups; 
    }
    /**
     * Retorna el tamaño de la taza.
     * @return tamaño calculado como (width+1)/2.
     */
    public int size() {
        return (width + 1) / 2; 
    }
    /**
     * Retorna true si la taza tiene tapa.
     * @return true si tiene tapa.
     */
    public boolean hasLid() { 
        return lid != null; 
    }
    /**
     * Retorna la tapa de la taza, o null si no tiene.
     * @return tapa actual o null.
     */
    public Lid getLid() { 
        return lid;
    }
    /**
     * Retorna true si esta taza contiene al menos una inner cup.
     * @return true si tiene inner cups.
     */
    public boolean hasInnerCup() { 
        return !innerCups.isEmpty();
    }
    /**
     * Retorna true porque este objeto es una taza.
     * @return true siempre.
     */
    public boolean isCup() { 
        return true; 
    }
    /**
     * Se ejecuta al entrar a la torre. Por defecto no hace nada.
     * Las subclases sobreescriben este método para comportamiento especial.
     * @param cups lista de elementos de la torre.
     */
    public void onPush(ArrayList<Stackable> cups) {
    }
    /**
     * Retorna true si la taza está fija en el fondo. Por defecto false.
     * @return false siempre en Cup base.
     */
    public boolean isFixed() { 
        return false; 
    }
    /**
     * Inserta la taza en la lista. Por defecto al final.
     * @param cups lista de elementos de la torre.
     */
    public void insertIntoCups(ArrayList<Stackable> cups) { 
        cups.add(this); 
    }
    protected final void changeLeftWallColor(String color) {
    leftWall.changeColor(color);
    }
}
