/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery
 */
package com.example.a4;
import java.util.ArrayList;
import java.util.Random;

import com.example.a4complete.R;

import android.graphics.*;
import android.graphics.Paint.Style;
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
    
    public Path splitPath;
    private Region testShape;
    
    private float flyX = 0;
    private float flyY = 0;
    
	Random rand = new Random(System.currentTimeMillis());

    private int direction;
    private int x_direction;
    private int max_y;
    private int max_x;
    private float multiplier_x;
    private float multiplier_y;
    public boolean isActive;
    public boolean sliced;

    private Path flyPath;
    private Region clipRegion;
    
    private float x_location; //Used for now

    /**
     * A fruit is represented as Path, typically populated 
     * by a series of points 
     */
    Fruit(float[] points) {
        init();
        this.path.reset();
        this.path.moveTo(points[0], points[1]);
        this.path.quadTo(points[0], points[1], points[2], points[3]);

        for (int i = 4; i < points.length; i += 2) {
            //this.path.lineTo(points[i], points[i + 1]);
        	this.path.cubicTo(points[0], points[1], points[2], points[3], points[i], points[i + 1]);
        	//this.path.cubicTo(points[i-4], points[i-3], points[i-2], points[i-1], points[i], points[i + 1]);
        	//this.path.quadTo(points[0], points[1], points[i], points[i + 1]);
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
        this.x_direction = this.getRandomNumber(2,  0);
        //if(x_direction == 0) {
            this.current = new PointF(getRandomNumber(300, 20), 500);
        //}
        //else {
        //    this.current = new PointF(getRandomNumber(400, 150), 500);
        //}
        this.direction = 1;
        this.max_y = getRandomNumber(120, 60);
        this.max_x = getRandomNumber(400, 20);
        this.isActive = true;
        this.sliced = false;
        this.flyPath = new Path();
                
        this.multiplier_x = getRandomFloat((float)0.09, (float)0.05);
        this.multiplier_y = getRandomFloat((float)0.07, (float)0.05);
        
        this.translate(current.x, current.y);
        this.x_location = current.x;
        this.clipRegion = new Region(0, 0, MainActivity.displaySize.x, MainActivity.displaySize.y);
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
    	//Log.d("Fruit", "Display size = " + MainActivity.displaySize.y  + ", current.y = " + current.y);
    	
    	if(direction == -1 && current.y > MainActivity.displaySize.y ) {
    		isActive = false;
    	}
    	
    	if(!this.isSliced()) {
    		performGravity();
    		drawRoutine(canvas, this.getTransformedPath());
    	}
    	else {
    		this.translate(flyX, flyY);
    		flyY += 1.5;
    		current.y += flyY;
    		current.x += flyX;
    		drawRoutine(canvas, this.getTransformedPath());
    		
    	}
    	
    	if(splitPath != null) {

        	canvas.drawPath((splitPath), this.paint);
    		//drawRoutine(canvas, splitPath);
    	}
    	if(testShape != null) {
    		canvas.drawPath(testShape.getBoundaryPath(), paint);
    	}
    }
    void drawRoutine(Canvas canvas, Path pathToBeDrawn) {
    	Paint tempPaint = new Paint(this.paint);
    	
    	this.paint.setStyle(Style.STROKE);
    	this.paint.setColor(Color.BLACK);
    	canvas.drawPath(pathToBeDrawn, this.paint);
    	this.paint.setStyle(Style.FILL);
    	this.paint.setColor(tempPaint.getColor());
    	canvas.drawPath(pathToBeDrawn, this.paint);
    }
    
    void performGravity() {
    	if(direction == 1) {
    		
        	current.y = current.y - current.y*multiplier_y;
        	
			current.x = current.x + (max_x - x_location)*multiplier_x;
        	translate((max_x - x_location)*multiplier_x, - current.y*multiplier_y);
        	x_location += (max_x - x_location)*multiplier_x;
        	
    	}
    	else {
    		
    		float adder = Math.abs((x_location - max_x )*multiplier_x);

        	current.y = current.y + current.y*multiplier_y;
    		current.x = current.x + adder;
        	translate(0, current.y*multiplier_y);
        	x_location += adder;
        	
    	}
    	
    }

    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit.
     */
    
    public boolean intersects(PointF p1, PointF p2) {
    	Region newRegion = new Region();
    	newRegion.setPath(this.getTransformedPath(), this.clipRegion);
    	
    	Path linePath = new Path();
    	linePath.moveTo(p1.x,  p1.y);
    	linePath.lineTo(p1.x + 2,  p1.y + 2);
    	linePath.lineTo(p2.x,  p2.y);
    	linePath.lineTo(p2.x + 2,  p2.y + 2);
    	
    	Region lineRegion = new Region();
    	
    	lineRegion.setPath(linePath,  clipRegion);
    	
    	splitPath = linePath;
    	
    	if (contains(p1) || contains(p2)) {
    		return false;
    	}
    	if (newRegion.op(lineRegion ,Region.Op.INTERSECT)) {
    		sliced = true;
    		return true;
    		
    	}
    	
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
     * Returns whether the given point is within the Fruit's shape.
     */
    public boolean contains(PointF p1) {
        Region region = new Region();
        boolean valid = region.setPath(getTransformedPath(), this.clipRegion);
        return valid && region.contains((int) p1.x, (int) p1.y);
    }

    /**
     * This method assumes that the line represented by the two points
     * intersects the fruit. If not, unpredictable results will occur.
     * Returns two new Fruits, split by the line represented by the
     * two points given.
     */
    public Fruit[] oldsplit(PointF p1, PointF p2) {
    	Path topPath = null;
    	Path bottomPath = null;
    	Region topRegion = new Region();
    	Region bottomRegion = new Region();
    	
    	if(!this.isSliced()) {
    		//this.setFillColor(Color.BLACK);
        	return new Fruit[0];
        }
    	
        this.isActive = false;
        this.sliced = true;
        
        double angle = Graphics2D.findAngle(p1, p2); 
        Matrix at = new Matrix();

        //at.postTranslate(p1.x, p1.y);

        // at.postTranslate(-p1.x,  -p1.y);
        at.postRotate((float)(angle), -p1.x,  -p1.y);
        //at.postTranslate(p1.x, p1.y);
        
        Region myCurrentShape = new Region();
        Path tempPath = this.getTransformedPath();
        tempPath.transform(at);
        myCurrentShape.setPath(tempPath, this.clipRegion);
        
        
        Region rect1 = new Region(new Rect(0, -1000, 1000, 1000));
        Region rect2 = new Region(new Rect(0, 0, 1000, 1000));
        
        topRegion = new Region(myCurrentShape);
        bottomRegion = new Region(myCurrentShape);

        topRegion.op(topRegion, rect1, Region.Op.INTERSECT);
        bottomRegion.op(bottomRegion, rect2, Region.Op.INTERSECT);

        at.invert(at);
        
       	topPath = topRegion.getBoundaryPath();
        bottomPath = bottomRegion.getBoundaryPath();
        topPath.transform(at);
        bottomPath.transform(at);

        Graphics2D.printMatrix(this.transform);
        Graphics2D.printMatrix(at);
        

		Fruit[] fruits = new Fruit[] {new Fruit(this.path), new Fruit(this.path)};
		
        if (topPath != null && bottomPath != null) {
        	fruits = new Fruit[] { new Fruit(topPath), new Fruit(bottomPath) };
        }
		// Fruit[] fruits = new Fruit[] {new Fruit(topPath), new Fruit(bottomPath)};
		
		fruits[0].sliced = true;
		fruits[0].current = this.current;
		fruits[0].direction = -1;
		fruits[0].multiplier_y = (float)0.01;
		fruits[0].multiplier_x = 0;
		fruits[0].transform = transform;
		fruits[0].x_location = this.x_location;
		fruits[0].paint = this.paint;
		fruits[0].splitPath = this.splitPath;
    	//fruits[0].translate(-10, 0);

		
		fruits[1].sliced = true;
		fruits[1].current = this.current;
		fruits[1].current.x = this.current.x;
		fruits[1].direction = -1;
		fruits[1].multiplier_y = (float)0.01;
		fruits[1].multiplier_x = 0;
		fruits[1].transform = transform;
		fruits[1].x_location = this.x_location;
		fruits[1].paint = this.paint;
		fruits[1].splitPath = this.splitPath;
    	//fruits[0].translate(+10, 0);
		
		return fruits ;
    	
    }
    public Path[] returnPathForLine(PointF p1, PointF p2) {
    	Path path1 = new Path();
    	Path path2 = new Path();
    	
    	path1.moveTo(p1.x,  p1.y - 1);
    	path1.lineTo(p2.x,  p2.y - 1);
    	path1.lineTo(p2.x + 1000,  p2.y - 1000);
    	path1.lineTo(p1.x - 1000,  p1.y - 1000);

    	path2.moveTo(p1.x,  p1.y + 1);
    	path2.lineTo(p2.x,  p2.y + 1);
    	path2.lineTo(p2.x + 1000,  p2.y + 1000);
    	path2.lineTo(p1.x - 1000,  p1.y + 1000);
    	
    	return new Path[] {path1, path2};
    }
    public Fruit[] split(PointF p1, PointF p2) {
    	Path topPath = new Path();
    	Path bottomPath = new Path();
    	Region topRegion = new Region();
    	Region bottomRegion = new Region();
    	
    	
    	if(!this.isSliced()) {
        	return new Fruit[0];
        }
    	
        
        if(p1.x < p2.x) {
	        Path[] paths = returnPathForLine(p1, p2);
	        topPath = paths[0];
	        bottomPath = paths[1];
        }
        else {
        	Path[] paths = returnPathForLine(p2, p1);
	        topPath = paths[0];
	        bottomPath = paths[1];
        }
        topRegion.setPath(topPath,  this.clipRegion);
        bottomRegion.setPath(bottomPath,  this.clipRegion);
        
        
        Region myregion = new Region();
        myregion.setPath(this.getTransformedPath(),  this.clipRegion);
        
        topRegion.op(topRegion, myregion, Region.Op.INTERSECT);
        bottomRegion.op(bottomRegion, myregion, Region.Op.INTERSECT);
        
        topPath = topRegion.getBoundaryPath();
        bottomPath = bottomRegion.getBoundaryPath();
        
        Matrix at = new Matrix(this.transform);
        at.invert(at);
        topPath.transform(at);
        bottomPath.transform(at);

        this.isActive = false;
        this.sliced = true;
        
		Fruit[] fruits = new Fruit[] {new Fruit(this.path), new Fruit(this.path)};
		
        if (topPath != null && bottomPath != null) {
        	fruits = new Fruit[] { new Fruit(topPath), new Fruit(bottomPath) };
        }
		Matrix newMatrix = new Matrix();
		newMatrix.postTranslate(current.x, current.y);
		
		fruits[0].sliced = true;
		fruits[0].current = this.current;
		fruits[0].direction = -1;
		fruits[0].multiplier_y = (float)0.08;
		fruits[0].multiplier_x = 0;
		fruits[0].x_location = this.x_location;
		fruits[0].paint = this.paint;
		fruits[0].splitPath = this.splitPath;
		fruits[0].transform = new Matrix();
		fruits[0].transform = new Matrix(newMatrix);
		fruits[0].flyX = (float)-0.5;
		fruits[0].flyY = (float)0;
		
		fruits[1].sliced = true;
		fruits[1].current = this.current;
		fruits[1].current.x = this.current.x;
		fruits[1].direction = -1;
		fruits[1].multiplier_y = (float)0.08;
		fruits[1].multiplier_x = 0;
		fruits[1].x_location = this.x_location;
		fruits[1].paint = this.paint;
		fruits[1].splitPath = this.splitPath;
		fruits[1].transform = new Matrix();
		fruits[1].transform = new Matrix(newMatrix);
		fruits[1].flyX = (float)0.5;
		fruits[1].flyY = (float)0;
		
		return fruits ;
    	
    }
    
    public boolean isSliced() {
    	
    	return sliced;
    }
    public boolean isActive() {
    	return isActive;
    }
    
    private int getRandomColor() {
    	return Color.argb(255, rand.nextInt(150), rand.nextInt(256), rand.nextInt(50)); 
    }
    
    private float getRandomFloat(float maxX, float minX) {
    	return rand.nextFloat() * (maxX - minX) + minX;
    }
}
