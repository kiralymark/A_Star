package a_star;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JPanel;

public class MainPanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int maximumCols = 15;             // * setting
    final int maximumRows = 10;             // * setting
    
    //final int displayMode = 1;        // ( 1 , 0 ) ;  WIP FEATURE:    1 displays with fixed resolution and 'nodeSize' changes accordingly
                                        //                              0 displays changing resolution and 'nodeSize' is fixed              

    //                                                  WIP FEATURE:
    //final int pathMode;               //  -- only show at the end
    //                                      -- show the Path on every frame
    
    final int nodeSize = 70;                // (* setting)  making a grid out of squares (with this size)
    final int screenWidth = nodeSize * maximumCols;
    final int screenHeight = nodeSize * maximumRows;

    
    // Setting The FPS for the Program
    int FPS = 15;                           // (* setting)  test numbers:    60, 30
    
    Thread mainThread;                      // to create  Time  in the program
    
    
    // NODE
    Node[][] nodesGrid = new Node[maximumCols][maximumRows];        // making the grid
    Node startNode, goalNode, currentNode;
    
    ArrayList<Node> openSet = new ArrayList<>();        // a List of "open" or "available" Nodes to check
    ArrayList<Node> closedSet = new ArrayList<>();      // a List of "closed" or "unavailable" Nodes
    
    ArrayList<Node> thePathSet = new ArrayList<>();
    
    
    // OTHERS
    boolean goalNodeReached = false;        // is the 'currentNode' equals 'goalNode' ( is the "goalReached" )
    //int step = 0;                         // limiting the number of steps to decrease (and restrict) pathFinding time
    
    boolean noSolution = false;
    

    // (note:  The constructor here 
    //  works like 'public static void main')
    public MainPanel(){
        
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));    // to set the size of the JFrame window
        this.setBackground(Color.black);                                    // (the background colour of the window)
        this.setLayout(new GridLayout(maximumRows, maximumCols));
        
        this.setDoubleBuffered(true);   // improving rendering performance
     

        // PLACE NODES
        int col = 0;
        int row = 0;
        
        while(col < maximumCols && row < maximumRows){              // making the grid
        
            nodesGrid[col][row] = new Node(col, row);               // using the 'Node' constructor
            this.add(nodesGrid[col][row]);                          // ~  adding the Nodes to the window to place them
            
            col++;
            if(col == maximumCols){     // (when at the end of a column, move one row down and start over)
                col = 0;
                row++;
                
            }
        
        }
        
        System.out.println("A*\n");     // test (I.)
        
        
        // SET START AND GOAL NODE
        setStartNode(0, 0);                             // * setting
        setGoalNode(maximumCols-1, maximumRows-1);      // (* setting)  (... at the bottom right corner of the window)


        // SET COST
        setCostOnNodes();
        

        // PLACE WALL (SOLID) NODES
        int rndMinimumLimit = 0;                        // (random)
        int rndMaximumLimit = 1;
        
        double randomNumber;
        
        for (int i = 0; i < maximumCols; i++) {
            
            for (int j = 0; j < maximumRows; j++) {
                randomNumber = Math.random()*(rndMaximumLimit-rndMinimumLimit+1)+rndMinimumLimit;
                
                if(randomNumber < 0.5){
                    
                    if(nodesGrid[i][j] != startNode && nodesGrid[i][j] != goalNode){
                        setWallNode(i, j);
                    
                    }
                    
                }
                
            }
            
        }
        //setWallNode(2, 2);    // test (II.)

        
    }
    
    private void setStartNode(int col, int row){        // a function for the "starting" Node
        nodesGrid[col][row].setAsStart();               // using a Function on an object (and setting its properties) ((many objects))
        startNode = nodesGrid[col][row];                // set the 'startNode'
        currentNode = startNode;                        // the 'startNode' will be the 'currentNode'

        openSet.add(startNode);     // add 'startNode' to the openList (mandatory)
        
    }
    
    private void setGoalNode(int col, int row){         // a function for the "ending" Node
        nodesGrid[col][row].setAsGoal();
        goalNode = nodesGrid[col][row];                 // set the 'goalNode'
        
    }
    
    private void setWallNode(int col, int row){         // set as a "solid" (wall) Node
        nodesGrid[col][row].setAsWall();
        
    }
    
    private void setCostOnNodes(){
    
        int col = 0;
        int row = 0;
        
        while(col < maximumCols && row < maximumRows){
        
            getNodeCost(nodesGrid[col][row]);
            col++;
            if(col == maximumCols){
                col = 0;
                row++;
                
            }
            
        }
        
    }
    
    private void getNodeCost(Node node){        

        // GET G COST (The distance from the start node)
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        node.gCost = xDistance + yDistance;
        
        // GET H COST (The distance from the goal node)
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost = xDistance + yDistance;                     // calculate the hCost (heuristic)
        
        // GET F COST (The total cost)
        node.fCost = node.gCost + node.hCost;
        
        // DISPLAY THE COST ON NODE
        if(node != startNode && node != goalNode){
            node.setText("<html>F:" + node.fCost + "<br>G:" + node.gCost + "</html>");
            //node.setText("<html>F:" + node.fCost + "<br>G:" + node.gCost + ", H:" + node.hCost + "</html>");
            
        }
        
    }
    
   
    int maxSteps = 300;     // * setting

    public void searchGoalNode() {      // the Search function

        //int placeInLine = 0;      // test (III.)
             
        
        if(!noSolution){
            
            // keep Searching for the 'goalNode'
            //System.out.println("\n" + "SIZE" + "; " + "COLUMN" + ", " + "ROW");     // test (I.)
            //System.out.println("\n" + "SIZE" + "; " + "COLUMN" + ", " + "ROW" + "; " + "placeInLine"); // test (III.)

            
            if(goalNodeReached == false && maxSteps > 0){

                //System.out.print(openSet.size() + "; ");    // test (I.)
                //System.out.println(currentNode.col + ", " + currentNode.row);   // test (I.)
                //System.out.println(currentNode.col + ", " + currentNode.row + "; " + placeInLine + "."); // test (III.)
                //placeInLine++; // test (III.)
                
                int col = currentNode.col;
                int row = currentNode.row;

                currentNode.setAsChecked();


                if(!(currentNode.isWall)){
                    
                    // OPEN THE UP NODE         (add node neighbour)
                    if(row -1 >= 0){
                        openNewNode(nodesGrid[col][row-1]);
                        
                    }
                    // OPEN THE LEFT NODE       (add node neighbour)
                    if(col -1 >= 0){
                        openNewNode(nodesGrid[col-1][row]);
                        
                    }
                    // OPEN THE DOWN NODE       (add node neighbour)
                    if(row +1 < maximumRows){
                        openNewNode(nodesGrid[col][row+1]);
                        
                    }
                    // OPEN THE RIGHT NODE      (add node neighbour)
                    if(col +1 < maximumCols){
                        openNewNode(nodesGrid[col+1][row]);
                        
                    }
                    
                }
                
                
                // FIND THE BEST NODE
                int bestNodeIndex = 0;                  // the "winnerIndex" or "lowestIndex"
                int bestNodefCost = 999;
  
                for(int i = 0; i < openSet.size(); i++) {       // checking the Node's every "neighbour"

                    //System.out.println(openSet.size()); // test (II.)
                    //System.out.print("evaluate ");      // test (I.)
                    //System.out.println(currentNode.col + ", " + currentNode.row); // test (I.)
                    //System.out.println(currentNode.col + ", " + currentNode.row + "; " + placeInLine + "."); // test (III.)

                    // Check if this node's F cost is better
                    if((openSet.get(i).fCost < bestNodefCost) && !(currentNode.isWall)) {  
                        bestNodeIndex = i;
                        bestNodefCost = openSet.get(i).fCost;
                        
                    }
                    // If F cost is equal, check the G cost
                    else if((openSet.get(i).fCost == bestNodefCost) && !(currentNode.isWall)) {
                        if((openSet.get(i).gCost < openSet.get(bestNodeIndex).gCost)  && !(currentNode.isWall)){
                            bestNodeIndex = i;
                            
                        } 
                        
                    }
                    
                }

                // After the loop, we get the best node which is our next step
                if(openSet.size() > 0){
                    currentNode = openSet.get(bestNodeIndex);       // (next step or "current neighbour")
                
                }
                
                if(currentNode == goalNode) {
                    System.out.println("'goalNode' FOUND!");        // test (I.)
                    goalNodeReached = true;
                    trackThePath();           // ~ (to show the result at the end)
                    
                    isSearchCompleted = true;
                    
                } 
                
                
                // (usage of removeFromArray(openSet,currentNode) Function ... )
                openSet.remove(currentNode);
                
                if(!(closedSet.contains(currentNode))){
                    closedSet.add(currentNode);
                    //System.out.println("X"); // test (IV.)
                    
                }
                
                maxSteps--;
                
                //System.out.println(maxSteps);       // test (I.)
                
            } else {
               // stop the Search.
               // no solution!

               //System.out.println(openSet.size());  // test (I.)

               System.out.println("no solution!");  // test (I.)
               noSolution = true;

           }
            
        }
        
        
        // test (I.)
        if(goalNodeReached || noSolution){

            //System.out.println("\nOPEN");           // test (I.)
            //System.out.println(openSet);          // test (IV.)
            //System.out.println(openSet.size());     // test (I.)
            
            //System.out.println("\nCLOSED");         // test (I.)
            //System.out.println(closedSet);        // test (IV.)
            //System.out.println(closedSet.size());   // test (I.)
            
            
            //System.out.println("COL, ROW");           // test (V.)
            //System.out.println(thePathSet.size());    // test (V.)
            
            for(int i = 0; i < thePathSet.size(); i++) {
                //System.out.println(thePathSet.get(i).col + ", " + thePathSet.get(i).row); // test (I.)
                
            }
            
            // test (I.)
            isSearchCompleted = true;
            
        }
        
    
    }
    
    
    private void openNewNode(Node node) {       // the "openNode" or "addNeighbours"  Function
        
        if(node.isOpen == false && node.isChecked == false && node.isWall == false){
    
            // If the node is not opened yet, add it to the open list
            node.setAsOpen();
            node.parent = currentNode;
            openSet.add(node);

        }

    }
    
    private void trackThePath() {               // To find the Path
        
        // Backtrack and draw the best path
        Node current = goalNode;
        
        while(current != startNode) {       // recursive code condition (while it is true)
        
            current = current.parent;       // recursive code statement (Backtrack ...)
            
            if(current != startNode) {
                current.setAsPath();

                thePathSet.add(current);
                
            }
            
        }
          
    }
    
    public void startMainThread() {             // to start a Thread
         
        mainThread = new Thread(this);      // to make a new thread
            
        // starting the  Thread
        mainThread.start(); // the command calls -- >  public void run() {} method ! !
        
    }
    
    
    boolean isSearchCompleted;

    // to run a Thread
    @Override
    public void run() {

        //  ##  "DELTA / ACCUMULATOR"  METHOD

        double drawInterval = 1000000000 / FPS;
        
        double delta = 0; // 0 for now ...
        
        long lastTime = System.nanoTime();
        
        long currentTime;
        
        
        //  Let's display the FPS
        long timer = 0; // start from 0 ... ;       (to display the FPS)
        int drawCount = 0; // start from 0 ... ;    (to display the FPS)
        
        
        while(mainThread != null){
            
            currentTime = System.nanoTime(); // at the beginning of the Loop, we check the currentTime
            
            delta += (currentTime - lastTime) / drawInterval; // (how much time has passed between two points) / drawInterval
            
            timer += (currentTime - lastTime); // in every loop we add the past time to the timer (to display the FPS)
            
            lastTime = currentTime; // assign the currentTime to lastTime
            
            //System.out.println(delta); //test (VI.)
            
            
            if(delta >= 1){ // delta = 1 :  means drawInterval
                //System.out.println(delta); //test (VI.)
                ////update();
                
                
                if(!isSearchCompleted){
                    searchGoalNode();       // to start the Search function
                    
                }
                
                
                ////repaint();
                delta--;        // reset delta
                drawCount++;    // increase by 1 (to display the FPS)
                
            }
                
            
            if(timer >= 1000000000){ // when 'timer' reaches 1 second .. (to display the FPS)
                System.out.println("FPS:" + drawCount); // How many times it updated and repainted 
                                                        // until the 'timer' hit one second.        ( test (I.) )

                drawCount = 0;  // to reset drawCount (( without this, we can calculate how many seconds have passed, using the if statement ))                                 
                timer = 0;      // to reset timer  

            }

        }
        
        
    }

    // This is a WIP feature (it is unused for now).
    public void update() {
    }
    
    // This is a WIP feature (it is unused for now).
    // " Override "
    public void paintComponent() {
    }
    
}
