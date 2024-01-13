
package welcome;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.*;
import classes.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import logic.GoogleAPI;
import welcome.welcomePage;

public class addJournal extends JFrame{
    Connection conn;
    JPanel formPanel = new JPanel();
    JPanel uploadPanel = new JPanel();
    JPanel topPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel uploadInner = new JPanel();
    JTextField dateField;
    JComboBox folderCombo;
    JTextField titleField;
    int uploadType = 0;
    List<file> folderListOuter;
    String userIDOuter;
    String filePath = "";
    JFrame welcomeFrameOuter;
    JTextField isbnField;
    
    public addJournal(Connection c, List<file> folderList, String userID, JFrame welcomeFrame) {
        conn = c;
        welcomeFrameOuter = welcomeFrame;
        userIDOuter = userID; 
        folderListOuter = folderList;
        setSize(800, 600);
        setLayout(new BorderLayout());
        setFont(new Font("Open Sans", Font.BOLD, 20));
        setResizable(false);
        this.setLocationRelativeTo(null);
        formPanel.setPreferredSize(new Dimension(400, 300));
        formPanel.setLayout(null);
        formPanel.setBackground(new Color(248, 248, 248));
        
        uploadPanel.setPreferredSize(new Dimension(400, 500));
        uploadPanel.setBackground(new Color(248, 248, 248));
        
        uploadInner.setPreferredSize(new Dimension(300, 300));
        uploadInner.setBackground(new Color(240, 240, 240));
        uploadInner.setLayout(null);
        uploadPanel.add(uploadInner);
        
        topPanel.setPreferredSize(new Dimension(400, 100));
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(new Color(248, 248, 248));
        buttonPanel.setPreferredSize(new Dimension(400, 50));
        buttonPanel.setBackground(new Color(248, 248, 248));

        JButton submit = new JButton("Submit");
        submit.setPreferredSize(new Dimension(100, 50));
        submit.setBackground(Color.orange);
        submit.addMouseListener(new MyMouseListener());

        JLabel title = new JLabel("Add a new journal");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Open Sans", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        title.setPreferredSize(new Dimension(100, 50));
        topPanel.add(title,BorderLayout.NORTH);
        
        JButton pdf = new JButton("PDF");
        JButton book = new JButton("BOOK");
        pdf.setSize(100, 50);
        book.setSize(100, 50);
        pdf.setBackground(Color.orange);
        book.setBackground(Color.orange);
        pdf.setBorderPainted(false);
        book .setBorderPainted(false);
        pdf.addMouseListener(new MyMouseListener());
        book.addMouseListener(new MyMouseListener());
        buttonPanel.add(pdf);
        buttonPanel.add(book);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
      
        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(uploadPanel, BorderLayout.EAST);
        add(submit, BorderLayout.SOUTH);

        JLabel journalTitle = new JLabel("Title*");
        journalTitle.setFont(new Font("Arial", Font.PLAIN, 15));
        journalTitle.setSize(50, 20);
        journalTitle.setLocation(40, 100);
        formPanel.add(journalTitle);
 
        titleField = new JTextField();
        titleField.setFont(new Font("Arial", Font.PLAIN, 15));
        titleField.setSize(180, 40);
        titleField.setLocation(90,90);
        formPanel.add(titleField);
        
        JLabel dateLabel = new JLabel("Date");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        dateLabel.setSize(50, 20);
        dateLabel.setLocation(40, 160);
        formPanel.add(dateLabel);
 
        dateField = new JTextField();
        dateField.setFont(new Font("Arial", Font.PLAIN, 15));
        dateField.setSize(180, 40);
        dateField.setLocation(90, 150);
        dateField.setEditable(false);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        Calendar cal = Calendar.getInstance();
        dateField.setText(dateFormat.format(cal.getTime()));
        formPanel.add(dateField);

        JLabel folderLabel = new JLabel("Folder");
        folderLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        folderLabel.setSize(50, 20);
        folderLabel.setLocation(40, 225);
        formPanel.add(folderLabel);
        
        String[] folderNames = createFolderList(folderList);
        folderCombo = new JComboBox(folderNames);
        folderCombo.setFont(new Font("Arial", Font.PLAIN, 15));
        folderCombo.setSize(180, 40);
        folderCombo.setLocation(90, 220);
        formPanel.add(folderCombo);

        addPdfForm();
        
        setVisible(true);
    }
    
