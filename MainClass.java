import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * 
 * Detailed Description - This program performs two task:
 * Task 1:	It reads a given schedule in .sch format
 * 			Constructs the precedence graph described by the schedule
 * 			Checks the graph for cycle(s) to determine whether or not the schedule is conflict serializable
 * 			If the schedule is conflict serializable, topologically sorts the graph to produce a serializability order 
 * 			Prints a list of the IDs of the transactions in the schedule, in ascending order by ID 
 * 			Prints if the schedule is conflict serializable or not
 * 			Prints the IDs of the transactions participating in the detected cycle
 * 			Prints the topological ordering if the schedule is conflict serializable schedule
 * 
 * Task 2: 	It reads a given set of transactions in .set format
 * 			Loops for a number of times equal to the non–negative integer that is supplied as a command line argument
 * 			Creates a legal schedule by creating a random interleaving of the operations of the transaction set
 * 			Determines whether or not the schedule is conflict serializable
 * 			Prints the command–line arguments
 * 			Prints the quantity of schedules that were conflict serializable
 * 			Prints the percentage of schedules that were conflict serializable
 * 
 * Techniques  and data structure used to solve the Task 1 and Task 2:
 * 
 * 3. Java Programming language is used to achieve the two tasks, 
 * Input Requirement for Task 1: 
 * The inputs to task two as provided as just the schedule file filename.sch
 * 
 * Input Requirement Task 2:
 * The inputs to task two are provided as follows:
 * filename.set non_int_num a-b
 * 
 */
public class MainClass {
	/**
     * Method name: getPrecedenceGraph
     * Purpose: Gets the list of nodes of the precedence graph
     * Pre-condition - The list of operations should be parsed and be ready for creating precendence graph
     * Post-condition - None
     * @param listOperations contains all the operations of a schedule, direction going inside the method
     * @return a List of String that contains the node participating in the precendence
     * graph with a directed edge between them, the format of the string is
     * T(i)->T(j) which means that there is a directed edge from T(i) to T(j) 
     * where i and j are the id of the Operation.
     */
    private static List<String> getPrecedenceGraph(List<Operation> listOperations) {
        List<String> listGraph = new ArrayList<String>(); // a List of string the stores the final output
        /* For loop that iterates over every conflicted nodes participating in the precedence graph 
           getConflictedOperations is a private method that returns a HashSet of String
           containing the string of operations that are conflicted. */
        for (String eachOperation : getConflictedOperations(listOperations)) {
        	System.out.println(eachOperation + "\n");
        	listGraph.add(eachOperation); // add every conflicted pair of nodes to the final result
        } 
        return listGraph; // return the final List of String that contains the conflicted pair
    }
    
