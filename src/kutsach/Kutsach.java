package kutsach;

import java.util.*;
import java.util.LinkedList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

class Kutsach {

    static int count;
    static int width;
    static int height;

    public static void main(String[] args) {
        count = 0;
        Figures figures = new Figures();
        width = (int) figures.getSizeShowFrame().getWidth();
        height = (int) figures.getSizeShowFrame().getHeight();

    }
}

class Figures extends Frame implements Observer, ActionListener, ItemListener, ComponentListener {

    private LinkedList LL = new LinkedList();
    private final int MAX_COL_FIGURES = 15;
    private final String[] FIGURE_STRING_LIST = {"круг", "овал", "прямоугольник", "квадрат", "треугольник"};
    private final String[] SPEED_STRING_LIST = {"1", "6", "12", "20", "60"};
    private final String[] FIGURE_LIST;
    private static Color color;
    private JFrame f;
    private JButton b;
    private JButton b_select_color;
    private JTextField tf_figure;
    private JTextField tf_figure_change_speed;
    private JComboBox cb_speed;
    private JComboBox number_of_figure;
    private JButton b_apply;
    int choice = 0;
    static int width;
    static int height;
    int x;
    int y;
    static int times = 0;

    Figures() {
        this.addWindowListener(new WindowAdapter2());
        this.setBackground(Color.white);
        f = new JFrame();
        f.setSize(new Dimension(1400, 100));
        f.setLayout(new GridLayout());
        f.addWindowListener(new WindowAdapter2());

        b = new JButton("Пуск");
        b.addActionListener(this);
        f.add(b);

        FIGURE_LIST = new String[MAX_COL_FIGURES + 1];
        color = Color.black;

        tf_figure = new JTextField("Фигура");
        f.add(tf_figure);

        b_select_color = new JButton("Цвет:диалог");
        b_select_color.addActionListener(this);
        f.add(b_select_color);

        number_of_figure = new JComboBox();
        number_of_figure.setActionCommand("Выбор");
        number_of_figure.addActionListener(this);
        f.add(number_of_figure);

        cb_speed = new JComboBox(SPEED_STRING_LIST);
        cb_speed.setActionCommand("Скорость");
        cb_speed.addActionListener(this);
        f.add(cb_speed);

        tf_figure_change_speed = new JTextField("Смена скорости");
        f.add(tf_figure_change_speed);

        b_apply = new JButton("Применить");
        b_apply.addActionListener(this);
        f.add(b_apply);

        f.setVisible(true);
        this.setSize(800, 300);
        this.setVisible(true);
        this.setLocation(300, 400);
    }

    static Color getDefaultFiguresColor() {
        return color;
    }

    public void update(Observable o, Object arg) {
        repaint();
    }

    public void paint(Graphics g) {
        if (!LL.isEmpty()) {
            for (Object LL1 : LL) {
                Figure figure = (Figure) LL1;
                g.setColor(figure.color);
                if (checkFigure()) {
                    if (figure.figurestr.equals("круг")) {
                        g.drawOval(figure.x, figure.y, 40, 40);
                    }
                    if (figure.figurestr.equals("овал")) {
                        g.drawOval(figure.x, figure.y, 40, 30);
                    }
                    if (figure.figurestr.equals("квадрат")) {
                        g.drawRect(figure.x, figure.y, 40, 40);
                    }
                    if (figure.figurestr.equals("прямоугольник")) {
                        g.drawRect(figure.x, figure.y, 40, 30);
                    }
                    if (figure.figurestr.equals("треугольник")) {
                        int[] xpoints = {figure.x, figure.x + 40, figure.x + 40};
                        int[] ypoints = {figure.y + 40, figure.y, figure.y + 40};
                        g.drawPolygon(xpoints, ypoints, 3);
                    }
                    g.drawString(String.valueOf(figure.num), figure.x, figure.y);
                }
            }
        }
    }

    Dimension getSizeShowFrame() {
        width = (int) this.getSize().getWidth();
        height = (int) this.getSize().getHeight();
        this.addComponentListener(this);
        return this.getSize();
    }

    public void componentResized(ComponentEvent e) {
        width = e.getComponent().getWidth();
        height = e.getComponent().getHeight();
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void itemStateChanged(ItemEvent iE) {
    }

    public void actionPerformed(ActionEvent aE) {
        String str = aE.getActionCommand();
        if (str.equals("Цвет:диалог")) {
            Figures.color = JColorChooser.showDialog(Figures.this, "Выбор цвета", Color.green);
        }

        if (str.equals("Пуск")) {
            if (checkFigure()) {
                if (LL.size() < MAX_COL_FIGURES) {
                    Figure figure = new Figure(Figures.color, this.tf_figure.getText(), Integer.parseInt((String) cb_speed.getSelectedItem()));
                    LL.add(figure);
                    figure.addObserver(this);
                    FIGURE_LIST[Kutsach.count] = String.valueOf(Kutsach.count);
                    times++;
                    number_of_figure.addItem((Object) Kutsach.count);
                } else {
                    System.out.println("Максимальное количество фигур");
                }
            }
        }

        if (str.equals("Выбор")) {
            choice = (Integer) number_of_figure.getSelectedItem();
        }

        if (str.equals("Применить")) {
            for (Object LL1 : LL) {
                Figure figure = (Figure) LL1;
                if (choice == figure.num) {
                    figure.speed = Integer.parseInt(tf_figure_change_speed.getText());
                }
            }
        }

        repaint();
    }

    Boolean checkFigure() {
        boolean res = false;
        for (int i = 0; i < FIGURE_STRING_LIST.length; i++) {
            if (tf_figure.getText().equals(FIGURE_STRING_LIST[i])) {
                res = true;
            }
        }
        return res;
    }

}

class Figure extends Observable implements Runnable {

    Thread thread;
    private boolean xplus; //delta
    private boolean yplus;
    int x, dx;
    int y, dy;
    String figurestr;
    Color color;
    int num;
    int speed;
    int times = 1;
    Random r;

    public Figure(Color color, String text, int speed) {
        r = new Random();
        xplus = true;
        yplus = true;
        x = 0;
        y = 20;
        this.color = color;
        this.speed = speed;
        Kutsach.count++;
        thread = new Thread(this, Kutsach.count + ":" + text + ":");
        figurestr = text;
        num = Kutsach.count;
        thread.start();
    }

    public void run() {
        while (true) {
            dx = r.nextInt(50) - 45;
            dy = r.nextInt(50) - 45;
            if (this.times == Figures.times) {
                x += dx;
                y += dy;
                Figures.times = 0;
            }
            if (x == Figures.width - 50) {
                xplus = false;
            }
            if (x == Figures.width - Figures.width) {
                xplus = true;
            }
            if (y == Figures.height - 40) {
                yplus = false;
            }
            if (y == Figures.height - Figures.height + 40) {
                yplus = true;
            }
            if (xplus) {
                x++;
            } else {
                x--;
            }
            if (yplus) {
                y++;
            } else {
                y--;
            }
            setChanged();
            notifyObservers(this);
            try {
                Thread.sleep(this.speed);
            } catch (InterruptedException e) {
            }
        }
    }
}

class WindowAdapter2 extends WindowAdapter {

    public void windowClosing(WindowEvent wE) {
        System.exit(0);
    }
}
