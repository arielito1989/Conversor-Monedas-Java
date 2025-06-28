package com.miempresa.conversor.ui;

import com.miempresa.conversor.service.ConversorService;
import com.miempresa.conversor.util.MonedaInfo;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.prefs.Preferences; // Importar Preferences para persistencia

public class ConversorGUI extends JFrame {

    private JComboBox<String> comboDesde;
    private JComboBox<String> comboHacia;
    private JTextField montoField;
    private JLabel resultadoLabel;
    private JButton btnConvertir; // Declarar el botón aquí para poder deshabilitarlo
    private JProgressBar progressBar; // Barra de progreso

    private DefaultListModel<String> historialModel;
    private JList<String> listaHistorial;

    private Map<String, Double> tasas;
    private Preferences prefs; // Objeto de preferencias para guardar y cargar configuraciones

    // Claves para las preferencias
    private static final String PREF_LAST_FROM_CURRENCY = "lastFromCurrency";
    private static final String PREF_LAST_TO_CURRENCY = "lastToCurrency";
    private static final String PREF_LAST_AMOUNT = "lastAmount";

    public ConversorGUI() {
        setTitle("Conversor de Monedas");
        setSize(450, 500); // Aumentar el tamaño para acomodar el historial y los nuevos elementos
        setMinimumSize(new Dimension(400, 450)); // Establecer un tamaño mínimo
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana

        prefs = Preferences.userNodeForPackage(ConversorGUI.class); // Inicializar preferencias

        inicializarComponentes();
        cargarMonedas();
        cargarPreferencias(); // Cargar las preferencias al iniciar
    }

    private void inicializarComponentes() {
        // Establece el Look and Feel de Nimbus para un diseño más moderno y "de empresa"
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel de Nimbus. Usando el predeterminado. " + e.getMessage());
        }

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding general