	/**
     * Method name: getConflictedOperations
     * Purpose: Gets the list of conflicted pair of the precedence graph
     * Pre-condition - The list of operations should be parsed and be ready for finding the conflicting pair
     * Post-condition - None
     * @param listOperations contains all the operations of a schedule, direction going inside the method
     * @return a HashSet of String that contains the node participating in the conflicting pair
     * T(i)->T(j) which means that there is a directed edge from T(i) to T(j) 
     * where i and j are the id of the Operation.
     */
	private static HashSet<String> getConflictedOperations(List<Operation> listOperations) {
		HashSet<String> results = new LinkedHashSet<String>(); // HashSet of String that stores the final result
        /* Loop over the List of Operations and find out the conflicting pair, this is the
		 Outer loop, that will compare every operation to all the other operations that occur after it. 
		 The inner loop is followed just after the outer loop */
        for (int i = 0; i < listOperations.size(); i++) {
            Operation outerOperation = listOperations.get(i);
            for (int j = i+1; j < listOperations.size(); j++) {
                Operation innerOperation = listOperations.get(j);
                
                //If the two operation's transaction ID is same, they are not conflicting, therefore, continue..
                if ((outerOperation.getiTransactionId() == innerOperation.getiTransactionId()))
                    continue;
                //If the two operation's are both reads, they are not conflicting, therefore, continue..
                else if (outerOperation.getcOperation() == 'r' && innerOperation.getcOperation() == 'r')
                    continue;
                //If the two operation's are working with difference items, they are not conflicting, therefore, continue..
                else if (outerOperation.getcItem() != innerOperation.getcItem())
                    continue;
                //If none of the above scenarios, then there must be a conflict, add in the result
                else  
                    results.add(outerOperation.getiTransactionId() + "->" + innerOperation.getiTransactionId());
            }
        }
        return results;
    }
	/**
     * Method name: isCyclic
     * Purpose: Checks if the precedence graph is cyclic or acyclic
     * Pre-condition - The precedence graph should be ready for processing  
     * Post-condition - None
     * @param listPrecedenceGraphOperations contains the operations of precedence graph
     * @return a boolean; True if the precedence graph is cyclic, False if the precedence graph is acyclic
     */
	public static boolean isCyclic(List<String> listPrecedenceGraphOperations) {
        String result = "Is Schedule Conflict-Serializable: "; 
        /* Iterates over the precedence graph's first node i.e a->b where a is the innerFrom and
           b is the outerTo that means the directed edge is going From a To b 
           String eachOuterString stores the outer transaction i.e a
           String eachOuterString stores the inner transaction i.e b 
           Every operation is compared to every other operation*/
        for (String eachOuterString : listPrecedenceGraphOperations) {
        	// Split the outer transaction "a->b" and fetch inner and outer operation i.e "a->b" 
        	String eachOuterStringArray[] = eachOuterString.split("->"); // Iterate over outer operation
        	String outerFrom = eachOuterStringArray[0]; // stores a
            String outerTo = eachOuterStringArray[1]; // stores b
            for (String eachInnerString : listPrecedenceGraphOperations) { // Iterate over inner operation eg. c->d
            	String eachInnerStringArray[] = eachInnerString.split("->");
            	String innerFrom = eachInnerStringArray[0]; // stores c
            	String innerTo = eachInnerStringArray[1]; //stores d
                // The below code checks if a cycle is formed e.g. a->b and b->a 
                if ((outerFrom.compareTo(innerTo) == 0) && (outerTo.compareTo(innerFrom) == 0)) {
                    result += "False\n";
                    result += "There is a cycle between transactions: T" + outerFrom + " and T" + innerFrom;
                    // If a cycle is formed; print the the Transactions due to which the cycle is formed.
                    System.out.println(result);
                    return true;
                }
            }
        }
        // If a cycle is not formed, we start the topological sorting of the precedence graph
        // by calling thr startTopologicalSorting method
        startTopologicalSorting(listPrecedenceGraphOperations);
        result += "True\n";
        result += "Schedule is acyclic, thus it's serializable.\n";
        result += "The schedule is also View-Serializable (Every conflict serializable schedule is also view serializable)";
        System.out.println(result);
        return false; // returns false as precedence is acyclic
    }
	
