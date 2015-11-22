package userInterface;

import imageProcessing.PNGProcesser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by Arnold on 2015-11-22.
 */
public class UserInterface extends JFrame {
    private JLabel imageLabel;

    private JButton encryptButton;
    private JButton decryptButton;
    private JButton saveButton;
    private JFileChooser fileChooser;

    private String imageFilePath;
    private JLabel imageFilePathLabel;
    private PNGProcesser pngProcesser;

    public UserInterface() {
        createFileChooser("PNG images", "png");
        createMenu();
        encryptButton = createButton("Encrypt", new EncryptAction());
        decryptButton = createButton("Decrypt", new DecryptAction());
        saveButton = createButton("Save", new SaveAction());
        imageLabel = new JLabel();
        imageFilePathLabel = new JLabel("no image");
        imageFilePathLabel.setHorizontalAlignment(JLabel.CENTER);
        arrangeComponents();
        setSize(800,600);

    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem openFileItem = new JMenuItem("Open");
        JMenuItem generateKeysItem = new JMenuItem("Generate keys");
        JMenuItem CloseItem = new JMenuItem("Close");
        openFileItem.addActionListener(new OpenFileAction());
        generateKeysItem.addActionListener(new GenerateKeysAction());
        CloseItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(openFileItem);
        menu.add(generateKeysItem);
        menu.add(CloseItem);
        setJMenuBar(menuBar);
        menuBar.add(menu);
    }

    private void createFileChooser(String description, String... args) {
        fileChooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, args);
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
    }

    private JButton createButton(String description, ActionListener actionListener) {
        JButton button = new JButton(description);
        button.addActionListener(actionListener);
        return button;
    }

    private void arrangeComponents() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
        saveButton.setEnabled(false);

        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(saveButton);
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(new JScrollPane(imageLabel),BorderLayout.CENTER);
        imagePanel.add(imageFilePathLabel, BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);
    }


    private class OpenFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.setCurrentDirectory(new File("."));
            int result = fileChooser.showOpenDialog(UserInterface.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                imageFilePath = fileChooser.getSelectedFile().getPath();
                imageFilePathLabel.setText(imageFilePath);
                imageLabel.setIcon(new ImageIcon(imageFilePath));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                saveButton.setEnabled(true);
                encryptButton.setEnabled(true);
                decryptButton.setEnabled(true);
                try{
                    pngProcesser = new PNGProcesser(imageFilePath);
                } catch (IOException exception){
                    exception.printStackTrace();
                }
                //wczytanie obiektu Mat
            }
        }
    }

    private class GenerateKeysAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    private class EncryptAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //
        }
    }

    private class DecryptAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //
        }
    }

    private class SaveAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            fileChooser.setCurrentDirectory(new File("."));
            int result  = fileChooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION){
                String newFilePath = fileChooser.getSelectedFile().getPath();
                pngProcesser.saveImage(newFilePath);
            }
        }
    }
}
