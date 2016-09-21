import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import java.util.ArrayList;
import java.util.Random;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;

// Each MyPolygon has a color and a Polygon object
class MyPolygon {

	Polygon polygon;
	Color color;

	public MyPolygon(Polygon _p, Color _c) {
		polygon = _p;
		color = _c;
	}

	public Color getColor() {
		return color;
	}

	public Polygon getPolygon() {
		return polygon;
	}

}

//cross over. take points from each polygon and average rgb colors
//random sampling of pixels from target image


// Each GASolution has a list of MyPolygon objects
class GASolution {

	ArrayList<MyPolygon> shapes;

	// width and height are for the full resulting image
	int width, height;
  double fitness;

	public GASolution(int _width, int _height) {
		shapes = new ArrayList<MyPolygon>();
		width = _width;
		height = _height;
	}

	public void addPolygon(MyPolygon p) {
		shapes.add(p);
	}	

	public ArrayList<MyPolygon> getShapes() {
		return shapes;
	}

	public int size() {
		return shapes.size();
	}

  public void setFitness(double _fitness)
  {
    fitness = _fitness;
  }

  public double getFitness()
  {
    return fitness;
  }

	// Create a BufferedImage of this solution
	// Use this to compare an evolved solution with 
	// a BufferedImage of the target image
	//
	// This is almost surely NOT the fastest way to do this...
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (MyPolygon p : shapes) {
			Graphics g2 = image.getGraphics();			
			g2.setColor(p.getColor());
			Polygon poly = p.getPolygon();
			if (poly.npoints > 0) {
				g2.fillPolygon(poly);
			}
		}
		return image;
	}

	public String toString() {
		return "" + shapes;
	}
}


// A Canvas to draw the highest ranked solution each epoch
class GACanvas extends JComponent{

    int width, height;
    GASolution solution;

    public GACanvas(int WINDOW_WIDTH, int WINDOW_HEIGHT) {
    	width = WINDOW_WIDTH;
    	height = WINDOW_HEIGHT;
    }
 
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setImage(GASolution sol) {
  	    solution = sol;
    }

    public void paintComponent(Graphics g) {
		BufferedImage image = solution.getImage();
		g.drawImage(image, 0, 0, null);
    }
}


public class GA extends JComponent{
	
    GACanvas canvas;
    int width, height;
    BufferedImage realPicture;
    ArrayList<GASolution> population;
    int size = 50;
    // Adjust these parameters as necessary for your simulation
    double MUTATION_RATE = 0.01;
    double CROSSOVER_RATE = 0.5;
    int MAX_POLYGON_POINTS = 5;
    int MAX_POLYGONS = 10;

    public GA(GACanvas _canvas, BufferedImage _realPicture) 
    {
        canvas = _canvas;
        realPicture = _realPicture;
        width = realPicture.getWidth();
        height = realPicture.getHeight();
        population = new ArrayList<GASolution>();
        createPopulation();
        canvas.setImage(population.get(0));
        canvas.repaint();
    }

    //creates init population
    public void createPopulation()
   	{
   		int i = 0;
   		int[] randX = new int[MAX_POLYGON_POINTS];
   		int[] randY = new int[MAX_POLYGON_POINTS];
   		Polygon randPoly; 
   		MyPolygon randMyPoly;
   		Random rand = new Random();
   		float r;
   		float g;
   		float b;
   		GASolution randSol;
   	
   		while(i < size)
   		{
   			randSol = new GASolution(canvas.width, canvas.height);
   			for( int k = 0; k < MAX_POLYGONS; k++)
   			{
   				for( int j = 0; j < MAX_POLYGON_POINTS; j++)
   				{
					  randX[j] = rand.nextInt(canvas.width) + 1;
   					randY[j] = rand.nextInt(canvas.height) + 1;    				
   				}
   				r = rand.nextFloat();
				  g = rand.nextFloat();
				  b = rand.nextFloat();
         // System.out.println("r: " + r + " g: " + g + " b: " + b + " " + randX[1] + randY[1]);
   				randPoly = new Polygon( randX, randY, MAX_POLYGON_POINTS);
   				randMyPoly = new MyPolygon(randPoly, new Color( r, g, b));
   				randSol.addPolygon(randMyPoly);
   			}
   			population.add(randSol);
   			i++;
   		}
   	}
    //calculates individual fitness
    public double fitness(GASolution currSol)
    {
      double fitLevel = 0;
      BufferedImage img = currSol.getImage();
      Random _rand = new Random();

      for( int i = 0; i < 100; i++)
      {
        int rand1 = _rand.nextInt(canvas.width);
        int rand2 = _rand.nextInt(canvas.height);
        //System.out.println("rand1: " + rand1 + ", rand2: " + rand2 + " ,canvas width: " + canvas.width + " ,canvas height: " + canvas.height );
        Color imgCol = new Color(img.getRGB(rand1,rand2));
        Color realPictureCol = new Color(realPicture.getRGB(rand1,rand2));
        double redDifSqr = Math.pow(imgCol.getRed() - realPictureCol.getRed(),2);
        double greenDifSqr = Math.pow(imgCol.getGreen() - realPictureCol.getGreen(),2);
        double blueDifSqr = Math.pow(imgCol.getBlue() - realPictureCol.getBlue(),2);
        fitLevel += Math.sqrt(redDifSqr + greenDifSqr + blueDifSqr);
      }
      return fitLevel/100;
    }
    //generates fitness for entire population
    public double generatePopulationFitness()
    {
      double popFitnessTotal = 0;
      double popFitness = 0;

      for(int i = 0; i < size; i++)
      {
        popFitness = fitness(population.get(i));
        popFitnessTotal += popFitness;
        population.get(i).setFitness(popFitness);
      }
      return popFitnessTotal;
    }

