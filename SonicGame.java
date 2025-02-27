import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SonicGame extends JPanel implements KeyListener, Runnable {
    private int x = 50;
    private int y = 300;
    private int velX = 0;
    private int velY = 0;
    private boolean jumping = false;
    private final int gravity = 1;
    private final int jumpStrength = -15;
    private Image sonicImage; // Imagem do Sonic
    private Image backgroundImage; // Imagem de fundo
    private Image groundImage; // Imagem da base
    private Image obstacleImage; // Imagem dos obstáculos
    private Image lifeImage; // Imagem da vida
    private boolean gameOver = false; // Controle de fim de jogo
    private final int finishLine = 750; // Linha de chegada
    private int score = 0; // Pontuação
    private int time = 0; // Tempo
    private int life = 3; // Vida do personagem

    // Obstáculos (posições x, y)
    private final int[][] obstacles = {
        {300, 320}, // {x, y}
        {600, 320},
        {900, 320}
    };

    public SonicGame() {
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.CYAN);
        addKeyListener(this);
        setFocusable(true);

        // Carregar as imagens
        try {
            sonicImage = ImageIO.read(new File("sonic.png")); // Imagem do Sonic
            backgroundImage = ImageIO.read(new File("map2.jpg")); // Imagem de fundo
            groundImage = ImageIO.read(new File("chao.png")); // Imagem da base
            obstacleImage = ImageIO.read(new File("bug3.png")); // Imagem dos obstáculos
            lifeImage = ImageIO.read(new File("vida.png")); // Imagem da vida
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenhar a imagem de fundo
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Desenhar a base
        if (groundImage != null) {
            g.drawImage(groundImage, 0, 340, getWidth(), 60, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(0, 340, getWidth(), 60);
        }

        // Desenhar os obstáculos
        if (obstacleImage != null) {
            for (int[] obstacle : obstacles) {
                g.drawImage(obstacleImage, obstacle[0], obstacle[1], 50, 50, null);
            }
        } else {
            g.setColor(Color.RED);
            for (int[] obstacle : obstacles) {
                g.fillRect(obstacle[0], obstacle[1], 50, 50);
            }
        }

        // Desenhar a linha de chegada
        g.setColor(Color.YELLOW);
        g.fillRect(finishLine, 300, 10, 40);

        // Desenhar o personagem
        if (sonicImage != null) {
            g.drawImage(sonicImage, x, y, 40, 40, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, 40, 40);
        }

        // Desenhar tempo e pontos no canto superior esquerdo
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Tempo: " + time, 10, 30);
        g.drawString("Pontos: " + score, 10, 60);

        // Desenhar a vida no canto superior direito
        if (lifeImage != null) {
            g.drawImage(lifeImage, 700, 10, 30, 30, null);
        }
        g.setColor(Color.RED);
        g.fillRect(740, 10, life * 20, 20); // Barra de vida

        // Verificar se o jogo acabou
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            if (x + 40 > finishLine && y + 40 > 300) {
                g.drawString("Você venceu!", 300, 200); // Mensagem de vitória
            } else {
                g.drawString("Game Over!", 300, 200); // Mensagem de derrota
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            velX = -5;
        }
        if (key == KeyEvent.VK_RIGHT) {
            velX = 5;
        }
        if (key == KeyEvent.VK_SPACE && !jumping) {
            velY = jumpStrength;
            jumping = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            velX = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void run() {
        while (!gameOver) {
            x += velX;
            y += velY;

            // Verificar se o personagem saiu da tela e fazê-lo retornar pelo outro lado
            if (x > getWidth()) {
                x = -40;
            } else if (x < -40) {
                x = getWidth();
            }

            // Aplicar gravidade
            if (y < 300) {
                velY += gravity;
            } else {
                y = 300;
                velY = 0;
                jumping = false;
            }

            // Verificar colisão com obstáculos
            for (int[] obstacle : obstacles) {
                if (x + 40 > obstacle[0] && x < obstacle[0] + 50 &&
                    y + 40 > obstacle[1] && y < obstacle[1] + 50) {
                    gameOver = true; // Fim de jogo imediato ao colidir com um obstáculo
                }
            }

            // Verificar se o personagem atingiu a linha de chegada
            if (x + 40 > finishLine && y + 40 > 300) {
                gameOver = true; // Fim de jogo ao atingir a linha de chegada
            }

            // Atualizar tempo e pontos
            time++;
            score += 1;

            repaint();

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sonic Simplificado");
        SonicGame game = new SonicGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(game).start();
    }
}