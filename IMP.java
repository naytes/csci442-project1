/*
 *Hunter Lloyd
 * Copyrite.......I wrote, ask permission if you want to use it outside of class.
 */

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;
import java.util.prefs.Preferences;

class IMP implements MouseListener {
    JFrame frame;
    JPanel mp;
    JButton start;
    //JButton blackW;
    JScrollPane scroll;
    JMenuItem openItem, exitItem, resetItem;
    Toolkit toolkit;
    File pic;
    ImageIcon img;
    int colorX, colorY;
    int[] pixels;
    int[] results;
    int rotations = 0; //keeps track of rotations for reset method...
    MyPanel redPanel; //global variable for panels so I can can create them and call them in different places
    MyPanel greenPanel;
    MyPanel bluePanel;
    //Instance Fields you will be using below

    //This will be your height and width of your 2d array
    int height = 0, width = 0;

    //your 2D array of pixels
    int picture[][];

    /*
     * In the Constructor I set up the GUI, the frame the menus. The open pulldown
     * menu is how you will open an image to manipulate.
     */
    IMP() {
        toolkit = Toolkit.getDefaultToolkit();
        frame = new JFrame("Image Processing Software by Hunter");
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu functions = getFunctions();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                quit();
            }
        });
        openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleOpen();
            }
        });
        resetItem = new JMenuItem("Reset");
        resetItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                reset();
            }
        });
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                quit();
            }
        });
        file.add(openItem);
        file.add(resetItem);
        file.add(exitItem);
        bar.add(file);
        bar.add(functions);
        frame.setSize(600, 600);
        mp = new JPanel();
        mp.setBackground(new Color(0, 0, 0));
        scroll = new JScrollPane(mp);
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        JPanel butPanel = new JPanel();
        butPanel.setBackground(Color.black);
        start = new JButton("start");
        start.setEnabled(false);
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                redPanel.drawHistogram();
                bluePanel.drawHistogram();
                greenPanel.drawHistogram();
            }
        });
        butPanel.add(start);
        ;
        frame.getContentPane().add(butPanel, BorderLayout.SOUTH);
        frame.setJMenuBar(bar);
        frame.setVisible(true);
    }

    /*
     * This method creates the pulldown menu and sets up listeners to selection of the menu choices. If the listeners are activated they call the methods
     * for handling the choice, fun1, fun2, fun3, fun4, etc. etc.
     */

    private JMenu getFunctions() {
        JMenu fun = new JMenu("Functions");

        JMenuItem firstItem = new JMenuItem("MyExample - fun1 method");

        firstItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                fun1();
            }
        });

        JMenuItem blackW = new JMenuItem("Grayscale (luminosity method)");
        blackW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                blackAndWhite();
            }
        });
        JMenuItem blur = new JMenuItem("blur photo 3x3 and grayscale");
        blur.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                blurPic();
            }
        });
        JMenuItem edge = new JMenuItem("Edge detection (5x5 mask)");
        edge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                edgeDetection();
            }
        });
        JMenuItem track = new JMenuItem("Color track orange");
        track.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                colorTrack();
            }
        });
        JMenuItem turn = new JMenuItem("Rotate 90' clockwise");
        turn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                rotatePic();
            }
        });
        JMenuItem histo = new JMenuItem("Show histogram");
        histo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                histogram();
            }
        });
        JMenuItem equal = new JMenuItem("Equalize");
        equal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                equalize();
            }
        });
        fun.add(firstItem);
        fun.add(turn);
        fun.add(blackW);
        fun.add(blur);
        fun.add(edge);
        fun.add(track);
        fun.add(histo);
        fun.add(equal);
        return fun;

    }

    /*
     * This method handles opening an image file, breaking down the picture to a one-dimensional array and then drawing the image on the frame.
     * You don't need to worry about this method.
     */
    private void handleOpen() {
        img = new ImageIcon();
        JFileChooser chooser = new JFileChooser();
        Preferences pref = Preferences.userNodeForPackage(IMP.class);
        String path = pref.get("DEFAULT_PATH", "");

        chooser.setCurrentDirectory(new File(path));
        int option = chooser.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            pic = chooser.getSelectedFile();
            pref.put("DEFAULT_PATH", pic.getAbsolutePath());
            img = new ImageIcon(pic.getPath());
        }
        width = img.getIconWidth();
        height = img.getIconHeight();

        JLabel label = new JLabel(img);
        label.addMouseListener(this);
        pixels = new int[width * height];

        results = new int[width * height];


        Image image = img.getImage();

        PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("Interrupted waiting for pixels");
            return;
        }
        for (int i = 0; i < width * height; i++)
            results[i] = pixels[i];
        turnTwoDimensional();
        mp.removeAll();
        mp.add(label);

        mp.revalidate();
    }

    /*
     * The libraries in Java give a one dimensional array of RGB values for an image, I thought a 2-Dimensional array would be more usefull to you
     * So this method changes the one dimensional array to a two-dimensional.
     */
    private void turnTwoDimensional() {
        picture = new int[height][width];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                picture[i][j] = pixels[i * width + j];


    }

    /*
     *  This method takes the picture back to the original picture
     */
