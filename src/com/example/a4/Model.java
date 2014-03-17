/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery & Michael Terry
 */
package com.example.a4;

import android.util.Log;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/*
 * Class the contains a list of fruit to display.
 * Follows MVC pattern, with methods to add observers,
 * and notify them when the fruit list changes.
 */
public class Model extends Observable {
	/*
    // List of fruit that we want to display
    private ArrayList<Fruit> shapes = new ArrayList<Fruit>();

    // Constructor
    Model() {
        shapes.clear();
    }

    // Model methods
    // You may need to add more methods here, depending on required functionality.
    // For instance, this sample makes to effort to discard fruit from the list.
    public void add(Fruit s) {
        shapes.add(s);
        setChanged();
        notifyObservers();
    }

    public void remove(Fruit s) {
        shapes.remove(s);
    }

    public ArrayList<Fruit> getShapes() {
        return (ArrayList<Fruit>) shapes.clone();
    }
    public ArrayList<Fruit> getActualShapes() {
        return (ArrayList<Fruit>) shapes;
    }

    // MVC methods
    // Basic MVC methods to bind view and model together.
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    // a helper to make it easier to initialize all observers
    public void initObservers() {
        setChanged();
        notifyObservers();
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
        setChanged();
        notifyObservers();
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
        setChanged();
        notifyObservers();
    }
}
public class NewModel {
*/
	  // Observer list

	  // Fruit that we want to display
	  private ArrayList<Fruit> shapes = new ArrayList();

	  private int slashed = 0;
	  private int dropped = 0;
	  private int total_shapes = 0;
	  private int difficulty = 3;
	  
	  private long startTime = System.currentTimeMillis();
	  private long stopTime = 0;
	  
	  private boolean running;
	  private boolean game_over;
	  
	  // Constructor
	  Model() {
	    shapes.clear();
	    running = false;
	    game_over = false;
	  }
		public boolean isRunning() {
			return running;
		}
		public boolean isGameOver() {
			return game_over;
		}
	  public void startGame() {
		  if(game_over) {
			  game_over = false;
			  running = true;
			  slashed = 0;
			  dropped = 0;
			  total_shapes = 0;
			  shapes.clear();
		  }
		  else {
			  running = true;
		  }
	  }
	  public void setStartTime(long value) {
		  startTime = value;
	  }
	  public void setStopTime(long value) {
		  stopTime = value;
	  }
	  public long getStartTime() {
		  return startTime;
	  }
	  public long getStopTime() {
		  return stopTime;
	  }
	  public String getCurrentTime() {
		  if(isGameOver()) {
				return "00:00";
			}
		  else //if(isRunning())
			  {
				long estimatedTime = System.currentTimeMillis() - startTime;
				int seconds = (int) (estimatedTime / 1000) % 60 ;
				int minutes = (int) ((estimatedTime / (1000*60)) % 60);
				String second = seconds < 10 ? "0" + seconds : "" + seconds;
				String minute = minutes < 10 ? "0" + minutes : "" + minutes;
				return new String(minute + ":" + second);
			}

	  }
	  public void stopGame() {
		  running = false;
	  }
	  public void EndGame() {
		  running = false;
		  game_over = true;
	  }
	  public int getSlashed() {
		  return slashed;
	  }
	  public int getDropped() {
		  return dropped;
	  }
	  public void incrementSlashed() {
		  slashed++;
	  }
	  public void incrementDropped() {
		  dropped++;

		  if(dropped >= 6) {
			  EndGame();
		  }
	  }
	  public int getNumLivesRemaining() {
		  int lives = 0;
		  if(5-dropped > 0) {
			  lives = 5-dropped;
		  }
		  return lives;
	  }
	  public int getTotalShapesPlayed() {
		  return total_shapes;
	  }
	  public void incrementTotal() {
		  total_shapes ++;
		  notifyObservers();
	  }
	  
	  public void setDifficulty(int diff) {
		  this.difficulty = diff;
	  }
	  public int getAnimateTimerDifficulty() {
		  switch(this.difficulty) {
			  case 1: return 80;
			  case 2: return 70;
			  case 3: return 60;
			  case 4: return 50;
			  case 5: return 40;
			  default: return 90;
		  }
	  }
	  public int getFruitTimerDifficulty() {
		  switch(this.difficulty) {
			  case 1: return 1500;
			  case 2: return 1200;
			  case 3: return 1000;
			  case 4: return 800;
			  case 5: return 500;
			  default: return 1000;
		  }
	  }


	    // Model methods
	    // You may need to add more methods here, depending on required functionality.
	    // For instance, this sample makes to effort to discard fruit from the list.
	    public void add(Fruit s) {
	        shapes.add(s);
	        setChanged();
	        notifyObservers();
	    }
	  public void remove(int index) {
		  if(index < shapes.size()) {
			  shapes.remove(index);
		  }
	  }

	  public ArrayList<Fruit> getShapes() {
	      return (ArrayList<Fruit>)shapes.clone();
	  }

	  public ArrayList<Fruit> getActualShapes() {
	      return shapes;
	  }
	  public int getNumActiveShapes() {
		  return shapes.size();
	  }
	  public int getRandomNumber(int maximum, int minimum) {
	      /* int randomNum = minimum + (int)(Math.random()*maximum); */
	  	int randomNum = minimum + (int)(Math.random()*(maximum - minimum));
	      //random.nextInt(upperBound - lowerBound) + lowerBound
	      return randomNum;
	  }

	    // MVC methods
	    // Basic MVC methods to bind view and model together.
	    public void addObserver(Observer observer) {
	        super.addObserver(observer);
	    }

	    // a helper to make it easier to initialize all observers
	    public void initObservers() {
	        setChanged();
	        notifyObservers();
	    }

	    @Override
	    public synchronized void deleteObserver(Observer observer) {
	        super.deleteObserver(observer);
	        setChanged();
	        notifyObservers();
	    }

	    @Override
	    public synchronized void deleteObservers() {
	        super.deleteObservers();
	        setChanged();
	        notifyObservers();
	    }
	}
