import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;


public class AR5_GUI{

    // Insert text file here!
    final AR5 ar5 = new AR5("test.txt");
    
    /**
     * @param args
     */
    private static JComponent textLabel[];
    private static JFrame frame;
    private static JTextComponent textBox[];
    private static JPanel panel[];
    private static JPanel mainPanel;
    private static JTextArea scrollMemoryArea;
    private static JScrollPane scrollingArea;

    public AR5_GUI(){
        // Instance fields declaration
        textLabel = new JComponent[15];
        textBox = new JTextComponent[14];
        panel = new JPanel[7];
        mainPanel = new JPanel();
        scrollMemoryArea = new JTextArea(6, 15);
        scrollingArea = new JScrollPane(scrollMemoryArea);

        // Labels
        textLabel[0] = new JLabel ("IR: ");
        textLabel[1] = new JLabel ("PC: ");
        textLabel[2] = new JLabel ("A: ");
        textLabel[3] = new JLabel ("R0: ");
        textLabel[4] = new JLabel ("R1: ");
        textLabel[5] = new JLabel ("R2: ");
        textLabel[6] = new JLabel ("R3: ");
        textLabel[7] = new JLabel ("R4: ");
        textLabel[8] = new JLabel ("R5: ");
        textLabel[9] = new JLabel ("R6: ");
        textLabel[10] = new JLabel ("R7: ");
        textLabel[11] = new JLabel ("SR: ");
        textLabel[12] = new JLabel ("Keyboard: ");
        textLabel[13] = new JLabel ("Display: ");
        textLabel[14] = new JLabel ("Memory: ");

        // Paint each text label white
        for(int i = 0 ; i < textLabel.length; i++) {
            textLabel[i].setForeground(Color.WHITE);
        }

        // Initializing each text field
        for(int i = 0 ; i < textBox.length; i++) {
            textBox[i] = new JTextField("0000000");
            textBox[i].setForeground(Color.DARK_GRAY);
            textBox[i].setEditable(false);
        }

        // Initializing each panel
        for (int i = 0; i < panel.length; i++) {
            panel[i] = new JPanel();
        }

        // Main panel
        GridLayout mainLayout = new GridLayout(1, 2, 20, 0);
        mainPanel.setLayout (mainLayout);
        mainPanel.setBackground(Color.DARK_GRAY);

        // Grid layout (main panel west)
        GridLayout grid1 = new GridLayout(11, 2, 0, 0);
        panel[0].setLayout(grid1);
        panel[0].setBackground(Color.DARK_GRAY);
        mainPanel.add(panel[0]);

        // Border layout
        BorderLayout border1 = new BorderLayout();
        panel[1].setLayout(border1);
        panel[1].setBackground(Color.DARK_GRAY);
        mainPanel.add(panel[1]);

        // North panel to the east main panel
        GridLayout grid2 = new GridLayout(3, 2, 1 , 1);
        panel[2].setLayout(grid2);
        panel[2].setBackground(Color.DARK_GRAY);
        panel[1].add(panel[2], BorderLayout.NORTH);

        Border etched = BorderFactory.createEtchedBorder();
        Border titled = BorderFactory.createTitledBorder(etched);
        mainPanel.setBorder(titled);


        // Adding each of the grid components (west)
        for (int i = 0; i < textBox.length; i++){
            panel[0].add(textLabel[i]);
            panel[0].add(textBox[i]);
        }

        // Adding each of the grid components (north)
        for (int i = 11; i < textBox.length ; i++){
            panel[2].add(textLabel[i]);
            panel[2].add(textBox[i]);
        }

        // Memory 
        BorderLayout border2 = new BorderLayout();
        panel[3].setLayout(border2);
        panel[1].add(panel[3], BorderLayout.CENTER);
        panel[3].add(textLabel[14], BorderLayout.NORTH);
        panel[3].add(scrollingArea, BorderLayout.EAST);
        panel[3].add(panel[5], BorderLayout.WEST);
        panel[3].setBackground(Color.DARK_GRAY);

        scrollMemoryArea.setEditable(false);
        scrollMemoryArea.setForeground(Color.DARK_GRAY);
        scrollMemoryArea.setBackground(Color.WHITE);

        final JButton run = new JButton("RUN");
        final JButton step = new JButton("STEP");

        // Positioning buttons in GUI
        GridLayout grid3 = new GridLayout(2, 1, 0, 0);
        panel[5].setLayout(grid3);
        panel[5].add(panel[6], BorderLayout.NORTH);
        panel[5].add(panel[4], BorderLayout.SOUTH);
        panel[5].setBackground(Color.DARK_GRAY);
        panel[6].setBackground(Color.DARK_GRAY);
        panel[4].setLayout(grid3);
        panel[4].add(run, BorderLayout.NORTH);
        panel[4].add(step, BorderLayout.SOUTH);
        panel[4].setBackground(Color.DARK_GRAY);

        // Whole window
        frame = new JFrame ("Processor Simulator (RISC AR5)");
        frame.setSize(600, 350);
        frame.getContentPane().add(mainPanel);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible (true);

        updateMemory();

        step.addActionListener(new ActionListener() {

            //Execute when button is pressed
            public void actionPerformed(ActionEvent e) {
                ar5.stepMode();
                scrollMemoryArea.setText("");
                updateMemory();

                for (int i = 0; i < ar5.getValues().length; i++)
                    textBox[i].setText(ar5.getValues()[i]);

                if (ar5.getIsStop()){
                    step.setEnabled(false);
                    run.setEnabled(false);
                }
            }
        });


        run.addActionListener(new ActionListener() {

            //Execute when button is pressed
            public void actionPerformed(ActionEvent e) {
                ar5.runMode();
                scrollMemoryArea.setText("");
                updateMemory();

                for (int i = 0; i < ar5.getValues().length; i++) {
                    textBox[i].setText(ar5.getValues()[i]);
                }

                if (ar5.stopInstruction() == true){
                    step.setEnabled(false);
                    run.setEnabled(false);
                }
            }
        });

    }


    /**
     * Updates memory.
     */
    public void updateMemory(){
        for (int i = 0; i < 256; i = i+2) {
            String ihex = Integer.toHexString(i).toUpperCase();
            String str;

            if (ihex.length()==1) {
                str = "0"+ihex+"\t"+AR5.getMemory()[i]+AR5.getMemory()[i+1]+"\n";
            }
            else if(AR5.getMemory()[i] == null && AR5.getMemory()[i + 1] == null ) {
                str = ihex+"\t"+"00"+"00"+"\n";
            }
            else if(AR5.getMemory()[i] != null && AR5.getMemory()[i + 1] == null ) {
                str = ihex+"\t"+AR5.getMemory()[i]+"00"+"\n";
            }
            else if(AR5.getMemory()[i] == null && AR5.getMemory()[i + 1] != null ) {
                str = ihex+"\t"+"00"+AR5.getMemory()[i+1]+"\n";
            }
            else {
                str = ihex+"\t"+AR5.getMemory()[i]+AR5.getMemory()[i+1]+"\n";
            }

            scrollMemoryArea.append(str);
        }

    }
}



