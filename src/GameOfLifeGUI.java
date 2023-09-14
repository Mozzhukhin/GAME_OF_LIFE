import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class GameOfLifeGUI extends JFrame {
    private final int rows = 40;
    private final int cols = 40;
    private final int cellSize = 15;
    private boolean[][] grid = new boolean[rows][cols];
    private boolean isRunning = false;
    private int generationDelay = 500; // Задержка в миллисекундах по умолчанию
    private final JPanel gridPanel;
    private final JButton startButton;
    private final JButton clearButton;
    private final JButton randomButton;
    private final JButton applyDelayButton;
    private final JSlider delaySlider;
    private Timer timer;

    public GameOfLifeGUI() {
        setTitle("Conway's Game of Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(rows, cols));
        add(gridPanel, BorderLayout.CENTER);

        initializeGrid();

        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / cellSize;
                int col = e.getX() / cellSize;
                grid[row][col] = !grid[row][col];
                updateGridUI();
            }
        });

        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isRunning) {
                    startSimulation();
                } else {
                    stopSimulation();
                }
            }
        });

        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearGrid();
            }
        });

        randomButton = new JButton("Random");
        randomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomizeGrid();
            }
        });

        applyDelayButton = new JButton("Apply Delay");
        applyDelayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generationDelay = delaySlider.getValue();
                if (isRunning) {
                    timer.setDelay(generationDelay);
                }
            }
        });

        delaySlider = new JSlider(JSlider.HORIZONTAL, 100, 1000, generationDelay);
        delaySlider.setMajorTickSpacing(200);
        delaySlider.setMinorTickSpacing(50);
        delaySlider.setPaintTicks(true);
        delaySlider.setPaintLabels(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(randomButton);
        buttonPanel.add(applyDelayButton);
        add(buttonPanel, BorderLayout.SOUTH);

        JPanel sliderPanel = new JPanel();
        sliderPanel.add(new JLabel("Generation Delay:"));
        sliderPanel.add(delaySlider);
        add(sliderPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void initializeGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col] = false;
            }
        }
        updateGridUI();
    }

    public void startSimulation() {
        isRunning = true;
        startButton.setText("Stop");
        timer = new Timer(generationDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                evolveGrid();
                updateGridUI();
            }
        });
        timer.start();
    }

    public void stopSimulation() {
        isRunning = false;
        startButton.setText("Start");
        timer.stop();
    }

    public void clearGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col] = false;
            }
        }
        updateGridUI();
    }

    public void randomizeGrid() {
        Random random = new Random();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col] = random.nextBoolean();
            }
        }
        updateGridUI();
    }

    public void evolveGrid() {
        boolean[][] newGrid = new boolean[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int liveNeighbors = countLiveNeighbors(row, col);
                if (grid[row][col]) {
                    newGrid[row][col] = liveNeighbors == 2 || liveNeighbors == 3;
                } else {
                    newGrid[row][col] = liveNeighbors == 3;
                }
            }
        }
        grid = newGrid;
    }

    public int countLiveNeighbors(int row, int col) {
        int liveNeighbors = 0;
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int newRow = row + dr[i];
            int newCol = col + dc[i];

            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                if (grid[newRow][newCol]) {
                    liveNeighbors++;
                }
            }
        }

        return liveNeighbors;
    }

    public void updateGridUI() {
        gridPanel.removeAll();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(cellSize, cellSize));
                cell.setBackground(grid[row][col] ? Color.BLACK : Color.WHITE);
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                gridPanel.add(cell);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameOfLifeGUI();
            }
        });
    }
}