import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class ControlUnit{

	// instance fields declaration
	private static String[] memory;
	private String instructionRegister;
	private int programCounter;
	private int[] statusRegister;
	private String accumulator;
	private String[] register;
	private String filename;
	private int index;
	private int numberOfInstructions;
	private String keyboard;
	private String display;
	private boolean isStop;

	public ControlUnit(String filename){

		// initializing instance fields

        // 256 addresses of 1 byte each
		memory = new String[256];

		programCounter = 0;

        // 8-BIT accumulator
        accumulator = "00000000";

        //initial instruction to be executed
        instructionRegister = "0000000000000000";

        //Status Register format: ZCNO
		statusRegister = new int[4];

		register = new String[8];
		this.filename = filename;
		index = 0;
		numberOfInstructions = 0;
		display = "";
		isStop = false;

		// initializing registers to "00000000"
		for(int j=0; j < 8; j++)
			register[j] = "00000000";

		// initializing flags to 0
		for(int k=0; k < 4; k++)
			statusRegister[k] = 0;

		// filling memory with what was read from file
		fillMemory();

	}

	/**
	 Fills memory with file data.
	 */
	public void fillMemory(){

		try{

			FileReader reader = new FileReader(filename);
			Scanner in1 = new Scanner(reader);


			while (in1.hasNextLine())
			{
				String instruction = in1.nextLine();
				String byte1 = instruction.substring(0, 2);
				String byte2 = instruction.substring(2, 4);

				memory[index] = byte1;
				index++;
				memory[index] = byte2;
				index++;

				numberOfInstructions++;
			} 

		}
		catch (IOException e) {

			System.out.println("File not found.");
		}	


		// assigning contents of memory to instruction register
		instructionRegister = anyToBin(memory[programCounter], 16) + anyToBin(memory[programCounter+1], 16);
	}


	/**
	 Converts an integer of any base to a binary number.
	  @param x number to be changed , b actual base of the number
	  @ return the binary equivalent number
	 */
	public String anyToBin(String x, int b){
		String bin = Integer.toBinaryString(Integer.parseInt(x, b));

		if(bin.length() < 8) {
			int leadingZeroesNum = 8 - bin.length();
			String zeroes = String.format("%0" + leadingZeroesNum + "d", 0);
			bin = zeroes + bin;
		}
		return bin;
	}


	/**
	 Converts an integer of any base to a binary number.
	  @param x number to be changed , b actual base of the number
	  @ return the binary equivalent number
	 */
	public String anyToBin2(String x, int b){
		String bin = Integer.toBinaryString(Integer.parseInt(x, b));

		if(bin.length() < 16) {
			int leadingZeroesNum = 8 - bin.length();
			String zeroes = String.format("%0" + leadingZeroesNum + "d", 0);
			bin = zeroes + bin;
		}
		return bin;
	}



	/**
	  Converts an integer to a decimal number.
	@param x number to be changed , b actual base of the number
	@ return the decimal equivalent number
	 */
	public int anyToDec(String x, int b){
		return Integer.parseInt(x, b);

	}


	/**
	 Converts an integer of any base to a hexadecimal number.
	@param x number to be changed , b actual base of the number
	@ return the hexadecimal equivalent number
	 */
	public String anyToHex(String x, int b){
		return Integer.toHexString(Integer.parseInt(x, b));
	}


	///--------------------------------------------------------------------------------------------

	/**
	 *  And Instruction.
	 */
	public void andInstruction(){
		String registerBin = instructionRegister.substring(5, 8);
		int registerIndex = anyToDec(registerBin, 2);
		int result = anyToDec(accumulator, 2) & anyToDec(register[registerIndex], 2);
		accumulator = anyToBin(Integer.toString(result), 10);
		updateStatusRegister();
		programCounter = programCounter + 2;

	}

	/**
	 * Not Instruction.
	 */
	public void notInstruction(){
		String accDec = Integer.toString((~anyToDec(accumulator, 2)));
		accumulator = anyToBin(accDec, 10).substring(24, 32); 
		updateStatusRegister();
		programCounter = programCounter + 2;

	}

	/**
	 * Ldar rf Instruction.
	 */
	public void ldarfInstruction(){
		String registerBin = instructionRegister.substring(5, 8);
		int registerIndex = anyToDec(registerBin, 2);
		accumulator = register[registerIndex];
		updateStatusRegister();
		programCounter = programCounter + 2;

	}

	/**
	 * Star rf Instruction.
	 */
	public void starfInstruction(){
		String registerBin = instructionRegister.substring(5, 8);
		int registerIndex = anyToDec(registerBin, 2);
		register[registerIndex] = accumulator;
		programCounter = programCounter + 2;

	}

	/**
	 * Lda Instruction.
	 */
	public void ldaInstruction(){
		String memoryBin = instructionRegister.substring(8, instructionRegister.length());
		int memoryIndex = anyToDec(memoryBin, 2);
		if(memoryIndex == 250 || memoryIndex == 251){

			String addressContent;
			Scanner input = new Scanner(System.in);

			System.out.println("Enter keyboard input: ");
			addressContent = input.next();

			char keyboardInput = addressContent.charAt(0);
			int j = (int) keyboardInput;
			addressContent = ""+j;
			accumulator = anyToBin(addressContent,10);
			keyboard = ""+keyboardInput;

		}

		else {

			accumulator = anyToBin(memory[memoryIndex], 16);
		}
		updateStatusRegister();
		programCounter = programCounter + 2;

	}

	/**
	 * Sta Instruction.
	 */
	public void staInstruction(){
		String memoryBin = instructionRegister.substring(8, instructionRegister.length());
		int memoryIndex = anyToDec(memoryBin, 2);
		memory[memoryIndex] = anyToHex(accumulator, 2).toUpperCase();
		programCounter = programCounter + 2;

		if(memoryIndex == 252)
			display = ""+(char)Integer.parseInt(accumulator, 2);//anyToHex(accumulator, 2)..toUpperCase();
		if(memoryIndex == 253) 
			display = display + anyToHex(accumulator, 2).toUpperCase();
		if(memoryIndex == 254) 
			display = display + anyToHex(accumulator, 2).toUpperCase();
		if(memoryIndex == 255)
			display = display + anyToHex(accumulator, 2).toUpperCase();

	}

	/**
	 * Ldi Instruction.
	 */
	public void ldiInstruction(){
		String immediateOperand = instructionRegister.substring(8, instructionRegister.length());
		accumulator = immediateOperand;
		updateStatusRegister();
		programCounter = programCounter + 2;


	}

	// ---------------------------------------------------------------------------------------------

	/**
	 * Neg Instruction.
	 */
	public void negInstruction() {

		int accumulatorNumber = anyToDec(accumulator,2);

		if (accumulatorNumber == 0) {
			accumulator = "00000000";
			statusRegister[0] = 1; //Zero flag
			statusRegister[1] = 1; //Carry flag
			statusRegister[2] = 0; //Negative flag
			statusRegister[3] = 1; //Overflow flag
		}

		else if (accumulatorNumber == 128) {
			accumulator = "10000000";
			statusRegister[0] = 0; //Zero flag
			statusRegister[1] = 0; //Carry flag
			statusRegister[2] = 1; //Negative flag
			statusRegister[3] = 1; //Overflow flag
		}
		else {
			String twos_complement = Integer.toString((256 - accumulatorNumber));
			accumulator = anyToBin(twos_complement,10);
			statusRegister[0] = 0; //Zero flag
			statusRegister[1] = 0; //Carry flag
			statusRegister[3] = 0; //Overflow flag

			if (accumulator.substring(0, 1).equals("1"))
				statusRegister[2] = 1; //Negative flag
			else
				statusRegister[2] = 0; //Negative flag
		}

		programCounter = programCounter + 2;

	}


	/**
	 * Addc Instruction.
	 */
	public void addcInstruction() {

		int registerNumber;
		int accumulatorNumber;
		int carry = statusRegister[1];
		String sum_binary;

		String registerBin = instructionRegister.substring(5, 8);

		int registerDec = anyToDec(registerBin,2);
		String registerContent = register[registerDec];

		if (registerContent.substring(0, 1).equals("1"))
			registerNumber = -128 + anyToDec(registerContent.substring(1, 8),2);
		else 
			registerNumber = anyToDec(registerContent,2);


		if (accumulator.substring(0, 1).equals("1"))
			accumulatorNumber = -128 + anyToDec(accumulator.substring(1, 8),2);
		else 
			accumulatorNumber = anyToDec(accumulator,2);


		int sum_to_set_carry = anyToDec(registerContent,2) + anyToDec(accumulator,2);

		int sum = registerNumber + accumulatorNumber + carry;
		sum_binary = anyToBin(Integer.toString(sum),10);

		if (sum_to_set_carry > 255)
			statusRegister[1] = 1; //Carry
		else 
			statusRegister[1] = 0; //No Carry


		if (sum < 0) {
			sum_binary = sum_binary.substring(24, 32);

			if (sum < -128) 
				statusRegister[3] = 1; //Overflow
			else 
				statusRegister[3] = 0; //Overflow

			if (sum_binary.substring(0, 1).equals("1")) 
				statusRegister[2] = 1; //Negative

		}
		else {
			if (sum > 127) 
				statusRegister[3] = 1; //Overflow
			else 
				statusRegister[3] = 0;

		}

		accumulator = sum_binary;

		if (accumulator.substring(0, 1).equals("1")) 
			statusRegister[2] = 1; //Negative
		else 
			statusRegister[2] = 0;


		if(accumulator.equals("00000000")) 
			statusRegister[0] = 1; //Zero
		else 
			statusRegister[0] = 0;


		updateStatusRegister();
		programCounter = programCounter + 2;   
	}


	/**
	 * Sub Instruction.
	 */
	public void subInstruction() {

		int registerNumber;
		int accumulatorNumber;
		String sum_binary;

		String registerBin = instructionRegister.substring(5, 8);

		int registerDec = anyToDec(registerBin,2);
		String registerContent = register[registerDec];

		//For Carry purposes
		int registerContentForCarry = anyToDec(registerContent,2);
		int twos_registerContentForCarry = 256 - registerContentForCarry;

		if (registerContent.substring(0, 1).equals("1"))
			registerNumber = -128 + anyToDec(registerContent.substring(1, 8),2);
		else {
			registerNumber = anyToDec(registerContent,2);
		}

		if (accumulator.substring(0, 1).equals("1"))
			accumulatorNumber = -128 + anyToDec(accumulator.substring(1, 8),2);
		else {
			accumulatorNumber = anyToDec(accumulator,2);
		}

		int sum_to_set_carry =  twos_registerContentForCarry + anyToDec(accumulator,2);

		int sum = accumulatorNumber - registerNumber;
		sum_binary = anyToBin(Integer.toString(sum),10);

		if (sum_to_set_carry > 255)
			statusRegister[1] = 1; //Carry

		if (sum < 0) {
			sum_binary = sum_binary.substring(24, 32);

			if (sum < -128) {
				statusRegister[3] = 1; //Overflow
			}

			if (sum_binary.substring(0, 1).equals("1")) {
				statusRegister[2] = 1; //Negative
			}
		}
		else {
			if (sum > 127) {
				statusRegister[3] = 1; //Overflow
			}
		}

		accumulator = sum_binary;

		if (accumulator.substring(0, 1).equals("1")) {
			statusRegister[2] = 1; //Negative
		}

		if(accumulator.equals("00000000")) {
			statusRegister[0] = 1; //Zero
		}

		programCounter = programCounter + 2;   
	}

	/**
	 * Mult Instruction.
	 */
	public void multInstruction() {

		int registerNumber;
		int accumulatorNumber;

		String registerBin = instructionRegister.substring(5, 8);

		int registerDec = anyToDec(registerBin,2);
		String registerContent = register[registerDec];
		String operand1 = registerContent.substring(4,8);
		String operand2 = accumulator.substring(4,8);

		if (operand1.substring(0, 1).equals("1"))
			registerNumber = -8 + anyToDec(operand1.substring(1, 4),2);
		else 
			registerNumber = anyToDec(operand1,2);


		if (operand2.substring(0, 1).equals("1"))
			accumulatorNumber = -8 + anyToDec(operand2.substring(1, 4),2);
		else 
			accumulatorNumber = anyToDec(operand2,2);


		// Multiplication
		int mult = accumulatorNumber*registerNumber;

		String mult_binary = anyToBin(Integer.toString(mult),10);

		if (mult < 0)
			mult_binary = mult_binary.substring(24, 32);

		accumulator = mult_binary;

		if (accumulator.substring(0,1).equals("1"))
			statusRegister[2] = 1; //Negative Flag
		else
			statusRegister[2] = 0; //Negative Flag

		if (accumulator.equals("00000000"))
			statusRegister[0] = 1; //Zero Flag
		else
			statusRegister[0] = 0; //Zero Flag

		statusRegister[1] = 0; //Carry flag
		statusRegister[3] = 0; // Overflow flow

		programCounter = programCounter + 2;
	}



	/**
	 * Rlc Instruction.
	 */
	public void rlcInstruction() {

		String carry = Integer.toString(statusRegister[1]);
		String result = accumulator.substring(1, 8)+ carry;

		//Update Carry Flag
		statusRegister[1] = Integer.parseInt(accumulator.substring(0, 1));
		accumulator = result;
		if (accumulator.equals("00000000"))
			statusRegister[0] = 1;
		else
			statusRegister[0] = 0;
		if (accumulator.substring(0,1).equals("1"))
			statusRegister[2] = 1;
		else
			statusRegister[2] = 0;
		statusRegister[3] = 0;

		programCounter = programCounter + 2; 
	}



	/**
	 * Rrc Instruction.
	 */
	public void rrcInstruction() {

		String carry = Integer.toString(statusRegister[1]);
		String result = carry + accumulator.substring(0, 7);

		//Update Carry Flag
		statusRegister[1] = Integer.parseInt(accumulator.substring(7, 8));
		accumulator = result;
		if (accumulator.equals("00000000"))
			statusRegister[0] = 1;
		else
			statusRegister[0] = 0;
		if (accumulator.substring(0,1).equals("1"))
			statusRegister[2] = 1;
		else
			statusRegister[2] = 0;
		statusRegister[3] = 0;

		programCounter = programCounter + 2;  
	}


	/**
	 * Nop Instruction.
	 */
	public void nopInstruction() {

		programCounter = programCounter + 2;
	}


	/// -----------------------------------------------------------------------------------------

	/**
	 * Brz Instruction.
	 */
	public void brzInstruction(){
		if(statusRegister[0] == 1)
			programCounter = anyToDec(register[7], 2);
		else
			programCounter = programCounter + 2;
	}

	/**
	 * Brc Instruction.
	 */
	public void brcInstruction(){
		if(statusRegister[1] == 1)
			programCounter =  anyToDec(register[7], 2);
		else
			programCounter = programCounter + 2;

	}

	/**
	 * Brn Instruction.
	 */
	public void brnInstruction(){
		if(statusRegister[2] == 1)
			programCounter =  anyToDec(register[7], 2);
		else
			programCounter = programCounter + 2;

	}

	/**
	 * Bro Instruction.
	 */
	public void broInstruction(){
		if(statusRegister[3] == 1)
			programCounter =  anyToDec(register[7], 2);
		else
			programCounter = programCounter + 2;

	}

	/**
	 * Or Instruction.
	 */
	public void orInstruction(){
		int registerIndex =  anyToDec(instructionRegister.substring(5, 8), 2);
		String registerString = register[registerIndex];
		int result = anyToDec(accumulator, 2) | anyToDec(registerString, 2);
		accumulator = anyToBin(Integer.toString(result), 10);
		updateStatusRegister();

		programCounter = programCounter + 2;
	}

	/**
	 * Xor Instruction.
	 */
	public void xorInstruction(){
		int registerIndex =  anyToDec(instructionRegister.substring(5, 8), 2);
		String registerString = register[registerIndex];
		int result = anyToDec(accumulator, 2) ^ anyToDec(registerString, 2);
		accumulator = anyToBin(Integer.toString(result), 10);
		updateStatusRegister();

		programCounter = programCounter + 2;
	}
	
	
	/**
	 * Stop Instruction
	 * @return returns true always.
	 */
	public boolean stopInstruction(){
		isStop = true;
		return isStop;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Resets Status Register
	 */
	public void clearStatusRegister(){
		// clearing flags
		for(int k=0; k < 4; k++)
			statusRegister[k] = 0;
	}



	/**
	 * Updates Status Register
	 */
	public void updateStatusRegister(){

		clearStatusRegister();
		if(accumulator.equals("00000000"))
			statusRegister[0] = 1;
		if(accumulator.startsWith("1"))
			statusRegister[2] = 1;
	}


	/**
	 * Run mode function
	 */
	public void runMode(){
		for(int i = 0; i < numberOfInstructions; i++)
			execute();
	}



	/**
	 * Step mode function
	 */
	public void stepMode(){
		execute();
	}



	/**
	 * Execute function
	 */
	public void execute(){

		instructionRegister = anyToBin(memory[programCounter], 16) + anyToBin(memory[programCounter+1], 16);

		String opcode = instructionRegister.substring(0, 5);

		if(opcode.equals("00000"))
			andInstruction();
		else if(opcode.equals("00001"))
			orInstruction();
		else if(opcode.equals("00010"))
			xorInstruction();
		else if(opcode.equals("00011"))
			addcInstruction();
		else if(opcode.equals("00100"))
			subInstruction();
		else if(opcode.equals("00101"))
			multInstruction();
		else if(opcode.equals("00110"))
			negInstruction();
		else if(opcode.equals("00111"))
			notInstruction();
		else if(opcode.equals("01000"))
			rlcInstruction();
		else if(opcode.equals("01001"))
			rrcInstruction();
		else if(opcode.equals("01010"))
			ldarfInstruction();
		else if(opcode.equals("01011"))
			starfInstruction();
		else if(opcode.equals("01100"))
			ldaInstruction();
		else if(opcode.equals("01101"))
			staInstruction();
		else if(opcode.equals("01110"))
			ldiInstruction();
		else if(opcode.equals("10000"))
			brzInstruction();
		else if(opcode.equals("10001"))
			brcInstruction();
		else if(opcode.equals("10010"))
			brnInstruction();
		else if(opcode.equals("10011"))
			broInstruction();
		else if(opcode.equals("11111"))
			stopInstruction();
		else if(opcode.equals("11000"))
			nopInstruction();
		else
			System.out.println("Error in opcode");

	}


	/**
	 * Getters
	 */

	public String[] getValues(){
		String[] valuesArray = new String[14];
		String SR = statusRegister[0]+"";
		SR = SR + statusRegister[1];
		SR = SR + statusRegister[2];
		SR = SR + statusRegister[3];

		valuesArray[0] = instructionRegister;
		valuesArray[1] = anyToBin(programCounter+"", 10);
		valuesArray[2] = accumulator;
		valuesArray[3] = register[0];
		valuesArray[4] = register[1];
		valuesArray[5] = register[2];
		valuesArray[6] = register[3];
		valuesArray[7] = register[4];
		valuesArray[8] = register[5];
		valuesArray[9] = register[6];
		valuesArray[10] = register[7];
		valuesArray[11] = SR;
		valuesArray[12] = keyboard;
		valuesArray[13] = display;

		return valuesArray;

	}
	/**
	 * Memory array getter.
	 * @return string containing elements of memory
	 */
	public static String[] getMemoryArray() {
		return memory;
	}

	/**
	 * Gets result of stop instruction execution.
	 * @return true if instruction was executed, false otherwise.
	 */
	public boolean getIsStop() {
		return isStop;
	}

}



