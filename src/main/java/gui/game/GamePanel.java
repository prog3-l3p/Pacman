package gui.game;

import gamelogic.EntityObserver;
import gamelogic.entities.Entity;
import gamelogic.entities.moving.PacMan;
import gamelogic.entities.moving.ghosts.*;
import gui.Main;
import gui.mainmenu.MainMenuFrame;
import utility.ResourceHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import static utility.GameConstants.*;

/**
 * This class is responsible for the visual representation of the game.
 */
public class GamePanel extends JPanel {
    private PacMan pacMan;
    private final ArrayList<Ghost> ghosts = new ArrayList<>();
    private final ArrayList<ArrayList<Entity>> level = ResourceHandler.getCurrentLevel();
    private final HashMap<String, Point> locations = ResourceHandler.getInitialLocations();
    private final transient EntityObserver observer = new EntityObserver();
    Timer timer;
    JLabel scoreLabel;

    /**
     * Constructor
     */
    public GamePanel() {
        setBackground(Color.BLACK);
        createScoreLabel();
        initEntities();
        addObservers();
        timer = new Timer(TIMER_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
                if (pacMan.isDead()) {
                    JOptionPane.showMessageDialog(GamePanel.this, "You died! Your score was: " + observer.getScore());
                    Main.setDisplayedFrame(new MainMenuFrame());
                    ((Timer) e.getSource()).stop();
                } else if (observer.getFoodCount() == 0) {
                    JOptionPane.showMessageDialog(GamePanel.this, "You won! Your score was: " + observer.getScore());
                    Main.setDisplayedFrame(new MainMenuFrame());
                    ((Timer) e.getSource()).stop();
                } else {
                    scoreLabel.setText("Score: " + observer.getScore());
                    pacMan.move();
                    for (Ghost ghost : ghosts) {
                        ghost.move();
                    }
                }
            }
        });
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pacMan.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if(timer.isRunning()){
                        timer.stop();
                        scoreLabel.setText("PAUSED");
                    }
                    else{
                        timer.start();
                        scoreLabel.setText("Score: " + observer.getScore());
                    }
                }
            }
        });

        setFocusable(true);
    }

    /**
     * @return the preferred size of the panel
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COLUMN_COUNT * CELL_SIZE,ROW_COUNT * CELL_SIZE);
    }

    /**
     * @param g  the <code>Graphics</code> context in which to paint
     */
    @Override
    public void paint(Graphics g){
        super.paint(g);
        for(ArrayList<Entity> yAxis : level){
            for(Entity e : yAxis){
                e.draw(g);
            }
        }
    }
    /**
     * Creates the score label
     */
    private void createScoreLabel(){
        scoreLabel = new JLabel();
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(ResourceHandler.getPacFont().deriveFont(20F));
        add(scoreLabel);
    }

    /**
     * Initializes the entities
     */
    private void initEntities(){
        // Initialize PacMan and the ghosts
        // could be better...
        pacMan = new PacMan(locations.get("pacman").x, locations.get("pacman").y);
        level.get(pacMan.getY()).set(pacMan.getX(), pacMan);

        Blinky blinky = new Blinky(locations.get("blinky").x, locations.get("blinky").y);
        level.get(blinky.getY()).set(blinky.getX(), blinky);

        Inky inky = new Inky(locations.get("inky").x, locations.get("inky").y);
        inky.setObservedBlinky(blinky);
        level.get(inky.getY()).set(inky.getX(), inky);

        Pinky pinky = new Pinky(locations.get("pinky").x, locations.get("pinky").y);
        level.get(pinky.getY()).set(pinky.getX(), pinky);

        Clyde clyde = new Clyde(locations.get("clyde").x, locations.get("clyde").y);
        level.get(clyde.getY()).set(clyde.getX(), clyde);

        ghosts.add(blinky);
        ghosts.add(inky);
        ghosts.add(pinky);
        ghosts.add(clyde);

        observer.setFoodCount(ResourceHandler.getFoodCount());
    }

    /**
     * Adds the observers
     */
    private void addObservers(){
        for(Ghost ghost : ghosts){
            pacMan.addObserver(ghost);
        }
        for(ArrayList<Entity> yAxis : level){
            for(Entity e : yAxis){
                e.addObserver(observer);
            }
        }
    }




}
