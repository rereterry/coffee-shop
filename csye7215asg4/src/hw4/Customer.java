package hw4;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;    
	
	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}

	public String toString() {
		return name;
	}
	
	public List<Food> getOrder(){
		return this.order;
	}
	public int getOrderNum(){
		return this.orderNum;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		
		//lock list of customers who are in shop right now
				synchronized(Simulation.currCapacity){
					while(!Simulation.tableAvailable())
						try {
							Simulation.currCapacity.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					Simulation.currCapacity.add(this);
					Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
					Simulation.occupyOneTable();
					Simulation.currCapacity.notifyAll();
				}
				Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, order, orderNum));
				Simulation.makeOrder(this, order, orderNum);
				
				synchronized(Simulation.completedOrders){
					while(!(Simulation.completedOrders.contains(this.orderNum))){
						try {
							Simulation.completedOrders.wait();
							Simulation.completedOrders.notifyAll();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					//Simulation.completedOrders.notifyAll();
				}
				//吃完餐點
				Simulation.finishOrder(this.orderNum);
				Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum));
				synchronized(Simulation.currCapacity){
					Simulation.currCapacity.remove(this);
					Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
					Simulation.releaseOneTable();
					Simulation.currCapacity.notifyAll();
				}
		
	}
}