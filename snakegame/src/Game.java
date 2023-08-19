import java.util.*;
import javax.imageio.ImageIO;
import java.util.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

@SuppressWarnings("serial")
class Game extends JPanel {
	//java.util.Timer: A facility for threads to schedule tasks for future execution in a background thread.
    private Timer timer;
    private Snake snake;
    private Point cherry1;
    private Point cherry2;
    private int points = 0;
    private int best = 0;
    private BufferedImage image;
    private BufferedImage image2;
    private GameStatus status;
    private boolean didLoadCherryImage = true;
    private boolean didLoadSnakeHeadImage = true;

    private static Font FONT_M = new Font("MV Boli", Font.PLAIN, 24);
    private static Font FONT_M_ITALIC = new Font("MV Boli", Font.ITALIC, 24);
    private static Font FONT_L = new Font("MV Boli", Font.PLAIN, 84);
    private static Font FONT_XL = new Font("MV Boli", Font.PLAIN, 150);
    private static int WIDTH = 760;
    private static int HEIGHT = 520;
    private static int DELAY = 50;

    // Constructor
    public Game() {
        try {
            image = ImageIO.read(new File("C:/Users/hp/OneDrive/Desktop/cherry.png"));
        } catch (IOException e) {
          didLoadCherryImage = false;
        }
        try {
            image2 = ImageIO.read(new File("C:/Users/hp/OneDrive/Desktop/snakehead2.png"));
        } catch (IOException e) {
          didLoadSnakeHeadImage = false;
        }

        addKeyListener(new KeyListener());
        setFocusable(true);
        setBackground(new Color(27, 30, 35));
        setDoubleBuffered(true);

        snake = new Snake(WIDTH / 2, HEIGHT / 2);
        status = GameStatus.NOT_STARTED;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        render(g);

        Toolkit.getDefaultToolkit().sync();
    }

    // Render the game
    private void update() {
        snake.move();

        if (cherry1 != null && snake.getHead().intersects(cherry1, 20)) {
            snake.addTail();
            
            cherry1 = null;
            spawnCherry1();
            
            points++;
        }

        if (cherry1 == null) {
            spawnCherry1();
          
        }
        if (cherry2 != null && snake.getHead().intersects(cherry2, 20)) {
            snake.addTail();
            
            cherry2 = null;
            spawnCherry2();
            
            points++;
        }

        if (cherry2 == null) {
            spawnCherry2();
          
        }

        checkForGameOver();
    }
    
    private void reset() {
        points = 0;
        cherry1 = null;
        cherry2 = null;
        snake = new Snake(WIDTH / 2, HEIGHT / 2);
        setStatus(GameStatus.RUNNING);
    }
    
    private void setStatus(GameStatus newStatus) {
        switch(newStatus) {
            case RUNNING:
                timer = new Timer();
                timer.schedule(new GameLoop(), 0, DELAY);
                break;
            case PAUSED:
                timer.cancel();
            case GAME_OVER:
                timer.cancel();
                best = points > best ? points : best;
                break;
		default:
			break;
        }

        status = newStatus;
    }

    private void togglePause() { 
        setStatus(status == GameStatus.PAUSED ? GameStatus.RUNNING : GameStatus.PAUSED);
    }
    public static BufferedImage rotate(BufferedImage img, int x)
    {
 
        // Getting Dimensions of image
        int width = img.getWidth();
        int height = img.getHeight();
 
        // Creating a new buffered image
        BufferedImage newImage = new BufferedImage(
            img.getWidth(), img.getHeight(), img.getType());
 
        // creating Graphics in buffered image
        Graphics2D g2 = newImage.createGraphics();
 
        // Rotating image by degrees using toradians()
        // method
        // and setting new dimension t it
        g2.rotate(Math.toRadians(x), width / 2,
                  height / 2);
        g2.drawImage(img, null, 0, 0);
 
        // Return rotated buffer image
        return newImage;
    }

    // Check if the snake has hit the wall or itself
    private void checkForGameOver() { 
        Point head = snake.getHead();
        boolean hitBoundary = head.getX() <= 20
            || head.getX() >= WIDTH + 10
            || head.getY() <= 40
            || head.getY() >= HEIGHT + 30;

        boolean ateItself = false;

        for(Point t : snake.getTail()) {
            ateItself = ateItself || head.equals(t);
        }

        if (hitBoundary || ateItself) {
            setStatus(GameStatus.GAME_OVER);
        }
    }

    // Spawn a cherry at a random location
    public void drawCenteredString(Graphics g, String text, Font font, int y) { 
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (WIDTH - metrics.stringWidth(text)) / 2;

        g.setFont(font);
        g.drawString(text, x, y);
    }

