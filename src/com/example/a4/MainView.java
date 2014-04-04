/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery & Michael Terry
 */
package com.example.a4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TimerTask;
import java.util.Timer;


/*
 * View of the main game area.
 * Displays pieces of fruit, and allows players to slice them.
 */
public class MainView extends View implements Observer {
    private final Model model;
    private final MouseDrag drag = new MouseDrag();
    private Activity main_activity;
    private AlertDialog.Builder alertDialogBuilder;

    public Timer gameTimer = new Timer("gameTimer");
    public Timer fruitTimer = new Timer("fruitTimer") ;
    public boolean gameRunning = false;
    public boolean dialogCreated = false;
    
    
    // Constructor
    MainView(Context context, Model m) {
        super(context);
        main_activity = (Activity) context;
        alertDialogBuilder = new AlertDialog.Builder( main_activity);
        
        // register this view with the model
        model = m;
        model.addObserver(this);

        startTimers();
		model.startGame();
		
        // add controller
        // capture touch movement, and determine if we intersect a shape
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drag.start(event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_UP:
                        drag.stop(event.getX(), event.getY());

                        // find intersected shapes
                        Iterator<Fruit> i = model.getShapes().iterator();
                        while(i.hasNext()) {
                            Fruit s = i.next();
                            if (s != null && s.isActive() && !s.isSliced() && s.intersects(drag.getStart(), drag.getEnd())) {
                            	model.incrementSlashed();
                            	
                                try {
                                    Fruit[] newFruits = s.split(drag.getStart(), drag.getEnd());

                                    if(newFruits != null && newFruits.length == 2) {
	                                    model.add(newFruits[0]);
	                                    model.add(newFruits[1]);
                                    }
                                    else {
                                    	//Log.e("fruit_ninja", "Split returned nothing");
                                    }

                                } catch (Exception ex) {
                                    //Log.e("fruit_ninja", "Error: " + ex.getMessage());
                                }
                                
                            } 
                            invalidate();
                        }
                        break;
                }
                return true;
            }
        });
    }
    public void checkModelState() {
    	if(model.isRunning() && !gameRunning) {
    		startTimers();
    	}
    	else if(!model.isRunning() && gameRunning) {
    		stopTimers();
    		model.stopGame();
    	}
    }
    void startTimers() {
    	// For actual game, keep timer speed 25 and 700
    	// For AVD, keep timer speed 40 and 1500
    	gameTimer.schedule(new TimerTask() { 
    		public void run() {
    			main_activity.runOnUiThread(new Runnable() {
    				@Override
    				public void run() {
    					drawGame();
    				}});
    		}
    	}, 0, 40);
    	
    	gameRunning = true;
    	
    	
    	fruitTimer.schedule(new TimerTask() { 
    		@Override
    		
    		public void run() {
    			//addFruits();
    			main_activity.runOnUiThread(new Runnable() {
    				@Override
    				public void run() {
    					int leafSize = model.getRandomNumber(40,  20);
    					
    					Fruit f1 = new Fruit(new float[] {
    							  0, leafSize, 
								  leafSize, 0, 
								  2*leafSize, 0, 
								  3*leafSize, leafSize, 
								  3*leafSize, 2*leafSize, 
								  3*leafSize, 3*leafSize, 
								  2*leafSize, 3*leafSize, 
								  leafSize, 3*leafSize, 
								  0, 2*leafSize,
								  0, leafSize});
    					
    			        model.add(f1);
    			        model.incrementTotal();
    				}});
    		}
    	}, 0, 1500); 
    	
    }
    
    void stopTimers() {
    	    	
    	gameTimer.cancel();
    	fruitTimer.cancel();
    	gameRunning = false;
    }
    void drawGame() {
    	update(this.model, this);
    	
    }
    // inner class to track mouse drag
    // a better solution *might* be to dynamically track touch movement
    // in the controller above
    class MouseDrag {
        private float startx, starty;
        private float endx, endy;

        protected PointF getStart() { return new PointF(startx, starty); }
        protected PointF getEnd() { return new PointF(endx, endy); }

        protected void start(float x, float y) {
            this.startx = x;
            this.starty = y;
        }

        protected void stop(float x, float y) {
            this.endx = x;
            this.endy = y;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(model.isRunning()) {
        	Iterator<Fruit> iter = model.getActualShapes().iterator();
        	
        	while(iter.hasNext() ) {
            	Fruit current = iter.next();
            	if(!current.isSliced() && !current.isActive()){	
            		model.incrementDropped();
            		iter.remove();
            	}
            	else if(current.isSliced() && !current.isActive()) {
            		iter.remove();
            	}
            	else if(current.isActive()) {
            		current.draw(canvas);
            	}
            }
        }
        else {
        	if(! dialogCreated) {
        		showEndGameDialog();
        	}
        	
        }
        
    }

    public void showEndGameDialog() {
    		String scoreCard = "";
    		scoreCard += "Scored: " + model.getSlashed() + "\n";
    		scoreCard += "Dropped: " + model.getDropped() + "\n";
    		scoreCard += "Lives remaining: " + model.getNumLivesRemaining();
			alertDialogBuilder.setTitle("Game Over!");
 
			// set dialog message
			alertDialogBuilder
				.setMessage(scoreCard)
				.setCancelable(false)
				.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.dismiss();
						dialogCreated = false;
						main_activity.finish();
						
					}
				  });

			dialogCreated = true;
			alertDialogBuilder.show();
    }
    @Override
    public void update(Observable observable, Object data) {
    	checkModelState();
        invalidate();
    }
}