	/**
     * Method name: startTopologicalSorting
     * Purpose: Finds the topological sort of a precedence graph
     * Pre-condition - The precedence graph should be acyclic
     * Post-condition - Should contain all the transactions participating in the schedule in addition to topological sort
     * @param listPrecedenceGraphOperations contains the operations of precedence graph
     * @return none; Just prints the topological sort on the console 
     */
	private static void startTopologicalSorting(List<String> listPrecedenceGraphOperations) {
		/* Create a map of all the all the transaction nodes and the nodes it is connected to via an edge
		   We need this because we are checking if the node getting removed should not have any incoming directed edge
		   If such a node is found, remove the node in the precedence graph, as well as all the outgoing directed edge 
		   from this particular node */
		Map<String, List<String>> lookUpMap = getLookUpMap(listPrecedenceGraphOperations); 
		// Contains the result of the topological sort 
		List<String> outputGraph = new ArrayList<String>();
		// We store all the unique transactions participating in the precedence graph
		Set<String> distinctPrecedenceGraphElements = getDistinctPrecedenceGraphElements(listPrecedenceGraphOperations);
		// While we have not exhausted all the Transactions or Nodes of the topological sort
		while(!lookUpMap.isEmpty()) {
			// Iterate over each entry in the look up map
		inner: for(Entry<String, List<String>> eachEntry : lookUpMap.entrySet()) {
			// Get the key of the look up map, Key is the Transaction or Node in the graph
				String eachKey = eachEntry.getKey();
				List<String> keyValue = eachEntry.getValue(); // Get the value of the key that was fetched above
				// Get all the "To" nodes where the Key node is going to 
				if(!getCurrentAllToStrings(lookUpMap.values()).contains(eachKey)) { 
					//eg. A->B, A->C, A->D, for the node A, we remove all the edges going To B C and D
					outputGraph.add(eachKey); // Add this node to the output topological sort
					// Remove it from the look up map as we have removed it from the precedence graph
					lookUpMap.remove(eachKey, keyValue); 
					continue inner;
				}
				// If we have reached the last pair of conflicting nodes eg. if a independent pair is left
				if(lookUpMap.size() == 1) { 
					outputGraph.add(eachKey);  // then append key to the topological sort
					for(String s : keyValue)   // For every value of the key (node) of the topological sort 
						outputGraph.add(s);    // Add this node to the output topological sort
					lookUpMap.remove(eachKey, keyValue);  // Subsequently remove it from the map
				}
			}
		}
		/* Recheck to see if all of the transactions in the precendence graph are present in the topological sort.
		   Some transactions become independent, i.e. not connected by any edge, when edges are removed, 
		   i.e. their order in the topological sort has no effect on the serializability of the resulting schedule, 
		   they can be placed anywhere, after discussion with the Professor in office hour decided to 
		   append them in the end */ 
		// Check if the nodes in the output graph are same as in the precedence graph
		if(outputGraph.size() != distinctPrecedenceGraphElements.size()) 
			for(String s : distinctPrecedenceGraphElements) // for every unique node in the precedence graph 
				if(!outputGraph.contains(s)) // if there is an independent node which is not added yet
					outputGraph.add(s); // Add the independent transactions explained above, to the result          
	}

	/**
     * Method name: getDistinctPrecedenceGraphElements
     * Purpose: Finds the unique nodes of the precedence graph sort of a precedence graph, so that we can compare it with 
     * the topological sort result, as some nodes become indpendent, i.e not connected by any edge when edges are removed.
     * Pre-condition - The precedence graph should be acyclic and ready for processing 
     * Post-condition - Should contain all the unqiue transactions participating in the topological sort
     * @param listPrecedenceGraphOperations contains the operations of precedence graph
     * @return Set of String; Using a set because it contains unique keys 
     */
	private static Set<String> getDistinctPrecedenceGraphElements(List<String> listPrecedenceGraphOperations) {
		// The string that contains the unique nodes of the precedence graph
		Set<String> distinctPrecedenceGraphElements = new HashSet<String>(); 
		// Iterate over the precedence graph nodes in a string i.e "a->b"
		for(String eachString : listPrecedenceGraphOperations) { 
			String eachStringArray[] = eachString.split("->"); // Split that on -> to get a and b
			String strFromTransaction = eachStringArray[0]; // Stores a
			String strToTransaction = eachStringArray[1]; // Stores b
			distinctPrecedenceGraphElements.add(strFromTransaction); // Add a to distinct graph
			distinctPrecedenceGraphElements.add(strToTransaction); // Add b to distinct graph
			// If the nodes are already present, since it's a set, there will be no duplicates.
		}
		return distinctPrecedenceGraphElements;
	}

	/**
     * Method name: getCurrentAllToStrings
     * Purpose: Finds the unique nodes of the precedence graph sort of a precedence graph, so that we can compare it with 
     * the topological sort result, as some nodes become indpendent, i.e not connected by any edge when edges are removed.
     * Pre-condition - The precedence graph should be acyclic and ready for processing 
     * Post-condition - Should contain all the unqiue transactions participating in the topological sort
     * @param listPrecedenceGraphOperations contains the operations of precedence graph
     * @return List of String; Using a set because it contains unique keys 
     */
	private static List<String> getCurrentAllToStrings(Collection<List<String>> values) {
		
		List<String> output = new LinkedList<String>();
		for(List<String> eachList : values) 
			for(String str: eachList)
				output.add(str);
		return output;
	}

