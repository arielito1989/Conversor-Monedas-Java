package com.miempresa.conversor.main;

import com.miempresa.conversor.ui.ConversorGUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ConversorGUI().setVisible(true);
        });
    }
}
