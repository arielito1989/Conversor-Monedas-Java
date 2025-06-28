package com.miempresa.conversor.ui;

import com.miempresa.conversor.service.ConversorService;
import com.miempresa.conversor.util.MonedaInfo;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ConversorGUI extends JFrame {

    private JComboBox<String> comboDesde;
    private JComboBox<String> comboHacia;
    private JTextField montoField;
    private JLabel resultadoLabel;
    private DefaultListModel<String> historialModel;
    private JList<String> listaHistorial;

    private Map<String, Double> tasas;

    public ConversorGUI() {
        setTitle("Conversor de Monedas");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana

        inicializarComponentes();
        cargarMonedas();
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1));

        comboDesde = new JComboBox<>();
        comboHacia = new JComboBox<>();
        montoField = new JTextField();
        resultadoLabel = new JLabel("Resultado: ");
        JButton btnConvertir = new JButton("Convertir");

        historialModel = new DefaultListModel<>();
        listaHistorial = new JList<>(historialModel);
        JScrollPane scrollHistorial = new JScrollPane(listaHistorial);
        scrollHistorial.setPreferredSize(new Dimension(250, 100));

        panel.add(new JLabel("Moneda de origen:"));
        panel.add(comboDesde);
        panel.add(new JLabel("Moneda de destino:"));
        panel.add(comboHacia);
        panel.add(new JLabel("Monto:"));
        panel.add(montoField);
        panel.add(btnConvertir);
        panel.add(resultadoLabel);

        add(panel, BorderLayout.CENTER);
        add(scrollHistorial, BorderLayout.SOUTH);

        btnConvertir.addActionListener(e -> convertir());
    }

    private void cargarMonedas() {
        try {
            ConversorService service = new ConversorService();
            tasas = service.obtenerTasas("USD"); // usamos USD como base

            comboDesde.removeAllItems();
            comboHacia.removeAllItems();

            for (String moneda : tasas.keySet()) {
                String descripcion = MonedaInfo.getDescripcion(moneda);
                String textoCombo = moneda + " - " + descripcion;
                comboDesde.addItem(textoCombo);
                comboHacia.addItem(textoCombo);
            }

            comboDesde.setSelectedItem("USD");
            comboHacia.setSelectedItem("EUR");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar monedas: " + e.getMessage());
        }
    }

    private void convertir() {
        try {
            String desde = comboDesde.getSelectedItem().toString().split(" ")[0];
            String hacia = comboHacia.getSelectedItem().toString().split(" ")[0];
            double monto = Double.parseDouble(montoField.getText());

            ConversorService service = new ConversorService();
            double resultado = service.convertir(desde, hacia, monto);

            resultadoLabel.setText(String.format("Resultado: %.2f %s", resultado, hacia));

            // Agregar al historial (máximo 10 entradas)
            String registro = String.format("%.2f %s → %.2f %s", monto, desde, resultado, hacia);
            if (historialModel.size() >= 10) {
                historialModel.remove(0);
            }
            historialModel.addElement(registro);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un monto válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
