
/**
 * Class name: Operation.java
 * @author ugola (Urvika Gola)
 * 
 * Detailed Description -  A class that parses the operation into three different data items
 * 
 * Technique useds - POJOs in Java. POJOs are used for increasing the readability and re-usability of a program.
 * Programming language used is Java. 
 * 
 * Input Requirement - The parsed line by line valid and legal operation of a transaction set or schedule.
 * Transactions will be provided as sequences of read, write, and commit operations, one
 * operation per line of a text file. Similarly, our schedules will be given as text files containing the operations of
 * one or more potentially interleaved transactions. Operations have the format <op><id><space(s)><item>,
 * where <op> is one of r (for read), w (write), or c (commit); <id> is an integer that identifies the transaction
 * executing <op>; <item> is a single upper–case letter representing the DB item being read or written
 * 
 */
public class Operation implements Comparable<Operation> {
	private char cOperation;
	private int iTransactionId;
	private char cItem;

	/**
     * Method name: getcOperation
     * Purpose: Getter method of operation
     * @param none
     * @return char; operation char - expected values - r, w or c
     */
	public char getcOperation() {
		return cOperation;
	}

	/**
     * Method name: setcOperation
     * Purpose: Setter method of operation
     * @param cOperation, sets the cOperation char passed to POJO's cOperation field.
     * @return none
     */
	public void setcOperation(char cOperation) {
		this.cOperation = cOperation;
	}
	
	/**
     * Method name: getiTransactionId
     * Purpose: Getter method of transaction ID
     * @param none
     * @return int; transaction ID
     */
	public int getiTransactionId() {
		return iTransactionId;
	}
	
	/**
     * Method name: setiTransactionId
     * Purpose: Setter method of transaction ID
     * @param iTransactionId, sets the iTransactionId int passed to POJO's iTransactionId field.
     * @return none
     */
	public void setiTransactionId(int iTransactionId) {
		this.iTransactionId = iTransactionId;
	}
	
	/**
     * Method name: getcItem
     * Purpose: Getter method of item
     * @param none
     * @return char; item character
     */
	public char getcItem() {
		return cItem;
	}
	
	/**
     * Method name: setcItem
     * Purpose: Setter method of item
     * @param cItem, sets the cItem int passed to POJO's cItem field.
     * @return none
     */
	public void setcItem(char cItem) {
		this.cItem = cItem;
	}

	/**
     * Method name: Operation()
     * Purpose: Constructor of Operation POJO Class
     * @param cOperation, operation value
     * @param iTransactionId, transaction ID
     * @param cItem, item identifier
     * @return none
     */
	public Operation(char cOperation, int iTransactionId, char cItem) {
		super();
		this.cOperation = cOperation;
		this.iTransactionId = iTransactionId;
		this.cItem = cItem;
	}
	
	/**
     * Method name: toString()
     * Purpose: Overriding the default implementation of ToString to print the following operation
     * in readable format whenever the object of operation class is converted to string
     * @param none
     * @return string
     */
	@Override
	public String toString() {
		return  cOperation + iTransactionId + " " + cItem;
	}
	
	/**
     * Method name: compareTo()
     * Purpose: Overriding the default implementation of compareTo 
     * It compares the current Operation object with the one passed as argument
     * @param o: The operation object
     * @return int, 1 if current object is bigger, -1 if current object is smaller, 0 if both are equal
     */
	@Override 
	public int compareTo(Operation o)
    {
        if (this.iTransactionId > o.iTransactionId) {
            // if current object is greater,then return 1
            return 1;
        }
        else if (this.iTransactionId < o.iTransactionId) {
            // if current object is greater,then return -1
            return -1;
        }
        else {
            // if current object is equal to o,then return 0
            return 0;
        }
    }
}