    public String[] createFolderList(List<file> folderList) { 
        int folderSize = folderList.size();
        String[] folderNames = new String[folderSize+1];
        folderNames[folderSize] = "None";
        int numTracker = 0;
        for (ListIterator<file> iter = folderList.listIterator(); iter.hasNext(); ) {
            file newFolder = iter.next();
            folderNames[numTracker] = newFolder.getTitle();
            numTracker++;
        }   
        return folderNames;
    }
    
    private class MyMouseListener extends MouseAdapter {
      @Override
      public void mouseClicked(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            String buttonText = button.getText();
            if (buttonText.equals("BOOK")) {
                uploadType = 1;
                uploadInner.removeAll();
                addBookForm();
                uploadInner.revalidate();
                uploadInner.repaint();
            }
            else if (buttonText.equals("PDF")) { 
                uploadType = 0;
                uploadInner.removeAll();
                addPdfForm();
                uploadInner.revalidate();
                uploadInner.repaint();
            }
            else if (buttonText.equals("Select File")) { 
                try { 
                    File pdfFile = uploadPDF();
                    filePath = pdfFile.getAbsolutePath();
                    String fileName = pdfFile.getName();
                    addFileName(fileName);
                    uploadInner.revalidate();
                    uploadInner.repaint();
                }
                catch (FileNotFoundException fileError){ 
                    System.out.println(fileError);
                }
            }
            else if (buttonText.equals("Check ISBN")) { 
                GoogleAPI gAPI = new GoogleAPI();
                String result = gAPI.checkISBN(isbnField.getText());
                if (result.equals("")) { 
                    JOptionPane.showMessageDialog(null, "Please try again.",
               "ISBN not valid", JOptionPane.WARNING_MESSAGE);
                }
                else { 
                    JOptionPane.showConfirmDialog(null, "ISBN is valid", "Success!", JOptionPane.DEFAULT_OPTION);
                }
            }
            else if (buttonText.equals("Submit")) { 
                System.out.println(uploadType);
                try { 
                    int rowID = insertJournal();
                    if (uploadType == 0) { 
                        insertPDF(rowID);
                    }
                    else { 
                        insertISBN(rowID);
                    }
                    int result = JOptionPane.showConfirmDialog(null, "Journal has been added", "Success!", JOptionPane.DEFAULT_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        welcomeFrameOuter.dispose();
                        welcomePage welcomeUi = new welcomePage(conn, userIDOuter);
                        dispose();
                    }
                }
                catch (Exception error) { 
                    JOptionPane.showMessageDialog(null, error,
                "Submission Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        } 
    }
    
    
    private void insertPDF(int journalID) throws Exception { 
        PreparedStatement statement = conn.prepareStatement("INSERT INTO `journalapp`.`pdf_journals` (`pdf_path`, `idjournals`) VALUES (?, ?);");
        statement.setString(1, filePath);
        statement.setInt(2, journalID);
        statement.executeUpdate();
    }
    
    private void insertISBN(int journalID) throws Exception {
        GoogleAPI gAPI = new GoogleAPI();
        String result = gAPI.checkISBN(isbnField.getText());
        if (result.equals("Success")) {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO `journalapp`.`isbn_journals` (`isbn`, `idjournals`) VALUES (?, ?);");
            statement.setString(1, isbnField.getText());
            statement.setInt(2, journalID);
            statement.executeUpdate();                  
        }
        else { 
            throw new Exception("An error occured with the ISBN");
        }
    }
    
    private int insertJournal() throws Exception{ 
        String journalTitle = titleField.getText();
        String date = dateField.getText();
        String type;
        if (uploadType == 0) { 
            type = "PDF";
        }
        else { 
            type = "ISBN";
        }
        String selectedFolder = folderCombo.getSelectedItem().toString();
        int level = 0;
        int folderID = 0;
        
        if (uploadType == 0) { 
            validateInput(journalTitle);   
        }
        if (selectedFolder.equals("None")) { 
            folderID = 0;
            level = 1;
        }
        else { 
            for (ListIterator<file> iter = folderListOuter.listIterator(); iter.hasNext(); ) {
                file newFolder = iter.next();
                if (newFolder.getTitle().equals(selectedFolder)) { 
                    folderID = newFolder.getID();
                    level = 2;
                }
            }   
        }
        int result;
        PreparedStatement statement;
        if (level == 2) { 
            statement = conn.prepareStatement("INSERT INTO `journalapp`.`journals` (`journal_title`, `level`, `folders_id`, `users_id`, `type`, `date`) VALUES (?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, journalTitle);
            statement.setInt(2, level);
            statement.setInt(3, folderID);
            statement.setString(4, userIDOuter);
            statement.setString(5, type);
            statement.setString(6, date);       
        }                   
        else {
            statement = conn.prepareStatement("INSERT INTO `journalapp`.`journals` (`journal_title`, `level`, `users_id`, `type`, `date`) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, journalTitle);
            statement.setInt(2, level);
            statement.setString(3, userIDOuter);
            statement.setString(4, type);
            statement.setString(5, date);  
        }
        result = statement.executeUpdate();

        if (result > 0) {
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return id;
            }
        }
        return 0;
    }
    
    private int validateInput(String journalTitle) throws Exception {
        if (journalTitle.equals("")) {
            throw new Exception("Title must not be empty");
        }
        else if (filePath.equals("")) { 
            throw new Exception("You must upload a PDF file");
        }
        return 0;
    }
    
    private File uploadPDF() throws FileNotFoundException{ 
        JFileChooser fileChooser = new JFileChooser();
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                ".pdf",
                "pdf");
        
        fileChooser.setFileFilter(filter);
        fileChooser.showOpenDialog(null);
                
        File file = new File(fileChooser
                .getSelectedFile()
                .getAbsolutePath());
        
        return file;
    }
    
    private void addFileName(String fileName) { 
        JTextArea fileLabel = new JTextArea(fileName);
        fileLabel.setFont(new Font("Open Sans", Font.PLAIN, 10));
        fileLabel.setSize(200, 50);
        fileLabel.setLocation(65, 230);
        fileLabel.setEditable(false);
        fileLabel.setLineWrap(true);
        uploadInner.add(fileLabel);
    }
    
    public void addBookForm() { 
        try {                
            BufferedImage image = ImageIO.read(new File("image/book.png"));
            JLabel picLabel = new JLabel(new ImageIcon(image));            
            picLabel.setSize(100, 100);
            picLabel.setLocation(100, 50);
            uploadInner.add(picLabel);
            JLabel uploadLabel = new JLabel("Upload using ISBN");
            uploadLabel.setBackground(Color.black);
            uploadLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            uploadLabel.setSize(200, 30);
            uploadLabel.setLocation(75, 150);
            uploadInner.add(uploadLabel);
            isbnField = new JTextField();
            isbnField.setFont(new Font("Arial", Font.PLAIN, 15));
            isbnField.setSize(180, 40);
            isbnField.setLocation(70,180);
            uploadInner.add(isbnField);
            JButton checkISBN = new JButton("Check ISBN");
            checkISBN.addMouseListener(new MyMouseListener());
            checkISBN.setSize(180, 40);
            checkISBN.setLocation(70,220);
            checkISBN.setBackground(Color.orange);
            checkISBN.setForeground(Color.white);
            checkISBN.setBorderPainted(false);
            uploadInner.add(checkISBN);

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    public void addPdfForm() { 
        try {                
            BufferedImage image = ImageIO.read(new File("image/pdf.png"));
            JLabel picLabel = new JLabel(new ImageIcon(image));            
            picLabel.setSize(100, 100);
            picLabel.setLocation(100, 70);
            uploadInner.add(picLabel);
            JLabel uploadLabel = new JLabel("Upload a PDF");
            uploadLabel.setFont(new Font("Open Sans", Font.BOLD, 15));
            uploadLabel.setSize(240, 20);
            uploadLabel.setLocation(105, 170);
            JButton uploadButton = new JButton("Select File");
            uploadButton.addMouseListener(new MyMouseListener());
            uploadButton.setBackground(Color.orange);
            uploadButton.setForeground(Color.white);
            uploadButton.setBorderPainted(false);
            uploadButton.setFont(new Font("Open Sans", Font.BOLD, 15));
            uploadButton.setSize(200, 20);
            uploadButton.setLocation(55, 200);
            uploadInner.add(uploadLabel);
            uploadInner.add(uploadButton);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
}
