package ru.vsu.cs.course1.tree;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import ru.vsu.cs.intBinaryTree;
import ru.vsu.cs.util.DrawUtils;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;

/**
 * Класс, выполняющий отрисовку дерева на Graphics
 */
public class BinaryTreePainter {

    public static boolean flag = true;
    public static int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;

    public static final int TREE_NODE_WIDTH = 70,
            TREE_NODE_HEIGHT = 30,
            HORIZONTAL_INDENT = 10,
            VERTICAL_INDENT = 50;

    public static final Font FONT = new Font("Microsoft Sans Serif", Font.PLAIN, 20);

    private static class NodeDrawResult {

        public int center;
        public int maxX;
        public int maxY;

        public NodeDrawResult(int center, int maxX, int maxY) {
            this.center = center;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }


    private static <T extends Comparable<T>> NodeDrawResult paint(DefaultBinaryTree.TreeNode<T> node,
                                                                  int x, int y, JPanel panel) {
        if (node == null) {
            return null;
        }


        NodeDrawResult leftResult = paint(node.getLeft(), x, y + (TREE_NODE_HEIGHT + VERTICAL_INDENT), panel);
        int rightX = (leftResult != null) ? leftResult.maxX : x + (TREE_NODE_WIDTH + HORIZONTAL_INDENT) / 2;
        NodeDrawResult rightResult = paint(node.getRight(), rightX, y + (TREE_NODE_HEIGHT + VERTICAL_INDENT), panel);
        int thisX;
        if (leftResult == null) {
            thisX = x;
        } else if (rightResult == null) {
            thisX = Math.max(x + (TREE_NODE_WIDTH + HORIZONTAL_INDENT) / 2, leftResult.center + HORIZONTAL_INDENT / 2);
        } else {
            thisX = (leftResult.center + rightResult.center) / 2 - TREE_NODE_WIDTH / 2;
        }

        JPanel container = new JPanel();
        container.setLayout(null);
        JPanel myPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics gr) {

                super.paintComponent(gr);
                Graphics2D gr2 = (Graphics2D) gr;

                Color color = node.getColor() == Color.BLACK ? Color.WHITE : node.getColor();
                if (node.getRoute())
                    color = Color.red;
                gr2.setColor(color);
                gr2.fillRect(1, 1, TREE_NODE_WIDTH, TREE_NODE_HEIGHT);
                gr2.setColor(Color.BLACK);
                gr2.drawRect(1, 1, TREE_NODE_WIDTH, TREE_NODE_HEIGHT);
                gr2.setColor(DrawUtils.getContrastColor(color));
                DrawUtils.drawStringInCenter(gr2, FONT, node.getValue().toString(), 1, 1, TREE_NODE_WIDTH, TREE_NODE_HEIGHT);
            }
        };

        double width = 0;
        double line = 0;
        if (leftResult != null) {
            line = Math.sqrt((leftResult.center - (thisX + TREE_NODE_WIDTH / 2)) * (leftResult.center - (thisX + TREE_NODE_WIDTH / 2)) +
                    (1 - VERTICAL_INDENT) * (1 - VERTICAL_INDENT));
        }
        if (rightResult != null) {
            line = Math.sqrt((rightResult.center - (thisX + TREE_NODE_WIDTH / 2)) * (rightResult.center - (thisX + TREE_NODE_WIDTH / 2)) +
                    (1 - VERTICAL_INDENT) * (1 - VERTICAL_INDENT));
        }
        width = Math.sqrt((line * line) - ((VERTICAL_INDENT - 1) * (VERTICAL_INDENT - 1)));


