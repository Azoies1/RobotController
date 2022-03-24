//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.Test;


public class newRobotTest {

    RobotController robotController;
    final String expectedOob = "outside the boundaries of the floor, enter a valid number";
    final String invalidCommand = "Invalid command: This command does not exists";

    @BeforeEach
    void setup(){
        this.robotController = new RobotController();
    }

    //ExecuteCommands in order
    @Test
    @DisplayName("Pen up Test")
    void penUpTest(){
        robotController.executeCommands("U");
        assertEquals("UP",robotController.getPenState());
    }

    @Test
    @DisplayName("Pen down Test")
    void penDownTest(){
        robotController.executeCommands("D");
        assertEquals("DOWN",robotController.getPenState());
    }

    @Test
    @DisplayName("Move Direction Right Test")
    void moveRightTest(){
        //Starts with North
        String currentDirection = robotController.getFacingDirection();
        robotController.executeCommands("R");
        assertEquals("EAST",robotController.getFacingDirection());

        robotController.executeCommands("R");
        assertEquals("SOUTH",robotController.getFacingDirection());

        robotController.executeCommands("R");
        assertEquals("WEST",robotController.getFacingDirection());

        robotController.executeCommands("R");
        assertEquals("NORTH",robotController.getFacingDirection());
    }

    @Test
    @DisplayName("Move Direction Left Test")
    void moveLeftTest(){
        //Starts with North
        String currentDirection = robotController.getFacingDirection();
        robotController.executeCommands("L");
        assertEquals("WEST",robotController.getFacingDirection());

        robotController.executeCommands("L");
        assertEquals("SOUTH",robotController.getFacingDirection());

        robotController.executeCommands("L");
        assertEquals("EAST",robotController.getFacingDirection());

        robotController.executeCommands("L");
        assertEquals("NORTH",robotController.getFacingDirection());
    }

    @Test
    @DisplayName("Move tests")
    void moveTests() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        robotController.executeCommands("I 4");
        robotController.executeCommands("M 5");
        assertEquals(expectedOob.strip(), outputStream.toString().strip());

        outputStream.reset();
        robotController.executeCommands("M");
        assertEquals("Did not enter a value", outputStream.toString().strip());

        outputStream.reset();
        robotController.executeCommands("M 0");
        assertEquals("Did not move forward, enter a value greater than zero", outputStream.toString().strip());

        outputStream.reset();
        robotController.executeCommands("M 2");
        String currentPos = "Position: 0, 2 - Pen: Up - Facing: North";
        robotController.printPosition();
        assertEquals(currentPos, outputStream.toString().strip());
    }

    @Test
    @Disabled
    void quitTest(){
        robotController.executeCommands("q");
    }

    @Test
    void printFloorTest(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        robotController.executeCommands("I 3");
        robotController.executeCommands("P");
        String expectedOutput = "";
        assertEquals(expectedOutput.strip(), outputStream.toString().strip());
    }

    @Test
    void printInstructionsTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        robotController.printInstructions();
        String expectedOutput = "Enter command or enter Q or q to stop the program or enter the following accepted commands"+"\n"
        +"[U|u] for Pen up "+"\n"
        +"[D|d] for Pen down "+"\n"
        +"[R|r] to Turn right "+"\n"
        +"[L|l] to Turn left "+"\n"
        +"[M s|m s] to Move forward s spaces "+"\n"
        +"[P|p] Print the N by N array and display the indices"+"\n"
        +"[C|c] Print current position of the pen and whether it is up or down and its facing direction"+"\n"
        +"[I n|i n] Initialize the system: The values of the array floor are zeros and the robot is back to [0, 0], pen up and facing north. n size of the array, an integer greater than zero ";
        assertEquals(expectedOutput.strip(), outputStream.toString().strip());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "b", "z", "s"})
    void invalidCommandTest(String command) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        robotController.executeCommands(command);
        assertEquals(invalidCommand.strip(), outputStream.toString().strip());
        outputStream.reset();
    }

    @Test
    void getFacingDirectionTest() {
        String expected = "NORTH";
        assertEquals(expected, robotController.getFacingDirection());
    }

    @Test
    void getPenStateTest() {
        String expected = "UP";
        assertEquals(expected, robotController.getPenState());
    }

    @Test
    void printPositionNotIntializedTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        assertThrows(Exception.class, () -> robotController.printPosition());
    }

    @ParameterizedTest
    @ValueSource(strings = {"C", "c"})
    void printPositionInitializedTest(String command) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        String expected = "Position: 0, 0 - Pen: Up - Facing: North";
        robotController.executeCommands("I 10");
        robotController.executeCommands(command);
        assertEquals(expected.strip(), outputStream.toString().strip());
    }

    @ParameterizedTest
    @ValueSource(strings = {"I -1", "i -1"})
    void initializeFloorInvalidTest(String command) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        String expected = "Cannot initialize array, enter a value greater than zero";
        robotController.executeCommands(command);
        assertEquals(expected.strip(), outputStream.toString().strip());
    }

    @ParameterizedTest
    @ValueSource(strings = {"I 10", "i 10"})
    void initializeFloorValidTest(String command) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        int[][] expectedFloor = new int[10][10];
        robotController.executeCommands(command);
        assertEquals(expectedFloor.length, robotController.getFloor().length);
    }

    @Test
    void initializeFloorNoSize() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        String expectedExceptionMessage = "Did not enter a value";
        robotController.executeCommands("i");
        assertEquals(expectedExceptionMessage.strip(), outputStream.toString().strip());
    }

}
