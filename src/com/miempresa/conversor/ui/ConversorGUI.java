package com.miempresa.conversor.ui;

import com.miempresa.conversor.service.ConversorService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ConversorGUI extends JFrame {

    private JComboBox<String> comboDesde;
    private JComboBox<String> comboHacia;
    private JTextField montoField;
    private JLabel resultadoLabel;

    private Map<String, Double> tasas;

    public ConversorGUI() {
        setTitle("Conversor de Monedas");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana

        inicializarComponentes();
        cargarMonedas();
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        comboDesde = new JComboBox<>();
        comboHacia = new JComboBox<>();
        montoField = new JTextField();
        resultadoLabel = new JLabel("Resultado: ");
        JButton btnConvertir = new JButton("Convertir");

        panel.add(new JLabel("Moneda de origen:"));
        panel.add(comboDesde);
        panel.add(new JLabel("Moneda de destino:"));
        panel.add(comboHacia);
        panel.add(new JLabel("Monto:"));
        panel.add(montoField);
        panel.add(btnConvertir);
        panel.add(resultadoLabel);

        add(panel);

        btnConvertir.addActionListener(e -> convertir());
    }

    private void cargarMonedas() {
        try {
            ConversorService service = new ConversorService();
            tasas = service.obtenerTasas("USD"); // usamos USD como base

            comboDesde.removeAllItems();
            comboHacia.removeAllItems();

            for (String moneda : tasas.keySet()) {
                comboDesde.addItem(moneda);
                comboHacia.addItem(moneda);
            }

            comboDesde.setSelectedItem("USD");
            comboHacia.setSelectedItem("EUR");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar monedas: " + e.getMessage());
        }
    }

    private void convertir() {
        try {
            String desde = comboDesde.getSelectedItem().toString();
            String hacia = comboHacia.getSelectedItem().toString();
            double monto = Double.parseDouble(montoField.getText());

            ConversorService service = new ConversorService();
            double resultado = service.convertir(desde, hacia, monto);

            resultadoLabel.setText(String.format("Resultado: %.2f %s", resultado, hacia));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

}
