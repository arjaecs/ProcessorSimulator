import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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


public class ProcessorSimulatorGUI{

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



	public ProcessorSimulatorGUI(){

		// Instance fields declaration
		jcomp = new JComponent[15];
		jtcomp = new JTextComponent[14];
		panel = new JPanel[4];
		mainPanel = new JPanel();
		memoryArea = new JTextArea(6, 15);
		scrollingArea = new JScrollPane(memoryArea);

		// Labels
		jcomp[0] = new JLabel ("IR");
		jcomp[1] = new JLabel ("PC");
		jcomp[2] = new JLabel ("A");
		jcomp[3] = new JLabel ("R0");
		jcomp[4] = new JLabel ("R1");
		jcomp[5] = new JLabel ("R2");
		jcomp[6] = new JLabel ("R3");
		jcomp[7] = new JLabel ("R4");
		jcomp[8] = new JLabel ("R5");
		jcomp[9] = new JLabel ("R6");
		jcomp[10] = new JLabel ("R7");
		jcomp[11] = new JLabel("SR");
		jcomp[12] = new JLabel("Keyboard");
		jcomp[13] = new JLabel("Display");
		jcomp[14] = new JLabel("Memory");


		// Filling text fields
		for(int i = 0 ; i <=13; i++)
		{			
			jtcomp[i] = new JTextField("0000000");
			jtcomp[i].setForeground(Color.DARK_GRAY);
			jtcomp[i].setEditable(false);
		}

		// creating panels
		for (int k = 0; k <= 3; k++) {
			panel[k] = new JPanel();
		}

		// Main panel
		GridLayout mainLayout = new GridLayout(1, 2, 40, 20);
		mainPanel.setLayout (mainLayout);
		mainPanel.setBackground(Color.DARK_GRAY);

		// Grid Layout - West of Main Panel
		GridLayout grid1 = new GridLayout(11, 2, 0, 0);  // row, column, hgap, vgap
		panel[0].setLayout(grid1);
		panel[0].setBackground(Color.DARK_GRAY);
		mainPanel.add(panel[0]);

		// Border Layout
		BorderLayout border1 = new BorderLayout();
		panel[1].setLayout(border1);
		panel[1].setBackground(Color.DARK_GRAY);
		mainPanel.add(panel[1]);

		// Adding a (NORTH) panel to the Main East Panel
		GridLayout grid2 = new GridLayout(3, 2, 1 , 1);
		panel[2].setLayout(grid2);
		panel[2].setBackground(Color.DARK_GRAY);
		panel[1].add(panel[2], BorderLayout.NORTH);

		// Main Panel Border
		Border etched = BorderFactory.createEtchedBorder();
		Border titled = BorderFactory.createTitledBorder(etched);
		mainPanel.setBorder(titled);


		// Components of grid layout (west of main)
		for(int l = 0; l <= 10; l++){
			panel[0].add(jcomp[l]);
			panel[0].add(jtcomp[l]);
		}

		// Components of grid layout (north of panel[1])
		for(int m = 11; m <=13 ; m++){
			panel[2].add(jcomp[m]);
			panel[2].add(jtcomp[m]);
		}


		// Memorys Panel
		BorderLayout border2 = new BorderLayout();
		panel[3].setLayout(border2);
		panel[1].add(panel[3], BorderLayout.CENTER);
		panel[3].add(jcomp[14], BorderLayout.NORTH);
		panel[3].add(scrollingArea, BorderLayout.CENTER);
		panel[3].setBackground(Color.DARK_GRAY);

		memoryArea.setEditable(false);
		memoryArea.setForeground(Color.DARK_GRAY);
		memoryArea.setBackground(Color.WHITE);


		// JButtons for RUN & STEP
		final JButton run = new JButton("RUN");
		final JButton step = new JButton("STEP");

		panel[1].add(step, BorderLayout.SOUTH);
		panel[3].add(run, BorderLayout.SOUTH);



		// Frame
		frame = new JFrame ("Processor Simulator | RISC AR5");
		frame.setSize(650, 500);
		frame.getContentPane().add(mainPanel);
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible (true);


		/// After creating GUI, a Control Unit object is created
		final ControlUnit cu = new ControlUnit("HH_test1.txt");

		updateMemory();


		// Action Listener for 'step' button
		step.addActionListener(new ActionListener() {

			//Execute when button is pressed
			public void actionPerformed(ActionEvent e)
			{
				cu.stepMode();
				memoryArea.setText("");
				updateMemory();

				for(int i = 0; i < cu.getValues().length; i++)
					jtcomp[i].setText(cu.getValues()[i]);

				if(cu.getIsStop()){
					step.setEnabled(false);
					run.setEnabled(false);
				}
			}
		});    


		// Action Listener for 'run' button
		run.addActionListener(new ActionListener() {

			//Execute when button is pressed
			public void actionPerformed(ActionEvent e)
			{
				cu.runMode();
				memoryArea.setText("");
				updateMemory();

				for(int i = 0; i < cu.getValues().length; i++)
					jtcomp[i].setText(cu.getValues()[i]);

				if(cu.stopInstruction() == true){
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
		for(int i = 0; i < 256; i=i+2){
			String ihex = Integer.toHexString(i).toUpperCase();
			String str;
			if(ihex.length()==1)
				str = "0"+ihex+"\t"+ControlUnit.getMemoryArray()[i]+ControlUnit.getMemoryArray()[i+1]+"\n";
			else if(ControlUnit.getMemoryArray()[i] == null && ControlUnit.getMemoryArray()[i + 1] == null )
				str = ihex+"\t"+"00"+"00"+"\n";
			else if(ControlUnit.getMemoryArray()[i] != null && ControlUnit.getMemoryArray()[i + 1] == null )
				str = ihex+"\t"+ControlUnit.getMemoryArray()[i]+"00"+"\n";
			else if(ControlUnit.getMemoryArray()[i] == null && ControlUnit.getMemoryArray()[i + 1] != null )
				str = ihex+"\t"+"00"+ControlUnit.getMemoryArray()[i+1]+"\n";
			else
				str = ihex+"\t"+ControlUnit.getMemoryArray()[i]+ControlUnit.getMemoryArray()[i+1]+"\n";
			memoryArea.append(str);
		}

	}
}



