import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.awt.image.*;
import java.io.File;


public class FractalExplorer {
    private JButton saveButton;
    private JButton resetButton;
    private JComboBox comboBox;
    private int displaySize;
    private JImageDisplay display;
    private FractalGenerator fractal;
    private Rectangle2D.Double range;
    public FractalExplorer(int dSize) {
        displaySize = dSize;
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();

        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);
    }

    public void createAndShowGUI() {
        display.setLayout(new BorderLayout()); 
        JFrame frame = new JFrame("Фрактал"); 
        frame.add(display, BorderLayout.CENTER);
        resetButton = new JButton("Очистить");
        ButtonHandler resetHandler = new ButtonHandler(); 
        resetButton.addActionListener(resetHandler); 
        MouseHandler click = new MouseHandler(); 
        display.addMouseListener(click); 
        comboBox = new JComboBox();

        FractalGenerator mandelbrot = new Mandelbrot(); 
        comboBox.addItem(mandelbrot); 
        FractalGenerator tricorn = new Tricorn(); 
        comboBox.addItem(tricorn); 
        FractalGenerator burningShip = new BurningShip(); 
        comboBox.addItem(burningShip); 
        ButtonHandler fractalChooser = new ButtonHandler(); 
        comboBox.addActionListener(fractalChooser); 
        JPanel panel = new JPanel(); 
        JLabel label = new JLabel("Выберите фрактал:"); 
        panel.add(label); panel.add(comboBox); 
        frame.add(panel, BorderLayout.NORTH); 
        saveButton = new JButton("Сохранить"); 
        JPanel bottomPanel = new JPanel(); 
        bottomPanel.add(saveButton); 
        bottomPanel.add(resetButton); 
        frame.add(bottomPanel, BorderLayout.SOUTH); 
        ButtonHandler saveHandler = new ButtonHandler(); 
        saveButton.addActionListener(saveHandler); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.pack(); frame.setVisible(true); 
        frame.setResizable(false);
    }

    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (e.getSource() instanceof JComboBox) {
                JComboBox source = (JComboBox) e.getSource();
                fractal = (FractalGenerator) source.getSelectedItem();
                fractal.getInitialRange(range); drawFractal();
            }
            else if (command.equals("Очистить")) {
                fractal.getInitialRange(range); drawFractal();
            }
            else if (command.equals("Сохранить")) {
                JFileChooser fileChooser = new JFileChooser();
                FileFilter extensionFilter = new FileNameExtensionFilter("PNG Images", "png");
                fileChooser.setFileFilter(extensionFilter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int userSelection = fileChooser.showSaveDialog(display);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try { BufferedImage image = display.getImage();
                        ImageIO.write(image, "png", file);
                    }
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(
                                display,
                                exception.getMessage(),
                                "Невозможно сохранить!",
                                JOptionPane.ERROR_MESSAGE );

                    }
                }
            }
        }
    }

    private void drawFractal() {
        for (int x = 0; x < displaySize; x++){
            for (int y = 0; y < displaySize; y++){
                // методы статические
                double xCoord = FractalGenerator.getCoord(range.x,range.x + range.width, displaySize, x);
                double yCoord = FractalGenerator.getCoord(range.y,range.y + range.height, displaySize, y);
                int numIter = fractal.numIterations(xCoord, yCoord);
                if (numIter == -1){
                    display.drawPixel(x, y, 0);
                }
                else {
                    float hue = 0.7f + (float) numIter / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    display.drawPixel(x, y, rgbColor);
                }
            }
        } display.repaint();
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            double xCoord = FractalGenerator.getCoord(range.x,range.x + range.width, displaySize, x);
            int y = e.getY();
            double yCoord = FractalGenerator.getCoord(range.y,range.y + range.height, displaySize, y);
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5); drawFractal();
        }
    }

    public static void main(String[] args) {
        FractalExplorer displayExplorer = new FractalExplorer(800);
        displayExplorer.createAndShowGUI(); displayExplorer.drawFractal();
    }
}