        // --- Panel Superior (Entrada de Datos) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Moneda de origen
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        inputPanel.add(new JLabel("Moneda de origen:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        comboDesde = new JComboBox<>();
        inputPanel.add(comboDesde, gbc);

        // Botón de Intercambiar
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; // Centrar el botón
        JButton btnIntercambiar = new JButton("Intercambiar");
        btnIntercambiar.setToolTipText("Intercambia las monedas de origen y destino");
        btnIntercambiar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnIntercambiar.setBackground(new Color(100, 149, 237)); // Azul cornflower
        btnIntercambiar.setForeground(Color.WHITE);
        btnIntercambiar.setFocusPainted(false);
        btnIntercambiar.setBorder(BorderFactory.createLineBorder(new Color(65, 105, 225), 1, true));
        btnIntercambiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        inputPanel.add(btnIntercambiar, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL; // Resetear para los siguientes componentes
        gbc.gridwidth = 1; // Resetear gridwidth

        // Moneda de destino
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        inputPanel.add(new JLabel("Moneda de destino:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        comboHacia = new JComboBox<>();
        inputPanel.add(comboHacia, gbc);

        // Monto
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        inputPanel.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        montoField = new JTextField();
        montoField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputPanel.add(montoField, gbc);

        // Botón Convertir
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; // Ocupa dos columnas
        gbc.fill = GridBagConstraints.NONE; // No rellenar completamente
        gbc.anchor = GridBagConstraints.CENTER; // Centrar el botón
        btnConvertir = new JButton("Convertir");
        btnConvertir.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnConvertir.setBackground(new Color(30, 144, 255)); // Azul brillante (Corporate look)
        btnConvertir.setForeground(Color.WHITE);
        btnConvertir.setFocusPainted(false);
        btnConvertir.setBorder(BorderFactory.createLineBorder(new Color(25, 118, 210), 2, true));
        btnConvertir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        inputPanel.add(btnConvertir, gbc);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // --- Panel Central (Resultado y Barra de Progreso) ---
        JPanel resultPanel = new JPanel(new BorderLayout(0, 10)); // Espaciado vertical
        resultadoLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        resultadoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultadoLabel.setForeground(new Color(0, 128, 0));
        resultPanel.add(resultadoLabel, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false); // Oculto por defecto
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setForeground(new Color(0, 150, 0));
        resultPanel.add(progressBar, BorderLayout.SOUTH);

        mainPanel.add(resultPanel, BorderLayout.CENTER);


        // --- Historial ---
        historialModel = new DefaultListModel<>();
        listaHistorial = new JList<>(historialModel);
        listaHistorial.setFont(new Font("Monospaced", Font.PLAIN, 12));
        listaHistorial.setBorder(BorderFactory.createTitledBorder("Historial de Conversiones"));
        JScrollPane scrollHistorial = new JScrollPane(listaHistorial);
        scrollHistorial.setPreferredSize(new Dimension(250, 100)); // Altura preferida

        mainPanel.add(scrollHistorial, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        btnConvertir.addActionListener(e -> convertir());
        btnIntercambiar.addActionListener(e -> intercambiarMonedas()); // Listener para el nuevo botón

        // Listeners para guardar preferencias automáticamente al cambiar la selección
        comboDesde.addActionListener(e -> guardarPreferencias());
        comboHacia.addActionListener(e -> guardarPreferencias());
        montoField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { guardarPreferencias(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { guardarPreferencias(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { guardarPreferencias(); }
        });
    }

    /**
     * Carga las monedas disponibles desde la API y las añade a los JComboBox.
     * Esta operación se realiza en un hilo de fondo (`SwingWorker`) para no bloquear la interfaz.
     */
    private void cargarMonedas() {
        progressBar.setVisible(true);
        progressBar.setString("Cargando monedas...");
        progressBar.setIndeterminate(true);
        btnConvertir.setEnabled(false); // Deshabilita el botón mientras se cargan

        new SwingWorker<Map<String, Double>, Void>() {
            @Override
            protected Map<String, Double> doInBackground() throws Exception {
                ConversorService service = new ConversorService();
                return service.obtenerTasas("USD"); // Usamos USD como base para obtener todos los códigos
            }

            @Override
            protected void done() {
                try {
                    tasas = get(); // Obtiene el resultado del hilo de fondo
                    if (tasas == null || tasas.isEmpty()) {
                        JOptionPane.showMessageDialog(ConversorGUI.this, "No se pudieron cargar las monedas. Verifique su conexión a internet o su API Key.", "Error de Carga", JOptionPane.ERROR_MESSAGE);
                        dispose(); // Cierra la aplicación si no hay monedas para cargar
                        return;
                    }

                    comboDesde.removeAllItems();
                    comboHacia.removeAllItems();

                    // Obtener los códigos de moneda y ordenarlos alfabéticamente
                    java.util.List<String> sortedCodes = new java.util.ArrayList<>(tasas.keySet());
                    java.util.Collections.sort(sortedCodes);

                    for (String monedaCode : sortedCodes) {
                        String descripcion = MonedaInfo.getDescripcion(monedaCode);
                        String textoCombo = monedaCode + " - " + descripcion;
                        comboDesde.addItem(textoCombo);
                        comboHacia.addItem(textoCombo);
                    }

                    // Asegúrate de que las preferencias se carguen DESPUÉS de que los items estén en los JComboBox
                    cargarPreferencias();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ConversorGUI.this, "Error al cargar monedas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    progressBar.setVisible(false);
                    progressBar.setIndeterminate(false);
                    progressBar.setString("");
                    btnConvertir.setEnabled(true); // Habilita el botón una vez que las monedas estén cargadas
                }
            }
        }.execute();
    }

    private void convertir() {
        progressBar.setVisible(true);
        progressBar.setString("Convirtiendo...");
        progressBar.setIndeterminate(true);
        btnConvertir.setEnabled(false); // Deshabilita el botón durante la conversión

        new SwingWorker<Double, Void>() {
            String desde;
            String hacia;
            double monto;

            @Override
            protected Double doInBackground() throws Exception {
                // Validación en segundo plano
                String selectedDesde = comboDesde.getSelectedItem() != null ? comboDesde.getSelectedItem().toString() : "";
                String selectedHacia = comboHacia.getSelectedItem() != null ? comboHacia.getSelectedItem().toString() : "";

                if (selectedDesde.isEmpty() || selectedHacia.isEmpty()) {
                    throw new IllegalArgumentException("Debe seleccionar las monedas de origen y destino.");
                }

                desde = selectedDesde.split(" ")[0];
                hacia = selectedHacia.split(" ")[0];

                try {
                    monto = Double.parseDouble(montoField.getText());
                    if (monto <= 0) {
                        throw new IllegalArgumentException("El monto debe ser un número positivo.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Por favor, ingrese un monto válido.");
                }

                if (desde.equals(hacia)) {
                    return monto; // No se necesita conversión
                }

                ConversorService service = new ConversorService();
                return service.convertir(desde, hacia, monto);
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                progressBar.setIndeterminate(false);
                progressBar.setString("");
                btnConvertir.setEnabled(true); // Habilita el botón nuevamente

                try {
                    double resultado = get();
                    String formattedResultado = String.format("Resultado: %.2f %s", resultado, hacia);
                    resultadoLabel.setText(formattedResultado);

                    String registro = String.format("%.2f %s → %.2f %s", monto, desde, resultado, hacia);
                    if (historialModel.size() >= 10) {
                        historialModel.remove(0);
                    }
                    historialModel.addElement(registro);

                    // Guardar las preferencias después de una conversión exitosa
                    guardarPreferencias();

                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(ConversorGUI.this, ex.getMessage(), "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ConversorGUI.this, "Error al convertir: " + ex.getMessage(), "Error de Conversión", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    resultadoLabel.setText("Resultado: Error");
                }
            }
        }.execute();
    }

    /**
     * Intercambia las monedas seleccionadas en los JComboBox de origen y destino.
     */
    private void intercambiarMonedas() {
        // Guardamos las selecciones actuales
        Object selectedDesde = comboDesde.getSelectedItem();
        Object selectedHacia = comboHacia.getSelectedItem();

        // Intercambiamos las selecciones
        comboDesde.setSelectedItem(selectedHacia);
        comboHacia.setSelectedItem(selectedDesde);

        // Al intercambiar, también guardamos las nuevas preferencias.
        guardarPreferencias();
    }

    /**
     * Carga las últimas monedas y monto utilizados desde las preferencias del usuario.
     */
    private void cargarPreferencias() {
        String lastFrom = prefs.get(PREF_LAST_FROM_CURRENCY, "USD - Dólar estadounidense"); // Valor por defecto
        String lastTo = prefs.get(PREF_LAST_TO_CURRENCY, "ARS - Peso argentino"); // Valor por defecto
        String lastAmount = prefs.get(PREF_LAST_AMOUNT, "1.0");

        // Intentar seleccionar los ítems guardados.
        // Es importante que estos métodos no disparen el listener de guardar preferencias
        // hasta que todas las preferencias se hayan cargado.
        comboDesde.setSelectedItem(lastFrom);
        comboHacia.setSelectedItem(lastTo);
        montoField.setText(lastAmount);
    }

    /**
     * Guarda las selecciones actuales de monedas y el monto en las preferencias del usuario.
     */
    private void guardarPreferencias() {
        if (comboDesde.getSelectedItem() != null) {
            prefs.put(PREF_LAST_FROM_CURRENCY, comboDesde.getSelectedItem().toString());
        }
        if (comboHacia.getSelectedItem() != null) {
            prefs.put(PREF_LAST_TO_CURRENCY, comboHacia.getSelectedItem().toString());
        }
        prefs.put(PREF_LAST_AMOUNT, montoField.getText());
    }
}
