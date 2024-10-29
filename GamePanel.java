import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    int score = 0;
    Image car_obstacle[] = new Image[3]; // 26 x 58 pixels
    Image car_player; // 26 x 59 pixels
    Image road; // 500 x 600 i.e. same as our Game Panel
    int panelWidth, panelHeight;
    int player_x_coord, player_y_coord;
    Timer timer;
    int x_lane[] = { 37, 137, 237, 337, 437 };
    int obstacle_velocity = 2;
    int y_axis_row = 42;
    ArrayList<Integer> obstacles_lane_1 = new ArrayList<Integer>();
    ArrayList<Integer> obstacles_lane_2 = new ArrayList<Integer>();
    ArrayList<Integer> obstacles_lane_3 = new ArrayList<Integer>();
    ArrayList<Integer> obstacles_lane_4 = new ArrayList<Integer>();
    ArrayList<Integer> obstacles_lane_5 = new ArrayList<Integer>();

    GamePanel() {
        panelWidth = 500;
        panelHeight = 600;

        this.setPreferredSize(new Dimension(panelWidth, panelHeight));
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow(); // Request focus for key events
        this.addKeyListener(this);

        car_player = new ImageIcon("assets/car_player.png").getImage();
        road = new ImageIcon("assets/road.png").getImage();
        car_obstacle[0] = new ImageIcon("assets/car_obstacle0.png").getImage();
        car_obstacle[1] = new ImageIcon("assets/car_obstacle1.png").getImage();
        car_obstacle[2] = new ImageIcon("assets/car_obstacle2.png").getImage();

        player_x_coord = (panelWidth - car_player.getWidth(null)) / 2;
        player_y_coord = panelHeight - 60;

        timer = new Timer(10, this);
        timer.start();
    }

    public void paint(Graphics g) {
        super.paint(g); // paint background
        // Graphics2D g2D = (Graphics2D) g;
        g.drawImage(road, 0, 0, null);
        g.drawImage(car_player, player_x_coord, player_y_coord, null);

        int no_of_obstacles_lane1 = obstacles_lane_1.size();
        int no_of_obstacles_lane2 = obstacles_lane_2.size();
        int no_of_obstacles_lane3 = obstacles_lane_3.size();
        int no_of_obstacles_lane4 = obstacles_lane_4.size();
        int no_of_obstacles_lane5 = obstacles_lane_5.size();

        for (int i = 0; i < no_of_obstacles_lane1; i++) {
            g.drawImage(car_obstacle[0], x_lane[0], obstacles_lane_1.get(i), null);
        }

        for (int i = 0; i < no_of_obstacles_lane2; i++) {
            g.drawImage(car_obstacle[1], x_lane[1], obstacles_lane_2.get(i), null);
        }

        for (int i = 0; i < no_of_obstacles_lane3; i++) {
            g.drawImage(car_obstacle[2], x_lane[2], obstacles_lane_3.get(i), null);
        }

        for (int i = 0; i < no_of_obstacles_lane4; i++) {
            g.drawImage(car_obstacle[0], x_lane[3], obstacles_lane_4.get(i), null);
        }

        for (int i = 0; i < no_of_obstacles_lane5; i++) {
            g.drawImage(car_obstacle[1], x_lane[4], obstacles_lane_5.get(i), null);
        }

        check_collision();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Each lane is 100 pixels wide in the road.png image
        // Also check if the player car is going out of the frame
        int player_x_movement = 100;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: {
                player_x_coord = player_x_coord - player_x_movement;
                if (player_x_coord < 0) {
                    player_x_coord = player_x_coord + player_x_movement;
                    break;
                }
                repaint();
                break;
            }

            case KeyEvent.VK_RIGHT: {
                player_x_coord = player_x_coord + player_x_movement;
                if (player_x_coord > panelWidth) {
                    player_x_coord = player_x_coord - player_x_movement;
                    break;
                }
                repaint();
                break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Integer> multipliers = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 0, 0, 0, 0));
        Collections.shuffle(multipliers);

        if (y_axis_row == 42) {
            y_axis_row = -58;
            if (multipliers.get(0) == 1)
                obstacles_lane_1.add(y_axis_row);
            if (multipliers.get(1) == 1)
                obstacles_lane_2.add(y_axis_row);
            if (multipliers.get(2) == 1)
                obstacles_lane_3.add(y_axis_row);
            if (multipliers.get(3) == 1)
                obstacles_lane_4.add(y_axis_row);
            if (multipliers.get(4) == 1)
                obstacles_lane_5.add(y_axis_row);
        }

        y_axis_row++;

        for (int i = 0; i < obstacles_lane_1.size(); i++) {
            obstacles_lane_1.set(i, obstacles_lane_1.get(i) + obstacle_velocity);
        }

        for (int i = 0; i < obstacles_lane_2.size(); i++) {
            obstacles_lane_2.set(i, obstacles_lane_2.get(i) + obstacle_velocity);
        }

        for (int i = 0; i < obstacles_lane_3.size(); i++) {
            obstacles_lane_3.set(i, obstacles_lane_3.get(i) + obstacle_velocity);
        }

        for (int i = 0; i < obstacles_lane_4.size(); i++) {
            obstacles_lane_4.set(i, obstacles_lane_4.get(i) + obstacle_velocity);
        }

        for (int i = 0; i < obstacles_lane_5.size(); i++) {
            obstacles_lane_5.set(i, obstacles_lane_5.get(i) + obstacle_velocity);
        }

        remove_obstacles();
        repaint();
        // System.out.println(score);
    }

    public void remove_obstacles() {
        if (obstacles_lane_1.size() > 0 && obstacles_lane_1.get(0) > 600) {
            obstacles_lane_1.remove(0);
            playSound("assets/score.wav");
            score++;
        }

        if (obstacles_lane_2.size() > 0 && obstacles_lane_2.get(0) > 600) {
            obstacles_lane_2.remove(0);
            playSound("assets/score.wav");
            score++;
        }

        if (obstacles_lane_3.size() > 0 && obstacles_lane_3.get(0) > 600) {
            obstacles_lane_3.remove(0);
            playSound("assets/score.wav");
            score++;
        }

        if (obstacles_lane_4.size() > 0 && obstacles_lane_4.get(0) > 600) {
            obstacles_lane_4.remove(0);
            playSound("assets/score.wav");
            score++;
        }

        if (obstacles_lane_5.size() > 0 && obstacles_lane_5.get(0) > 600) {
            obstacles_lane_5.remove(0);
            playSound("assets/score.wav");
            score++;
        }
    }

    public void check_collision() {
        // The player car dimensions are 26 x 59
        // The obstacles are 26 x 58

        int lane = (player_x_coord - 37) / 100;

        int player_top_y_coord = player_y_coord;
        int player_bottom_y_coord = player_y_coord + 59;

        int obstacle_top_y_coord;
        int obstacle_bottom_y_coord;

        ArrayList<Integer> currentLaneObstacles = switch (lane) {
            case 0 -> obstacles_lane_1;
            case 1 -> obstacles_lane_2;
            case 2 -> obstacles_lane_3;
            case 3 -> obstacles_lane_4;
            case 4 -> obstacles_lane_5;
            default -> null; // Out of bounds, should not occur if player movement is restricted correctly
        };

        if (currentLaneObstacles != null) {
            // Check collision with up to the first two obstacles in the lane
            for (int i = 0; i < Math.min(2, currentLaneObstacles.size()); i++) {
                obstacle_top_y_coord = currentLaneObstacles.get(i);
                obstacle_bottom_y_coord = obstacle_top_y_coord + 58;

                if (obstacle_top_y_coord <= player_bottom_y_coord && player_top_y_coord <= obstacle_bottom_y_coord) {
                    endGame();
                    return;
                }
            }
        }
    }

    private void endGame() {
        timer.stop();
        JOptionPane.showMessageDialog(null, "Game Over! \n Score: " + score);
        System.exit(0);
    }

    public void playSound(String soundFileName) {
        try {
            // Load the sound file
            File soundFile = new File(soundFileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            // Get a clip resource
            Clip clip = AudioSystem.getClip();

            // Open the audio stream and start playing it
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}