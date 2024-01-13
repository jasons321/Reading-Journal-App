
package welcome;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.*;
import classes.*;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import wrap.*;

public class welcomePage {
    JFrame welcomeFrame = new JFrame();
    Connection conn;
    List<file> folderList = new ArrayList<file>();
    List<journal> journalList = new ArrayList<journal>();
    String userIDOuter;
    JPanel filePanel = new JPanel();
    JTextField folderTitleField;
    JFrame folderFrame;
    JPanel optionPanel;
    JButton back;
    JButton delete;
    int currentFolder; 
    List<journal> subJournalsList; 
    
    public welcomePage(Connection c, String userID) {
        conn = c;
        
        retrieveFiles(userID, folderList, journalList);
        userIDOuter = userID;
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setSize(1420, 740);
        welcomeFrame.setMinimumSize(new Dimension(700, 500));
        welcomeFrame.setLayout(new BorderLayout());
        welcomeFrame.setFont(new Font("Open Sans", Font.BOLD, 20));

        filePanel.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout());
        JLabel menuLabel = new JLabel("Welcome to your journal!");
        menuLabel.setFont(new Font("Open Sans", Font.BOLD, 20));
        menuLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        menuPanel.setPreferredSize(new Dimension(100, 100));
        menuPanel.add(menuLabel, BorderLayout.WEST);
        
        optionPanel = new JPanel();
        optionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        optionPanel.setPreferredSize(new Dimension(260, 50));
        JButton addJournal = new JButton("Add Journal");
        JButton addFolder = new JButton("Add Folder");
        addJournal.addMouseListener(new MyMouseListener());
        addFolder.addMouseListener(new MyMouseListener());
        addJournal.setBounds(0,0, 100, 50);
        optionPanel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        addFolder.setBounds(0,0,100, 50);
        addJournal.setBackground(Color.orange);
        addFolder.setBackground(Color.orange);
        addJournal.setBorderPainted(false);
        addFolder.setBorderPainted(false);
        optionPanel.add(addJournal);
        optionPanel.add(addFolder);
        
        menuPanel.add(optionPanel, BorderLayout.SOUTH);
        
