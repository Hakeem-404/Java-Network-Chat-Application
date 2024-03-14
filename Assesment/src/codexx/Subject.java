package codexx;

//the Subject is an object that maintains a list of its dependents, 
//called Observers, and notifies them automatically of any state changes.

public interface Subject {

	//methods to register and unregister observers
	public void register(Observer observer);
	public void unregister(Observer observer);
	
	//method to notify observers of change
	public void notifyObservers(String message);
	
}