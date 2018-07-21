# coffee-shop
In this project, you will write a simulation for a coffee shop. The simulation has five parameters:
- the number of customers (patrons) that wish to enter the coffee shop;
- the number of tables in the coffee shop (note: there can only be as many
customers inside the coffee shop as there are tables at any one time - other customers must wait for a free table before going into the coffee shop and placing their order);
- the number of cooks in the kitchen that fill orders;
- the capacity of machines in the kitchen used for producing food; and - a flag as to whether everyone has the same order or not.
Hint: you will need some way for your Machine to cook food items in parallel, up to the capacity, but ensure that each item takes the required time. You might do this by having a Machine use threads internally to perform the "work" of cooking the Food. This approach will require some way of communicating a request by a Cook to make Food to an internal thread, and a way to communicate back to that Cook that the Food is done. In this simulation, the only thing that will actually be done during the "cooking" time is waiting the proper amount of time.
Note: You should model the time a food item is actually cooking using a statement of form Thread.sleep(n), where n is the cook time of the food. So, modeling the actual cooking of a pizza would be represented via the statement Thread.sleep(600). This
means that simulations will run much “faster” than real time, since Thread.sleep(600) terminates in approximately 600 milliseconds rather than 600 seconds. This is a standard practice in simulation development! Simulation time and real time are rarely the same. Sometimes simulations run much faster than the phenomena being modeled (as is the case here); other times they run much slower.