    //picks rit parent for reproduction
    public GASolution pickFitParent()
    {
      double popFitLevel = generatePopulationFitness();
      double r = popFitLevel * Math.random();
      int i = -1;
      while( r > 0 )
      {
        i++;
        r -= population.get(i).getFitness();
      }
      return population.get(i);

    }

    //tried color Avg from: http://www.java2s.com/Code/Java/2D-Graphics-GUI/Blendtwocolors.htm
    //Still getting bad blend of colors
    public Color colorAvg( Color c1, Color c2 )
    {
        double totalAlpha = c1.getAlpha() + c2.getAlpha();
        double weight1 = c1.getAlpha() / totalAlpha;
        double weight2 = c2.getAlpha() / totalAlpha;

        double newRed = (weight1 * c1.getRed()) + (weight2 * c2.getRed());
        double newBlue = (weight1 * c1.getBlue()) + (weight2 * c2.getBlue());
        double newGreen = (weight1 * c1.getGreen()) + (weight2 * c2.getGreen());
        double a = Math.max(c1.getAlpha(), c2.getAlpha());
        Color childColor = new Color((int)newRed, (int)newGreen, (int)newBlue, (int)a);

        return childColor;
    }
    //takes half of one array and half of another and returns the mix
    public int[] crossArrays( int[] p1, int[] p2 )
    {
      int[] newP = new int[p1.length];
      int i = 0;

      while(i < p1.length/2)
      {
        newP[i] = (p1[i]);
        i++;
      }
      while(i < p1.length)
      {
        newP[i] = (p2[i]);
        i++;
      }
      return newP;
    }
    //averages two colors for each polygon and mixes points
    public GASolution crossover(GASolution p1, GASolution p2)
    {
      GASolution newGA = new GASolution(p1.width, p1.height);
      ArrayList<MyPolygon> p1Shapes = p1.getShapes();
      ArrayList<MyPolygon> p2Shapes = p2.getShapes();
      ArrayList<MyPolygon> newShapes = new ArrayList<MyPolygon>();
      for(int i = 0; i < MAX_POLYGONS; i++)
      {
        Polygon oldPolygon = p1Shapes.get(i).getPolygon();
        int[] newXPoints = crossArrays(p1Shapes.get(i).getPolygon().xpoints, p2Shapes.get(i).getPolygon().xpoints);
        int[] newYPoints = crossArrays(p1Shapes.get(i).getPolygon().ypoints, p2Shapes.get(i).getPolygon().ypoints);
        Polygon newPoly = new Polygon(newXPoints, newYPoints, oldPolygon.npoints);
        Color newColor = colorAvg(p1Shapes.get(i).getColor(), p2Shapes.get(i).getColor());
        newShapes.add(new MyPolygon(newPoly, newColor));
      }
      for( int j = 0; j < newShapes.size(); j++ )
      {
        newGA.addPolygon(newShapes.get(j));
      }
      return newGA;

    }
    //calculates fitness, picks fit parents, crosses parents, mutates, and appends to new child
    public ArrayList<GASolution> generateNewPopulation()
    {
      ArrayList<GASolution> newPopulation = new ArrayList<GASolution>(population.size());
      GASolution p1;
      GASolution p2;
      GASolution child;
      for(int i = 0; i < size; i++)
      {
        p1 = pickFitParent();
        p2 = pickFitParent();

        if(Math.random() < CROSSOVER_RATE) child = crossover(p1, p2);
        else child = population.get(i);
        child = mutate(child);
        newPopulation.add(i,child);
      }
      return newPopulation;
    }

