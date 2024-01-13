package welcome;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.*;
import classes.*;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import logic.GoogleAPI;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

public class ViewJournal {
    JFrame vjournalFrame = new JFrame();
    Connection conn;
    JPanel sectionsPanel;
    List<section> sectionList = new ArrayList<section>();
    JList<String> list;
    JPanel mainPanel;
    JTextArea textEditor;
    JTextField sectionTitle;
    journal cJournal;
    String uID;
    String filePath = "";
    JFrame welcomeOuterFrame;
    String currentTitle; 
    String currentNotes;
    String journalType; 
    SwingController controller;
    
    public ViewJournal(Connection c, String userID, journal currentJournal, JFrame welcomeFrame) {
        conn = c;
        cJournal = currentJournal;
        uID = userID;
        welcomeOuterFrame = welcomeFrame;
        journalType  = currentJournal.getStore();
        
        vjournalFrame.setSize(1000, 900);
        vjournalFrame.setMinimumSize(new Dimension(1000, 800));
        vjournalFrame.setLayout(new BorderLayout());
        vjournalFrame.setFont(new Font("Open Sans", Font.BOLD, 20));
        vjournalFrame.setVisible(true);
        
        if (journalType.equals("PDF")) { 
            loadPDFViewer();
        }
        else { 
            loadISBNViewer();
        }
 
        sectionsPanel = new JPanel();

        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editPanel.setPreferredSize(new Dimension(200, 40));
        
        JLabel journalName  = new JLabel(currentJournal.getTitle());
        JButton addSection = new JButton("Add Section");
        addSection.setBackground(Color.orange);
        addSection.setBorderPainted(false);
        addSection.addMouseListener(new MyMouseListener());
        JButton deleteJournal = new JButton("Delete Journal");
        deleteJournal.setBackground(Color.red);
        deleteJournal.setBorderPainted(false);
        deleteJournal.addMouseListener(new MyMouseListener());
        editPanel.add(addSection);
        editPanel.add(deleteJournal);
        editPanel.add(journalName);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setPreferredSize(new Dimension(200, 250));
        textPanel.setBackground(Color.red);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setSize(100, 50);
        
        JButton editButton = new JButton("Save Edit");
        editButton.setBackground(Color.orange);
        editButton.setBorderPainted(false);
        editButton.addMouseListener(new MyMouseListener());
        
        JButton deleteButton = new JButton("Delete Section");
        deleteButton.setBackground(Color.red);
        deleteButton.setBorderPainted(false);
        JLabel title = new JLabel("Title:");
        deleteButton.addMouseListener(new MyMouseListener());
        
        sectionTitle = new JTextField("");
        sectionTitle.setPreferredSize(new Dimension(200,20));
        sectionTitle.setEnabled(false);

        textEditor = new JTextArea();
        JScrollPane scroll = new JScrollPane(textEditor);
        textEditor.setEnabled(false);

        textPanel.add(buttonsPanel, BorderLayout.NORTH);
        textPanel.add(scroll, BorderLayout.CENTER);
        buttonsPanel.add(title, BorderLayout.SOUTH);
        buttonsPanel.add(sectionTitle, BorderLayout.SOUTH);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        textPanel.setBackground(Color.BLUE);
        if (journalType.equals("PDF")) { 
                mainPanel.add(textPanel, BorderLayout.SOUTH);
        }
        else {
            mainPanel.add(textPanel, BorderLayout.CENTER);
        }
        vjournalFrame.add(mainPanel);
        addSections();
        
        vjournalFrame.add(editPanel, BorderLayout.NORTH);
        vjournalFrame.setVisible(true);
        if (journalType.equals("PDF")) { 
            controller.openDocument(filePath);
        }
    }
    
    private void loadSections(journal journal) throws Exception {
        // Setting the query
        PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM sections WHERE idjournals = ?" );
        statement.setInt(1, journal.getID());
        // Exectue the query 
        ResultSet result = statement.executeQuery();
        
        // Iterating through the results and adding it to a list
        
        while (result.next()) {
            section newSection = new section(
                result.getString("title"), 
                result.getString("note"),
                result.getInt("idsections")
            );
            sectionList.add(newSection);
        }
    }
    
