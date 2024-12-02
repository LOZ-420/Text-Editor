package com.example;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;

public class GUI extends JFrame {
    private JFileChooser jFileChooser;
    private UndoManager undoManager;

    public GUI() {
        super("Text Editor");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("icon.png")));

        // Initialize the UndoManager
        undoManager = new UndoManager();

        Color softBlack = new Color(45, 45, 45);
        Color softWhite = new Color(220, 220, 220);

        JToolBar toolBar = new JToolBar();
        toolBar.setBackground(softBlack);
        toolBar.setBorder(null);
        add(toolBar, BorderLayout.NORTH);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(softBlack);
        menuBar.setForeground(softWhite);
        toolBar.add(menuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(softWhite);
        JMenu editMenu = new JMenu("Edit");
        editMenu.setForeground(softWhite);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File(""));
        jFileChooser.setAcceptAllFileFilterUsed(false);

        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save as");

        JTextArea textArea = new JTextArea();
        textArea.setBorder(null);
        textArea.setFont(new Font("monospace", Font.PLAIN, 18));
        textArea.setBackground(softBlack);
        textArea.setForeground(softWhite);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.getViewport().setBackground(softBlack);
        add(scrollPane, BorderLayout.CENTER);

        // Set up the undo and redo functionality
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // Save Button Action
        saveItem.addActionListener(new ActionListener() {
            private File currentFile = null;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile == null) {
                    int result = jFileChooser.showSaveDialog(GUI.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        currentFile = jFileChooser.getSelectedFile();
                    } else {
                        return;
                    }
                }

                try {
                    FileWriter writer = new FileWriter(currentFile);
                    writer.write(textArea.getText());
                    writer.close();
                    JOptionPane.showMessageDialog(GUI.this, "File saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(GUI.this, "Error saving file: " + ioException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Save As Button Action
        saveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = jFileChooser.showSaveDialog(GUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jFileChooser.getSelectedFile();
                    if (!selectedFile.getName().endsWith(".txt")) {
                        selectedFile = new File(selectedFile.getPath() + ".txt");
                    }

                    try {
                        FileWriter writer = new FileWriter(selectedFile);
                        writer.write(textArea.getText());
                        writer.close();
                        JOptionPane.showMessageDialog(GUI.this, "File saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(GUI.this, "Error saving file: " + ioException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Open Button Action
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = jFileChooser.showOpenDialog(GUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jFileChooser.getSelectedFile();
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        textArea.setText(content.toString());
                        reader.close();
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(GUI.this, "Error opening file: " + ioException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Undo Button Action
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });

        // Redo Button Action
        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });

        // Add to Edit Menu
        editMenu.add(undoItem);
        editMenu.add(redoItem);

        // Add to File Menu
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(openItem);

        // Keyboard Shortcuts

        // Ctrl+S (Save)
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        
        // Ctrl+Shift+S (Save As)
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        
        // Ctrl+Z (Undo)
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        
        // Ctrl+Y (Redo)
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
    }
}
