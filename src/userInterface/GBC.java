package userInterface;

import java.awt.*;

/**
 * Created by Arnold on 2015-11-22.
 */
public class GBC extends GridBagConstraints{

    public GBC(int gridx, int gridy){
        this.gridx = gridx;
        this.gridy = gridy;
    }

    public GBC(int gridx, int gridy, int gridwwidth, int gridheight){
        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwwidth;
        this.gridheight = gridheight;
    }
    public GBC setInsets(int distance){
        this.insets = new Insets(distance,distance,distance,distance);
        return this;
    }
    public GBC setIpads(int iPadx, int iPady){
        this.ipadx = iPadx;
        this.ipady = iPady;
        return this;
    }
    public GBC setWeight(double weightx, int weighty){
        this.weightx = weightx;
        this.weighty = weighty;
        return this;
    }
    public GBC setFill(int fill){
        this.fill = fill;
        return this;
    }
    public GBC setAnchor(int anchor){
        this.anchor = anchor;
        return this;
    }
}
