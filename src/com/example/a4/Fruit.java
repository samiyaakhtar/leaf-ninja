/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery
 */
package com.example.a4;
import java.util.Random;
import android.graphics.*;
import android.util.Log;

/**
 * Class that represents a Fruit. Can be split into two separate fruits.
 */
public class Fruit {
    private Path path = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix transform = new Matrix();
    public PointF current;
    public Color color;
    
    private RectF fruitBounds;
    private int flyX = 0;
    
	Random rand = new Random(System.currentTimeMillis());

    private int direction;
    private int max_y;
    private int max_x;
    private float multiplier_x;
    private float multiplier_y;
    private boolean isActive;
    private boolean sliced;
    private Path splitLine;
    private Path flyPath;
    


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
    }

    Fruit(Region region) {
        init();
        this.path = region.getBoundaryPath();
    }

    Fruit(Path path) {
        init();
        this.path = path;
        this.path.computeBounds(this.fruitBounds, true);
    }
    Fruit(RectF bounds) {
    	init();
    	this.fruitBounds = bounds;
    	this.path.addArc(this.fruitBounds, 0, 360);
    }

    private void init() {
        this.paint.setColor(getRandomColor());
        this.paint.setStrokeWidth(5);
        this.current = new PointF(getRandomNumber(120, 100), 500);
        this.direction = 1;
        this.max_y = getRandomNumber(120, 80);
        this.max_x = getRandomNumber(80, 50);
        this.isActive = true;
        this.sliced = false;
        this.splitLine = null;
        this.flyPath = new Path();
        
        this.multiplier_x = getRandomFloat((float)0.05, (float)0.02);
        this.multiplier_y = getRandomFloat((float)0.09, (float)0.05);
        
        this.translate(current.x, current.y);
        
        Log.d("MainActivity", "max_x = " + max_x + ", max_y = " + max_y + ", multiplier_x = " + multiplier_x + ", multiplier_y = " + multiplier_y);
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

    public int getRandomNumber(int maximum, int minimum) {
	  	int randomNum = minimum + (int)(Math.random()*(maximum - minimum));
	      return randomNum;
	  }
    /**
     * Paints the Fruit to the screen using its current affine
     * transform and paint settings (fill, outline)
     */
    public void draw(Canvas canvas) {
        // TODO BEGIN CS349
        // tell the shape to draw itself using the matrix and paint parameters
        // TODO END CS349
    	performGravity();
    	if(current.y < max_y) {
    		direction = -1;
    	}
    	if(direction == -1 && current.y > 900) {
    		isActive = false;
    	}
    	canvas.drawCircle(current.x, current.y, 2, paint);
    	canvas.drawPath(this.getTransformedPath(), this.paint);
    }
    
    void performGravity() {
    	if(current.x > max_x) {
    		
    	}
    	if(current.y > max_y) {
    		
    	}

    	if(direction == 1) {
    		
    		current.x = current.x + (current.x)*multiplier_x;
        	current.y = current.y - current.y*multiplier_y;
        	translate((current.x)*multiplier_x, - current.y*multiplier_y);
    	}
    	else {
    		current.x = current.x + (current.x)*multiplier_x;
        	current.y = current.y + current.y*multiplier_y;
        	translate((current.x)*multiplier_x, current.y*multiplier_y);
    	}
    	Log.d("MainActivity", "Current.x = " + current.x + ", current.y = " + current.y);
    }

    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit.
     */
    /*
    public boolean intersects(PointF p1, PointF p2) {
        // TODO BEGIN CS349
        // calculate angle between points
        // rotate and flatten points passed in 
        // rotate path and create region for comparison
        // TODO END CS349
        return false;
    } */
    /*
    public boolean intersection(PointF p1, PointF p2) {
        
    	// create a line
    	Path line = new Path(); 
    	line.lineTo(p1.x, p1.y);
    	line.lineTo(p2.x, p2.y);
    	
    	// if start/end point falls within the fruit then it doesn't intersect 
    	if (this.contains(p1) || this.contains(p2)) {
			return false;
		}
    	
    	if(line.op(getTransformedPath(), Path.Op.INTERSECT)) {
    		return true;
    	}
    	return false;
    } */
    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit. 
     */
    public boolean intersects(PointF p1, PointF p2) {
    	 // TODO BEGIN CS349
        // TODO END CS349
    	int pointx1 = (int)current.x;
    	int pointx2 = (int)(current.x + fruitBounds.width());
    	int pointy1 = (int)current.y;
    	int pointy2 = (int)(current.y + fruitBounds.height());

    	boolean intersected = false;
    	/*
    	 * Checking to see if the projection of the line from the center lies inside the fruit boundaries. If yes, then 
    	 * checking to see if the line goes through the entire fruit 
    	 */
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

	    	return intersected;
		}

		return false;

    	
    }
    /*
     * Returns the center of the fruit (approximate)
     */
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
    /*
     * Returns boolean based on whether the point lies inside the circle or not
     * 
     */
    private boolean CheckIfPointLiesInside(PointF p) {
    	if(p.x >= current.x 
    			&& p.y >= current.y 
    			&& p.x <= current.x + fruitBounds.width() 
    			&& p.y <= current.y + fruitBounds.height()) {
    		return true;
    	}
    	return false;
    }
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
    	// TODO BEGIN CS349
        // calculate angle between points
        // rotate and flatten points passed in
        // rotate region
        // define region masks and use to split region into top and bottom
        // TODO END CS349
        if (topPath != null && bottomPath != null) {
           return new Fruit[] { new Fruit(topPath), new Fruit(bottomPath) };
        }
        return new Fruit[0];
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
