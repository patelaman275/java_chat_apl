import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements ActionListener, Runnable {

    private final String serverAddress;
    private final int serverPort;
    public final String username;
    public final Color themeColor;
    public final String avatarPath; 

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private JFrame frame;
    private JPanel topPanel;
    private JTextField msgField;
    private JButton sendButton;
    private JPanel textArea;
    private Box vertical;
    static String mymsg;
    

    private final static Color purple = new Color(138, 37, 196);
    private final static Color pinkish = new Color(245, 48, 94);
    private final static Color yellowColor = new Color(246, 181, 0);

    public final Color blackForBg = new Color(26, 32, 47);
    public final Color blackForMsg = new Color(57, 71, 101);

    public Client(String serverAddress, int serverPort, String username, Color themeColor, String avatarPath) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
        this.themeColor = themeColor; // Set the theme color
        this.avatarPath = avatarPath; // Set the avatar path

        initializeGUI(username);
    }

    private void initializeGUI(String username) {
        frame = new JFrame(username);
        topPanel = new JPanel();
        msgField = new JTextField();
        sendButton = new JButton("Send");
        textArea = new JPanel();
        vertical = Box.createVerticalBox();

        frame.getContentPane().setBackground(blackForBg);
        frame.setLayout(null);
        frame.setSize(450, 700);
        frame.setLocation(10, 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        topPanel.setLayout(null);
        topPanel.setBackground(themeColor);
        topPanel.setBounds(0, 0, 450, 70);

        // Add avatar display
        JLabel avatarLabel = new JLabel();
        try {
            Image avatarImage = new ImageIcon(avatarPath).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(avatarImage));
        } catch (Exception e) {
            e.printStackTrace();
        }
        avatarLabel.setBounds(5, 5, 60, 60); // Position the avatar

        ImageIcon backIcon = new ImageIcon("icons/3.png");
        Image backIconSized = backIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        JLabel label1 = new JLabel(new ImageIcon(backIconSized));
        label1.setBounds(70, 17, 30, 30);

        JLabel name = new JLabel("Chat Group");
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        name.setForeground(Color.WHITE);
        name.setBounds(110, 15, 100, 18);

        JLabel activeStatus = new JLabel("You, other members");
        activeStatus.setFont(new Font("SAN_SERIF", Font.PLAIN, 14));
        activeStatus.setForeground(Color.WHITE);
        activeStatus.setBounds(110, 35, 110, 20);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(0, 70, 431, 545);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        msgField.setBorder(BorderFactory.createEmptyBorder());
        msgField.setBackground(blackForMsg);
        msgField.setForeground(Color.WHITE);
        msgField.setBounds(0, 615, 310, 40);
        msgField.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));

        sendButton.setBounds(310, 615, 123, 40);
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(themeColor);
        frame.getRootPane().setDefaultButton(sendButton);
        sendButton.addActionListener(this);

        topPanel.add(avatarLabel); // Add avatar to the top panel
        topPanel.add(label1);
        topPanel.add(name);
        topPanel.add(activeStatus);

        frame.add(topPanel);
        frame.add(scrollPane);
        frame.add(msgField);
        frame.add(sendButton);

        frame.setVisible(true);

        textArea.setBackground(blackForBg); // main background, grayish black
    }

    public void connectToServer() {
        try {
            socket = new Socket(serverAddress, serverPort);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            Thread thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel(String out, Color themeColor) {
        JPanel panel3 = new JPanel();
        JLabel l1 = new JLabel("<html><p style=\"width:150px;\">" + out + "</p></html>");

        l1.setBackground(themeColor);
        l1.setForeground(Color.WHITE);
        l1.setOpaque(true);
        l1.setBorder(new EmptyBorder(15, 15, 15, 50));

        mymsg = out;
        panel3.add(l1);
        return panel3;
    }

    public static JPanel formatLabelReceived(String out) {
        JPanel panel3 = new JPanel();
        JLabel l1 = new JLabel("<html><p style=\"width:150px;\">" + out + "</p></html>");
        l1.setBackground(new Color(57, 71, 101));
        l1.setForeground(Color.WHITE);
        l1.setOpaque(true);
        l1.setBorder(new EmptyBorder(15, 15, 15, 50));
        mymsg = out;
        panel3.add(l1);
        return panel3;
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            textArea.setLayout(new BorderLayout());
            JPanel right = new JPanel(new BorderLayout());

            String out = "<b style=\"color:white;\"><u>" + username + ":</u></b><br>" + msgField.getText();

            JPanel p4 = new JPanel();
            p4 = formatLabel(out, themeColor);

            p4.setBackground(blackForBg);
            right.setBackground(blackForBg);

            right.add(p4, BorderLayout.LINE_END);
            vertical.add(right);

            textArea.add(vertical, BorderLayout.PAGE_START);
            frame.validate();
            msgField.setText("");

            dataOutputStream.writeUTF(out);
            System.out.println("Message sent successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                String receivedMessage = dataInputStream.readUTF();
                System.out.println("Received message - " + receivedMessage);
                displayReceivedMessage(receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayReceivedMessage(String message) {
        int startIndex = message.indexOf("<u>") + 3;
        int endIndex = message.indexOf(":", startIndex); // Find the index of ":" after the username
        String senderUsername = message.substring(startIndex, endIndex);
        System.out.println("Received message from: " + senderUsername);

        if (senderUsername.equals(username)) {
            frame.validate();
            JPanel p2 = formatLabel(message, themeColor);
            JPanel left = new JPanel(new BorderLayout());
            left.add(p2, BorderLayout.LINE_START);
            System.out.println("I removed the sent message");
            vertical.remove(left);
            frame.validate();
        } else {
            frame.validate();
            JPanel p2 = formatLabelReceived(message);
            JPanel left = new JPanel(new BorderLayout());
            p2.setBackground(blackForBg);
            left.setBackground(blackForBg);
            left.add(p2, BorderLayout.LINE_START);
            vertical.add(left);
            frame.validate();
            System.out.println(username + " : " + message);
        }

        frame.repaint();
    }
}
