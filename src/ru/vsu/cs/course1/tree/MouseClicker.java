package ru.vsu.cs.course1.tree;


import ru.vsu.cs.course1.tree.demo.TreeDemoFrame;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseClicker implements MouseListener {


    private JPanel container;
    private JLabel label;
    private JPanel mainPanel;

    public MouseClicker(JPanel container, JLabel label, JPanel paintPanel) {
        this.container = container;
        this.label = label;
        this.mainPanel = paintPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            boolean visible = label.isVisible();
            label.setVisible(!visible);
            container.repaint();
            SwingUtils.setFixedSize(mainPanel, TreeDemoFrame.size.x, TreeDemoFrame.size.y);
        });


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
