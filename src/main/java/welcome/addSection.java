
package welcome;

import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.*;
import classes.*;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class addSection {
    JFrame aSectionFrame = new JFrame();
    Connection conn;
    JTextField titleField;
    journal cJournal;
    JFrame joFrame; 
    String userID;
    JFrame welcomeFrameOuter;
    public addSection(Connection c, journal currentJournal, JFrame journalFrame, String uID, JFrame welcomeFrame) {
        conn = c;
        cJournal = currentJournal;
        joFrame = journalFrame;
        userID = uID;
        welcomeFrameOuter = welcomeFrame;
        
        aSectionFrame.setSize(500, 200);
        aSectionFrame.setMinimumSize(new Dimension(500, 200));
        aSectionFrame.setLayout(null);
        aSectionFrame.setFont(new Font("Open Sans", Font.BOLD, 30));
        
        JLabel addTitle= new JLabel("Enter the title of your new section");
        addTitle.setSize(200, 45);
        addTitle.setLocation(70, 0);
        
        titleField = new JTextField();
        titleField.setSize(300, 40);
        titleField.setLocation(70, 50);
        
        JButton addNew = new JButton("Add Section");
        addNew.setSize(300, 20);
        addNew.setLocation(70, 100);
        addNew.setBackground(Color.orange);
        addNew.setBorderPainted(false);
        addNew.addMouseListener(new MyMouseListener());
        
        aSectionFrame.add(addNew);
        aSectionFrame.add(addTitle);
        aSectionFrame.add(titleField);
        aSectionFrame.setVisible(true);
    }

    private class MyMouseListener extends MouseAdapter {
      @Override
      public void mouseClicked(MouseEvent e) {
          try {
                createSection();
                int result = JOptionPane.showConfirmDialog(null, "Section has been added", "Success!", JOptionPane.DEFAULT_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    joFrame.dispose();
                    aSectionFrame.dispose();
                    ViewJournal viewUi = new ViewJournal(conn, userID, cJournal, welcomeFrameOuter);
                }
          }
          catch(Exception error){ 
                JOptionPane.showMessageDialog(null, error,
                "Creation Failed", JOptionPane.WARNING_MESSAGE);
          }
      } 
    }
    
    private void createSection() throws Exception{ 
        retrieveSections();
        PreparedStatement statement = conn.prepareStatement("INSERT INTO `journalapp`.`sections` (`title`, `idjournals`) VALUES (?, ?);");
        statement.setString(1, titleField.getText());
        statement.setInt(2, cJournal.getID());

        statement.executeUpdate();

    }
    
    private void retrieveSections() throws Exception { 
        PreparedStatement  statement = conn.prepareStatement("SELECT * FROM sections WHERE idjournals = ?");
        statement.setInt(1, cJournal.getID());
        ResultSet result = statement.executeQuery();

        while (result.next()) {
            if (result.getString("title").equals(titleField.getText())) { 
                throw new Exception("Title must be unique");
            }
        }
    }

}
