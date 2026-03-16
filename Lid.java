/**
 * Representa una tapa para una taza, con visualización gráfica.
 */
public class Lid extends Stackable {

    private Rectangle rect;
    private int x;
    private int y;

    /**
     * Crea una tapa del mismo tamaño que la taza indicada y con su color.
     * @param i tamaño de la taza a tapar.
     * @param color color de la tapa, igual al de su taza.
     */
    public Lid(int i, String color) {
        super(2 * i - 1, 1);
        this.color = color;
        rect = new Rectangle();
        rect.changeSize(height * scale, width * scale);
        rect.changeColor(color);
        x = 70;
        y = 15;
    }

    /**
     * Mueve la tapa a la posición indicada en el canvas.
     * @param newX nueva posición horizontal.
     * @param newY nueva posición vertical.
     */
    public void setPosition(int newX, int newY) {
        int dx = newX - x;
        int dy = newY - y;
        rect.moveHorizontal(dx);
        rect.moveVertical(dy);
        x = newX;
        y = newY;
    }

    /** Hace visible la tapa en el canvas. */
    public void makeVisible() { rect.makeVisible(); }

    /** Oculta la tapa del canvas. */
    public void makeInvisible() { rect.makeInvisible(); }

    /** Retorna el tamaño de la tapa. */
    public int size() { return (width + 1) / 2; }

    /** Retorna false porque este objeto es una tapa, no una taza. */
    public boolean isCup() { return false; }
}