private void reset() {
    if(rotations % 2 != 0) rotatePic(); //something was wrong with my reset getting messed up with rotations and this was an easy fix :)
    int l = 0; //extra iterator
        for (int i = 0; i < (height*width); i++)
            pixels[i] = results[i];

    for (int j = 0; j < height; j++){ //loop that assigns values to the picture array
        for (int k = 0; k < width; k++){
            picture[j][k] = pixels[l];
            l++;
        }
    }
    Image img2 = toolkit.createImage(new MemoryImageSource(width, height, pixels, 0, width));
    JLabel label2 = new JLabel(new ImageIcon(img2));
        mp.removeAll();
        mp.add(label2);

        mp.revalidate();
    }

    /*
     * This method is called to redraw the screen with the new image.
     */
    private void resetPicture() {
        mp.repaint(); //added this to reset picture becuase smaller pics kept overlapping and this cleared the screen out
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                pixels[i * width + j] = picture[i][j];
        Image img2 = toolkit.createImage(new MemoryImageSource(width, height, pixels, 0, width));

        JLabel label2 = new JLabel(new ImageIcon(img2));
        mp.removeAll();
        mp.add(label2);

        mp.revalidate();

    }

    /*
     * This method takes a single integer value and breaks it down doing bit manipulation to 4 individual int values for A, R, G, and B values
     */
    private int[] getPixelArray(int pixel) {
        int temp[] = new int[4];
        temp[0] = (pixel >> 24) & 0xff;
        temp[1] = (pixel >> 16) & 0xff;
        temp[2] = (pixel >> 8) & 0xff;
        temp[3] = (pixel) & 0xff;
        return temp;

    }

    /*
     * This method takes an array of size 4 and combines the first 8 bits of each to create one integer.
     */
    private int getPixels(int rgb[]) {
        int alpha = 0;
        int rgba = (rgb[0] << 24) | (rgb[1] << 16) | (rgb[2] << 8) | rgb[3];
        return rgba;
    }

    public void getValue() {
        int pix = picture[colorY][colorX];
        int temp[] = getPixelArray(pix);
        System.out.println("Color value " + temp[0] + " " + temp[1] + " " + temp[2] + " " + temp[3]);
    }

    /**************************************************************************************************
     * This is where you will put your methods. Every method below is called when the corresponding pulldown menu is
     * used. As long as you have a picture open first the when your fun1, fun2, fun....etc method is called you will
     * have a 2D array called picture that is holding each pixel from your picture.
     *************************************************************************************************/
    /*
     * Example function that just removes all red values from the picture.
     * Each pixel value in picture[i][j] holds an integer value. You need to send that pixel to getPixelArray the method which will return a 4 element array
     * that holds A,R,G,B values. Ignore [0], that's the Alpha channel which is transparency, we won't be using that, but you can on your own.
     * getPixelArray will breaks down your single int to 4 ints so you can manipulate the values for each level of R, G, B.
     * After you make changes and do your calculations to your pixel values the getPixels method will put the 4 values in your ARGB array back into a single
     * integer value so you can give it back to the program and display the new picture.
     */
    private void fun1() { //I didnt make this

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int rgbArray[] = new int[4];

                //get three ints for R, G and B
                rgbArray = getPixelArray(picture[i][j]);


                rgbArray[1] = 0;
                //take three ints for R, G, B and put them back into a single int
                picture[i][j] = getPixels(rgbArray);
            }
        resetPicture();
    }

    private void blackAndWhite() {
        for (int i = 0; i < height; i++) { //loop through all pixels
            for (int j = 0; j < width; j++) {
                int rgbArray[] = new int[4];
                rgbArray = getPixelArray(picture[i][j]); //get current rgb values
                rgbArray[1] = (int) Math.round(((rgbArray[1] * (.2126) + rgbArray[2] * (.7152) + rgbArray[3] * (.0722)))); //using luminosity method and wieghting colors differently
                if (rgbArray[1] > 255) rgbArray[1] = 255; //making sure values between 0 and 255
                if (rgbArray[1] < 0) rgbArray[1] = 0;
                rgbArray[2] = rgbArray[1]; //sets red green and blue all the same to make pixel a shade of gray
                rgbArray[3] = rgbArray[2];
                picture[i][j] = getPixels(rgbArray);
            }
        }
        resetPicture();
    }

    private void blurPic() {
        int tempPicture[][] = picture; //temp pic so i dont pull already blured pixel values from picture
        for (int i = 1; i < height - 1; i++) { //loop through picture excluding the outwide edge so i dont go out of (array) bounds
            for (int j = 1; j < width - 1; j++) {
                int rgbArray[] = new int[4]; //holds pixel values
                int rgbTotals[] = new int[4]; //holds totals of each color
                for (int k = i - 1; k < i + 2; k++) { //loops in a 3x3 square centered at given pixel
                    for (int l = j - 1; l < j + 2; l++) {
                        rgbArray = getPixelArray(tempPicture[k][l]); // gets pixel
                        rgbTotals[1] += rgbArray[1]; //adds current values to total values
                        rgbTotals[2] += rgbArray[2];
                        rgbTotals[3] += rgbArray[3];
                    }
                }
                rgbArray[1] = rgbTotals[1] / 9; //divides by 9 to get average value then puts it back in pixel
                rgbArray[2] = rgbTotals[2] / 9;
                rgbArray[3] = rgbTotals[3] / 9;
                picture[i][j] = getPixels(rgbArray);
            }
        }
        resetPicture();
    }

   /* private int averageRGB(int array[]) { //didnt end up using this although I probably shoulda have
        int total = 0;
        for (int i = 1; i < 4; i++) {
            total += array[i];
        }
        total = total / 3;
        return total;
    }*/

    private void edgeDetection() {
        //blurPic(); //this didnt help my edge detection
        blackAndWhite();
/*        int[][] edgeMatrix = { //matrix i used for reference
                {-1, -1, -1, -1, -1},
                {-1, 0, 0, 0, -1},
                {-1, 0, 16, 0, -1},
                {-1, 0, 0, 0, -1},
                {-1, -1, -1, -1, -1}
        };*/

        int tempPicture[][] = new int[height][width]; //temp pic so i dont pull already changed pixel values from picture
        int edges;
        for (int i = 2; i < height - 2; i++) { //loop through picture excluding the outwide edge(2pixels thick) so i dont go out of (array) bounds
            for (int j = 2; j < width - 2; j++) {
                    edges = (picture[i-2][j-2] * -1) + (picture[i-2][j-1] * -1) + (picture[i-2][j] * -1) + (picture[i-2][j+1] * -1) + (picture[i-2][j+2] * -1) + //matrix math spaced out to look nice
                            (picture[i-1][j-2] * -1)                                                                                + (picture[i-1][j+2] * -1) +
                            (picture[i]  [j-2] * -1)                            + (picture[i]  [j] * 15)                            + (picture[i]  [j+2] * -1) +
                            (picture[i+1][j-2] * -1)                                                                                + (picture[i+1][j+2] * -1) +
                            (picture[i+2][j-2] * -1) + (picture[i+2][j-1] * -1) + (picture[i+2][j] * -1) + (picture[i+2][j+1] * -1) + (picture[i+2][j+2] * -1) ;
                    tempPicture[i][j] = edges; //place value into temp
                }
            }
         picture = tempPicture; //place value back into picture
        //flipPic();
        resetPicture();
        }

        /*private void makeBlack(){
            for (int i = 0; i < height; i++) { //loop through all pixels
                for (int j = 0; j < width; j++) {
                    int rgbArray[];
                    rgbArray = new int[]{255, 255, 255, 255}; //get current rgb values
                    picture[i][j] = getPixels(rgbArray);
                }
            }
            resetPicture();
            mp.revalidate();
        }*/

        private void rotatePic(){

            int[][] tempPicture;
            tempPicture = new int[width][height];
            for (int i = 0; i < width; i++) { //loop through all pixels
                for (int j = height-1; j >= 0; j--) { //prevents image from flipping
                    tempPicture[i][height - 1 - j] = picture[j][i];
                }
            }
            rotations++; //adds
            int temp = height;
            height = width;//switches height with width
            width = temp;
            picture = tempPicture;

            resetPicture();

        }
    /*private void flipPic(){ //never used this method but it could be useful
        int tempPicture[][] = picture;
        for (int i = 0; i < height; i++) { //loop through all pixels
            int k = 0;
            for (int j = width - 1; j >= 0; j--) {
                picture[i][j] = tempPicture[i][k];
                k++;
            }
        }
       // picture = tempPicture;
        resetPicture();
    }*/

    private void equalize(){
        float freqRed[] = new float[256];
        float cdfArrayRed[] = new float[256];
        float freqGreen[] = new float[256];
        float cdfArrayGreen[] = new float[256];
        float freqBlue[] = new float[256];
        float cdfArrayBlue[] = new float[256];
        //blackAndWhite();
        int rgbArray[] = new int[4];
        for (int i = 0; i < height; i++) { //loop through all pixels and counts frequencies
            for (int j = 0; j < width; j++) {
                rgbArray = getPixelArray(picture[i][j]);
                freqRed[rgbArray[1]] += 1;
                freqGreen[rgbArray[2]] += 1;
                freqBlue[rgbArray[3]] += 1;
            }
        }
        float totalRed = 0; //holds totals
        float totalGreen = 0;
        float totalBlue = 0;
        for (int i = 0; i < freqRed.length; i++) { //loop through all pixels and adds all the frequencies of each color seperately
            cdfArrayRed[i] = freqRed[i] + totalRed;
            totalRed += freqRed[i];
            cdfArrayGreen[i] = freqGreen[i] + totalGreen;
            totalGreen += freqGreen[i];
            cdfArrayBlue[i] = freqBlue[i] + totalBlue;
            totalBlue += freqBlue[i];
        }
        for (int i = 0; i < cdfArrayRed.length; i++){ //changes all the totals to percentage values between 0 and 1 depending on relevence to total
            cdfArrayRed[i] = cdfArrayRed[i]/(height*width);
            cdfArrayGreen[i] = cdfArrayGreen[i]/(height*width);
            cdfArrayBlue[i] = cdfArrayBlue[i]/(height*width);

            // cdfArray[i] = (((1 - cdfArray[i]) * i));
        }

        for (int i = 0; i < height; i++) { //loop through all pixels, multiplies all the precentage values by the cumulative distribution function
            for (int j = 0; j < width; j++) {
                rgbArray = getPixelArray(picture[i][j]);
                rgbArray[1] = (int) (rgbArray[1] * cdfArrayRed[rgbArray[1]]);
                rgbArray[2] = (int) (rgbArray[2] * cdfArrayGreen[rgbArray[2]]);
                rgbArray[3] = (int) (rgbArray[3] * cdfArrayBlue[rgbArray[3]]);

                // rgbArray[2] = rgbArray[1];
               // rgbArray[3] = rgbArray[1];
                picture[i][j] = getPixels(rgbArray); //puts em back in the picture
            }
        }

        resetPicture();
   //System.out.println("final int = " + cdfArrayRed[255]); //reset to test and make sure final number was 1 (100%)
    }

    private void colorTrack() {
        for (int i = 0; i < height; i++) { //loop through all pixels
            for (int j = 0; j < width; j++) {
                int rgbArray[] = new int[4];
                rgbArray = getPixelArray(picture[i][j]); //get current rgb values
                if (rgbArray[1] > 205 && rgbArray[3] < 100 && rgbArray[2] < 200) { //check to see if the color is within my specified zone of orange
                    rgbArray[1] = 255; //if within zone goes white
                    rgbArray[2] = 255;
                    rgbArray[3] = 255;
                } else { //else black
                    rgbArray[1] = 0;
                    rgbArray[2] = 0;
                    rgbArray[3] = 0;
                }
                picture[i][j] = getPixels(rgbArray);
            }
        }
     //   mp.removeAll();
        resetPicture();
    }

    private void histogram(){
    int[] red = new int[256];
    int[] green = new int[256];
    int[] blue = new int[256];
        int rgbArray[] = new int[4]; //holds pixel values
        for (int i = 0; i < height; i++) { //loop through all pixels and gets frequency values
            for (int j = 0; j < width; j++) {
                rgbArray = getPixelArray(picture[i][j]);
                red[rgbArray[1]] += 1;
                green[rgbArray[2]] += 1;
                blue[rgbArray[3]]  += 1;
            }
        }
        JFrame redFrame = new JFrame("Red"); //code below was given
        redFrame.setSize(305, 600);
        redFrame.setLocation(800, 0);
        JFrame greenFrame = new JFrame("Green");
        greenFrame.setSize(305, 600);
        greenFrame.setLocation(1150, 0);
        JFrame blueFrame = new JFrame("blue");
        blueFrame.setSize(305, 600);
        blueFrame.setLocation(1450, 0);
        redPanel = new MyPanel(red);
        greenPanel = new MyPanel(green);
        bluePanel = new MyPanel(blue);
        redFrame.getContentPane().add(redPanel, BorderLayout.CENTER);
        redFrame.setVisible(true);
        greenFrame.getContentPane().add(greenPanel, BorderLayout.CENTER);
        greenFrame.setVisible(true);
        blueFrame.getContentPane().add(bluePanel, BorderLayout.CENTER);
        blueFrame.setVisible(true);
//        redPanel.drawHistogram();
        start.setEnabled(true);
    }

    private void quit() {
        System.exit(0);
    }

    @Override
    public void mouseEntered(MouseEvent m) {
    }

    @Override
    public void mouseExited(MouseEvent m) {
    }

    @Override
    public void mouseClicked(MouseEvent m) {
        colorX = m.getX();
        colorY = m.getY();
        System.out.println(colorX + "  " + colorY);
        getValue();
        start.setEnabled(true);
    }

    @Override
    public void mousePressed(MouseEvent m) {
    }

    @Override
    public void mouseReleased(MouseEvent m) {
    }

    public static void main(String[] args) {
        IMP imp = new IMP();
    }

}