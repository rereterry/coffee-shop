package hw4;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	
	private Customer customer;
	public List<Food> finishedFood;

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;

	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(true) {
				//YOUR CODE GOES HERE...
				finishedFood = new LinkedList<Food>();
				//get the customer currently up next
				synchronized(Simulation.orderList){

					while(Simulation.orderList.isEmpty()){
						Simulation.orderList.wait();
					}
					customer = Simulation.orderList.remove();
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, customer.getOrder(), customer.getOrderNum()));
					Simulation.orderList.notifyAll();
				}
				//use hashmap to get specific machien of each food
				for(int index = 0; index < customer.getOrder().size(); index++){
					Food currFood = customer.getOrder().get(index);
					Machine currMachine = Simulation.foodMachinePair.get(currFood);
					
					synchronized(currMachine.foodList){
						
					//	Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.cake , customer.getOrderNum()));
					//	currMachine.makeFood(this, customer.getOrderNum());
					//	currMachine.foodList.notifyAll();
						
						
							switch(currFood.cookTimeMS){
								case 500:
									Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.cake , customer.getOrderNum()));
									currMachine.makeFood(this, customer.getOrderNum());
									currMachine.foodList.notifyAll();
									break;
								case 350:
									Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.pudding , customer.getOrderNum()));
									currMachine.makeFood(this, customer.getOrderNum());
									currMachine.foodList.notifyAll();
									break;
								case 100:
									Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.cdrink , customer.getOrderNum()));
									currMachine.makeFood(this, customer.getOrderNum());
									currMachine.foodList.notifyAll();
									break;
								case 80:
									Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.tdrink , customer.getOrderNum()));
									currMachine.makeFood(this, customer.getOrderNum());
									currMachine.foodList.notifyAll();
									break;
							}
							
						

					}
				}
				//單子上的食物全部完成
				synchronized(finishedFood){
					while(!(finishedFood.size() == customer.getOrder().size())){
						finishedFood.wait();
						//finishedFood.notifyAll();
					}
				}
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, customer.getOrderNum()));
				//完成單子通知消費者
				synchronized(Simulation.completedOrders){
					Simulation.completedOrders.add(customer.getOrderNum());
					Simulation.completedOrders.notify();
				}			
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}