	  //randomly changes points and colors of passed in child 
    public GASolution mutate(GASolution child)
    {
      Random rand = new Random();
      ArrayList<MyPolygon> oldChild = child.getShapes();
      GASolution newChild = new GASolution(child.height, child.width);
      for( MyPolygon p : oldChild )
      {
        int[] xpoints = cloneArray(p.getPolygon().xpoints);
        int[] ypoints = cloneArray(p.getPolygon().ypoints);
        for(int i = 0; i < xpoints.length; i++)
        {
          if(Math.random()<MUTATION_RATE)
          {
            xpoints[i] = rand.nextInt(canvas.width) + 1;
            ypoints[i] = rand.nextInt(canvas.height) + 1;
          }
        }
        Polygon newChildPolygon = new Polygon(xpoints, ypoints, MAX_POLYGON_POINTS);
        Color newChildColor = new Color(p.getColor().getRGB());
        if(Math.random()<MUTATION_RATE)
        {
           newChildColor = getRandomColor();
        }
        newChild.addPolygon(new MyPolygon(newChildPolygon, newChildColor));
      }
      return newChild;
    }
    //random color generator
    public Color getRandomColor()
    {
      Random rand = new Random();
      Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
      return randomColor;
    }
    //deep clone of array
    public int[] cloneArray(int[] oldArray)
    {
      int[] newArray = new int[oldArray.length];
      for(int i = 0; i < oldArray.length; i++)
      {
        newArray[i] = oldArray[i];
      }
      return newArray;
    }

    //replaces current population with new population
    public void updatePopulation(ArrayList<GASolution> newPopulation)
    {
      population.clear();
      for( int i = 0; i < newPopulation.size(); i++ ) 
      {
        GASolution newSolution = cloneGASolution(newPopulation.get(i));
        population.add(newSolution);
      }
    }
    //deep clone of GASolution
    public GASolution cloneGASolution( GASolution g )
    {
      GASolution newG = new GASolution(g.width, g.height);
      ArrayList<MyPolygon> shapes = g.getShapes();
      for( int i = 0; i < 10; i++ )
      {
        newG.addPolygon(clonePolygon(shapes.get(i)));
      }
      return newG;
    }

    //deep clone of polygon
    public MyPolygon clonePolygon( MyPolygon p )
    {
      int[] xpoints = new int[MAX_POLYGON_POINTS]; //make seperate method
      int[] ypoints = new int[MAX_POLYGON_POINTS];
      xpoints = cloneArray(p.polygon.xpoints);
      ypoints = cloneArray(p.polygon.ypoints);
      //CHANGE 10 and all other polygon declerations
      Polygon newP = new Polygon(xpoints, ypoints, MAX_POLYGON_POINTS);
      Color colorClone = new Color(p.getColor().getRGB());
      MyPolygon newMyP = new MyPolygon( newP, colorClone );
      return newMyP;


    }
    //prints bestfit in population
    public GASolution getBestFit()
    {
      int bestFit = 0;
      for(int i = 0; i < population.size(); i++)
      {
        if( population.get(i).getFitness() > population.get(bestFit).getFitness() )
        {
          bestFit = i;
        }
      }
      return population.get(bestFit);
    }

    public void runSimulation(int epoch) 
    {
      ArrayList<GASolution> newPopulation;
      for( int i = 0; i < epoch; i++)
      {
        newPopulation = generateNewPopulation();
        canvas.setImage(getBestFit());
        System.out.println("Fitness: " + getBestFit().getFitness());
        canvas.repaint(); 
        updatePopulation(newPopulation);
        //cloneNewPopulation(newPopulation);
      }

      

    }

    public static void main(String[] args) throws IOException {

        String realPictureFilename = "test.jpg";

        BufferedImage realPicture = ImageIO.read(new File(realPictureFilename));

        JFrame frame = new JFrame();
        frame.setSize(realPicture.getWidth(), realPicture.getHeight());
        frame.setTitle("GA Simulation of Art");
        GACanvas theCanvas = new GACanvas(realPicture.getWidth(), realPicture.getHeight());
        GASolution defaultSol = new GASolution( realPicture.getWidth(), realPicture.getHeight());
        theCanvas.setImage(defaultSol);
        frame.add(theCanvas);
        frame.setVisible(true);
        GA pt = new GA(theCanvas, realPicture);
            pt.runSimulation(1000);
    }
}
