package tower;
import shapes.*;
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
     * Crea una torre vacía con dimensiones indicadas.
     * @param width ancho máximo de la torre en unidades.
     * @param maxHeight altura máxima de la torre en unidades.
     */
    public Tower(int width, int maxHeight) {
        this.towerWidth = width;
        this.maxHeight = maxHeight;
        initialize();
        setupAxes();
    }
    /**
     * Crea una torre con n tazas de tamaños 1 a n apiladas de menor a mayor.
     * @param n número de tazas a crear.
     */
    public Tower(int n) {
        this.towerWidth = 2 * n - 1;
        this.maxHeight = n * n + n;
        initialize();
        setupAxes();
        for (int i = 1; i <= n; i++) cups.add(new Cup(i));
    }
    /**
     * Agrega una taza normal de tamaño i a la torre.
     * Si cabe dentro de la última taza sin tapa, se anida.
     * Rechaza duplicados, tazas que excedan el ancho o altura máxima.
     * @param i tamaño de la taza a agregar.
     */
    public void pushCup(int i) {
        if (fixed || 2 * i - 1 > towerWidth || findCup(i) != null) {
            fail("No es posible agregar la taza " + i);
            return;
        }
        Cup cup = new Cup(i);
        if (!tryNest(cup, i)) {
            if (totalHeight() + cup.height() > maxHeight) {
                fail("Excede altura maxima");
                return;
            }
            cups.add(cup);
            if (visible) cup.makeVisible();
        }
        success();
    }
    /**
     * Agrega una taza del tipo indicado a la torre.
     * Tipos válidos: "normal", "opener", "hierarchical".
     * @param type tipo de la taza.
     * @param i tamaño de la taza a agregar.
     */
    public void pushCup(String type, int i) {
        if (fixed || 2 * i - 1 > towerWidth || findCup(i) != null) {
            fail("No es posible agregar la taza " + i);
            return;
        }
        Cup cup;
        switch (type.toLowerCase()) {
            case "opener":       cup = new OpenerCup(i); break;
            case "hierarchical": cup = new HierarchicalCup(i); break;
            default:             cup = new Cup(i); break;
        }
        cup.onPush(cups);
        if (!tryNest(cup, i)) {
            if (totalHeight() + cup.height() > maxHeight) {
                fail("Excede altura maxima");
                return;
            }
            cup.insertIntoCups(cups);
        }
        if (visible) cup.makeVisible();
        success();
    }
    /**
     * Elimina la taza más reciente de la torre o la inner cup más interna.
     * No permite eliminar tazas con tapa ni tazas fijas.
     */
    public void popCup() {
        if (cups.isEmpty()) {
            fail("No hay tazas para eliminar");
            return;
        }
        Stackable last = cups.get(cups.size() - 1);
        if (last.isFixed()) {
            fail("La taza jerarquica esta fija en el fondo");
            return;
        }
        if (!tryPopInner((Cup) last)) {
            if (last.hasLid()) {
                fail("La taza tiene tapa");
                return;
            }
            cups.remove(cups.size() - 1);
            last.makeInvisible();
            success();
        }
    }
    /**
     * Elimina la taza de tamaño indicado de la torre.
     * No permite eliminar tazas fijas.
     * @param size tamaño de la taza a eliminar.
     */
    public void removeCup(int size) {
        for (int i = 0; i < cups.size(); i++) {
            if (cups.get(i).size() == size) {
                if (cups.get(i).isFixed()) {
                    fail("La taza jerarquica esta fija en el fondo");
                    return;
                }
                cups.remove(i).makeInvisible();
                success();
                return;
            }
        }
        for (Stackable s : cups) {
            if (removeFromCup((Cup) s, size)) { 
                success(); 
                return; 
            }
        }
        fail("No existe taza de tamaño " + size);
    }
    /**
     * Agrega una tapa normal a la taza de tamaño i.
     * No permite agregar si hay una CrazyLid debajo de la taza.
     * @param i tamaño de la taza a tapar.
     */    public void pushLid(int i) {
        Stackable c = findCup(i);
        int idxC = cups.indexOf(c);
        if (idxC > 0 && !cups.get(idxC - 1).isCup() && ((Lid) cups.get(idxC - 1)).isCrazy()) {
            fail("La taza con CrazyLid no puede recibir tapa");
            return;
        }
        if (c == null || c.hasLid() || !coverIfPossible((Cup) c, false) || totalHeight() + 1 > maxHeight) {
            fail("No es posible agregar tapa a la taza " + i);
            return;
        }
        c.addLid();
        success();
    }
    /**
     * Agrega una tapa del tipo indicado a la taza de tamaño i.
     * Tipos válidos: "normal", "fearful", "crazy", "sticky".
     * @param type tipo de la tapa.
     * @param i tamaño de la taza a tapar.
     */
    public void pushLid(String type, int i) {
        Stackable c = findCup(i);
        int idxC = cups.indexOf(c);
        if (idxC > 0 && !cups.get(idxC - 1).isCup() && ((Lid) cups.get(idxC - 1)).isCrazy()) {
            fail("La taza con CrazyLid no puede recibir tapa");
            return;
        }
        if (c == null || c.hasLid() || !coverIfPossible((Cup) c, false) || totalHeight() + 1 > maxHeight) {
            fail("No es posible agregar tapa a la taza " + i);
            return;
        }
        Lid newLid;
        switch (type.toLowerCase()) {
            case "fearful": newLid = new FearfulLid(i, c.color(), i - 1); 
                break;
            case "crazy":   newLid = new CrazyLid(i, c.color()); 
                break;
            case "sticky":  newLid = new StickyLid(i, c.color()); 
                break;
            default:        newLid = new Lid(i, c.color()); 
                break;
        }
        if (!newLid.canEnter(findCup(newLid.getCompanionSize()) != null)) {
            fail("La tapa no puede entrar");
            return;
        }
        if (newLid.isCrazy()) {
            cups.add(cups.indexOf(c), newLid);
            if (visible) newLid.makeVisible();
        } else {
            ((Cup) c).addSpecificLid(newLid);
        }
        success();
    }
    /**
     * Elimina la tapa más reciente de la torre.
     * Verifica si la tapa puede salir según su tipo.
     */
    public void popLid() {
        for (int i = cups.size() - 1; i >= 0; i--) {
            if (!cups.get(i).isCup() && ((Lid) cups.get(i)).isCrazy()) {
                cups.get(i).makeInvisible();
                cups.remove(i);
                success();
                return;
            }
            if (cups.get(i).hasLid()) {
                Lid currentLid = ((Cup) cups.get(i)).getLid();
                boolean isCovering = (findCup(currentLid.getCompanionSize()) == cups.get(i));
                if (!currentLid.canExit(isCovering)) {
                    fail(currentLid.exitMessage());
                    return;
                }
                cups.get(i).removeLid();
                success();
                return;
            }
        }
        fail("No hay tapas para eliminar");
    }
    /**
     * Elimina la tapa de la taza con el tamaño indicado.
     * Verifica si la tapa puede salir según su tipo.
     * @param size tamaño de la taza a destapar.
     */
    public void removeLid(int size) {
        for (int i = 0; i < cups.size(); i++) {
            if (!cups.get(i).isCup() && cups.get(i).size() == size && ((Lid) cups.get(i)).isCrazy()) {
                cups.get(i).makeInvisible();
                cups.remove(i);
                success();
                return;
            }
        }
        Stackable c = findCup(size);
        if (c == null) { fail("No existe taza de tamaño " + size); 
            return; 
        }
        if (!c.hasLid()) { 
            fail("Esa taza no tiene tapa"); 
            return; 
        }
        Lid currentLid = ((Cup) c).getLid();
        boolean isCovering = (findCup(currentLid.getCompanionSize()) == c);
        if (!currentLid.canExit(isCovering)) {
            fail(currentLid.exitMessage());
            return;
        }
        ((Cup) c).removeLid();
        success();
    }
    /**
     * Ordena las tazas de mayor a menor altura, quedando la menor en la cima.
     */
    public void orderTower() { 
        cups.sort((a, b) -> b.height() - a.height()); success(); 
    }
    /**
     * Invierte el orden actual de las tazas en la torre.
     */
    public void reverseTower() { 
        Collections.reverse(cups); success(); 
    }
    /**
     * Intercambia dos objetos en la torre por tipo y número.
     * Si tras el swap una taza queda sobre otra más grande, se anida.
     * @param o1 primer objeto, ej: {"cup","4"}.
     * @param o2 segundo objeto, ej: {"cup","2"}.
     */
    public void swap(String[] o1, String[] o2) {
        Cup c1 = (Cup) findCup(Integer.parseInt(o1[1]));
        Cup c2 = (Cup) findCup(Integer.parseInt(o2[1]));
        if (c1 == null || c2 == null) { lastOperationOk = false; return; }
        ArrayList<Cup> list1 = findParentList(c1);
        ArrayList<Cup> list2 = findParentList(c2);
        list1.set(list1.indexOf(c1), c2);
        list2.set(list2.indexOf(c2), c1);
        for (int k = cups.size() - 1; k > 0; k--) {
            Stackable upper = cups.get(k);
            Stackable lower = cups.get(k - 1);
            if (lower.isCup() && lower.size() > upper.size() && !lower.hasLid()) {
                cups.remove(k);
                findInnermostCup((Cup) lower, upper.size()).setInnerCup((Cup) upper);
            }
        }
        success();
    }
    /**
     * Tapa automáticamente todas las tazas de la torre.
     * Si alguna causa sobreposición, no realiza ningún cambio.
     */
    public void cover() {
        if (cups.isEmpty()) { fail("No hay tazas"); return; }
        for (Stackable cup : cups)
            if (!coverIfPossible((Cup) cup, true)) {
                fail("No es posible cubrir sin sobreposición");
                return;
            }
        success();
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
        for (Stackable c : cups) {
            if (c.isCup()) traverseCups((Cup) c, null, covered, 0);
        }
        covered.sort(Integer::compareTo);
        int[] result = new int[covered.size()];
        for (int i = 0; i < covered.size(); i++) result[i] = covered.get(i);
        return result;
    }
    /**
     * Retorna todos los elementos de la torre de base a cima con tipo y número.
     * @return matriz con tipo y número de cada elemento.
     */
    public String[][] stackingItems() {
        int total = 0;
        for (Stackable c : cups) total += traverseCups((Cup) c, null, null, 0);
        String[][] data = new String[total][2];
        int index = 0;
        for (Stackable c : cups) index = traverseCups((Cup) c, data, null, index);
        return data;
    }
    /**
     * Retorna el par de objetos cuyo intercambio reduciría la altura de la torre.
     * Si no existe tal par, retorna un arreglo vacío.
     * @return par de objetos a intercambiar.
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
    /** Hace visible la torre en el canvas gráfico. */
    public void makeVisible() {
        Canvas.getCanvas();
        visible = true;
        drawGrid();
        drawAxes();
        reposition();
        for (Stackable cup : cups) cup.makeVisible();
    }
    /** Oculta la torre del canvas gráfico. */
    public void makeInvisible() {
        visible = false;
        axisX.makeInvisible();
        axisY.makeInvisible();
        for (Rectangle r : grid) r.makeInvisible();
        for (Stackable c : cups) c.makeInvisible();
    }
    /** Oculta la torre y limpia todos sus elementos. */
    public void exit() { 
        makeInvisible(); 
        cups.clear(); 
    }   
    /**
     * Retorna true si la última operación fue exitosa, false en caso contrario.
     * @return resultado de la última operación.
     */
    public boolean ok() { 
        return lastOperationOk; 
    }
    /** Marca la última operación como exitosa y repositiona los elementos. */
    private void success() { 
        reposition(); 
        lastOperationOk = true;
    }
    /**
     * Marca la última operación como fallida y muestra un mensaje.
     * @param msg mensaje de error a mostrar.
     */
    private void fail(String msg) { 
        showMessage(msg); 
        lastOperationOk = false; 
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
    /**
     * Busca una taza por tamaño en nivel raíz y en inner cups.
     * @param size tamaño a buscar.
     * @return taza encontrada o null.
     */
    private Stackable findCup(int size) { 
        return findCupIn(cups, size); 
    }
    /**
     * Busca recursivamente una taza por tamaño en la lista indicada.
     * @param list lista donde buscar.
     * @param size tamaño a buscar.
     * @return taza encontrada o null.
     */
    private Stackable findCupIn(List<? extends Stackable> list, int size) {
        for (Stackable c : list) {
            if (c.isCup() && c.size() == size) return c;
            if (c.isCup()) {
                Stackable found = findCupIn(((Cup) c).getInnerCups(), size);
                if (found != null) return found;
            }
        }
        return null;
    }
    /**
     * Retorna la cup más interna que pueda contener una taza de tamaño i.
     * @param cup taza contenedora.
     * @param i tamaño de la taza a anidar.
     * @return cup más interna disponible.
     */
    private Cup findInnermostCup(Cup cup, int i) {
        if (cup.hasInnerCup() && cup.getInnerCup().size() > i)
            return findInnermostCup(cup.getInnerCup(), i);
        return cup;
    }
    /**
     * Retorna la lista que contiene directamente la cup objetivo.
     * @param target taza a buscar.
     * @return lista padre de la taza.
     */
    private ArrayList<Cup> findParentList(Cup target) {
        ArrayList<Cup> found = findParentIn((ArrayList<Cup>)(ArrayList<?>) cups, target);
        return found != null ? found : (ArrayList<Cup>)(ArrayList<?>) cups;
    }
    /**
     * Busca recursivamente la lista padre de una cup dentro de otra cup.
     * @param list lista donde buscar.
     * @param target taza a buscar.
     * @return lista padre o null si no se encuentra.
     */
    private ArrayList<Cup> findParentIn(ArrayList<Cup> list, Cup target) {
        for (Cup c : list) {
            if (c == target) 
            return list;
            ArrayList<Cup> found = findParentIn(c.getInnerCups(), target);
            if (found != null) 
            return found;
        }
        return null;
    }
    /**
     * Intenta anidar la taza dentro de la última taza de la torre.
     * @param cup taza a anidar.
     * @param i tamaño de la taza.
     * @return true si se anidó exitosamente, false si no fue posible.
     */
    private boolean tryNest(Cup cup, int i) {
        if (cups.isEmpty()) 
            return false;
        Stackable last = cups.get(cups.size() - 1);
        if (!last.isCup() || last.size() <= i || last.hasLid())
            return false;
        Cup target = findInnermostCup((Cup) last, i);
        if (target.hasLid()) 
            return false;
        target.setInnerCup(cup);
        if (visible) cup.makeVisible();
            return true;
    }
    /**
     * Intenta eliminar la inner cup más interna de una taza.
     * @param cup taza contenedora.
     * @return true si se eliminó una inner cup, false si no había ninguna.
     */
    private boolean tryPopInner(Cup cup) {
        if (!cup.hasInnerCup() || cup.hasLid()) 
            return false;
        Cup innermost = findInnermostCup(cup, 0);
        if (innermost.hasLid()) { fail("La taza tiene tapa"); 
            return true; }
        removeFromCup(cup, innermost.size());
        success();
        return true;
    }
    /**
     * Elimina recursivamente una taza de tamaño dado dentro de una cup padre.
     * @param parent taza contenedora.
     * @param size tamaño de la taza a eliminar.
     * @return true si se encontró y eliminó la taza.
     */
    private boolean removeFromCup(Cup parent, int size) {
        ArrayList<Cup> inners = parent.getInnerCups();
        for (int i = 0; i < inners.size(); i++) {
            if (inners.get(i).size() == size) {
                inners.get(i).makeInvisible();
                inners.remove(i);
                return true;
            }
            if (removeFromCup(inners.get(i), size)) return true;
        }
        return false;
    }
    /**
     * Verifica si una cup puede recibir tapa sin causar sobreposición.
     * Si doCover es true, agrega la tapa a todas las tazas que puedan recibirla.
     * @param cup taza a verificar.
     * @param doCover si true agrega tapas, si false solo verifica.
     * @return true si es posible tapar sin sobreposición.
     */
    private boolean coverIfPossible(Cup cup, boolean doCover) {
        int innerTotal = 0;
        for (Cup inner : cup.getInnerCups()) {
            innerTotal += inner.height() + (inner.hasLid() ? 1 : 0);
            if (!coverIfPossible(inner, doCover))
                return false;
        }
        if (!cup.getInnerCups().isEmpty() && innerTotal + cup.getInnerCups().size() > cup.height() - 1)
            return false;
        if (doCover && !cup.hasLid()) cup.addLid();
            return true;
    }
    /**
     * Recorre recursivamente una cup recolectando información de elementos.
     * @param c taza a recorrer.
     * @param data matriz a llenar con tipo y tamaño, o null si solo se cuenta.
     * @param covered lista donde agregar tamaños de tazas tapadas, o null.
     * @param index índice actual en la matriz data.
     * @return índice actualizado tras recorrer la taza.
     */
    private int traverseCups(Cup c, String[][] data, ArrayList<Integer> covered, int index) {
        if (data != null) { data[index][0] = "cup"; data[index][1] = String.valueOf(c.size()); }
        index++;
        if (covered != null && c.hasLid()) covered.add(c.size());
        for (Cup inner : c.getInnerCups()) index = traverseCups(inner, data, covered, index);
        if (c.hasLid()) {
            if (data != null) { data[index][0] = "lid"; data[index][1] = String.valueOf(c.size()); }
            index++;
        }
        return index;
    }
    /**
     * Calcula la altura total de todos los elementos en la torre.
     * @return altura total en unidades.
     */
    private int totalHeight() {
        int total = 0;
        for (Stackable c : cups) total += c.height() + (c.hasLid() ? 1 : 0);
        return total;
    }
    /** Recalcula y actualiza las posiciones visuales de todas las tazas. */
    private void reposition() {
        int accumulated = 0;
        for (int i = 0; i < cups.size(); i++) {
            Stackable cup = cups.get(i);
            if (i > 0 && cup.size() > cups.get(i - 1).size() && !cups.get(i - 1).hasLid())
                accumulated = accumulated - 1;
            int yPixel = margin + (maxHeight - accumulated - cup.height()) * scale;
            int xPixel = margin + ((towerWidth - cup.width()) / 2) * scale;
            cup.setPosition(xPixel, yPixel);
            accumulated = accumulated + cup.height();
            if (cup.hasLid()) accumulated = accumulated + 1;
        }
    }
    /** Dibuja los ejes visuales de la torre. */
    private void drawAxes() {
        axisX.changeColor("magenta");
        axisY.changeColor("magenta");
        axisX.makeVisible();
        axisY.makeVisible();
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
    /**
     * Agrega una taza sin anidamiento a la torre.
     * @param i tamaño de la taza a agregar.
     */
    public void pushCupNoNest(int i) {
        Cup cup = new Cup(i);
        if (totalHeight() + cup.height() > maxHeight)
            return;
        cups.add(cup);
        if (visible) cup.makeVisible();
        success();
    }
    /**
     * Muestra un mensaje al usuario si la torre es visible.
     * @param msg mensaje a mostrar.
     */
    private void showMessage(String msg) {
        if (visible) JOptionPane.showMessageDialog(null, msg);
    }
}