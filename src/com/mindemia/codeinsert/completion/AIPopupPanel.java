/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.codeinsert.completion;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.openide.util.Exceptions;

/**
 *
 * @author rickard
 */
public class AIPopupPanel {

    public static void showSuggestionPopup(JTextComponent component, int caretOffset, String suggestion) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel suggestionLabel = new JLabel("<html><pre>" + suggestion + "</pre></html>");
        panel.add(suggestionLabel, BorderLayout.CENTER);

        JPopupMenu popup = new JPopupMenu();
        popup.add(panel);

        suggestionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Document doc = component.getDocument();
                    doc.insertString(caretOffset, suggestion, null);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                popup.setVisible(false);
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                popup.setVisible(false);
            }
        });

        Point location;
        try {
            Rectangle2D caretCoords = component.modelToView2D(caretOffset);
            location = new Point((int) caretCoords.getX(), (int) (caretCoords.getY() + caretCoords.getHeight()));
        } catch (BadLocationException ex) {
            location = new Point(0, 0);
        }
        component.requestFocus();
        popup.show(component, location.x, location.y);
    }
}