        welcomeFrame.add(filePanel);
        JScrollPane scrollPane = new JScrollPane(filePanel);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        scrollPane.getViewport().setBorder(null);
        scrollPane.setViewportBorder(null);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        welcomeFrame.add(scrollPane);
        welcomeFrame.add(menuPanel, BorderLayout.NORTH);
        welcomeFrame.setVisible(true);
    }
    
    public void reloadFile() {
        folderList.clear();
        journalList.clear();
        filePanel.removeAll();
        retrieveFiles(userIDOuter, folderList, journalList);
        filePanel.revalidate();
        filePanel.repaint();
    }
    
    private void retrieveFiles(String userID, List<file> folderList, List<journal> journalList) { 
        try { 
            PreparedStatement  statement = conn.prepareStatement("SELECT * FROM folders WHERE user_id = ?");
            statement.setString(1, userID);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                file newFile = new file(
                    result.getString("folder_title"),
                    result.getString("type"),
                    result.getInt("idfolders")
                );

                folderList.add(newFile);
            }
            // change so that level 2 files are also included 
            statement = conn.prepareStatement("SELECT * FROM journals WHERE users_id = ?" );
            statement.setString(1, userID);

            result = statement.executeQuery();

            while (result.next()) {
                journal newFile = new journal(
                    result.getString("journal_title"), 
                    "file",
                    result.getInt("idjournals"), 
                    result.getInt("level"), 
                    result.getInt("folders_id"),
               result.getString("type")
                );
                journalList.add(newFile);
            }
            
            addPanelsFolder(folderList);
            addPanelsJournal(journalList, 1);
        }
        catch (Exception error) { 
                    JOptionPane.showMessageDialog(null, error,
                "Submission Failed", JOptionPane.WARNING_MESSAGE);        }
    }
    
    public void addPanelsFolder(List<file> folderList) { 
        for (ListIterator<file> iter = folderList.listIterator(); iter.hasNext(); ) {
            file newFile = iter.next();
            JPanel newPanel = new JPanel(); 
            newPanel.setLayout(new BorderLayout());
            newPanel.setPreferredSize(new Dimension(250, 270));
            newPanel.addMouseListener(new MyMouseListener());

            ImageIcon oriImage = new ImageIcon("image/1.png");
            ImageIcon imageFinal = imageResize(oriImage);
            JLabel imageLabel = new JLabel(imageFinal);
            newPanel.add(imageLabel, BorderLayout.NORTH);
            newPanel.setBackground(new Color(255, 233, 162));

            JLabel newLabel = new JLabel(newFile.getTitle());
            newLabel.setPreferredSize(new Dimension(70, 60));
            newLabel.setBackground(new Color(184, 180, 180));
            newLabel.setForeground(Color.white);
            newLabel.setOpaque(true);
            newLabel.setFont(new Font("Open Sans", Font.BOLD, 15));
            newLabel.setHorizontalAlignment(JLabel.CENTER);

            newPanel.add(newLabel, BorderLayout.SOUTH);
            filePanel.add(newPanel);                
        }
    } 
    
      public void addPanelsJournal(List<journal> journalList, int level) { 
        for (ListIterator<journal> iter = journalList.listIterator(); iter.hasNext(); ) {
            journal newFile = iter.next();
            if (newFile.getLevel() == level) { 
                JPanel newPanel = new JPanel(); 
                newPanel.setLayout(new BorderLayout());
                newPanel.setPreferredSize(new Dimension(250, 270));
                newPanel.addMouseListener(new MyMouseListener());

                ImageIcon oriImage = new ImageIcon("image/2.png");
                ImageIcon imageFinal = imageResize(oriImage);
                JLabel imageLabel = new JLabel(imageFinal);
                newPanel.add(imageLabel, BorderLayout.NORTH);
                newPanel.setBackground(new Color(173, 216, 230));

                JLabel newLabel = new JLabel(newFile.getTitle());
                newLabel.setPreferredSize(new Dimension(70, 60));
                newLabel.setBackground(new Color(184, 180, 180));
                newLabel.setForeground(Color.white);
                newLabel.setOpaque(true);
                newLabel.setFont(new Font("Open Sans", Font.BOLD, 15));
                newLabel.setHorizontalAlignment(JLabel.CENTER);

                newPanel.add(newLabel, BorderLayout.SOUTH);
                filePanel.add(newPanel);                
            }

        }
    } 
    
    private class MyMouseListener extends MouseAdapter {
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getSource() instanceof JButton) {
            JButton button = (JButton) e.getSource();
            if(button.getText().equals("Add Journal")) { 
                addJournal addJ = new addJournal(conn, folderList, userIDOuter, welcomeFrame);
            }
            else if (button.getText().equals("Add new folder")) { 
                try { 
                    submitFolder();
                    int result = JOptionPane.showConfirmDialog(null, "Folder has been added", "Success!", JOptionPane.DEFAULT_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        welcomeFrame.dispose();
                        folderFrame.dispose();
                        welcomePage wp = new welcomePage(conn, userIDOuter);
                    }
                }
                catch (Exception error){ 
                    JOptionPane.showMessageDialog(null, error,
                "Folder can't be created", JOptionPane.WARNING_MESSAGE);
                }
            }
            else if (button.getText().equals("Delete Folder")) {
                int result = JOptionPane.showConfirmDialog( null, "Confirm Delete",
               "alert", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try { 
                            deleteFolder();
                            optionPanel.remove(back);
                            optionPanel.remove(delete);
                            optionPanel.revalidate();
                            optionPanel.repaint();
                            reloadFile();
                        }
                        catch (Exception error) {
                            JOptionPane.showMessageDialog(null, error,
                       "Folder can't be created", JOptionPane.WARNING_MESSAGE);
                        }

                    }
            }
            else if(button.getText().equals("Add Folder")) { 
                createFolder();
            }
            else if (button.getText().equals("Back")) { 
                filePanel.removeAll();
                optionPanel.remove(back);
                optionPanel.remove(delete);
                addPanelsFolder(folderList);
                addPanelsJournal(journalList, 1);
                optionPanel.revalidate();
                optionPanel.repaint();
                filePanel.revalidate();
                filePanel.repaint();
            }
        }
        else if (e.getSource() instanceof JPanel) {            
            reloadPanel(e);
        }
      }
    }
    
    private void deleteFolder() throws Exception {
        String query;
        for (ListIterator<journal> iter = subJournalsList.listIterator(); iter.hasNext(); ) {
            journal newJournal = iter.next();
            query = "DELETE FROM sections WHERE idjournals = ?";
            deleteStatement(query, newJournal.getID());
            
            query = "DELETE FROM isbn_journals WHERE idjournals = ?";
            deleteStatement(query, newJournal.getID());

            query = "DELETE FROM pdf_journals WHERE idjournals = ?";
            deleteStatement(query, newJournal.getID());
        }
        query = "DELETE FROM journals WHERE folders_id = ?";
        deleteStatement(query, currentFolder);  

        query = "DELETE FROM folders WHERE idfolders = ?";
        deleteStatement(query, currentFolder);  
    }
    
    private void deleteStatement(String query, int parameter) throws Exception { 
        PreparedStatement statement = conn.prepareStatement(query );
        statement.setInt(1, parameter);
        statement.executeUpdate();
    }
    
    private void reloadPanel(MouseEvent e) { 
        JPanel panel = (JPanel) e.getSource();
        JLabel label = (JLabel)panel.getComponent(1);
        subJournalsList = new ArrayList<journal>();

        int folder = 0; 
        for (ListIterator<file> iter = folderList.listIterator(); iter.hasNext(); ) {
            file newFile = iter.next();
            if (newFile.getTitle().equals(label.getText())) {
                folder = 1;
                currentFolder = newFile.getID();
                for (ListIterator<journal> jIter = journalList.listIterator(); jIter.hasNext(); ) {
                    journal newJournal = jIter.next();
                    if (newJournal.getParentID() == currentFolder) {
                        subJournalsList.add(newJournal);
                    }
                }
                filePanel.removeAll();
                addPanelsJournal(subJournalsList, 2);
                back = new JButton("Back");
                back.addMouseListener(new MyMouseListener());
                back.setBounds(0,0, 100, 50);
                back.setBackground(Color.orange);
                back.setBorderPainted(false);
                delete = new JButton("Delete Folder");
                delete.addMouseListener(new MyMouseListener());
                delete.setBounds(0,0, 100, 50);
                delete.setBackground(Color.red);
                delete.setBorderPainted(false);
                optionPanel.add(delete);
                optionPanel.add(back);
                optionPanel.revalidate();
                optionPanel.repaint();
                filePanel.revalidate();
                filePanel.repaint();
            }
        }

        if (folder == 0) { 
            for (ListIterator<journal> iter = journalList.listIterator(); iter.hasNext(); ) {
                journal newFile = iter.next();
                if (newFile.getTitle().equals(label.getText())) {
                    currentFolder = newFile.getID();
                    ViewJournal viewUi = new ViewJournal(conn, userIDOuter, newFile, welcomeFrame);
                }
            }    
        }
    }
    
    public void createFolder() { 
        folderFrame = new JFrame();
        folderFrame.setSize(500, 200);
        folderFrame.setMinimumSize(new Dimension(500, 200));
        folderFrame.setLayout(null);
        folderFrame.setFont(new Font("Open Sans", Font.BOLD, 30));
        
        JLabel addTitle= new JLabel("Enter the title of your new folder");
        addTitle.setSize(200, 45);
        addTitle.setLocation(70, 0);
        
        folderTitleField = new JTextField();
        folderTitleField.setSize(300, 40);
        folderTitleField.setLocation(70, 50);
        
        JButton addNew = new JButton("Add new folder");
        addNew.setSize(300, 20);
        addNew.setLocation(70, 100);
        addNew.setBackground(Color.orange);
        addNew.setBorderPainted(false);
        addNew.addMouseListener(new MyMouseListener());
        
        folderFrame.add(addNew);
        folderFrame.add(addTitle);
        folderFrame.add(folderTitleField);
        folderFrame.setVisible(true);
    }
    
    private void submitFolder() throws Exception { 
        if (folderTitleField.getText().equals("")) { 
            throw new Exception("Title can't be empty");
        }
        for (ListIterator<file> iter = folderList.listIterator(); iter.hasNext(); ) {
            file newFile = iter.next();
            if (newFile.getTitle().equals(folderTitleField.getText())) {
                throw new Exception("Title must be unique");
            }
        }
        
        PreparedStatement statement = conn.prepareStatement("INSERT INTO `journalapp`.`folders` (`folder_title`, `user_id`, `type`) VALUES (?, ?, ?);");
        statement.setString(1, folderTitleField.getText());
        statement.setString(2, userIDOuter);
        statement.setString(3, "folder");

        statement.executeUpdate();
    }
    
    public ImageIcon imageResize(ImageIcon oriImage) { 
        Image imageNew = oriImage.getImage();
        Image newImage = imageNew.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH);//The error appears on this line
        ImageIcon imageFinal = new ImageIcon(newImage);
        return imageFinal;
    }
            
}
