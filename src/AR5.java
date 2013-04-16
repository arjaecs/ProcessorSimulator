import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class AR5 {

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


    public AR5(String filename){

        // Initializing instance fields

        // 256 addresses of 1 byte each
        memory = new String[256];

        // Program Counter
        programCounter = 0;

        // 8-BIT accumulator
        accumulator = "00000000";

        //initial instruction to be executed
        instructionRegister = "0000000000000000";

        //Status Register format: ZCNO
        statusRegister = new int[4];

        // 8 byte register file
        register = new String[8];

        // The test file
        this.filename = filename;

        // Index for memory filling
        index = 0;

        // Amount of instructions
        numberOfInstructions = 0;

        // ASCII Display
        display = "";

        // Stop state verifier
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
        instructionRegister = binaryConverter(memory[programCounter], 16) + binaryConverter(memory[programCounter+1], 16);
    }

    ///--------------------------------------------------------------------------------------------
    //                                  Base Converters
    ///--------------------------------------------------------------------------------------------

    /**
     Converts an integer from any given base to a binary number.
     @param value number to be changed , base base of the number
     @ return the binary equivalent number
     */
    public String binaryConverter(String value, int base){
        String bin = Integer.toBinaryString(Integer.parseInt(value, base));

        if(bin.length() < 8) {
            int leadingZeroesNum = 8 - bin.length();
            String zeroes = String.format("%0" + leadingZeroesNum + "d", 0);
            bin = zeroes + bin;
        }
        return bin;
    }



    /**
     Converts an integer to a decimal number.
     @param value number to be changed , base actual base of the number
     @ return the decimal equivalent number
     */
    public int decimalConverter(String value, int base){
        return Integer.parseInt(value, base);

    }


    /**
     Converts an integer of any base to a hexadecimal number.
     @param value number to be changed , base actual base of the number
     @ return the hexadecimal equivalent number
     */
    public String hexConverter(String value, int base){
        return Integer.toHexString(Integer.parseInt(value, base));
    }


    ///--------------------------------------------------------------------------------------------
    //                                    Instruction Set
    ///--------------------------------------------------------------------------------------------

    /**
     *  AND rf
     *  OPCODE: 00 000
     *  Logical AND
     */
    public void andInstruction(){
        String registerBin = instructionRegister.substring(5, 8);
        int registerIndex = decimalConverter(registerBin, 2);
        int result = decimalConverter(accumulator, 2) & decimalConverter(register[registerIndex], 2);
        accumulator = binaryConverter(Integer.toString(result), 10);
        updateStatusRegister();
        programCounter = programCounter + 2;

    }

    /**
     * OR rf
     * OPCODE:00 001
     * Logical OR
     */
    public void orInstruction(){
        int registerIndex =  decimalConverter(instructionRegister.substring(5, 8), 2);
        String registerString = register[registerIndex];
        int result = decimalConverter(accumulator, 2) | decimalConverter(registerString, 2);
        accumulator = binaryConverter(Integer.toString(result), 10);
        updateStatusRegister();

        programCounter = programCounter + 2;
    }

    /**
     * ADDC rf
     * OPCODE: 00 011
     * Addition with Carry
     */
    public void addcInstruction() {

        int registerNumber;
        int accumulatorNumber;
        int carry = statusRegister[1];
        String binarySum;

        String registerBin = instructionRegister.substring(5, 8);

        int registerDec = decimalConverter(registerBin,2);
        String registerContent = register[registerDec];

        if (registerContent.substring(0, 1).equals("1"))
            registerNumber = -128 + decimalConverter(registerContent.substring(1, 8),2);
        else
            registerNumber = decimalConverter(registerContent,2);


        if (accumulator.substring(0, 1).equals("1"))
            accumulatorNumber = -128 + decimalConverter(accumulator.substring(1, 8),2);
        else
            accumulatorNumber = decimalConverter(accumulator,2);


        int sum = registerNumber + accumulatorNumber + carry;
        binarySum = binaryConverter(Integer.toString(sum),10);

        if (decimalConverter(registerContent,2) + decimalConverter(accumulator,2) > 255)
            statusRegister[1] = 1; //Carry
        else
            statusRegister[1] = 0; //No Carry


        if (sum < 0) {
            binarySum = binarySum.substring(24, 32);

            if (sum < -128)
                statusRegister[3] = 1; //Overflow
            else
                statusRegister[3] = 0; //Overflow

            if (binarySum.substring(0, 1).equals("1"))
                statusRegister[2] = 1; //Negative

        }
        else {
            if (sum > 127)
                statusRegister[3] = 1; //Overflow
            else
                statusRegister[3] = 0;

        }

        accumulator = binarySum;

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
     * SUB rf
     * OPCODE: 00 100
     * Subtraction
     */
    public void subInstruction() {

        int registerNumber;
        int accumulatorNumber;
        String sum_binary;

        String registerBin = instructionRegister.substring(5, 8);

        int registerDec = decimalConverter(registerBin,2);
        String registerContent = register[registerDec];

        //For Carry purposes
        int registerContentForCarry = decimalConverter(registerContent,2);
        int twos_registerContentForCarry = 256 - registerContentForCarry;

        if (registerContent.substring(0, 1).equals("1"))
            registerNumber = -128 + decimalConverter(registerContent.substring(1, 8),2);
        else {
            registerNumber = decimalConverter(registerContent,2);
        }

        if (accumulator.substring(0, 1).equals("1"))
            accumulatorNumber = -128 + decimalConverter(accumulator.substring(1, 8),2);
        else {
            accumulatorNumber = decimalConverter(accumulator,2);
        }



        int sum = accumulatorNumber - registerNumber;
        sum_binary = binaryConverter(Integer.toString(sum),10);

        if (twos_registerContentForCarry + decimalConverter(accumulator,2) > 255)
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
     * MUL rf
     * OPCODE: 00 101
     * Multiply
     */
    public void multInstruction() {

        int registerNumber;
        int accumulatorNumber;

        String registerBin = instructionRegister.substring(5, 8);

        int registerDec = decimalConverter(registerBin,2);
        String registerContent = register[registerDec];
        String operand1 = registerContent.substring(4,8);
        String operand2 = accumulator.substring(4,8);

        if (operand1.substring(0, 1).equals("1"))
            registerNumber = -8 + decimalConverter(operand1.substring(1, 4),2);
        else
            registerNumber = decimalConverter(operand1,2);


        if (operand2.substring(0, 1).equals("1"))
            accumulatorNumber = -8 + decimalConverter(operand2.substring(1, 4),2);
        else
            accumulatorNumber = decimalConverter(operand2,2);


        // Multiplication
        int mult = accumulatorNumber*registerNumber;

        String mult_binary = binaryConverter(Integer.toString(mult),10);

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
     * NEG
     * OPCODE: 00 110
     * Two's Compliment
     */
    public void negInstruction() {

        int accumulatorNumber = decimalConverter(accumulator,2);

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
            String twosComplement = Integer.toString((256 - accumulatorNumber));
            accumulator = binaryConverter(twosComplement,10);
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
     * NOT
     * OPCODE: 00 111
     * Negate
     */
    public void notInstruction(){
        // One's compliment
        String accDec = Integer.toString((~decimalConverter(accumulator, 2)));
        accumulator = binaryConverter(accDec, 10).substring(24, 32);
        updateStatusRegister();
        programCounter = programCounter + 2;

    }

    /**
     * RLC
     * OPCODE: 01 000
     * Rotate left through Carry
     */
    public void rlcInstruction() {

        String carry = Integer.toString(statusRegister[1]);
        String result = accumulator.substring(1, 8)+ carry;

        //Update Carry Flag
        statusRegister[1] = Integer.parseInt(accumulator.substring(0, 1));
        accumulator = result;
        //Update Zero Flag
        if (accumulator.equals("00000000"))
            statusRegister[0] = 1;
        else
            statusRegister[0] = 0;
        //Update Negative Flag
        if (accumulator.substring(0,1).equals("1"))
            statusRegister[2] = 1;
        else
            statusRegister[2] = 0;
        statusRegister[3] = 0;

        programCounter = programCounter + 2;
    }

    /**
     * RRC
     * OPCODE:01 001
     * Rotate right through carry
     */
    public void rrcInstruction() {

        String carry = Integer.toString(statusRegister[1]);
        String result = carry + accumulator.substring(0, 7);

        //Update Carry Flag
        statusRegister[1] = Integer.parseInt(accumulator.substring(7, 8));
        accumulator = result;
        //Update Zero Flag
        if (accumulator.equals("00000000"))
            statusRegister[0] = 1;
        else
            statusRegister[0] = 0;
        //Update Negative Flag
        if (accumulator.substring(0,1).equals("1"))
            statusRegister[2] = 1;
        else
            statusRegister[2] = 0;
        statusRegister[3] = 0;

        programCounter = programCounter + 2;
    }


    /**
     * LDA rf
     * OPCODE:01 010
     * Load accumulator from register f
     */
    public void ldarfInstruction(){
        String registerBin = instructionRegister.substring(5, 8);
        int registerIndex = decimalConverter(registerBin, 2);
        accumulator = register[registerIndex];
        updateStatusRegister();
        programCounter = programCounter + 2;

    }

    /**
     * STA rf
     * OPCODE:01 011
     * Store accumulator to register f
     */
    public void starfInstruction(){
        String registerBin = instructionRegister.substring(5, 8);
        int registerIndex = decimalConverter(registerBin, 2);
        register[registerIndex] = accumulator;
        programCounter = programCounter + 2;

    }

    /**
     * LDA addr
     * OPCODE:01 100
     * Load Accumulator from memory location addr
     */
    public void ldaInstruction(){
        String memoryBin = instructionRegister.substring(8, instructionRegister.length());
        int memoryIndex = decimalConverter(memoryBin, 2);

        // Input from keyboard
        if(memoryIndex == 250 || memoryIndex == 251){

            String addressContent;
            Scanner input = new Scanner(System.in);

            System.out.println("Please enter keyboard input: ");
            addressContent = input.next();

            char keyboardInput = addressContent.charAt(0);
            int j = (int) keyboardInput;
            addressContent = ""+j;
            accumulator = binaryConverter(addressContent,10);
            keyboard = ""+keyboardInput;

        }

        // Load from memory location at memoryIndex
        else {

            accumulator = binaryConverter(memory[memoryIndex], 16);
        }
        updateStatusRegister();
        programCounter = programCounter + 2;

    }

    /**
     * STA addr
     * OPCODE: 01 101
     * Store accumulator to memory location addr
     */
    public void staInstruction(){
        String memoryBin = instructionRegister.substring(8, instructionRegister.length());
        int memoryIndex = decimalConverter(memoryBin, 2);
        memory[memoryIndex] = hexConverter(accumulator, 2).toUpperCase();
        programCounter = programCounter + 2;

        if(memoryIndex == 252)
            display = ""+(char)Integer.parseInt(accumulator, 2);//hexConverter(accumulator, 2)..toUpperCase();
        if(memoryIndex == 253)
            display = display + hexConverter(accumulator, 2).toUpperCase();
        if(memoryIndex == 254)
            display = display + hexConverter(accumulator, 2).toUpperCase();
        if(memoryIndex == 255)
            display = display + hexConverter(accumulator, 2).toUpperCase();

    }

    /**
     * LDI Immediate
     * OPCODE: 01 110
     * Load accumulator with immediate
     */
    public void ldiInstruction(){
        String immediateOperand = instructionRegister.substring(8, instructionRegister.length());
        accumulator = immediateOperand;
        updateStatusRegister();
        programCounter = programCounter + 2;


    }

    /**
     * BRZ
     * OPCODE: 10 000
     * Branch if Zero
     */
    public void brzInstruction(){
        if(statusRegister[0] == 1)
            programCounter = decimalConverter(register[7], 2);
        else
            programCounter = programCounter + 2;
    }

    /**
     * BRC
     * OPCODE: 10 001
     * Branch if Carry
     */
    public void brcInstruction(){
        if(statusRegister[1] == 1)
            programCounter =  decimalConverter(register[7], 2);
        else
            programCounter = programCounter + 2;

    }

    /**
     * BRN
     * OPCODE: 10 010
     * Branch if Negative
     */
    public void brnInstruction(){
        if(statusRegister[2] == 1)
            programCounter =  decimalConverter(register[7], 2);
        else
            programCounter = programCounter + 2;

    }

    /**
     * BRO
     * OPCODE: 10 011
     * Branch if Overflow
     */
    public void broInstruction(){
        if(statusRegister[3] == 1)
            programCounter =  decimalConverter(register[7], 2);
        else
            programCounter = programCounter + 2;

    }

    /**
     * STOP
     * OPCODE:11 111
     * Stop execution
     */
    public boolean stopInstruction(){
        isStop = true;
        return isStop;
    }

    /**
     * NOP
     * OPCODE:11 000
     * No operation
     */
    public void nopInstruction() {

        programCounter = programCounter + 2;
    }


    // --------------------------------------------------------------------------------------------
    // Flag Handlers
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

    // --------------------------------------------------------------------------------------------
    // Execution Modes
    // --------------------------------------------------------------------------------------------

    /**
     * Step mode
     */
    public void stepMode(){
        execute();
    }

    /**
     * Run mode
     */
    public void runMode(){
        for(int i = 0; i < numberOfInstructions; i++)
            execute();
    }


    /**
     * Execute from Fetch-Decode-Execute cycle
     */
    public void execute(){

        instructionRegister = binaryConverter(memory[programCounter], 16) + binaryConverter(memory[programCounter+1], 16);

        String opcode = instructionRegister.substring(0, 5);

        if(opcode.equals("00000"))
            andInstruction();
        else if(opcode.equals("00001"))
            orInstruction();
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

        String[] values = new String[14];
        String SR = statusRegister[0]+"";
        SR = SR + statusRegister[1];
        SR = SR + statusRegister[2];
        SR = SR + statusRegister[3];

        values[0] = instructionRegister;
        values[1] = binaryConverter(programCounter+"", 10);
        values[2] = accumulator;
        values[3] = register[0];
        values[4] = register[1];
        values[5] = register[2];
        values[6] = register[3];
        values[7] = register[4];
        values[8] = register[5];
        values[9] = register[6];
        values[10] = register[7];
        values[11] = SR;
        values[12] = keyboard;
        values[13] = display;

        return values;

    }
    /**
     * Memory array getter.
     * @return string containing elements of memory
     */
    public static String[] getMemory() {
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



