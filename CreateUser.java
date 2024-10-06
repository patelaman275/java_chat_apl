import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class CreateUser extends JFrame implements ActionListener {

    private JTextField nameField;
    private JButton createButton;
    private JButton chooseColorButton;
    private JButton chooseAvatarButton;
    private Color selectedColor;
    private String selectedAvatar;

    private final String[] avatarPaths = {
            "C:\\Users\\patel\\Downloads\\JAVA_AOOP\\ps\\js\\icons\\c1.jpeg",
            "C:\\Users\\patel\\Downloads\\JAVA_AOOP\\ps\\js\\icons\\c2.jpeg",
            "C:\\Users\\patel\\Downloads\\JAVA_AOOP\\ps\\js\\icons\\c3.jpeg",
            "C:\\Users\\patel\\Downloads\\JAVA_AOOP\\ps\\js\\icons\\c4.jpeg",
            "C:\\Users\\patel\\Downloads\\JAVA_AOOP\\ps\\js\\icons\\c5.jpeg"
    };

    public CreateUser() {
        setTitle("Create New User");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel nameLabel = new JLabel("Enter Name:");
        nameField = new JTextField(15);
        createButton = new JButton("Create User");
        chooseColorButton = new JButton("Choose Theme Color");
        chooseAvatarButton = new JButton("Choose Avatar");

        getRootPane().setDefaultButton(createButton);

        createButton.addActionListener(this);
        chooseColorButton.addActionListener(this);
        chooseAvatarButton.addActionListener(this);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(new JLabel());
        panel.add(chooseColorButton);
        panel.add(new JLabel());
        panel.add(chooseAvatarButton);
        panel.add(new JLabel());
        panel.add(createButton);

        add(panel);
    }
    ////////////////////////////////////////

    private void showAvatarSelector() {
        JDialog avatarDialog = new JDialog(this, "Choose Avatar", true);
        avatarDialog.setLayout(new GridLayout(2, 3));
        avatarDialog.setSize(400, 200);
        avatarDialog.setLocationRelativeTo(this);

        for (int i = 0; i < avatarPaths.length; i++) {
            String avatarPath = avatarPaths[i];
            JButton avatarButton = new JButton(new ImageIcon(avatarPath));
            int index = i;

            avatarButton.addActionListener(e -> selectAvatar(index, avatarDialog));
            avatarDialog.add(avatarButton);
        }

        avatarDialog.setVisible(true);
    }

    private void selectAvatar(int index, JDialog avatarDialog) {
        selectedAvatar = avatarPaths[index];
        avatarDialog.dispose();
        JOptionPane.showMessageDialog(this, "Selected Avatar: " + new File(selectedAvatar).getName());
    }


    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton) {
            String username = nameField.getText().trim();
            if (!username.isEmpty() && selectedAvatar != null) {
                SwingUtilities.invokeLater(() -> {
                    Client client = new Client("127.0.0.1", 6001, username, selectedColor, selectedAvatar);
                    client.connectToServer();
                });
                nameField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a name and select an avatar.");
            }
        } else if (e.getSource() == chooseColorButton) {
            selectedColor = JColorChooser.showDialog(this, "Choose Theme Color", selectedColor);
            if (selectedColor != null) {
                getContentPane().setBackground(selectedColor);
            }
        } else if (e.getSource() == chooseAvatarButton) {
            showAvatarSelector();
        }
    }




    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreateUser createUser = new CreateUser();
            createUser.setVisible(true);
        });
    }
}
