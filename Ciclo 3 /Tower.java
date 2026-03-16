import java.util.*;
import javax.swing.*;
/**
 * Representa una torre de tazas apilables con soporte para anidamiento,
 * tapas y visualización gráfica.
 */

public class Tower {

    private int towerWidth;
    private int maxHeight;
    private final int scale = 10;
    private final int margin = 65;
    private final int RECT_INIT_X = 70;
    private final int RECT_INIT_Y = 15;
    private boolean fixed;

    private ArrayList<Stackable> cups;
    private boolean visible;
    private boolean lastOperationOk;

    private Rectangle axisX;
    private Rectangle axisY;
    private ArrayList<Rectangle> grid;

    /**
     * Representa una torre de tazas apilables con soporte para anidamiento,
     * tapas y visualización gráfica.
     */
    public Tower(int width, int maxHeight) {
        this.towerWidth = width;
        this.maxHeight = maxHeight;
        initialize();
        setupAxes();
    }
    /**
     * Crea una torre con n tazas de tamaños 1 a n apiladas de menor a mayor.
     * Ej: cups=4 crea tazas 1, 2, 3, 4 de alturas 1, 3, 5, 7.
     * @param n número de tazas a crear.
     */
    public Tower(int n) {
        this.towerWidth = 2 * n - 1;
        this.maxHeight = n * n + n;
        initialize();
        setupAxes();

        for (int i = 1; i <= n; i++) {
            cups.add(new Cup(i));
        }
    }
    /**
     * Agrega una taza de tamaño i a la torre.
     * Si cabe dentro de la última taza sin tapa, se anida.
     * Rechaza duplicados y tazas que excedan el ancho o altura máxima.
     * @param i tamaño de la taza a agregar.
     */    
    public void pushCup(int i) {
        if (fixed) { 
            showMessage("Esta torre no permite agregar tazas"); 
            lastOperationOk = false; 
            return; 
        }
        if (2 * i - 1 > towerWidth) { 
            showMessage("La taza es más grande que la torre"); 
            lastOperationOk = false; 
            return; 
        }
        if (findCup(i) != null) { 
            showMessage("Ya existe una taza de tamaño " + i); 
            lastOperationOk = false; 
            return; 
        }
        Cup cup = new Cup(i);
        if (!cups.isEmpty()) {
            Stackable last = cups.get(cups.size() - 1);
            if (last.isCup() && last.size() > i && !last.hasLid()) {
                Cup target = findInnermostCup((Cup) last, i);
                if (!target.hasLid()) {
                    target.setInnerCup(cup);
                    if (visible) cup.makeVisible();
                    reposition();
                    lastOperationOk = true;
                    return;
                }
            }
        }
        if (totalHeight() + cup.height() <= maxHeight) {
            cups.add(cup);
            if (visible) cup.makeVisible();
            reposition();
            lastOperationOk = true;
        } else {
            showMessage("Excede altura máxima");
            lastOperationOk = false;
        }
    }
    /**
     * Elimina la taza más reciente de la torre.
     * Si tiene inner cups, elimina la más interna sin tapa.
     * No permite eliminar tazas con tapa.
     */
    public void popCup() {
        if (cups.isEmpty()) { 
            showMessage("No hay tazas para eliminar"); 
            lastOperationOk = false; 
            return; 
        }
        Stackable last = cups.get(cups.size() - 1);
        Cup lastCup = (Cup) last;
        if (lastCup.hasInnerCup() && !lastCup.hasLid()) {
            Cup innermost = findInnermostCup(lastCup, 0);
            if (innermost.hasLid()) { showMessage("La taza tiene tapa"); lastOperationOk = false; return; }
            removeFromCup(lastCup, innermost.size());
            reposition();
            lastOperationOk = true;
            return;
        }
        if (last.hasLid()) {
            showMessage("La taza tiene tapa"); 
            lastOperationOk = false; 
            return; 
        }
        cups.remove(cups.size() - 1);
        last.makeInvisible();
        reposition();
        lastOperationOk = true;
    }
    /**
     * Elimina la taza de tamaño indicado de la torre.
     * Busca tanto en nivel raíz como en tazas anidadas.
     * @param size tamaño de la taza a eliminar.
     */
    public void removeCup(int size) {
        for (int i = 0; i < cups.size(); i++) {
            if (cups.get(i).size() == size) {
                Stackable c = cups.remove(i);
                c.makeInvisible();
                reposition();
                lastOperationOk = true;
                return;
            }
        }
        for (Stackable s : cups) {
            if (removeFromCup((Cup) s, size)) {
                reposition();
                lastOperationOk = true;
                return;
            }
        }
        showMessage("No existe taza de tamaño " + size);
        lastOperationOk = false;
    }
    /**
     * Agrega una tapa a la taza más reciente sin tapa que no cause sobreposición.
     */
    public void pushLid() {
        if (cups.isEmpty()) { showMessage("No hay tazas"); lastOperationOk = false; return; }
        for (int i = cups.size() - 1; i >= 0; i--) {
            Stackable cup = cups.get(i);
            if (!cup.hasLid() && canCover((Cup) cup)) {
                if (totalHeight() + 1 > maxHeight) { 
                    showMessage("Excede altura máxima"); 
                    lastOperationOk = false; 
                    return; 
                }
                cup.addLid();
                reposition();
                lastOperationOk = true;
                return;
            }
        }
        showMessage("No es posible agregar tapa sin sobreposición");
        lastOperationOk = false;
    }
    /**
     * Elimina la tapa más reciente de la torre.
     */  
    public void popLid() {
        for (int i = cups.size() - 1; i >= 0; i--) {
            if (cups.get(i).hasLid()) {
                cups.get(i).removeLid();
                reposition();
                lastOperationOk = true;
                return;
            }
        }
        showMessage("No hay tapas para eliminar");
        lastOperationOk = false;
    }
    /**
     * Elimina la tapa de la taza con el tamaño indicado.
     * @param size tamaño de la taza a destapar.
     */
    public void removeLid(int size) {
        Stackable c = findCup(size);
        if (c != null) {
            if (c.hasLid()) { 
                c.removeLid(); 
                reposition(); 
                lastOperationOk = true; 
            }
            else {
                showMessage("Esa taza no tiene tapa"); 
                lastOperationOk = false; 
            }
        } 
        else { 
            showMessage("No existe taza de tamaño " + size); 
            lastOperationOk = false; 
        }
    }
    /**
     * Ordena las tazas de mayor a menor altura, quedando la menor en la cima.
     */
    public void orderTower() {
        cups.sort((a, b) -> b.height() - a.height());
        reposition();
        lastOperationOk = true;
    }
    /**
     * Invierte el orden actual de las tazas en la torre.
     */
    public void reverseTower() {
        Collections.reverse(cups);
        reposition();
        lastOperationOk = true;
    }
    /**
     * Intercambia dos objetos en la torre por tipo y número.
     * Si alguno no existe, ok() retorna false.
     * Si tras el swap una taza queda flotando sobre otra más grande, se anida.
     * @param o1 primer objeto, ej: {"cup","4"}.
     * @param o2 segundo objeto, ej: {"cup","2"}.
     */
    public void swap(String[] o1, String[] o2) {
        Cup c1 = (Cup) findCup(Integer.parseInt(o1[1]));
        Cup c2 = (Cup) findCup(Integer.parseInt(o2[1]));
        if (c1 == null || c2 == null) { 
            lastOperationOk = false;
            return; }

        ArrayList<Cup> list1 = findParentList(c1);
        ArrayList<Cup> list2 = findParentList(c2);
        int i1 = list1.indexOf(c1);
        int i2 = list2.indexOf(c2);
        list1.set(i1, c2);
        list2.set(i2, c1);

        for (int k = cups.size() - 1; k > 0; k--) {
            Stackable upper = cups.get(k);
            Stackable lower = cups.get(k - 1);
            if (lower.isCup() && lower.size() > upper.size() && !lower.hasLid()) {
                cups.remove(k);
                findInnermostCup((Cup) lower, upper.size()).setInnerCup((Cup) upper);
            }
        }
        reposition();
        lastOperationOk = true;
    }
    /**
     * Tapa automáticamente todas las tazas de la torre.
     * Si alguna causa sobreposición, no realiza ningún cambio y ok() retorna false.
     */
    public void cover() {
        if (cups.isEmpty()) { 
            showMessage("No hay tazas"); 
            lastOperationOk = false; 
            return; 
        }
        for (Stackable cup : cups) {
            if (!canCover((Cup) cup)) {
                showMessage("No es posible cubrir sin sobreposición");
                lastOperationOk = false;
                return;
            }
        }
        for (Stackable cup : cups) coverCup((Cup) cup);
        reposition();
        lastOperationOk = true;
    }
     /**
     * Retorna la altura total de los elementos apilados en la torre.
     * @return altura total en unidades.
     */
    public int height() {
        return totalHeight();
    }
    /**
     * Retorna los tamaños de las tazas que tienen tapa, ordenados de menor a mayor.
     * @return arreglo con los tamaños de tazas tapadas.
     */
    public int[] lidedCups() {
        ArrayList<Integer> covered = new ArrayList<>();
        for (Stackable c : cups) collectLidedCups((Cup) c, covered);
        for (int i = 0; i < covered.size() - 1; i++)
            for (int j = i + 1; j < covered.size(); j++)
                if (covered.get(i) > covered.get(j)) {
                    int temp = covered.get(i); covered.set(i, covered.get(j)); covered.set(j, temp);
                }
        int[] result = new int[covered.size()];
        for (int i = 0; i < covered.size(); i++) result[i] = covered.get(i);
        return result;
    }
    /**
     * Retorna todos los elementos de la torre de base a cima con tipo y número.
     * Ej: {{"cup","4"},{"lid","4"}}.
     * @return matriz con tipo y número de cada elemento.
     */
    public String[][] stackingItems() {
        int total = 0;
        for (Stackable c : cups) total += countItems((Cup) c);
        String[][] data = new String[total][2];
        int index = 0;
        for (Stackable c : cups) index = fillItems(data, index, (Cup) c);
        return data;
    }
    /**
     * Retorna el par de objetos cuyo intercambio reduciría la altura de la torre.
     * Si no existe tal par, retorna un arreglo vacío.
     * @return par de objetos a intercambiar, ej: {{"cup","4"},{"cup","2"}}.
     */
    public String[][] swapToReduce() {
        for (int i = 0; i < cups.size() - 1; i++)
            for (int j = i + 1; j < cups.size(); j++)
                if (cups.get(i).height() < cups.get(j).height())
                    return new String[][]{
                        {"cup", String.valueOf(cups.get(j).size())},
                        {"cup", String.valueOf(cups.get(i).size())}
                    };
        return new String[0][0];
    }
     /**
     * Hace visible la torre en el canvas gráfico.
     */
    public void makeVisible() {
        Canvas.getCanvas();
        visible = true;
        drawGrid();
        drawAxes();
        reposition();
        for (Stackable cup : cups) cup.makeVisible();
    }
    /**
     * Oculta la torre del canvas gráfico.
     */
    public void makeInvisible() {
        visible = false;
        axisX.makeInvisible();
        axisY.makeInvisible();
        for (Rectangle r : grid) r.makeInvisible();
        for (Stackable c : cups) c.makeInvisible();
    }
    /**
     * Oculta la torre y limpia todos sus elementos.
     */
    public void exit() { 
        makeInvisible(); cups.clear(); 
    }
    /**
     * Retorna true si la última operación fue exitosa, false en caso contrario.
     * @return resultado de la última operación.
     */
    public boolean ok() { 
        return lastOperationOk; 
    }
    /** Inicializa los atributos de la torre. */
    private void initialize() {
        cups = new ArrayList<>();
        grid = new ArrayList<>();
        visible = false;
        lastOperationOk = false;
        fixed = false;
        axisX = new Rectangle();
        axisY = new Rectangle();
    }
    /** Configura los ejes visuales de la torre. */
    private void setupAxes() {
        axisX.changeSize(3, towerWidth * scale);
        axisX.moveHorizontal(margin - RECT_INIT_X);
        axisX.moveVertical(margin + maxHeight * scale - RECT_INIT_Y);
        axisY.changeSize(maxHeight * scale, 3);
        axisY.moveHorizontal(margin - RECT_INIT_X);
        axisY.moveVertical(margin - RECT_INIT_Y);
    }
    /** Busca una taza por tamaño en nivel raíz y en inner cups. */
    private Stackable findCup(int size) {
        for (Stackable c : cups) {
            if (c.size() == size) 
            return c;
            Stackable found = findCupInner((Cup) c, size);
            if (found != null) 
            return found;
        }
        return null;
    }
    /** Busca recursivamente una taza por tamaño dentro de una cup. */
    private Stackable findCupInner(Cup cup, int size) {
        for (Cup inner : cup.getInnerCups()) {
            if (inner.size() == size)
            return inner;
            Stackable found = findCupInner(inner, size);
            if (found != null) 
            return found;
        }
        return null;
    }
    /** Retorna la cup más interna que pueda contener una taza de tamaño i. */
    private Cup findInnermostCup(Cup cup, int i) {
        if (cup.hasInnerCup() && cup.getInnerCup().size() > i) {
            return findInnermostCup(cup.getInnerCup(), i);
        }
        return cup;
    }
    /** Elimina recursivamente una taza de tamaño dado dentro de una cup padre. */
    private boolean removeFromCup(Cup parent, int size) {
        ArrayList<Cup> inners = parent.getInnerCups();
        for (int i = 0; i < inners.size(); i++) {
            if (inners.get(i).size() == size) {
                inners.get(i).makeInvisible();
                inners.remove(i);
                return true;
            }
            if (removeFromCup(inners.get(i), size)) 
            return true;
        }
        return false;
    }
    /** Verifica si una cup puede recibir tapa sin causar sobreposición. */
    private boolean canCover(Cup cup) {
        int innerTotal = 0;
        for (Cup inner : cup.getInnerCups()) {
            innerTotal += inner.height();
            if (inner.hasLid()) innerTotal += 1;
            if (!canCover(inner)) 
            return false;
        }
        if (!cup.getInnerCups().isEmpty()) {
            int space = cup.height() - 1;
            if (innerTotal + cup.getInnerCups().size() > space) 
            return false;
        }
        return true;
    }
    /** Agrega tapa recursivamente a una cup y sus inner cups. */
    private void coverCup(Cup cup) {
        if (!cup.hasLid()) cup.addLid();
        for (Cup inner : cup.getInnerCups()) coverCup(inner);
    }
    /** Recolecta recursivamente los tamaños de tazas con tapa. */
    private void collectLidedCups(Cup c, ArrayList<Integer> covered) {
        if (c.hasLid()) covered.add(c.size());
        for (Cup inner : c.getInnerCups()) collectLidedCups(inner, covered);
    }
    /** Cuenta recursivamente el número de items en una cup incluyendo inner cups. */
    private int countItems(Cup c) {
        int total = 1;
        if (c.hasLid()) total++;
        for (Cup inner : c.getInnerCups()) total += countItems(inner);
        return total;
    }
    /** Llena la matriz de items recursivamente de base a cima. */
    private int fillItems(String[][] data, int index, Cup c) {
        data[index][0] = "cup";
        data[index][1] = String.valueOf(c.size());
        index++;
        for (Cup inner : c.getInnerCups()) index = fillItems(data, index, inner);
        if (c.hasLid()) {
            data[index][0] = "lid";
            data[index][1] = String.valueOf(c.size());
            index++;
        }
        return index;
    }
    /** Retorna la lista que contiene directamente la cup objetivo. */
    private ArrayList<Cup> findParentList(Cup target) {
        for (Stackable s : cups) {
            if (s == target) return (ArrayList<Cup>)(ArrayList<?>) cups;
            ArrayList<Cup> found = findParentListIn((Cup) s, target);
            if (found != null) return found;
        }
        return (ArrayList<Cup>)(ArrayList<?>) cups;
    }
    /** Busca recursivamente la lista padre de una cup dentro de otra cup. */
    private ArrayList<Cup> findParentListIn(Cup current, Cup target) {
        for (Cup inner : current.getInnerCups()) {
            if (inner == target) 
            return current.getInnerCups();
            ArrayList<Cup> found = findParentListIn(inner, target);
            if (found != null) 
            return found;
        }
        return null;
    }
    /** Calcula la altura total de todos los elementos en la torre. */
    private int totalHeight() {
        int total = 0;
        for (Stackable c : cups) {
            total += c.height();
            if (c.hasLid()) total += 1;
        }
        return total;
    }
    /** Recalcula y actualiza las posiciones visuales de todas las tazas. */
    private void reposition() {
        int accumulated = 0;
        for (Stackable cup : cups) {
            int yPixel = margin + maxHeight * scale - accumulated * scale - cup.height() * scale;
            int xPixel = margin + ((towerWidth - cup.width()) / 2) * scale;
            cup.setPosition(xPixel, yPixel);
            accumulated += cup.height();
            if (cup.hasLid()) accumulated += 1;
        }
    }
    /** Dibuja los ejes visuales de la torre. */
    private void drawAxes() {
        axisX.changeColor("magenta"); axisY.changeColor("magenta");
        axisX.makeVisible(); axisY.makeVisible();
    }
    /** Dibuja la cuadrícula de fondo de la torre. */
    private void drawGrid() {
        for (Rectangle r : grid) r.makeInvisible();
        grid.clear();
        for (int i = 0; i <= maxHeight; i++) {
            Rectangle line = new Rectangle();
            line.changeSize(1, towerWidth * scale);
            line.changeColor("black");
            line.moveHorizontal(margin - RECT_INIT_X);
            line.moveVertical(margin + i * scale - RECT_INIT_Y);
            line.makeVisible();
            grid.add(line);
        }
        for (int i = 0; i <= towerWidth; i++) {
            Rectangle line = new Rectangle();
            line.changeSize(maxHeight * scale, 1);
            line.changeColor("black");
            line.moveHorizontal(margin + i * scale - RECT_INIT_X);
            line.moveVertical(margin - RECT_INIT_Y);
            line.makeVisible();
            grid.add(line);
        }
    }
    /** Muestra un mensaje al usuario si la torre es visible. */
    private void showMessage(String msg) {
        if (visible) JOptionPane.showMessageDialog(null, msg);
    }
}