    private void loadISBNViewer() { 
        try {
            String isbn = getISBN(cJournal);
            GoogleAPI gAPI = new GoogleAPI();
            gAPI.searchBook(isbn);
            loadSections(cJournal);
            
            BufferedImage image = ImageIO.read(new URL(gAPI.getImage()));
            ImageIcon oriImage = new ImageIcon(image);
            ImageIcon imageFinal = imageResize(oriImage);
            JLabel picLabel = new JLabel(imageFinal);            
            picLabel.setLocation(100, 50);
            
            JPanel bookInfo = new JPanel(new BorderLayout());
            bookInfo.add(picLabel, BorderLayout.CENTER);
            
            JLabel title = new JLabel(gAPI.getTitle(), SwingConstants.CENTER);
            title.setFont(new Font("Open Sans", Font.BOLD, 25));
            
            bookInfo.add(title, BorderLayout.NORTH);
            
            JLabel author = new JLabel("By: " + gAPI.getAuthors().get(0), SwingConstants.CENTER);
            author.setFont(new Font("Open Sans", Font.BOLD, 15));
            
            bookInfo.add(author, BorderLayout.SOUTH);
            
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setPreferredSize(new Dimension(400, 100));
            mainPanel.add(bookInfo, BorderLayout.NORTH);
        }
        catch (Exception error) { 
            JOptionPane.showMessageDialog(null, error,
             "Failed to load ISBN DATA", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void loadPDFViewer() { 
        try { 
            filePath = getPath(cJournal);
            loadSections(cJournal);
        }
        catch (Exception error) { 
            JOptionPane.showMessageDialog(null, error,
             "Failed to load pdf", JOptionPane.WARNING_MESSAGE);
        }

        // Adding the controller
        controller = new SwingController();
        SwingViewBuilder factory = new SwingViewBuilder(controller);
        JPanel viewerComponentPanel = (JPanel) factory.buildViewerPanel();
        controller.setToolBarVisible(true);
        
        ComponentKeyBinding.install(controller, viewerComponentPanel);
        
        // Configuring the controller 
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
             controller.getDocumentViewController()));

        viewerComponentPanel.setPreferredSize(new Dimension(700, 500));
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        Dimension pdfSize = new Dimension(200,200);
        mainPanel.setMinimumSize(pdfSize);
        mainPanel.setMaximumSize(pdfSize);
        mainPanel.setPreferredSize(pdfSize);
        mainPanel.add(viewerComponentPanel, BorderLayout.CENTER);
        mainPanel.setVisible(true);   
    }
    
    private class MyMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            try { 
                JButton button = (JButton) e.getSource();
                if(button.getText().equals("Add Section")) { 
                    addSection frame = new addSection(conn, cJournal, vjournalFrame, uID, welcomeOuterFrame);
                }
                else if(button.getText().equals("Delete Journal")) { 
                    int result = JOptionPane.showConfirmDialog( null, "Confirm Delete",
               "alert", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        deleteJournal();
                        vjournalFrame.dispose();
                        welcomeOuterFrame.dispose();
                        welcomePage welcomeUi = new welcomePage(conn, uID);
                    }
                }
                else if (button.getText().equals("Save Edit")) { 
                    editSections();
                    sectionsPanel.removeAll(); 
                    sectionList.removeAll(sectionList);
                    loadSections(cJournal);
                    addSections();
                    sectionsPanel.revalidate();
                    sectionsPanel.repaint();
                }
                else if (button.getText().equals("Delete Section")) { 
                    deleteSections();
                    sectionsPanel.removeAll(); 
                    sectionList.removeAll(sectionList);
                    loadSections(cJournal);
                    addSections();
                    textEditor.setText("");
                    sectionTitle.setText("");
                    textEditor.setEnabled(false);
                    sectionTitle.setEnabled(false);
                    sectionsPanel.revalidate();
                    sectionsPanel.repaint();
                }
            }    
            catch(Exception error){ 
                JOptionPane.showMessageDialog(null, error,
                "Creation Failed", JOptionPane.WARNING_MESSAGE);
            }
        } 
    }
    
    public ImageIcon imageResize(ImageIcon oriImage) { 
        Image imageNew = oriImage.getImage();
        Image newImage = imageNew.getScaledInstance(200, 300, java.awt.Image.SCALE_SMOOTH);//The error appears on this line
        ImageIcon imageFinal = new ImageIcon(newImage);
        return imageFinal;
    }
    
    private void deleteJournal() throws Exception {
        PreparedStatement statement = conn.prepareStatement("DELETE FROM pdf_journals WHERE idjournals = ?" );
        statement.setInt(1, cJournal.getID());
        statement.executeUpdate();
        statement = conn.prepareStatement("DELETE FROM sections WHERE idjournals = ?" );
        statement.setInt(1, cJournal.getID());
        statement.executeUpdate();
        statement = conn.prepareStatement("DELETE FROM journals WHERE idjournals = ?" );
        statement.setInt(1, cJournal.getID());
        statement.executeUpdate();
    }
    
    private void addSections() { 
        int sectionSize = sectionList.size();
        List<String> myList = new ArrayList<>(sectionSize);
        int row = 1;
        for (ListIterator<section> iter = sectionList.listIterator(); iter.hasNext(); ) {
            section newSection = iter.next();
            myList.add(newSection.getTitle());
            row++;
        }
        
        list = new JList<String>(myList.toArray(new String[myList.size()]));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        list.addListSelectionListener(new ListSelection());
        scrollPane.setPreferredSize(new Dimension(100, 700));
        sectionsPanel.add(scrollPane);
        vjournalFrame.add(sectionsPanel, BorderLayout.WEST);   
    }
    
    private void editSections() throws Exception {
        if (sectionTitle.getText().equals("")) { 
            throw new Exception("Title must not be empty");
        }
        for (ListIterator<section> jIter = sectionList.listIterator(); jIter.hasNext(); ) {
            section newSection = jIter.next();
            if (newSection.getTitle().equals(currentTitle)) {
                int sectionID = newSection.getID();
                PreparedStatement statement = conn.prepareStatement("UPDATE `journalapp`.`sections` SET `title` = ?, `note` = ? WHERE (`idsections` = ?);");
                statement.setString(1, sectionTitle.getText());
                statement.setString(2, textEditor.getText());
                statement.setInt(3, sectionID);
                statement.executeUpdate();
                currentTitle = sectionTitle.getText();
                currentNotes = textEditor.getText();
            }   
        }
    }
    
    private void deleteSections() throws Exception { 
        for (ListIterator<section> jIter = sectionList.listIterator(); jIter.hasNext(); ) {
            section newSection = jIter.next();
            if (newSection.getTitle().equals(currentTitle)) {
                int sectionID = newSection.getID();
                PreparedStatement statement = conn.prepareStatement("DELETE FROM sections WHERE idsections = ?" );
                statement.setInt(1, sectionID);
                statement.executeUpdate();
            }   
        }
    }
    
    private class ListSelection implements ListSelectionListener {
        @Override 
        public void valueChanged (ListSelectionEvent e) 
        {
            if (e.getValueIsAdjusting()) return;
                String sectionName = list.getSelectedValue();
                for (ListIterator<section> jIter = sectionList.listIterator(); jIter.hasNext(); ) {
                   section newSection = jIter.next();
                   if (newSection.getTitle().equals(sectionName)) {
                        textEditor.setEnabled(true);
                        sectionTitle.setEnabled(true);
                        sectionTitle.setText(sectionName);
                        currentTitle = sectionName;
                        textEditor.setText(newSection.getNotes());
                        currentNotes = newSection.getNotes();
                   }
                }
        }
    }
    
    private String getPath(journal journal) throws Exception { 
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM pdf_journals WHERE idjournals = ?" );
        statement.setInt(1, journal.getID());
        ResultSet result = statement.executeQuery();
        result.next();
        return result.getString("pdf_path");
    }
    
    private String getISBN(journal journal) throws Exception { 
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM isbn_journals WHERE idjournals = ?" );
        statement.setInt(1, journal.getID());
        ResultSet result = statement.executeQuery();
        result.next();
        return result.getString("isbn");
    }
}
