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


public class AR5_GUI {

    /**
     * @param args
     */
    private static JComponent jcomp[];
    private static JFrame frame;
    private static JTextComponent jtcomp[];
    private static JPanel panel[];
    private static JPanel mainPanel;
    private static JTextArea memoryArea;
    private static JScrollPane scrollingArea;

    public AR5_GUI(){

        // Insert Text File Here!!!
        final AR5 processor = new AR5("test1.txt");

        // Instance fields declaration
        jcomp = new JComponent[15];
        jtcomp = new JTextComponent[14];
        panel = new JPanel[7];
        mainPanel = new JPanel();
        memoryArea = new JTextArea(6, 15);
        scrollingArea = new JScrollPane(memoryArea);

        // Labels
        jcomp[0] = new JLabel ("IR: ");
        jcomp[1] = new JLabel ("PC: ");
        jcomp[2] = new JLabel ("A: ");
        jcomp[3] = new JLabel ("R0: ");
        jcomp[4] = new JLabel ("R1: ");
        jcomp[5] = new JLabel ("R2: ");
        jcomp[6] = new JLabel ("R3: ");
        jcomp[7] = new JLabel ("R4: ");
        jcomp[8] = new JLabel ("R5: ");
        jcomp[9] = new JLabel ("R6: ");
        jcomp[10] = new JLabel ("R7: ");
        jcomp[11] = new JLabel("SR: ");
        jcomp[12] = new JLabel("Keyboard: ");
        jcomp[13] = new JLabel("Display: ");
        jcomp[14] = new JLabel("Memory: ");

        // Initializing each text field
        for(int i = 0 ; i <=13; i++) {
            jtcomp[i] = new JTextField("0000000");
            jtcomp[i].setForeground(Color.DARK_GRAY);
            jtcomp[i].setEditable(false);
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
        for (int i = 0; i <= 10; i++){
            panel[0].add(jcomp[i]);
            panel[0].add(jtcomp[i]);
        }

        // Adding each of the grid components (north)
        for (int i = 11; i <=13 ; i++){
            panel[2].add(jcomp[i]);
            panel[2].add(jtcomp[i]);
        }

        // Memory
        BorderLayout border2 = new BorderLayout();
        panel[3].setLayout(border2);
        panel[1].add(panel[3], BorderLayout.CENTER);
        panel[3].add(jcomp[14], BorderLayout.NORTH);
        panel[3].add(scrollingArea, BorderLayout.EAST);
        panel[3].add(panel[5], BorderLayout.WEST);
        panel[3].setBackground(Color.DARK_GRAY);

        memoryArea.setEditable(false);
        memoryArea.setForeground(Color.DARK_GRAY);
        memoryArea.setBackground(Color.WHITE);

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
                processor.stepMode();
                memoryArea.setText("");
                updateMemory();

                for (int i = 0; i < processor.getValues().length; i++)
                    jtcomp[i].setText(processor.getValues()[i]);

                if (processor.getIsStop()){
                    step.setEnabled(false);
                    run.setEnabled(false);
                }
            }
        });


        // Action Listener for 'run' button
        run.addActionListener(new ActionListener() {

            //Execute when button is pressed
            public void actionPerformed(ActionEvent e) {
                processor.runMode();
                memoryArea.setText("");
                updateMemory();

                for (int i = 0; i < processor.getValues().length; i++) {
                    jtcomp[i].setText(processor.getValues()[i]);
                }

                if (processor.stopInstruction() == true){
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
                str = "0"+ihex+"\t"+ AR5.getMemory()[i]+ AR5.getMemory()[i+1]+"\n";
            }
            else if(AR5.getMemory()[i] == null && AR5.getMemory()[i + 1] == null ) {
                str = ihex+"\t"+"00"+"00"+"\n";
            }
            else if(AR5.getMemory()[i] != null && AR5.getMemory()[i + 1] == null ) {
                str = ihex+"\t"+ AR5.getMemory()[i]+"00"+"\n";
            }
            else if(AR5.getMemory()[i] == null && AR5.getMemory()[i + 1] != null ) {
                str = ihex+"\t"+"00"+ AR5.getMemory()[i+1]+"\n";
            }
            else {
                str = ihex+"\t"+ AR5.getMemory()[i]+ AR5.getMemory()[i+1]+"\n";
            }

            memoryArea.append(str);
        }

    }
}