    private void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.lightGray);
        g2d.setFont(FONT_M);

        if (status == GameStatus.NOT_STARTED) {
          drawCenteredString(g2d, "SNAKE", FONT_XL, 200);
          drawCenteredString(g2d, "GAME", FONT_XL, 300);
          drawCenteredString(g2d, "Press  any  key  to  begin", FONT_M_ITALIC, 350);

          return;
        }

        Point p = snake.getHead();

        g2d.drawString("SCORE: " + String.format ("%02d", points), 20, 30);
        g2d.drawString("BEST: " + String.format ("%02d", best), 630, 30);

        if (cherry1 != null) {
          if (didLoadCherryImage) {
            g2d.drawImage(image, cherry1.getX()-10, cherry1.getY()-11, 30, 30, null);
          } else {
            g2d.setColor(Color.BLUE);
            g2d.fillOval(cherry1.getX()-10, cherry1.getY()-10, 10, 10);
            g2d.setColor(Color.BLUE);
          }
        }
        if (cherry2 != null) {
            if (didLoadCherryImage) {
              g2d.drawImage(image, cherry2.getX()-10, cherry2.getY()-11, 30, 30, null);
            } else {
              g2d.setColor(Color.BLUE);
              g2d.fillOval(cherry2.getX()-10, cherry2.getY()-10, 10, 10);
              g2d.setColor(Color.BLUE);
            }
          }
 
        if (didLoadSnakeHeadImage) {
            g2d.drawImage(rotate(image2,270), snake.getHead().getX(), snake.getHead().getY()-5, 17, 17,null);
          }
        if (status == GameStatus.GAME_OVER) {
            drawCenteredString(g2d, "Press  enter  to  start  again", FONT_M_ITALIC, 330);
            drawCenteredString(g2d, "GAME OVER", FONT_L, 300);
        }

        if (status == GameStatus.PAUSED) {
            g2d.drawString("Paused", 600, 14);
        }

        g2d.setColor(new Color(205, 219, 57));
        g2d.fillRect(p.getX(), p.getY(), 10, 10);

        for(int i = 0, size = snake.getTail().size(); i < size; i++) {
            Point t = snake.getTail().get(i);

            g2d.fillRect(t.getX(), t.getY(), 10, 10);
        }

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(20, 40, WIDTH, HEIGHT);
    }

    // spawn cherry in random position
    public void spawnCherry1() {
        cherry1 = new Point((new Random()).nextInt(WIDTH - 60) + 20,
            (new Random()).nextInt(HEIGHT - 60) + 40);
    }
     public void spawnCherry2() {
            cherry2 = new Point((new Random()).nextInt(WIDTH - 60) + 20,
                (new Random()).nextInt(HEIGHT - 60) + 40);
    }

    // game loop
    private class KeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (status == GameStatus.RUNNING) {
                switch(key) {
                    case KeyEvent.VK_LEFT:
                    	snake.turn(Direction.LEFT);
                    	break;
                    case KeyEvent.VK_RIGHT: 
                    	snake.turn(Direction.RIGHT); 
                    	break;
                    case KeyEvent.VK_UP:
                    	snake.turn(Direction.UP); 
                    	break;
                    case KeyEvent.VK_DOWN: 
                    	snake.turn(Direction.DOWN); 
                    	break;
                }
            }

            if (status == GameStatus.NOT_STARTED) {
                setStatus(GameStatus.RUNNING);
            }

            if (status == GameStatus.GAME_OVER && key == KeyEvent.VK_ENTER) {
                reset();
            }

            if (key == KeyEvent.VK_P) {
                togglePause();
            }
        }
    }

    private class GameLoop extends java.util.TimerTask {
        public void run() {
            update();
            repaint();
        }
    }
}


enum GameStatus 
{ 
    NOT_STARTED, RUNNING, PAUSED, GAME_OVER
}

// direction of snake
enum Direction { 
    UP, DOWN, LEFT, RIGHT;
    
    public boolean isX() {
        return this == LEFT || this == RIGHT;
    }
    
    public boolean isY() {
        return this == UP || this == DOWN;
    }
}


class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    public void move(Direction d, int value) {
        switch(d) {
            case UP: this.y -= value; break;
            case DOWN: this.y += value; break;
            case RIGHT: this.x += value; break;
            case LEFT: this.x -= value; break;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point setX(int x) {
        this.x = x;

        return this;
    }

    public Point setY(int y) {
        this.y = y;

        return this;
    }

    public boolean equals(Point p) {
        return this.x == p.getX() && this.y == p.getY();
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean intersects(Point p) {
        return intersects(p, 10);
    }

    public boolean intersects(Point p, int tolerance) {
        int diffX = Math.abs(x - p.getX());
        int diffY = Math.abs(y - p.getY());

        return this.equals(p) || (diffX <= tolerance && diffY <= tolerance);
    }
}

class Snake {
    private Direction direction;
    private Point head;
    private ArrayList<Point> tail;
    
    public Snake(int x, int y) {
        this.head = new Point(x, y);
        this.direction = Direction.RIGHT;
        this.tail = new ArrayList<Point>();
        
        this.tail.add(new Point(0, 0));
        this.tail.add(new Point(0, 0));
        this.tail.add(new Point(0, 0));
    }

    public void move() {
        ArrayList<Point> newTail = new ArrayList<Point>();
        
        for (int i = 0, size = tail.size(); i < size; i++) {
            Point previous = i == 0 ? head : tail.get(i - 1);

            newTail.add(new Point(previous.getX(), previous.getY()));
        }
        
        this.tail = newTail;
        
        this.head.move(this.direction, 10);
    }
    
    public void addTail() {
        this.tail.add(new Point(-10, -10));
    }
    
    public void turn(Direction d) {       
        if (d.isX() && direction.isY() || d.isY() && direction.isX()) {
           direction = d; 
        }       
    }
    
    public ArrayList<Point> getTail() {
        return this.tail;
    }
    
    public Point getHead() {
        return this.head;
    }
}




    