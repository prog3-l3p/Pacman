package gui.leveleditor;

import gamelogic.Entity;
import gui.Main;
import gui.mainmenu.MainMenuFrame;
import resourcehandler.ResourceHandler;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;


/**
 * This class is responsible for everything concerning the level editor.
 * It is a JFrame that contains a EntityChooserPanel and an EditorViewPanel.
 * It also contains a save button and a load button.
 */
public class LevelEditorFrame extends JFrame {
    EntityChooserPanel entityChooserPanel = new EntityChooserPanel();
    EditorViewPanel viewPanel = new EditorViewPanel();
    private static String currentSprite;
    private static String currentEntityType;
    private static final String SAVE_LEVEL = "Save Level";
    private static final String LOAD_LEVEL = "Load Level";

    // Initialize the level editor panel
    public LevelEditorFrame(){
        setTitle("Level editor");
        setIconImage(ResourceHandler.getIcon("LevelEditorIcon"));
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        add(entityChooserPanel, BorderLayout.NORTH);
        add(viewPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        JButton mainMenuButton = new JButton("Main menu");

        entityChooserPanel.add(saveButton);
        entityChooserPanel.add(loadButton);
        entityChooserPanel.add(mainMenuButton);

        loadButton.setBorderPainted(false);
        mainMenuButton.setBorderPainted(false);

        saveButton.addActionListener(e -> showDialog(SAVE_LEVEL));
        loadButton.addActionListener(e -> showDialog(LOAD_LEVEL));
        mainMenuButton.addActionListener(e -> Main.setDisplayedFrame(new MainMenuFrame()));
        pack();
    }

    public static String getCurrentSprite(){
        return currentSprite;
    }
    public static String getCurrentEntityType(){return currentEntityType;}
    public static void setCurrentSprite(String spriteName){
        currentSprite = spriteName;
    }
    public static void setCurrentEntityType(String entityType){currentEntityType = entityType;}

    // Show a dialog to save or load the level
    private void showDialog(String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);

        int userSelection = 0;

        switch(title) {
            case SAVE_LEVEL -> userSelection = fileChooser.showSaveDialog(this);
            case LOAD_LEVEL -> userSelection = fileChooser.showOpenDialog(this);
            default -> Main.logger.log(Level.WARNING, "Unexpected error.");
        }

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            switch (title) {
                case SAVE_LEVEL -> ResourceHandler.saveLevel(selectedFile.getAbsolutePath(), viewPanel.getEntities());
                case LOAD_LEVEL -> {
                    ArrayList<ArrayList<Entity>> level =  ResourceHandler.loadLevel(selectedFile.getAbsolutePath());
                    viewPanel.loadEntities(level);
                }
                default -> Main.logger.log(Level.WARNING, "Unexpected error while performing file operation.");
            }
        }
    }
}