        JPanel linesPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D gr2 = (Graphics2D) g;
                if (leftResult != null) {
                    gr2.drawLine(thisX + TREE_NODE_WIDTH / 2, 1, leftResult.center,
                            VERTICAL_INDENT);
                }
                if (rightResult != null) {
                    gr2.drawLine(thisX + TREE_NODE_WIDTH / 2, 1, rightResult.center,
                            VERTICAL_INDENT);
                }
            }
        };

        int maxX = Math.max((leftResult == null) ? 0 : leftResult.maxX, (rightResult == null) ? 0 : rightResult.maxX);
        int maxY = Math.max((leftResult == null) ? 0 : leftResult.maxY, (rightResult == null) ? 0 : rightResult.maxY);


        panel.add(container);
        if (!Double.isNaN(width)) ;
        panel.add(linesPanel);
        if (node.getSum() != null) {
            JLabel label = new JLabel();
            label.setSize(100, 100);
            label.setText(node.getSum().toString());
            label.setVisible(false);
            label.setBounds(thisX, y - 20, 500, 20);
            container.addMouseListener(new MouseClicker(container, label, panel));
            panel.add(label);
        }
        container.add(myPanel);
        myPanel.setBounds(1, 1, TREE_NODE_WIDTH, TREE_NODE_HEIGHT);
        container.setBounds(thisX, y, TREE_NODE_WIDTH, TREE_NODE_HEIGHT);
        if (leftResult != null && rightResult != null) {
            width *= 2;
            if (leftResult != null)
                width += leftResult.center;
        }
        if (!(leftResult != null && rightResult != null)) {
            if (leftResult != null)
                width += leftResult.center;

            if (rightResult != null) {
                width += rightResult.center;
            }
        }


        linesPanel.setBounds(1, y + TREE_NODE_HEIGHT, (int) (width) + 2, Math.abs(VERTICAL_INDENT));


        return new NodeDrawResult(
                thisX + TREE_NODE_WIDTH / 2,
                Math.max(thisX + TREE_NODE_WIDTH + HORIZONTAL_INDENT, maxX),
                Math.max(y + TREE_NODE_HEIGHT, maxY)
        );


    }


    /**
     * Рисование дерева
     *
     * @param <T>
     * @param tree Дерево
     * @param gr   Graphics
     * @return Размеры картинки
     */
    public static <T extends Comparable<T>> Point paint(DefaultBinaryTree<T> tree, JPanel panel) {
        panel.removeAll();
        NodeDrawResult rootResult = paint(tree.getRoot(), HORIZONTAL_INDENT, HORIZONTAL_INDENT, panel);
        return new Point((rootResult == null) ? 0 : rootResult.maxX, (rootResult == null) ? 0 : rootResult.maxY + HORIZONTAL_INDENT);
    }

    /**
     * Сохранение изображения дерева в SVG-файл
     *
     * @param tree                  Двоичное дерево
     * @param filename              Имя файла
     * @param backgroundTransparent Оставлять ли прозрачным фон
     * @throws Exception Возможное исключение
     */
    public static <T extends Comparable<T>> void saveIntoFile(DefaultBinaryTree<T> tree, String filename, boolean backgroundTransparent)
            throws IOException {
        // первый раз рисуем, только чтобы размеры изображения определить
        SVGGraphics2D g2 = new SVGGraphics2D(1, 1);
        Point size = BinaryTreePainter.paint(tree, new JPanel());
        // второй раз рисуем непосредственно для сохранения
        g2 = new SVGGraphics2D((int) size.getX(), (int) size.getY());
        if (!backgroundTransparent) {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, (int) size.getX(), (int) size.getY());
        }
        BinaryTreePainter.paint(tree, new JPanel());

        SVGUtils.writeToSVG(new File(filename), g2.getSVGElement());
    }

    /**
     * Сохранение изображения дерева в SVG-файл (с белым фоном)
     *
     * @param tree     Двоичное дерево
     * @param filename Имя файла
     * @throws Exception Возможное исключение
     */
    public static <T extends Comparable<T>> void saveIntoFile(DefaultBinaryTree<T> tree, String filename)
            throws IOException {
        saveIntoFile(tree, filename, false);
    }

}
