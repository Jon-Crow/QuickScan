package com.quickscan.proto;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Image;
import com.leapmotion.leap.ImageList;
import java.awt.image.BufferedImage;

/**
 * @author Jon
 */
public class Main
{
    public static void main(String[] args)
    {
        UI.openMainFrame();
        System.out.println(UI.openSubFrame(HandTabbedPane.getInstance()));
        
        Controller ctr = new Controller();
        Frame frame;
        HandList hands;
        VectorPlotter plot = UI.getMainPanel().getVectorPlotter();
        //loop pulls data from device as long as the window is open
        while(UI.isOpen())
        {
            //Part of the "freeze frame" feature, not yet finished
            if(!plot.isFrozen())
            {
                frame = ctr.frame();
                //prevents displaying of invalid frames
                if(frame.isValid())
                {
                    //If enabled, will show the camera's images on the window
                    ImageList imgs = frame.rawImages();
                    if(imgs.count() > 0) plot.setCameraImage(rawImage(imgs.get(0)));
                    hands = frame.hands();
                    //Only updates hands if at least one present
                    if(hands.count() > 0) plot.plotHand(hands);
                    else plot.setMessage("No Hands");
                }
                else plot.setMessage("Invalid Frame");
            }
            else plot.setMessage("Frozen");
            UI.repaint();
            
            //Reduces the sample rate of the device which improves performance.
            //Waits 100 ms to rerun the loop.
            long wait = System.currentTimeMillis()+100;
            while(System.currentTimeMillis() < wait);
        }
    }
    /**
     * 
     * @param img the image value from the leap motion API
     * @return a BufferedImage value converted from the img parameter
     */
    private static BufferedImage rawImage(Image img)
    {
        BufferedImage rawImg = new BufferedImage(img.width(), img.height(), BufferedImage.TYPE_INT_RGB);
        int r, g, b;
        byte[] imgData = img.data();
        for(int i = 0; i < img.width() * img.height(); i++)
        {
            r = (imgData[i] & 0xFF) << 16; //convert to unsigned and shift into place
            g = (imgData[i] & 0xFF) << 8;
            b = imgData[i] & 0xFF;
            rawImg.setRGB(img.width()-i%img.width()-1,i/img.width(),r|g|b);
        }
        return rawImg;
    }
}