	/**
     * Method name: getLookUpMap
     * Purpose: Creates a Map of Key Value pairs such that the Key is a particular node in the precedence graph
     * and Values are all the nodes it's connected to such that the outgoing node is from the Key node
     * For eg. A->B, A->C, A->D, The Map would look like {"A", {"B", "C", "D"}}
     * Pre-condition - The precedence graph should be acyclic and ready for processing 
     * Post-condition - The Map should contain all the nodes as keys participating in the Precendence graph
     * @param listPrecedenceGraphOperations contains the operations of precedence graph
     * @return a Map with Key Value Pair such that the Key is a particular node in the precedence graph
     * and Values are all the nodes it's connected to such that the outgoing node is from the Key node
     */
	private static Map<String, List<String>> getLookUpMap(List<String> listPrecedenceGraphOperations) {
		//Declare a lookup Map with Key Value Pair such that the Key is a particular node in the precedence graph
	    // and Values are all the nodes it's connected to such that the outgoing node is from the Key node
		Map<String, List<String>> lookUpMap = new ConcurrentHashMap<String, List<String>>(); 
		// Iterate over the precedence graph nodes in a string i.e "a->b"
		for(String eachString : listPrecedenceGraphOperations) { 
			String eachStringArray[] = eachString.split("->"); // Split that on -> to get a and b, from->to
			String strFromTransaction = eachStringArray[0]; // Stores a
			String strToTransaction = eachStringArray[1]; // Stores b
			if(!lookUpMap.containsKey(strFromTransaction)) { // If the key is not there in the map
				List<String> valueList = new ArrayList<String>(); // Then create a value list for this key
				valueList.add(strToTransaction); // Add the To transaction i.e 'b' to it's value
				lookUpMap.put(strFromTransaction, valueList); // Add this key value pair to the resulting map
			} else { // If the key value pair a->b already exists in the map and a->c pair arrives, 
				// then the map should also add c to it's values in addition to b.
				List<String> valueList = lookUpMap.get(strFromTransaction); // Then the get the existing values of this key
				valueList.add(strToTransaction); // and add the To transaction to this list for this key
				lookUpMap.put(strFromTransaction, valueList); // Add this key value pair to the resulting map
			}
		}
		return lookUpMap; 
	}
	
	/**
     * Method name: createTransactionOperationQueues
     * Purpose: We create a Map containing the Queue for every operation for a unique Transaction 
     * i.e If the schedule contains T1 A, T1 B, T2 A, T2 C; A queue is created for T1 (Key) with T1 A and T1 B (Values);
     * and a different queue is created with T2 (Key) with T2 A, T2 C (Values)
     * Note; Using a queue due to it's FIFO property, that will assist in picking the first 'n' schedules
     * Pre-condition - The listOperations should contain all the operations in the set file
     * Post-condition - The Key of the Map should contain all the nodes as keys participating in the .set file
     * @param listOperations contains the operations of precedence graph
     * @return a Map with Key Value Pair such that the Key is a unique Transaction ID in the .set file
     * and Values are all the operations performed by the Transaction ID
     */
	private static Map<Integer, Queue<Operation>> createTransactionOperationQueues(List<Operation> listOperations) {
		// Declaring the resulting map
		Map<Integer, Queue<Operation>> mapTransactionToOperations = new HashMap<Integer, Queue<Operation>>(); 
		for(Operation eachOperation : listOperations) { // Iterate over every operation in the operations of the .set file
			int iTransactionID = eachOperation.getiTransactionId(); // Get the Transaction ID of the operation
			// If the Map already contains the transaction ID
			if(mapTransactionToOperations.keySet().contains(iTransactionID)) { 
				Queue<Operation> transactionQueue = mapTransactionToOperations.get(iTransactionID); // Get its values
				transactionQueue.add(eachOperation); // Add the operation in the queue
				mapTransactionToOperations.put(iTransactionID, transactionQueue); // Update the map with the new values
			} else {
				// If the Map does not contain the transaction ID
				Queue<Operation> eachTransactionQueue = new LinkedList<Operation>(); 
				eachTransactionQueue.add(eachOperation); // Add the operation to the queue
				mapTransactionToOperations.put(iTransactionID, eachTransactionQueue); // Update the map with the new values
			}
		}
		return mapTransactionToOperations;
	}
	
