
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class MyPanel extends JPanel
{
 
int startX, flag, startY, endX, endY;

    BufferedImage grid;
    Graphics2D gc;
    int[] values;

	public MyPanel()
	{
	   startX = startY = 0;
           endX = endY = 100;
 	}

 	public MyPanel(int[] array){
	    startX = startY = 0; //idk
	    endX = endY = 100;
	    values = array; //gets array
    }

    public void drawHistogram(){
        try{
            Thread.sleep(100);
        }catch (InterruptedException ex){

        }
//        gc.drawLine(0,0,100,100);
       for (int i = 1; i < values.length; i++){
           gc.drawLine(i,600, i, (600 - values[i]/5)); //draws lines starting and bottom
       }
       repaint();
    }

     public void clear()
    {
       grid = null;
       repaint();
    }
    public void paintComponent(Graphics g)
    {  
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;
         if(grid == null){
            int w = this.getWidth();
            int h = this.getHeight();
            grid = (BufferedImage)(this.createImage(w,h));
            gc = grid.createGraphics();

         }
         g2.drawImage(grid, null, 0, 0);
     }
    public void drawing()
    {
        
        gc.drawLine(startX, startY, endX, endY);
        repaint();
    }
   
}
