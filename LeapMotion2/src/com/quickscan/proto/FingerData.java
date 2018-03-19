package com.quickscan.proto;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FingerData
{
    @XmlElement
    private Hand left, right;
    
    
    
    @XmlRootElement
    private class Hand
    {
        @XmlElement
        private Finger[] fingers;
    }
    @XmlRootElement
    private class Finger
    {
        @XmlElement
        private com.leapmotion.leap.Finger.Type type;
        @XmlElement
        private HashMap<JointAngle, double[]> angles;
    }
}