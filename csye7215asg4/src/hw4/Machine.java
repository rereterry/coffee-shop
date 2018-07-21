package hw4;

import java.util.ArrayList;
import java.util.List;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;

	public int capacity;
	public List<Food> foodList;
	//YOUR CODE GOES HERE...
	private int currMachineCapacity;


	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		
		//YOUR CODE GOES HERE...
		
		this.capacity = capacityIn;
		this.foodList = new ArrayList<Food>();
		this.currMachineCapacity = 0;

	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	
	public void makeFood(Cook name, int orderNum) throws InterruptedException {
		//YOUR CODE GOES HERE...
		foodList.add(machineFoodType);
		Thread cooking = new Thread(new CookAnItem(name,orderNum,this));
		cooking.start();
		
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		
		Cook cook;//currCook
		Machine machine;
		int orderNum;
		public CookAnItem(Cook currCook, int orderNum,Machine machine){
			this.cook = currCook;
			this.machine = machine;
			this.orderNum = orderNum;
		}
		
		public void run() {
			try {
				//YOUR CODE GOES HERE...
				
				
				synchronized(machine){
					//當容量滿的時候，機器的線程暫停
					while(isFullCapacity()){
						machine.wait();
					}
					currMachineCapacity++;
					Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));
					
					//machine.notifyAll();
				}
				//料理時間
				Thread.sleep(machineFoodType.cookTimeMS);
				
				//機器對應的食物跟單子
				synchronized(machine){
					
					Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));
					Simulation.logEvent(SimulationEvent.cookFinishedFood(cook, machineFoodType,orderNum));
					currMachineCapacity--;
					machine.notifyAll();
				}
				//接單子，所以再次全部喚醒
				synchronized(foodList){
					foodList.remove(0);
					foodList.notifyAll();	
				}
				//廚師完成食物
				synchronized(cook.finishedFood){
					cook.finishedFood.add(machineFoodType);
					cook.finishedFood.notifyAll();	
				}
			} catch(InterruptedException e) { }
		}
	}
	// check the machine whether still can use
	private boolean isFullCapacity(){
		synchronized(this){
			if(currMachineCapacity < capacity){
				return false;
			}
			else{
				return true;
			}
		}
	}
 

	public String toString() {
		return machineName;
	}
}