	/**
     * Method name: createRandomLegalSchedules
     * Purpose: We create a legal random interleaving of the .set file
     * Pre-condition - The listOperations should contain all the operations in the set file
     * Post-condition - The Key of the Map should contain all the nodes as keys participating in the .set file
     * @param nonNegativeInteger that is passed as the command line argument
     * @param a lower range (inclusive) that is passed as the command line argument
     * @param b upper range (inclusive) that is passed as the command line argument
     * @param mapTransactionToOperations The Map containing the transaction number and it's operations in form of a queue
     * @param deepClonedMap This is the original Map from the previous iteration; 
     * it's deep cloned because we need it in the initial form for every (nonNegativeInteger)th iteration. 
     * We need it in the initial form because the original form of the map is changed when we dequeue from the queue.
     * @return a Map with Key Value Pair such that the Keys are the integer values from 1 to nonNegativeInteger
     * Transaction ID in the .set file and Values are all the random interleaved operations performed by 
     * the 1 to nonNegativeInteger range schedules.
     */
	private static Map<Integer, List<Operation>> createRandomLegalSchedules(
			int nonNegativeInteger, int a, int b, Map<Integer, Queue<Operation>> mapTransactionToOperations, Map<Integer, Queue<Operation>> deepClonedMap) {
		// Declaring a Map with the key value pair
		Map<Integer, List<Operation>> mapTransactionRandomLegalSchedules = new HashMap<Integer, List<Operation>>();
		Random randomInterleaving = new Random();
		Random randomTransaction = new Random();
		for(int i = 1; i <= nonNegativeInteger; i++) { // Loop until the non negative integer
			// Refer the original deep cloned map for every iteration
			mapTransactionToOperations = getDeepClonedMap(deepClonedMap); 
			// Get the available transactions every time, as they may become exhausted if we have dequeued 
			// all the operations with that transaction ID.
			List<Integer> availableTransactions = getAllAvailableTransactions(mapTransactionToOperations); 
			// Declare a list of operations of the new random schedule 
			List<Operation> eachRandomLegalSchedule = new LinkedList<Operation>(); 
	inner: while(!availableTransactions.isEmpty()) { // Loop until all the available transaction IDs are exhausted
				// Generate a random transaction number that chooses amongst the available transaction IDs
				// by calling the private method that gets the random transaction ID 
				int randomTransactionID = getRandomTransactionID(randomTransaction, availableTransactions);
				// Getting a random number to get a value between a and b (inclusive)
				int randomInterleave = getRandomInterleaveBetweenIntervals(randomInterleaving, a, b); 
				//Pick the random transaction from the queue
				Queue<Operation> transactionQueue = mapTransactionToOperations.get(randomTransactionID); 
				// Dequeue the transactions in FIFO form from the operation queue of a random transaction
				// until the transaction queue is NOT empty and until the we have picked all the transactions 
				while(!transactionQueue.isEmpty() && randomInterleave > 0) { 
					// Dequeue from the queue of a transaction and add it into the random schedule 
					eachRandomLegalSchedule.add(transactionQueue.poll()); 
					randomInterleave--; // Decrement to keep track of how much we have removed
				}
				// Incase the queue becomes empty, before removing randomInterleave operations
				// Then remove this transaction ID from map and update the available transactions
				if(transactionQueue.isEmpty()) {
					mapTransactionToOperations.remove(randomTransactionID);
					availableTransactions = getAllAvailableTransactions(mapTransactionToOperations); //updating
				}
		   }
			// Add the random legal scheduled by performing the above operations, to the final map with Key Value 
			// Pair such that the Keys are the integer values from 1 to nonNegativeInteger
    		// Transaction ID in the .set file and Values are all the operations performed by the Transaction ID
		   mapTransactionRandomLegalSchedules.put(i, eachRandomLegalSchedule);
		}
		return mapTransactionRandomLegalSchedules;
	}
	
