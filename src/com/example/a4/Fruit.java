/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery
 */
package com.example.a4;
import java.util.Random;

import com.example.a4complete.R;

import android.graphics.*;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Display;

/**
 * Class that represents a Fruit. Can be split into two separate fruits.
 */
public class Fruit {
    private Path path = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix transform = new Matrix();
    public PointF current;
    public Color color;
    
    private int flyX = 0;
    
	Random rand = new Random(System.currentTimeMillis());

    private int direction;
    private int max_y;
    private int max_x;
    private float multiplier_x;
    private float multiplier_y;
    public boolean isActive;
    public boolean sliced;
    private PointF splitPoint1;
    private PointF splitPoint2;
    private Path splitPath;
    private Path flyPath;
    
    private float x_location; //Used for now
    private float x_start;

    /**
     * A fruit is represented as Path, typically populated 
     * by a series of points 
     */
    Fruit(float[] points) {
        init();
        this.path.reset();
        this.path.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) {
            this.path.lineTo(points[i], points[i + 1]);
        }
        this.path.moveTo(points[0], points[1]);
        //this.path.computeBounds(this.fruitBounds, true);
    }

    Fruit(Region region) {
        init();
        this.path = region.getBoundaryPath();
    }

    Fruit(Path path) {
        init();
        this.path = path;
        //this.path.computeBounds(this.fruitBounds, true);
    }
   

    private void init() {
        this.paint.setColor(getRandomColor());
        this.paint.setStrokeWidth(5);
        this.current = new PointF(getRandomNumber(120, 20), 500);
        this.direction = 1;
        this.max_y = getRandomNumber(120, 60);
        this.max_x = getRandomNumber(200, 100);
        this.isActive = true;
        this.sliced = false;
        this.flyPath = new Path();
        
        this.multiplier_x = getRandomFloat((float)0.09, (float)0.05);
        this.multiplier_y = getRandomFloat((float)0.07, (float)0.05);
        
        this.translate(current.x, current.y);
        this.x_location = current.x;
        this.x_start = current.x;
        
        // Log.d("MainActivity", "max_x = " + max_x + ", max_y = " + max_y + ", multiplier_x = " + multiplier_x + ", multiplier_y = " + multiplier_y);
    }
    

    /**
     * The color used to paint the interior of the Fruit.
     */
    public int getFillColor() { return paint.getColor(); }
    public void setFillColor(int color) { paint.setColor(color); }

    /**
     * The width of the outline stroke used when painting.
     */
    public double getOutlineWidth() { return paint.getStrokeWidth(); }
    public void setOutlineWidth(float newWidth) { paint.setStrokeWidth(newWidth); }

    /**
     * Concatenates transforms to the Fruit's affine transform
     */
    public void rotate(float theta) { transform.postRotate(theta); }
    public void scale(float x, float y) { transform.postScale(x, y); }
    public void translate(float tx, float ty) { transform.postTranslate(tx, ty); }

    /**
     * Returns the Fruit's affine transform that is used when painting
     */
    public Matrix getTransform() { return transform; }

    /**
     * The path used to describe the fruit shape.
     */
    public Path getTransformedPath() {
        Path originalPath = new Path(path);
        Path transformedPath = new Path();
        originalPath.transform(transform, transformedPath);
        return transformedPath;
    }
    public Path getTransformedPath(Path originalPath) {
    	
        Path transformedPath = new Path();
        originalPath.transform(transform, transformedPath);
        return transformedPath;
    }

    public int getRandomNumber(int maximum, int minimum) {
	  	int randomNum = minimum + (int)(Math.random()*(maximum - minimum));
	      return randomNum;
	  }
    /**
     * Paints the Fruit to the screen using its current affine
     * transform and paint settings (fill, outline)
     */
    public void draw(Canvas canvas) {
        
    	if(!isSliced() && current.y < max_y && (Math.abs(x_location - max_x) < 19)) {
    		direction = -1;
    		x_location = Math.abs(x_location - max_x) + max_x;
    	}
    	performGravity();
    	
    	if(direction == -1 && current.y > 900) {
    		isActive = false;
    	}
    	
    	if(this.isSliced() && this.splitPoint1 != null && splitPoint2 != null && this.splitPath != null) {
    		// Log.d("draw", "I'm drawing the line Samiya");
    		//canvas.drawPath(this.getTransformedPath(this.splitPath), paint);
    		Log.d("draw", "Supposed to draw plit path");
    		canvas.drawPath(this.splitPath, paint);
    		//canvas.drawLine(splitPoint1.x, splitPoint1.y, splitPoint2.x, splitPoint2.y, this.paint);
    	}
    	canvas.drawPath(this.getTransformedPath(), this.paint);
    }
    
    void performGravity() {
    	if(direction == 1) {
    		current.x = current.x + (max_x - x_location)*multiplier_x;
        	current.y = current.y - current.y*multiplier_y;
        	translate((max_x - x_location)*multiplier_x, - current.y*multiplier_y);
        	x_location += (max_x - x_location)*multiplier_x;
    	}
    	else {
    		float adder = Math.abs((x_location - max_x )*multiplier_x);
    		current.x = current.x + adder;
        	current.y = current.y + current.y*multiplier_y;
        	translate(adder, current.y*multiplier_y);
        	x_location += adder;
        	
    	}
    	
    }

    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit.
     */
    
    public boolean intersects(PointF p1, PointF p2) {
        // TODO BEGIN CS349
        // calculate angle between points
        // rotate and flatten points passed in 
        // rotate path and create region for comparison
        // TODO END CS349
    	// calculate angle between points
    	// rotate the points so that they're horizontal
    	// rotate the region by the same angle
    	double angle = Angle(p1, p2);
    	
    	
        return false;
    } 
    
    /*
     * Calculates and returns the angle between the x axis and the line formed by 
     * two points passed as parameter to this function
     */
    private double Angle(PointF p1, PointF p2)
    {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double angle = Math.atan2(dy, dx); 
        
        return angle;
    }
    
    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit. 
     */
    public boolean myintersects(PointF p1, PointF p2) {
    	 // TODO BEGIN CS349
        // TODO END CS349
    	/*
    	int pointx1 = (int)current.x;
    	int pointx2 = (int)(current.x + fruitBounds.width());
    	int pointy1 = (int)current.y;
    	int pointy2 = (int)(current.y + fruitBounds.height());

    	boolean intersected = false;
    	
		PointF intersection_point = ProjectPointOnLine(p1, p2, getCenterOfFruit());
		intersected = CheckIfPointLiesInside(intersection_point);

		
		if(intersected) {
			
				if(p1.y < pointy1 && p2.y > pointy2) {
		    		sliced = true;
		    		intersected = true;
		    	}
		    	else if(p2.y < pointy1 && p1.y > pointy2) {
		    		sliced = true;
		    		intersected = true;
		    	}
		    	else if(p1.x < pointx1 && p2.x > pointx2) {
		    		sliced = true;
		    		intersected = true;
		    	}
		    	else if(p2.x < pointx1 && p1.x > pointx2) {
		    		sliced = true;
		    		intersected = true;
		    	}

				return sliced;
	    	
		}

		return false;
*/
    	return false;
    }
    /*
     * Returns the center of the fruit (approximate)
     */
    /*
    private PointF getCenterOfFruit() {
    	int diameter = 0;
    	if(fruitBounds.width() >= fruitBounds.height()) {
    		diameter = (int)fruitBounds.width();
    	}
    	else {
    		diameter = (int)fruitBounds.height();
    	}
    	PointF center = new PointF(current.x + diameter/2, current.y + diameter/2);
    	return center;
    }
    */
    /*
     * Returns boolean based on whether the point lies inside the circle or not
     * 
     */
    /*
    private boolean CheckIfPointLiesInside(PointF p) {
    	if(p.x >= current.x 
    			&& p.y >= current.y 
    			&& p.x <= current.x + fruitBounds.width() 
    			&& p.y <= current.y + fruitBounds.height()) {
    		return true;
    	}
    	return false;
    }
    */
    /*
     * Function to project point on a line 
     * http://www.vcskicks.com/code-snippet/point-projection.php
     */
    private PointF ProjectPointOnLine(PointF line1, PointF line2, PointF toProject)
    {
        double m = (double)(line2.y - line1.y) / (line2.x - line1.x);
        double b = (double)line1.y - (m * line1.x);

        double x = (m * toProject.y + toProject.x - m * b) / (m * m + 1);
        double y = (m * m * toProject.y + m * toProject.x + b) / (m * m + 1);

        return new PointF((int)x, (int)y);
    }
    
    
    
    /**
     * Returns whether the given point is within the Fruit's shape.
     */
    public boolean contains(PointF p1) {
        Region region = new Region();
        boolean valid = region.setPath(getTransformedPath(), new Region());
        return valid && region.contains((int) p1.x, (int) p1.y);
    }

    /**
     * This method assumes that the line represented by the two points
     * intersects the fruit. If not, unpredictable results will occur.
     * Returns two new Fruits, split by the line represented by the
     * two points given.
     */
    public Fruit[] split(PointF p1, PointF p2) {
    	Path topPath = null;
    	Path bottomPath = null;
    	
    	if(!this.isSliced()) {
    		//this.setFillColor(Color.BLACK);
        	return new Fruit[0];
        }
    	
    	this.splitPoint1 = p1;
    	this.splitPoint2 = p2;
    	this.splitPath = new Path();
    	this.splitPath.moveTo(p1.x,  p1.y);
    	this.splitPath.lineTo(p2.x,  p2.y);
    	//this.splitPath.moveTo(p1.x,  p1.y);
        this.isActive = false;
        this.sliced = true;
        
        Log.d("split", "Plit path = " + splitPath.toString());
        
        /*
        double angle = Angle(p1, p2); 
        Matrix at = new Matrix();
        
        at.postRotate((float)-angle);
        at.postTranslate(-p1.x, -p1.y);
        
        Path myCurrentShape = this.getTransformedPath();

        Path newPath = this.getTransformedPath();
        this.getTransformedPath().transform(at, newPath);
        RectF bigRect = new RectF();
        newPath.computeBounds(bigRect, true);
        //myCurrentShape.computeBounds(bigRect, true);
        //RectF rect1 = new RectF(bigRect.left, -1000, 1000, 1000);
        //RectF rect2 = new RectF(bigRect.left, 0, 1000, 1000);
        RectF rect1 = new RectF(bigRect.left, -1000, 1000, 1000);
        RectF rect2 = new RectF(bigRect.left, 0, 1000, 1000);
        
        Path newPath1 = new Path();
        newPath1.addRect(rect1, Path.Direction.CCW);
        Path newPath2 = new Path();
        newPath2.addRect(rect2, Path.Direction.CCW);

        topPath = new Path(myCurrentShape);
       	bottomPath = new Path(myCurrentShape);

       	topPath.op(topPath, newPath1, Path.Op.XOR);
       	bottomPath.op(bottomPath, newPath2, Path.Op.XOR);

        
        if(at.invert(at)) {
        	//topPath = 
        	topPath.transform(at);
        	bottomPath.transform(at);
        }
        
        
    	// TODO BEGIN CS349
        // calculate angle between points
        // rotate and flatten points passed in
        // rotate region
        // define region masks and use to split region into top and bottom
        // TODO END CS349
    	*/
        //if (topPath != null && bottomPath != null) {
           return new Fruit[] { new Fruit(topPath), new Fruit(bottomPath) };
        //}
		
        
		//Fruit[] fruits = new Fruit[] {new Fruit(this.fruitBounds), new Fruit(new RectF(this.fruitBounds))};
		// Fruit[] fruits = new Fruit[] {new Fruit(topPath), new Fruit(bottomPath)};
		/*
		fruits[0].sliced = true;
		fruits[0].current = this.current;
		fruits[0].direction = -1;
		fruits[0].multiplier_y = (float)0.01;
		fruits[0].multiplier_x = 0;
		fruits[0].transform = this.transform;
		fruits[0].x_location = this.x_location;
		fruits[0].x_start = this.x_start;
		fruits[0].paint = this.paint;
		fruits[0].splitPoint1 = this.splitPoint1;
		fruits[0].splitPoint2 = this.splitPoint2;
		fruits[0].splitPath = this.splitPath;
		
		fruits[1].sliced = true;
		fruits[1].current = this.current;
		fruits[1].current.x = this.current.x + 100;
		fruits[1].direction = -1;
		fruits[1].multiplier_y = (float)0.01;
		fruits[1].multiplier_x = 0;
		fruits[1].transform = transform;
		fruits[1].x_location = this.x_location + 100;
		fruits[1].x_start = this.x_start + 100;
		fruits[1].paint = this.paint;
		fruits[1].splitPoint1 = this.splitPoint1;
		fruits[1].splitPoint2 = this.splitPoint2;
		fruits[1].splitPath = this.splitPath;
		
		return fruits ;
    	*/
    }
    
    public boolean isSliced() {
    	
    	return sliced;
    }
    public boolean isActive() {
    	return isActive;
    }
    
    private int getRandomColor() {
    	return Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)); 
    }
    
    private float getRandomFloat(float maxX, float minX) {
    	return rand.nextFloat() * (maxX - minX) + minX;
    }
}
