import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Class name: GetContentromFile.java
 * @author ugola (Urvika Gola)
 * 
 * Detailed Description -  A class that fetches data from the .set and .sch files using buffered reader
 * 
 * Technique useds - Read line by line, seprated by new line delimeter, remove all spaces in between the operations, and trim
 * and accidental space.  
 * Data structure used is BufferedReader in Java.
 * Programming language used is Java. 
 * 
 * Input Requirement for .set or .sch 
 * Transactions will be provided as sequences of read, write, and commit operations, one
 * operation per line of a text file. Similarly, our schedules will be given as text files containing the operations of
 * one or more potentially interleaved transactions. Operations have the format <op><id><space(s)><item>,
 * where <op> is one of r (for read), w (write), or c (commit); <id> is an integer that identifies the transaction
 * executing <op>; <item> is a single upper–case letter representing the DB item being read or written
 * 
 */
public class GetContentromFile {
	/**
     * Method name: getListOfOperationsFromSchedule
     * Purpose: This public method reads the operation per line and stores in a 
     * user defined data type of Class Operation, which is a POJO (Plain Old Java Object).
     * Pre-condition - The file should contain operations in the correct syntax.
     * Post-condition - None
     * @param br Buffered reader object
     * @return a list of Operation class object
     */
	public static List<Operation> getListOfOperationsFromSchedule(BufferedReader br)   
	{  
		String line = ""; // Initialize a string
		List<Operation> listOperations = new ArrayList<Operation>();
		try {  
			while ((line = br.readLine()) != null) { // Read until the end of the file				
				if(line.isBlank() || line == "\n" || line == "") // Continue if a blank line is encountered
					continue;
				// Get rid of all spaces and store in it Operation pojo class object
				Operation operation = getValidOperation(line.replace(" ", "")); 
				if(operation != null)
					listOperations.add(operation); // Add a non null operation to the list of operations
			}  
		} catch (IOException e) {  
			e.printStackTrace();  // Print exception if any
		}
		return listOperations; // return a List of Operation Pojo
	}
	/**
     * Method name: getValidOperation
     * Purpose: Check if an operation is valid, i.e Operations have the format <op><id><space(s)><item>
     * Pre-condition - Operations in the file should have the correct syntax.
     * Post-condition - None
     * @param line Non empty, non null line read by the buferred reader
     * @return a list of Operation class object
     */
	private static Operation getValidOperation(String line) {
		/* Since we have removed all the spaces:
		   The first char will be operation
		   The last char would be item
		   and everything in between in the transaction ID (it takes care of double digit transaction IDs */
		char cAction = line.charAt(0); // first char is the action 
		//Operation operation;
		if(cAction == 'r' || cAction == 'w' || cAction == 'c') {
		//Ignoring c - commit operation
		//if(cAction == 'r' || cAction == 'w') {
			int iLineLength = line.length();

			String strTransactionNumber = line.substring(1, iLineLength-1); // 
			int iTransactionNumber; // The transaction ID
			try {
				iTransactionNumber = Integer.parseInt(strTransactionNumber); // Parse the string to int for Transaction ID
		    } catch (NumberFormatException nfe) { // Acceptable format of transaction is an int only
 		    	iTransactionNumber = -1;
		    }
			if(iTransactionNumber >= 0) // Check if a non negative transaction number
				if(cAction == 'r' || cAction=='w')
					 // Item is the last char
					return new Operation(cAction, iTransactionNumber, line.charAt(line.length()-1));
				else
					return new Operation(cAction, iTransactionNumber, (char) 0);
			else {
				System.out.println("--------------------------------------------------------- \n");
				System.out.println("Ignoring Operation - Transaction Number not in right format");
				System.out.println("Required Format - <op><id><space(s)><item>");
			}
		} 
		return null;
	}  
}