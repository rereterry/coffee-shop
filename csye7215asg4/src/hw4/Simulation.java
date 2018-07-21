package hw4;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Simulation is the main class used to run the simulation.  You may
 * add any fields (static or instance) or any methods you wish.
 */
public class Simulation {
	// List to track simulation events during simulation
	public static List<SimulationEvent> events; 
	private static int shopCapacity;
	private static Integer occupiedTables = 0;
	public static Queue<Customer> orderList = new LinkedList<Customer>();
	public static Queue<Customer> currCapacity = new LinkedList<Customer>();
	public static Set<Integer> completedOrders = new HashSet<Integer>();
	public static Map<Food,Machine> foodMachinePair = new HashMap<Food,Machine>();




	/**
	 * Used by other classes in the simulation to log events
	 * @param event
	 */
	public static void logEvent(SimulationEvent event) {
		events.add(event);
		System.out.println(event);
	}

	/**
	 * 	Function responsible for performing the simulation. Returns a List of 
	 *  SimulationEvent objects, constructed any way you see fit. This List will
	 *  be validated by a call to Validate.validateSimulation. This method is
	 *  called from Simulation.main(). We should be able to test your code by 
	 *  only calling runSimulation.
	 *  
	 *  Parameters:
	 *	@param numCustomers the number of customers wanting to enter the coffee shop
	 *	@param numCooks the number of cooks in the simulation
	 *	@param numTables the number of tables in the coffe shop (i.e. coffee shop capacity)
	 *	@param machineCapacity the capacity of all machines in the coffee shop
	 *  @param randomOrders a flag say whether or not to give each customer a random order
	 *
	 */
	public static List<SimulationEvent> runSimulation(
			int numCustomers, int numCooks,
			int numTables, 
			int machineCapacity,
			boolean randomOrders
			) {

		//This method's signature MUST NOT CHANGE.  


		//We are providing this events list object for you.  
		//  It is the ONLY PLACE where a concurrent collection object is 
		//  allowed to be used.
		events = Collections.synchronizedList(new ArrayList<SimulationEvent>());




		// Start the simulation
		logEvent(SimulationEvent.startSimulation(numCustomers,
				numCooks,
				numTables,
				machineCapacity));



		// Set things up you might need
		shopCapacity = numTables;


		// Start up machines
		Machine cakeMachine = new Machine("Cake", FoodType.cake, machineCapacity);
		Machine puddingMachine = new Machine("Pudding", FoodType.pudding, machineCapacity);
		Machine coffee = new Machine("Coffee Drink", FoodType.cdrink, machineCapacity);
		Machine tea = new Machine("Tea Drink", FoodType.tdrink, machineCapacity);
		foodMachinePair.put(FoodType.cake, cakeMachine);
		foodMachinePair.put(FoodType.pudding, puddingMachine);
		foodMachinePair.put(FoodType.cdrink, coffee);
		foodMachinePair.put(FoodType.tdrink, tea);
		logEvent(SimulationEvent.machineStarting(cakeMachine, FoodType.cake, machineCapacity));
		logEvent(SimulationEvent.machineStarting(puddingMachine, FoodType.pudding, machineCapacity));
		logEvent(SimulationEvent.machineStarting(coffee, FoodType.cdrink, machineCapacity));
		logEvent(SimulationEvent.machineStarting(tea, FoodType.tdrink, machineCapacity));



		// Let cooks in
		Thread[] cooks = new Thread[numCooks];
		for(int i = 0; i < numCooks; i++){
			cooks[i] = new Thread(new Cook("cook"+(i+1)));
			cooks[i].start();
		}


		// Build the customers.
		Thread[] customers = new Thread[numCustomers];
		LinkedList<Food> order;
		if (!randomOrders) {
			order = new LinkedList<Food>();
			order.add(FoodType.cake);
			order.add(FoodType.pudding);
			order.add(FoodType.cdrink);
			order.add(FoodType.tdrink);
			for(int i = 0; i < customers.length; i++) {
				customers[i] = new Thread(
						new Customer("Customer " + (i+1), order)
						);
			}
		}
		else {
			for(int i = 0; i < customers.length; i++) {
				Random rnd = new Random(27);
				int cakeCount = rnd.nextInt(3);
				int puddingCount = rnd.nextInt(3);
				int coffeeCount = rnd.nextInt(3);
				int teaCount = rnd.nextInt(3);
				order = new LinkedList<Food>();
				for (int b = 0; b < cakeCount; b++) {
					order.add(FoodType.cake);
				}
				for (int f = 0; f < puddingCount; f++) {
					order.add(FoodType.pudding);
				}
				for (int c = 0; c < coffeeCount; c++) {
					order.add(FoodType.cdrink);
				}
				for (int c = 0; c < teaCount; c++) {
					order.add(FoodType.tdrink);
				}
				customers[i] = new Thread(
						new Customer("Customer " + (i+1), order)
						);
			}
		}


		// Now "let the customers know the shop is open" by
		//    starting them running in their own thread.
		for(int i = 0; i < customers.length; i++) {
			customers[i].start();
			//NOTE: Starting the customer does NOT mean they get to go
			//      right into the shop.  There has to be a table for
			//      them.  The Customer class' run method has many jobs
			//      to do - one of these is waiting for an available
			//      table...
		}


		try {
			// Wait for customers to finish
			//   -- you need to add some code here...
			
			
			for(int i = 0; i < customers.length; i++)
			{
				customers[i].join();
			}
			
			

			// Then send cooks home...
			// The easiest way to do this might be the following, where
			// we interrupt their threads.  There are other approaches
			// though, so you can change this if you want to.
			for(int i = 0; i < cooks.length; i++)
				cooks[i].interrupt();
			
			
			for(int i = 0; i < cooks.length; i++)
				cooks[i].join();

		}
		catch(InterruptedException e) {
			System.out.println("Simulation thread interrupted.");
		}

		// Shut down machines
		logEvent(SimulationEvent.machineEnding(cakeMachine));
		logEvent(SimulationEvent.machineEnding(puddingMachine));
		logEvent(SimulationEvent.machineEnding(coffee));
		logEvent(SimulationEvent.machineEnding(tea));



		// Done with simulation		
		logEvent(SimulationEvent.endSimulation());

		return events;
	}
	
