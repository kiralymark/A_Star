package a_star;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class Node extends JButton implements ActionListener {
    
    Node parent;                    // ~  'parent' object, keeping track of "neighbours"
    
    int col;                        // the "x" coordinate
    int row;                        // the "y" coordinate
    
    int fCost;                      // the sum of g + h (The overall cost)
    int gCost;                      // the distance from the start
    int hCost;                      // the distance from the goal
    
    boolean isStart;                // is this the "starting" Node
    boolean isGoal;                 // is this the "ending" Node
    
    boolean isOpen;                 // is it an "open" or a "isAvailable" Node
    boolean isChecked;              // is it a "checked" Node
    boolean isWall;                 // is it a "solid" (wall) Node
    
    boolean isPath;
    
    
    public Node(int col, int row){      // every Node in the 'nodesGrid' has these properties 
    
        this.col = col;
        this.row = row;
        
        setBackground(Color.white);     // (the background colour of a button)
        setForeground(Color.black);     // (the text colour of a button)
        setFocusable(false);            // (make the border around the text disappear)
        addActionListener(this);        //  ~  if the button is clicked
        
    }
    
    public void setAsStart(){           // a function for the "starting" Node
        setBackground(Color.blue);
        setForeground(Color.white);
        setText("Start");               // (set the text of the button)
        isStart = true;
        
    }
    
    public void setAsGoal(){            // a function for the "ending" Node
        setBackground(Color.yellow);
        setForeground(Color.black);
        setText("Goal");
        isGoal = true;
        
    }
    
    public void setAsWall(){            // set as a "solid" (wall) Node
        setBackground(Color.black);
        setForeground(Color.black);
        isWall = true;
        
    }
    
    public void setAsOpen(){
        if(isStart == false && isGoal == false && isChecked == false){
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.black);
            
        }
        isOpen = true;

    }
    
    public void setAsChecked(){
        if(isStart == false && isGoal == false){
            setBackground(Color.orange);
            setForeground(Color.black);
            
        }
        isChecked = true;
        
    }
    
    public void setAsPath(){
        setBackground(Color.magenta);
        setForeground(Color.white);
        isPath = true;
        
    }
    
    // WIP feature
    @Override
    public void actionPerformed(ActionEvent e) {        //  ~  if the button is clicked
        setBackground(Color.orange);
        
    }
    
    
}
