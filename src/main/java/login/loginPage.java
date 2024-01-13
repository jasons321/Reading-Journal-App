package login;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.sql.*;
import welcome.welcomePage;


public class loginPage extends JFrame implements ActionListener{
    JFrame loginFrame = new JFrame();
    JButton loginButton = new JButton("Login");
    JTextField userId = new JTextField();
    JPasswordField userPass = new JPasswordField();
    JLabel userIdLabel = new JLabel("Username");
    JLabel userPasswordLabel = new JLabel("Password");
    JButton registerButton = new JButton("Register");
    Connection conn;
    
    public loginPage(Connection c) {
        conn = c;
        loginFrame.setResizable(false);
        userIdLabel.setBounds(90, 100, 75, 25);
        userPasswordLabel.setBounds(90, 150, 75, 25);
        userId.setBounds(170, 100, 120, 25);
        userPass.setBounds(170, 150, 120, 25);
        loginButton.setBounds(120, 190, 130, 25);
        loginButton.setBackground(Color.orange);
        loginButton.addActionListener(this);
        
        registerButton.setBounds(120, 220, 130, 25);
        registerButton.setBackground(Color.cyan);
        registerButton.addActionListener(this);
        
        loginFrame.add(userIdLabel);
        loginFrame.add(userPasswordLabel);
        loginFrame.add(userId);
        loginFrame.add(userPass);
        loginFrame.add(loginButton);
        loginFrame.add(registerButton);

        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(420, 340);
        loginFrame.setLayout(null);
        loginFrame.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) { 
        if (e.getSource() == loginButton) { 
            String userID = userId.getText();
            String userPassword = String.valueOf(userPass.getPassword());
            try { 
                PreparedStatement  statement = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
                statement.setString(1, userID);
                ResultSet result = statement.executeQuery();
                result.next();
                if (result.getString("password").equals(userPassword)) { 
                    welcomePage welcomeUi = new welcomePage(conn, result.getString("idusers") );
                    loginFrame.dispose();
                }
                else { 
                    JOptionPane.showMessageDialog(loginFrame, "Please ensure that your password is correct",
                    "Login Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
            catch (Exception error) { 
                JOptionPane.showMessageDialog(loginFrame, error,
            "Login Failed", JOptionPane.WARNING_MESSAGE);
            }
        }
        else if (e.getSource() == registerButton) { 
            loginFrame.dispose();
            registerPage registerUI = new registerPage(conn);
        }
    }
    
}