	/**
     * Method name: getDeepClonedMap
     * Purpose: We must deep clone the Map containing the transaction number and its operations as a queue because
     * it must be initial form for every (nonNegativeInteger)th iteration. 
     * It is in its initial form because the map's original form is changed when we dequeue from the queue.
     * Pre-condition - The map to be clone should be the initial one, without any alterations.
     * Post-condition - The clone should be unaltered.
     * @param mapToClone should be ready
     * @return a Map; i.e Clone of the Map with Key Value Pair such that the Keys are the integer values from 1 to nonNegativeInteger
     * Transaction ID in the .set file and Values are all the random interleaved operations performed by 
     * the 1 to nonNegativeInteger range schedules.
     */
	private static Map<Integer, Queue<Operation>> getDeepClonedMap(Map<Integer, Queue<Operation>> mapToClone) {
		/* The below steps perform a deep copy in Java */
		Gson gson = new Gson();
		String jsonString = gson.toJson(mapToClone);
		Type type = new TypeToken<HashMap<Integer, Queue<Operation>>>(){}.getType();
		HashMap<Integer, Queue<Operation>> deepClonedMapOutput = gson.fromJson(jsonString, type); 
		return deepClonedMapOutput;
	}

	/**
     * Method name: getRandomInterleaveBetweenIntervals
     * Purpose: Generate a random number in the range of a and b (both inclusive) 
     * Pre-condition - a and b should be non negative valid integers, can be same as well
     * Post-condition - The random int generated must lie within the expected range i.e a <= random_int <=b
	 * @param randomInterleaving 
     * @param a lower range (inclusive) that is passed as the command line argument
     * @param b upper range (inclusive) that is passed as the command line argument     
     * @return an int in the range of a and b (both inclusive) 
     */
	private static int getRandomInterleaveBetweenIntervals(Random randomInterleaving, int a, int b) {
		if (a == b) // If both are equal, then return any of the value a or b. 
			return a;
		return randomInterleaving.nextInt(b-(a+1)) + a; // Otherwise generate a random number in the range a <= random_int <=b
	}
	
	/**
     * Method name: getRandomTransactionID
     * Purpose: Picks a random transaction ID amongst the available Transaction IDs.
     * Note that when all the operations of a particular transactions ID is exhausted
     * then the transaction ID is no more 'available'
     * Pre-condition - availableTransactions should contain the available Transaction IDs only
     * otherwise a Transaction ID may be picked which has nomore operations to be dequeued.
     * Post-condition - The transaction ID picked should contain some operations to dequeue
	 * @param randomTransaction 
     * @param availableTransactions contains the List of Integer containing the available Transaction IDs.  
     * @return a random int whose value is one amongst the available transaction int IDs
     */
	private static int getRandomTransactionID(Random randomTransaction, List<Integer> availableTransactions) {
		// Using the Random inbuilt utility in Java
		// Generate random values from 0 - size of the list
	    int upperbound = availableTransactions.size();   
	    
		return availableTransactions.get(randomTransaction.nextInt(upperbound));
	}

	/**
     * Method name: getAllAvailableTransactions
     * Purpose: Gets all the transaction IDs that still has operations in it that can be dequeued.
     * Pre-condition - mapTransactionToOperations should have data
     * Post-condition - None
     * @param mapTransactionToOperations The Map containing the transaction number and it's operations in form of a queue
     * @return a List of Int containing the available Transaction IDs
     */
	private static List<Integer> getAllAvailableTransactions(Map<Integer, Queue<Operation>> mapTransactionToOperations) {
		List<Integer> allAvailableTransactions = new LinkedList<Integer>(); // Declaring a resultant list
		// Iterate over each key in the Map
		for(Entry<Integer, Queue<Operation>> eachEntry : mapTransactionToOperations.entrySet()) 
			allAvailableTransactions.add(eachEntry.getKey()); // Every key in the Map is an available transaction ID
		return allAvailableTransactions;
	}
	
