/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import welcome.welcomePage;

public class registerPage implements ActionListener {
    JFrame registerFrame = new JFrame();
    JButton registerButton = new JButton("Register");
    JTextField userId = new JTextField();
    JPasswordField userPass = new JPasswordField();
    JLabel userIdLabel = new JLabel("Username");
    JLabel userPasswordLabel = new JLabel("Password");
    JLabel title = new JLabel("Create an account");
    Connection conn;
    
    public registerPage(Connection c) {
        conn = c;
        
        registerFrame.setResizable(false);
        
        title.setBounds(90, 70, 120, 25);
        
        userIdLabel.setBounds(90, 100, 75, 25);
        userPasswordLabel.setBounds(90, 150, 75, 25);
        userId.setBounds(170, 100, 120, 25);
        userPass.setBounds(170, 150, 120, 25);
        registerButton.setBounds(120, 190, 130, 25);
        registerButton.setBackground(Color.orange);
        registerButton.addActionListener(this);
        
        registerFrame.add(userIdLabel);
        registerFrame.add(userPasswordLabel);
        registerFrame.add(userId);
        registerFrame.add(userPass);
        registerFrame.add(registerButton);
        registerFrame.add(title);

        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerFrame.setSize(420, 340);
        registerFrame.setLayout(null);
        registerFrame.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) { 
        if (e.getSource() == registerButton) { 
            String userID = userId.getText();
            String userPassword = String.valueOf(userPass.getPassword());
            try { 
                PreparedStatement  statement = conn.prepareStatement("INSERT INTO `journalapp`.`users` (`username`, `password`) VALUES (?, ?);");
                statement.setString(1, userID);
                statement.setString(2, userPassword);
                statement.executeUpdate();
                        
                registerFrame.dispose();
                loginPage loginUi = new loginPage(conn);
            }
            catch (Exception error) { 
                System.out.println(error);
                JOptionPane.showMessageDialog(registerFrame, "Username must be unique",
            "Login Failed", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
