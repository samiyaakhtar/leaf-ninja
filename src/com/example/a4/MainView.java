/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery & Michael Terry
 */
package com.example.a4;

import android.app.Activity;
import android.content.Context;
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

    public Timer gameTimer = new Timer("gameTimer");
    public Timer fruitTimer = new Timer("fruitTimer") ;
    
    
    // Constructor
    MainView(Context context, Model m) {
        super(context);
        main_activity = (Activity) context;

        // register this view with the model
        model = m;
        model.addObserver(this);

        // TODO BEGIN CS349
        // test fruit, take this out before handing in! 
        /*
        Fruit f1 = new Fruit(new float[] {0, 20, 20, 0, 40, 0, 60, 20, 60, 40, 40, 60, 20, 60, 0, 40});
        f1.translate(100, 100);
        model.add(f1);

        Fruit f2 = new Fruit(new float[] {0, 20, 20, 0, 40, 0, 60, 20, 60, 40, 40, 60, 20, 60, 0, 40});
        f2.translate(200, 200);
        model.add(f2); */
        
        startTimers();
        // TODO END CS349

        // add controller
        // capture touch movement, and determine if we intersect a shape
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Log.d(getResources().getString(R.string.app_name), "Touch down");
                        drag.start(event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_UP:
                        // Log.d(getResources().getString(R.string.app_name), "Touch release");
                        drag.stop(event.getX(), event.getY());

                        // find intersected shapes
                        Iterator<Fruit> i = model.getShapes().iterator();
                        while(i.hasNext()) {
                            Fruit s = i.next();
                            if (s.intersects(drag.getStart(), drag.getEnd())) {
                            	s.setFillColor(Color.RED);
                                try {
                                    Fruit[] newFruits = s.split(drag.getStart(), drag.getEnd());

                                    // TODO BEGIN CS349
                                    // you may want to place the fruit more carefully than this
                                    newFruits[0].translate(0, -10);
                                    newFruits[1].translate(0, +10);
                                    // TODO END CS349
                                    model.add(newFruits[0]);
                                    model.add(newFruits[1]);

                                    // TODO BEGIN CS349
                                    // delete original fruit from model
                                    // TODO END CS349

                                } catch (Exception ex) {
                                    Log.e("fruit_ninja", "Error: " + ex.getMessage());
                                }
                            } else {
                                //s.setFillColor(Color.BLUE);
                            }
                            invalidate();
                        }
                        break;
                }
                return true;
            }
        });
    }
    
    void startTimers() {
    	gameTimer.schedule(new TimerTask() { 
    		public void run() {
    			main_activity.runOnUiThread(new Runnable() {
    				@Override
    				public void run() {
    					drawGame();
    				}});
    		}
    	}, 0, 25);
    	

    	fruitTimer.schedule(new TimerTask() { 
    		@Override
    		public void run() {
    			//addFruits();
    			main_activity.runOnUiThread(new Runnable() {
    				@Override
    				public void run() {
    	   			   //Fruit f1 = new Fruit(new float[] {0, 20, 20, 0, 40, 0, 60, 20, 60, 40, 40, 60, 20, 60, 0, 40});
    	   			   Fruit f1 = new Fruit(new RectF(0, 0, 100, 100));
    			       //f1.translate(0, 500);
    			       //f1.current.x = 0;
    			       //f1.current.y = 500;
    			        model.add(f1);
    				}});
    		}
    	}, 0, 1000); 
    	
    }
    
    void stopTimers() {
    	    	
    	gameTimer.cancel();
    	fruitTimer.cancel();
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

    @Override
    public void update(Observable observable, Object data) {
    	
        invalidate();
    }
}