	//use to check the table and customer situation
	public static boolean tableAvailable(){
		synchronized(occupiedTables){
			return occupiedTables < shopCapacity;
		}
	}
	
	public static void occupyOneTable(){
		synchronized(occupiedTables){
			occupiedTables++;
		}
	}
	
	public static void releaseOneTable(){
		synchronized(occupiedTables){
			occupiedTables--;
		}
	}
	public static void makeOrder(Customer cu,List<Food> order, int orderNum){
		synchronized(orderList){
			orderList.add(cu);
			orderList.notifyAll();
		}
	}
	
	public static void finishOrder(int orderNum){
		synchronized(Simulation.completedOrders){
			while(!(Simulation.completedOrders.contains(orderNum))){
				try {
					Simulation.completedOrders.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Simulation.completedOrders.notifyAll();
		}
	}

	/**
	 * Entry point for the simulation.
	 *
	 * @param args the command-line arguments for the simulation.  There
	 * should be exactly four arguments: the first is the number of customers,
	 * the second is the number of cooks, the third is the number of tables
	 * in the coffee shop, and the fourth is the number of items each cooking
	 * machine can make at the same time.  
	 */
	public static void main(String args[]) throws InterruptedException {
		// Parameters to the simulation
		/*
		if (args.length != 4) {
			System.err.println("usage: java Simulation <#customers> <#cooks> <#tables> <capacity> <randomorders");
			System.exit(1);
		}
		int numCustomers = new Integer(args[0]).intValue();
		int numCooks = new Integer(args[1]).intValue();
		int numTables = new Integer(args[2]).intValue();
		int machineCapacity = new Integer(args[3]).intValue();
		boolean randomOrders = new Boolean(args[4]);
		 */
		int numCustomers = 50;
		int numCooks =3;
		int numTables = 5;
		int machineCapacity = 4;
		boolean randomOrders = true;


		// Run the simulation and then 
		//   feed the result into the method to validate simulation.
		System.out.println("Did it work? " + 
				Validate.validateSimulation(
						runSimulation(
								numCustomers, numCooks, 
								numTables, machineCapacity,
								randomOrders
								)
						)
				);
	}

}



