import java.util.*;

/**
 * Representa una taza apilable con paredes, base, tapa opcional e inner cups.
 */
public class Cup extends Stackable {

    private Rectangle base;
    private Rectangle leftWall;
    private Rectangle rightWall;
    private Lid lid;
    private ArrayList<Cup> innerCups;
    private int x;
    private int y;
    private boolean visible;

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
        x = 70;
        y = 15;
        visible = false;
        innerCups = new ArrayList<>();
    }

    /** Agrega una cup anidada dentro de esta taza. */
    public void setInnerCup(Cup cup) { innerCups.add(cup); }

    /** Agrega una tapa a la taza si no tiene una. */
    public void addLid() {
        if (lid == null) {
            lid = new Lid(size(), color);
            lid.setPosition(x, y - scale);
            if (visible) lid.makeVisible();
        }
    }

    /** Elimina la tapa de la taza si tiene una. */
    public void removeLid() {
        if (lid != null) {
            lid.makeInvisible();
            lid = null;
        }
    }

    /**
     * Mueve la taza a la posición indicada actualizando sus rectángulos e inner cups.
     * @param newX nueva posición horizontal.
     * @param newY nueva posición vertical.
     */
    public void setPosition(int newX, int newY) {
        int leftCurrX = x;
        int leftCurrY = y;
        int rightCurrX = x + (width - 1) * scale;
        int baseCurrY = y + (height - 1) * scale;
        leftWall.moveHorizontal(newX - leftCurrX);
        leftWall.moveVertical(newY - leftCurrY);
        rightWall.moveHorizontal(newX + (width - 1) * scale - rightCurrX);
        rightWall.moveVertical(newY - leftCurrY);
        base.moveHorizontal(newX - x);
        base.moveVertical(newY + (height - 1) * scale - baseCurrY);
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

    /** Hace visible la taza, su tapa y sus inner cups. */
    public void makeVisible() {
        visible = true;
        base.makeVisible();
        leftWall.makeVisible();
        rightWall.makeVisible();
        for (Cup inner : innerCups) inner.makeVisible();
        if (lid != null) lid.makeVisible();
    }

    /** Oculta la taza, su tapa y sus inner cups. */
    public void makeInvisible() {
        visible = false;
        base.makeInvisible();
        leftWall.makeInvisible();
        rightWall.makeInvisible();
        for (Cup inner : innerCups) inner.makeInvisible();
        if (lid != null) lid.makeInvisible();
    }

    /** Retorna la inner cup más reciente, o null si no hay ninguna. */
    public Cup getInnerCup() { 
        return innerCups.isEmpty() ? null : innerCups.get(innerCups.size() - 1); 
    }

    /** Retorna la lista de inner cups de esta taza. */
    public ArrayList<Cup> getInnerCups() { 
        return innerCups;
    }

    /** Retorna el tamaño de la taza. */
    public int size() { 
        return (width + 1) / 2; 
    }

    /** Retorna true si la taza tiene tapa. */
    public boolean hasLid() { 
        return lid != null; 
    }

    /** Retorna true si esta taza contiene al menos una inner cup. */
    public boolean hasInnerCup() { 
        return !innerCups.isEmpty(); 
    }

    /** Retorna true porque este objeto es una taza. */
    public boolean isCup() { 
        return true; 
    }
}
