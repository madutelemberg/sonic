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
    private boolean gameOver = false; // Controle de fim de jogo
    private final int finishLine = 750; // Linha de chegada

    // Obstáculos (retângulos)
    private final int[][] obstacles = {
        {200, 320, 50, 20}, // {x, y, largura, altura}
        {400, 320, 50, 20},
        {600, 320, 50, 20}
    };

    public SonicGame() {
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.CYAN);
        addKeyListener(this);
        setFocusable(true);

        // Carregar a imagem do Sonic
        try {
            sonicImage = ImageIO.read(new File("sonic.png")); // Substitua pelo caminho da sua imagem
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar a imagem do Sonic. Certifique-se de que o arquivo 'sonic.png' está no diretório correto.");
        }

        // Carregar a imagem de fundo
        try {
            backgroundImage = ImageIO.read(new File("map.jpg")); // Substitua pelo caminho da sua imagem de fundo
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar a imagem de fundo. Certifique-se de que o arquivo 'background.jpg' está no diretório correto.");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenhar a imagem de fundo
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null); // Redimensiona a imagem para caber na tela
        } else {
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Desenhar o chão
        g.setColor(Color.GREEN);
        g.fillRect(0, 340, 800, 60);

        // Desenhar os obstáculos
        g.setColor(Color.RED);
        for (int[] obstacle : obstacles) {
            g.fillRect(obstacle[0], obstacle[1], obstacle[2], obstacle[3]);
        }

        // Desenhar a linha de chegada
        g.setColor(Color.YELLOW);
        g.fillRect(finishLine, 300, 10, 40);

        // Desenhar a imagem do Sonic
        if (sonicImage != null) {
            g.drawImage(sonicImage, x, y, 40, 40, null); // Ajuste o tamanho da imagem (40x40)
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, 40, 40);
        }

        // Verificar se o jogo acabou
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Você venceu!", 300, 200);
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
                x = -40; // Retorna pela esquerda
            } else if (x < -40) {
                x = getWidth(); // Retorna pela direita
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
                if (x + 40 > obstacle[0] && x < obstacle[0] + obstacle[2] &&
                    y + 40 > obstacle[1] && y < obstacle[1] + obstacle[3]) {
                    gameOver = true; // Fim de jogo se colidir com um obstáculo
                }
            }

            // Verificar se o personagem atingiu a linha de chegada
            if (x + 40 > finishLine && y + 40 > 300) {
                gameOver = true; // Fim de jogo ao atingir a linha de chegada
            }

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