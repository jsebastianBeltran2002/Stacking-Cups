import java.util.*;

/**
 * Clase abstracta que representa un elemento apilable en la torre.
 * Define atributos y comportamientos comunes para tazas y tapas.
 */
public abstract class Stackable {

    protected int width;
    protected int height;
    protected String color;
    protected final int scale = 10;
    private static ArrayList<String> colors = new ArrayList<>(Arrays.asList(
        "red","blue","green","yellow","magenta","cyan"
    ));
    private static Random random = new Random();

    /**
     * Crea un elemento apilable con dimensiones dadas y asigna un color aleatorio único.
     * @param width ancho del elemento en unidades.
     * @param height altura del elemento en unidades.
     */
    public Stackable(int width, int height) {
        this.width = width;
        this.height = height;
        if (colors.isEmpty()) {
            colors.addAll(Arrays.asList("red","blue","green","yellow","magenta","cyan"));
        }
        int index = random.nextInt(colors.size());
        color = colors.remove(index);
    }

    /**
     * Mueve el elemento a la posición indicada en el canvas.
     * @param x nueva posición horizontal.
     * @param y nueva posición vertical.
     */
    public abstract void setPosition(int x, int y);

    /** Hace visible el elemento en el canvas. */
    public abstract void makeVisible();

    /** Oculta el elemento del canvas. */
    public abstract void makeInvisible();

    /** Agrega una tapa al elemento si aplica. */
    public void addLid() {
    }

    /** Elimina la tapa del elemento si aplica. */
    public void removeLid() {
    }

    /** Retorna el ancho del elemento en unidades. */
    public int width() { 
        return width; 
    }

    /** Retorna la altura del elemento en unidades. */
    public int height() { 
        return height; 
    }

    /** Retorna el color del elemento. */
    public String color() { 
        return color; 
    }

    /** Retorna true si el elemento tiene tapa. */
    public boolean hasLid() { 
        return false; 
    }

    /** Retorna true si el elemento es una taza. */
    public abstract boolean isCup();

    /** Retorna el tamaño del elemento. */
    public abstract int size();
}