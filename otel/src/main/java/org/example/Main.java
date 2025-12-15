package org.example;

import view.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Uygulamayı güvenli bir şekilde başlatır
        SwingUtilities.invokeLater(() -> {
            // Arayüzü (MainFrame) ekrana getirir
            new MainFrame().setVisible(true);
        });
    }
}