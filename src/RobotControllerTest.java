import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RobotControllerTest {

	private final ByteArrayOutputStream ouput = new ByteArrayOutputStream();
    private final PrintStream firstOuput = System.out;


	@BeforeEach
    void setUp() {
        System.setOut(new PrintStream(ouput));
    }

    @AfterEach
    void tearDown() {
        System.setOut(firstOuput);
    }

	@Test
	void printInstructionTest(){
		RobotController rc = new RobotController();
		var expected = "Enter command or enter Q or q to stop the program or enter the following accepted commands"+"\n"
        +"[U|u] for Pen up "+"\n"
        +"[D|d] for Pen down "+"\n"
        +"[R|r] to Turn right "+"\n"
        +"[L|l] to Turn left "+"\n"
        +"[M s|m s] to Move forward s spaces "+"\n"
        +"[P|p] Print the N by N array and display the indices"+"\n"
        +"[C|c] Print current position of the pen and whether it is up or down and its facing direction"+"\n"
        +"[I n|i n] Initialize the system: The values of the array floor are zeros and the robot is back to [0, 0], pen up and facing north. n size of the array, an integer greater than zero ";
		rc.printInstructions();
		assertEquals(expected.strip(), ouput.toString().strip());
	}

	@Test
	void invalidCommandTest(){
		RobotController rc = new RobotController();
		rc.executeCommands("S 10");	
		var expected = "Invalid command: This command does not exists";
		assertEquals(expected.strip(), ouput.toString().strip());
	}

	@Test
	void commandWithNoSpaceTest(){
		RobotController rc = new RobotController();
		rc.executeCommands("M10");	
		var expected = "Invalid command: This command does not exists";
		assertEquals(expected.strip(), ouput.toString().strip());
	}

	@Test
	void initializeFloorTest(){
		 RobotController rc = new RobotController();
		
		 rc.executeCommands("i 3");	
		var arraySize = 3;
		int[][] expected = new int[arraySize][arraySize];
		assertArrayEquals(expected, rc.getFloor());
	}

	@Test
	void initializeCommandWithNoValueTest(){
		RobotController rc = new RobotController();
		rc.executeCommands("I ");	
		var expected = "Did not enter a value";
		assertEquals(expected.strip(), ouput.toString().strip());
	}

	@Test
	void initializeCommandInvalidSizeTest(){
		RobotController rc = new RobotController();
		rc.executeCommands("I 0");	
		var expected = "Cannot initialize array, enter a value greater than zero";
		assertEquals(expected.strip(), ouput.toString().strip());
	}

	@Test
	void moveCommandWithNoValueTest(){
		RobotController rc = new RobotController();
		rc.executeCommands("M ");	
		var expected = "Did not enter a value";
		assertEquals(expected.strip(), ouput.toString().strip());
	}

	@Test
	void moveCommandInvalidStepsTest(){
		RobotController rc = new RobotController();
		rc.executeCommands("M 0");	
		var expected = "Did not move forward, enter a value greater than zero";
		assertEquals(expected.strip(), ouput.toString().strip());
	}

	@Test
	void moveCommandOutOfBoudariesTest(){
		RobotController rc = new RobotController();
		rc.executeCommands("I 3");
		
		rc.executeCommands("m 4");
		var expected = "outside the boundaries of the floor, enter a valid number";
		assertEquals(expected.strip(), ouput.toString().strip());
	}

	@Test
	void canMoveForwardTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");		
		boolean canMoveForward = rc.canMoveForward(3);
		assertEquals(true, canMoveForward);
	}

	@Test
	void canMoveForwardFearthestNorthTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 3");		
		assertEquals(false, rc.canMoveForward(5));
	}

	@Test
	void canMoveForwardFearthestSouthTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 3");		
		rc.executeCommands("R");
		rc.executeCommands("r");
		assertEquals(false, rc.canMoveForward(3));
	}

	@Test
	void canMoveForwardFearthestEastTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 3");		
		rc.executeCommands("R");
		assertEquals(false, rc.canMoveForward(5));
	}

	@Test
	void canMoveForwardFearthestWestTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");		
		rc.executeCommands("l");
		assertEquals(false, rc.canMoveForward(3));
	}
	
	@Test
	void printFloorEmptyTest() {
		RobotController rc = new RobotController();
        rc.executeCommands("I 10");
        rc.executeCommands("P");
        String printed = ouput.toString();
        assertTrue(printed.matches("[\\n\\r ]+"));//string contains only \r \n and spaces (empty floor)
	}

	@Test
	void printFloorWithShapeTest() {
		RobotController rc = new RobotController();
        rc.executeCommands("I 10");
        rc.executeCommands("D");//facing north
        rc.executeCommands("M 9");
        rc.executeCommands("R");//facing east
        rc.executeCommands("M 9");
        rc.executeCommands("p");
        String lines[] = ouput.toString().split("\\r?\\n");
        for (int i = 0; i < 10; i++)
            lines[i] = lines[i].replaceAll("\\s+", "");//get rid of white spaces
        for (int i = 0; i < 10; i++) {
            assertEquals('*', lines[0].charAt(i));
            assertEquals('*', lines[i].charAt(0));
        }
	}
	
	@Test
	void penUpTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");		
		rc.executeCommands("u");
		assertEquals("UP", rc.getPenState());
	}

	@Test
	void penDownTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");	
		rc.executeCommands("D");
		assertEquals("DOWN", rc.getPenState());
	}
	
	@Test
	void moveRightFacingEastTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");
		rc.executeCommands("M 5");
		rc.executeCommands("R");
		rc.executeCommands("R");
		assertEquals("SOUTH", rc.getFacingDirection());
	}

	@Test
	void moveRightFacingWestTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");
		rc.executeCommands("M 5");
		rc.executeCommands("L");
		rc.executeCommands("R");
		assertEquals("NORTH", rc.getFacingDirection());
	}

	@Test
	void moveRightFacingNorthTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");
		rc.executeCommands("M 5");
		rc.executeCommands("R");
		assertEquals("EAST", rc.getFacingDirection());
	}

	@Test
	void moveRightFacingSouthTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");
		rc.executeCommands("M 5");
		rc.executeCommands("R");
		rc.executeCommands("R");
		rc.executeCommands("R");
		assertEquals("WEST", rc.getFacingDirection());
	}

	@Test
	void moveLeftFacingEastTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");
		rc.executeCommands("M 5");
		rc.executeCommands("R");
		rc.executeCommands("L");
		assertEquals("NORTH", rc.getFacingDirection());
	}
	
	@Test
	void moveLeftFacingWestTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");
		rc.executeCommands("M 5");
		rc.executeCommands("L");
		rc.executeCommands("L");
		assertEquals("SOUTH", rc.getFacingDirection());
	}
	
	@Test
	void moveLeftFacingNorthTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");
		rc.executeCommands("M 5");
		rc.executeCommands("L");
		assertEquals("WEST", rc.getFacingDirection());
	}
	
	@Test
	void moveLeftFacingSouthTest() {
		RobotController rc = new RobotController();
		rc.executeCommands("I 10");
		rc.executeCommands("M 5");
		rc.executeCommands("R");
		rc.executeCommands("R");
		rc.executeCommands("L");
		assertEquals("EAST", rc.getFacingDirection());
	}

	@Test
	void modifyingFloorNorthFacingTest() {
		 RobotController rc = new RobotController();
        rc.executeCommands("I 10");
        rc.executeCommands("D");
        rc.executeCommands("M 9");
        rc.executeCommands("P");
        String lines[] = ouput.toString().split("\\r?\\n");
        for (int i = 0; i < 10; i++)
            assertEquals('*', lines[i].charAt(0));
	}
	
	@Test
	void modifyingFloorEastFacingTest() {
		RobotController rc = new RobotController();
        rc.executeCommands("I 10");
        rc.executeCommands("D");
        rc.executeCommands("R");//facing east
        rc.executeCommands("M 9");
        rc.executeCommands("P");
        String lines[] = ouput.toString().split("\\r?\\n");
        for (int i = 0; i < 10; i++)
            lines[i] = lines[i].replaceAll("\\s+", "");//get rid of white spaces
        for (int i = 0; i < 10; i++)
            assertEquals('*', lines[9].charAt(i));
	}
	
	@Test
	void modifyingFloorSouthFacingTest() {
		RobotController rc = new RobotController();
        rc.executeCommands("I 10");
        rc.executeCommands("D");//facing nord
        rc.executeCommands("M 9");
        rc.executeCommands("R");//facing east
        rc.executeCommands("M 1");
        rc.executeCommands("R");//facing south
        rc.executeCommands("M 9");
        rc.executeCommands("P");
        String lines[] = ouput.toString().split("\\r?\\n");
        for (int i = 0; i < 10; i++)
            lines[i] = lines[i].replaceAll("\\s+", "");
        for (int i = 1; i < 10; i++)
            assertEquals('*', lines[i].charAt(1));
	}
	
	@Test
	void modifyingFloorWestFacingTest() {
		RobotController rc = new RobotController();
        rc.executeCommands("I 10");
        rc.executeCommands("d");
        rc.executeCommands("R");//facing east
        rc.executeCommands("M 9");
        rc.executeCommands("L");//facing nord
        rc.executeCommands("M 1");
        rc.executeCommands("L");//facing west
        rc.executeCommands("M 9");
        rc.executeCommands("P");
        String lines[] = ouput.toString().split("\\r?\\n");
        for (int i = 0; i < 10; i++)
            lines[i] = lines[i].replaceAll("\\s+", "");
        for (int i = 1; i < 10; i++)
            assertEquals('*', lines[8].charAt(i));
	}
	
	@Test
	void printPositionStartingTest() {
		RobotController rc = new RobotController();
        rc.executeCommands("I 10");
        rc.executeCommands("C");
        assertEquals("Position: 0, 0 - Pen: Up - Facing: North\r\n".strip(), ouput.toString().strip());
	}
	
	@Test
	void printPositionAfterMovingRobotTest() {
		RobotController rc = new RobotController();
        rc.executeCommands("I 10");
        rc.executeCommands("D");
        rc.executeCommands("M 4");
        rc.executeCommands("c");
        assertEquals("Position: 0, 4 - Pen: Down - Facing: North\r\n".strip(), ouput.toString().strip());
	}
	
}