package hw4;

/**
 * We create all food objects used by the simulation in one place, here.  
 * This allows us to safely check equality via reference, rather than by 
 * structure/values.
 *
 */
public class FoodType {
	public static final Food cake = new Food("cake",500);
	public static final Food pudding = new Food("pudding",350);
	public static final Food cdrink = new Food("coffee_drink",100);
	public static final Food tdrink = new Food("tea_drink",80);
}