	/**
     * Method name: processRandomLegalSchedules
     * Purpose: Process to test for conflict serializability for all the random legal schedules that were created
     * Pre-condition - mapTransactionRandomLegalSchedules the result map that contains all the random, legal schedules
     * Post-condition - The result should be greater than 0 and less than or equal to the total number of schedules
     * @param mapTransactionRandomLegalSchedules Map containing the transaction num & its operations in form of a queue
     * @return a int informing how many schedules are conflict serializable
     */
	private static int processRandomLegalSchedules(Map<Integer, List<Operation>> mapTransactionRandomLegalSchedules) {
		Map<String, Integer> finalResultMap = new HashMap<String, Integer>();
		int numOfConflicSerializables = 0; // Stores the number of conflict serializable schedules, default value is 0
		/* Iterate over each random, legal schedule and check if it's conflict serializable 
		   By creating it's precedence graph and checking if the graph is asyclic */
		for(Entry<Integer, List<Operation>> eachEntry : mapTransactionRandomLegalSchedules.entrySet()) {
			List<String> listPrecedenceGraphOperations = (getPrecedenceGraph(eachEntry.getValue()));
			boolean isCyclic = isCyclic(listPrecedenceGraphOperations); // Check if it's asyclic
			if(!isCyclic)
				numOfConflicSerializables++; // If the schedule is acyclic, it's conflict serializable, increment counter.
		}
		return numOfConflicSerializables;
	}
	
	/**
     * Method name: main
     * Purpose: Process to test for conflict serializability for all the random legal schedules that were created
     * Pre-condition - mapTransactionRandomLegalSchedules the result map that contains all the random, legal schedules
     * Post-condition - The result should be greater than 0 and less than or equal to the total number of schedules
     * @param mapTransactionRandomLegalSchedules The Map containing the transaction number and it's operations in form of a queue
     * @return a int informing how many schedules are conflict serializable
     */
	public static void main(String[] args) {
		BufferedReader reader;
		
		String strFilePath = args[0];
		Path inputFilePath = Paths.get(strFilePath);
		String inputFileName = inputFilePath.getFileName().toString();
		try {
			reader = new BufferedReader(new FileReader(strFilePath));
			List<Operation> listOperations = GetContentromFile.getListOfOperationsFromSchedule(reader); 
			if(inputFileName.trim().endsWith(".sch")) {
			//Check logic for .sch & .set
					List<String> listPrecedenceGraphOperations = (getPrecedenceGraph(listOperations));
					boolean isCyclic = isCyclic(listPrecedenceGraphOperations);
					if(!isCyclic)
						//If non-cyclic it is conflic serializable
						startTopologicalSorting(listPrecedenceGraphOperations);
					// printTask1Point5(listOperations, isCyclic, )
					else {
						System.out.println("Topological Sorting cannot be created because the graph is cyclic");
					}
					
			} else if(inputFileName.trim().endsWith(".set")) {
				
				/*int nonNegativeInteger = Integer.valueOf(args[1]);
				String range = args[2];
				String [] ab = range.split("-");
				int a = Integer.valueOf(ab[0]);
				int b = Integer.valueOf(ab[1]);
				*/
				int nonNegativeInteger = 3;
				int a = 2;
				int b = 4;
				Map<Integer, Queue<Operation>> mapTransactionToOperations = createTransactionOperationQueues(listOperations);
				Map<Integer, Queue<Operation>> deepClonedMap = getDeepClonedMap(mapTransactionToOperations);
				Map<Integer, List<Operation>> mapTransactionRandomLegalSchedules = createRandomLegalSchedules(nonNegativeInteger, a, b, mapTransactionToOperations, deepClonedMap);
				int numberOfConflictSerializables = processRandomLegalSchedules(mapTransactionRandomLegalSchedules);
				double percentageOfConflictSerializable = Math.round((Double.valueOf(numberOfConflictSerializables)/Double.valueOf(nonNegativeInteger)) * 100);
				System.out.println();
				System.out.println("---------------------------------------------------------------------------------------------------------------------------");
				System.out.println("Total number of Conflict Serializables in " + nonNegativeInteger +  " random legal schedules = " + numberOfConflictSerializables);
				System.out.println("Percentage of Conflict Serializables in " + nonNegativeInteger +  " random legal schedules = " + percentageOfConflictSerializable + "%");
			}
		} catch(Exception e) {
			System.out.println(e.toString());
		}
	}
}
