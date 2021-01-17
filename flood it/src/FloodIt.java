import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//Represents a single square of the game area
class Cell {
  //In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // constructor to mandate the connected cells
  Cell(int x, int y, Color color, boolean flooded, 
      Cell left, Cell top, Cell right, Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  // constructor for creating initial board without connecting 
  // each cell 
  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // draws a single cell
  WorldImage drawCell(int cellSize) {
    return new RectangleImage(cellSize, cellSize, OutlineMode.SOLID, this.color);
  }
}

// To represent the world class of game
class FloodItWorld extends World {
  // represents a list of colors that can choose from, max 8 colors
  static ArrayList<Color> COLORLIST = new ArrayList<Color>(Arrays.asList(Color.red, Color.blue,
      Color.green, Color.orange, Color.cyan, Color.pink, Color.gray, Color.magenta));

  static int BOARD_SIZE = 22;
  static double TICKRATE = 0.05;


  Random rand;
  
  // All the cells of the game
  ArrayList<Cell> board;
  
  // represents if the board is in the process of flooding
  // default to false; if true, user click will have no effect
  boolean isflooding;
  
  // represents the dimension of the board
  // an n dimensional board consists of n*n cells
  int dimension;
  
  //represents the number of color on board
  int numColors;
  
  //represents the number of clicks
  int numClicked;
  
  //represents 
  Color after;
  Color before;

  //represents time elapsed
  double time;

  // constructor for game
  FloodItWorld(int dim, int numColors) {
    this.rand = new Random();
    ArrayList<Cell> arr = new ArrayList<Cell>();

    // to confirm that the number of colors will not exceeds 8
    if (numColors <= 8) {
      this.numColors = numColors;
    }
    else {
      throw new IllegalArgumentException("Maximum 8 colors");
    }

    // to create a list of cells of random colors
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        arr.add(new Cell(j, i, randColor(this.numColors), false));
      }
    }

    // to connect each cells of this FloodItWorld
    for (int i = 0; i < dim * dim; i++) {
      if (i >= dim) {
        arr.get(i).top = arr.get(i - dim);
      }
      if (i < dim * (dim - 1)) {
        arr.get(i).bottom = arr.get(i + dim);
      }

      if (i % dim != 0) {
        arr.get(i).left = arr.get(i - 1);
      }

      if ((i - dim + 1) % dim != 0) {
        arr.get(i).right = arr.get(i + 1);
      }
    }

    arr.get(0).flooded = true;

    this.board = arr;
    this.dimension = dim;
    this.isflooding = true;
    this.before = this.board.get(0).color;
    this.after = this.before;
    this.numClicked = 0;
    this.time = 0.0;
  }

  //Constructor for testing purpose
  FloodItWorld(int dimension, int numColors, Random rand) {
    this.rand = rand;
    ArrayList<Cell> arr = new ArrayList<Cell>();

    // to make sure the number of colors of the board is up to six
    if (numColors <= 8) {
      this.numColors = numColors;
    }
    else {
      throw new IllegalArgumentException("Maximum 8 colors");
    }

    // to create a list of cells of random colors 
    for (int i = 0; i < dimension; i ++) {
      for (int j = 0; j < dimension; j++) {
        arr.add(new Cell(j, i, randColor(numColors), false));
      }
    }

    // to connect each cells of this FloodItWorld 
    for (int i = 0; i < dimension * dimension; i++) {
      if (i >= dimension) {
        arr.get(i).top = arr.get(i - dimension);
      }
      if (i < dimension * (dimension - 1)) {
        arr.get(i).bottom = arr.get(i + dimension);
      }

      if (i  % dimension != 0) {
        arr.get(i).left = arr.get(i - 1);
      }

      if ((i - dimension + 1) % dimension != 0) {
        arr.get(i).right = arr.get(i + 1);
      }
    }
    this.board = arr;
    this.dimension = dimension;
    this.isflooding = false;
    this.before = board.get(0).color;
    this.after = before;
    this.numClicked = 0;
    this.time = 0.0;
  }

  // constructor for testing big bang handlers
  FloodItWorld(int dimension, int numColors, Random rand,
      ArrayList<Cell> board, boolean isflooding, int numClicked,
      double time, Color before, Color after) {
    this.dimension = dimension;
    this.numColors = numColors;
    this.rand = rand;
    this.board = board;
    this.isflooding = isflooding;
    this.numClicked = numClicked;
    this.time = time;
    this.before = before;
    this.after = after;
  }


  // randomly chooses a color from the list of 8 colors
  Color randColor(int numColors) {
    int index = rand.nextInt(numColors);
    return COLORLIST.get(index);
  }

  // render the image of the cells and board on to the WorldScene
  public WorldScene makeScene() {
    WorldScene s = getEmptyScene();
    // to represent size of each cell, given the dimension of board
    int cellSize = 20 * BOARD_SIZE / this.dimension;
    // to represent size of board
    int boardSize = 20 * BOARD_SIZE + 180;
    // to represent size of margin
    int margin = 90;
    int halfmargin = 45;
    // to represent number of available chances, regression line from the sample
    // game data
    int numChance = (int) (this.dimension * 1.64137 + this.numColors * 4.16735
         - 23.54243);

    // to draw the list of cells on the board
    for (int i = 0; i < dimension * dimension; i++) {
      s.placeImageXY(board.get(i).drawCell(cellSize),
          board.get(i).x * cellSize + (cellSize / 2) + margin,
          board.get(i).y * cellSize + (cellSize / 2) + margin);
    }
    
    // render the title of game
    s.placeImageXY(new TextImage("Flood It!", halfmargin - 10, Color.black), boardSize / 2,
        halfmargin);

    // render the number of times clicked
    // and the number of click allowed on to the board
    s.placeImageXY(new TextImage(numClicked + "/" + numChance, halfmargin - 10, 
        Color.black), 90,
        boardSize - halfmargin);

    // render the instruction on the board
    s.placeImageXY(
        new AboveAlignImage(AlignModeX.LEFT,
            new TextImage("Up Down Keys: To Change Dimension", 
                (halfmargin / 2), Color.black),
            new TextImage("Left Right Keys: To Change NumColors", 
                (halfmargin / 2), Color.black)),
        boardSize - 180, boardSize - halfmargin);

    // render the timer on the board
    s.placeImageXY(new TextImage("" + (int) time, halfmargin - 20, Color.black),
        boardSize - halfmargin * 2, halfmargin);

    // render the number of colors and gridSize on the board
    s.placeImageXY(new AboveAlignImage(AlignModeX.LEFT,
        new TextImage(dimension + " X " + dimension, halfmargin / 2, Color.black),
        new TextImage(numColors + " Colors", halfmargin / 2, 
            Color.black)), halfmargin * 2, halfmargin);
    

    
    if (numChance < numClicked) {
      s.placeImageXY(new RectangleImage(400, 100, 
          OutlineMode.SOLID, Color.black), boardSize / 2, 200);
      s.placeImageXY(new TextImage("You Lose", 50, Color.cyan), boardSize / 2, 200);
      s.placeImageXY(new TextImage("Press R to Restart", halfmargin, Color.black), 
          boardSize / 2, boardSize / 2);
    }

    if (isWin() && (numChance >= numClicked)) {
      s.placeImageXY(new RectangleImage(400, 100, 
          OutlineMode.SOLID, Color.black), boardSize / 2, 200);
      s.placeImageXY(new TextImage("You Win", 50, Color.cyan), boardSize / 2, 200);
      s.placeImageXY(new TextImage("Press R to Restart", halfmargin, Color.black), 
          boardSize / 2, boardSize / 2);
    }
    return s;
  }
  
  // to test if the user wins
  public boolean isWin() {

    Color colorOne = board.get(0).color;

    for (int i = 1; i < dimension * dimension; i++) {
      if (!board.get(i).color.equals(colorOne)) {
        return false;
      }
    }
    return true;
  }
  
  // to represent the initial condition of each game
  // used to reset the game
  public void init() {
    ArrayList<Cell> arr = new ArrayList<Cell>();

    for (int i = 0; i < dimension; i ++) {
      for (int j = 0; j < dimension; j++) {
        arr.add(new Cell(j, i, randColor(numColors), false));
      }
    }

    for (int i = 0; i < dimension * dimension; i++) {
      if (i >= dimension) {
        arr.get(i).top = arr.get(i - dimension);
      }
      if (i < dimension * (dimension - 1)) {
        arr.get(i).bottom = arr.get(i + dimension);
      }

      if (i  % dimension != 0) {
        arr.get(i).left = arr.get(i - 1);
      }

      if ((i - dimension + 1) % dimension != 0) {
        arr.get(i).right = arr.get(i + 1);
      }
    }
    this.board = arr;
    this.isflooding = false;
    this.before = board.get(0).color;
    this.after = before;
    this.numClicked = 0;
    this.time = 0.0;
  }
  
  // to reset the game if r is pressed 
  // increase the dimension with up key
  // decrease dimension with down key min = 12 max = 20
  // increase number of color with right
  // decrease number of color with left min = 2 max = 8
  public void onKeyEvent(String key) {
    if (key.equals("r") || key.equals("R") ) {
      init();
    }

    if (key.equals("up") && dimension < 20) {
      dimension = dimension + 1;
      init();
    }

    if (key.equals("down") && dimension > 2) {
      dimension = dimension - 1;
      init();
    }

    if (key.equals("left") && numColors > 2) {
      numColors = numColors - 1;
      init();
    }

    if (key.equals("right") && numColors < 8) {
      numColors = numColors + 1;
      init();
    }
  }
  
  // mouse click 
  public void onMouseClicked(Posn pos) {
    int cellsize = 20 * BOARD_SIZE / this.dimension;
    int margin = 90;

    for (int i = 0; i < dimension * dimension; i++) {
      if (Math.abs((board.get(i).x * cellsize + cellsize / 2 +
          margin) - pos.x) < cellsize / 2
          && Math.abs((board.get(i).y * cellsize + cellsize / 2 +
             margin) - pos.y) < cellsize / 2) {
        if (!isflooding && !board.get(i).color.equals(after)) {
          after = board.get(i).color;
          board.get(0).color = after;
          isflooding = true;
          numClicked = numClicked + 1;
        }
      }
    }
  }

  // on tick
  public void onTick() {
    // show the flood effect of the game 
    if (isflooding) {
      board.get(0).flooded = true;
      boolean floodingDone = true;
      ArrayList<Cell> flooded = new ArrayList<Cell>();

      for (int i = 0; i < dimension * dimension; i++) {
        if (board.get(i).flooded) {
          flooded.add(board.get(i));
        }
      }

      for (int i = 0; i < flooded.size(); i++) {
        Cell top = flooded.get(i).top;
        Cell bottom = flooded.get(i).bottom;
        Cell left = flooded.get(i).left;
        Cell right = flooded.get(i).right;

        if (top != null && top.color.equals(before)) {
          top.color = after; 
          top.flooded = true; 
          floodingDone = false;
        }

        if (bottom != null && bottom.color.equals(before)) {
          bottom.color = after; 
          bottom.flooded = true; 
          floodingDone = false;
        }

        if (left != null && left.color.equals(before)) {
          left.color = after; 
          left.flooded = true; 
          floodingDone = false;
        }

        if (right != null && right.color.equals(before)) {
          right.color = after; 
          right.flooded = true; 
          floodingDone = false;
        }
        flooded.get(i).flooded = false;
      }
      
      if (floodingDone) {
        before = after;
        isflooding = false;
      }
    }
    // count time each tick rate 
    time = time + TICKRATE;
  }
}

class ExamplesFlood {
  FloodItWorld world = new FloodItWorld(3, 3);

  Cell cell0 = new Cell(0, 0, Color.BLUE, false, null, null,
      this.cell1, this.cell2);
  Cell cell1 =  new Cell(1, 0, Color.RED, false, cell0, null,
      null, this.cell3);
  Cell cell2 = new Cell(0, 1, Color.CYAN, false, null, cell0,
      this.cell3, null);
  Cell cell3 = new Cell(1, 1, Color.BLUE, false, cell2, cell1,
      null, null);
  ArrayList<Cell> board1 = new ArrayList<Cell>(Arrays.asList(
      cell0, cell1, cell2, cell3));
  FloodItWorld world2 = new FloodItWorld(2, 2, new Random(2),
      board1, false, 0, 0.0, Color.BLUE, Color.RED);

  // initial condition
  void init() {
    cell0 = new Cell(0, 0, Color.BLUE, false, null, null,
        this.cell1, this.cell2);
    cell1 =  new Cell(1, 0, Color.RED, false, cell0, null,
        null, this.cell3);
    cell2 = new Cell(0, 1, Color.CYAN, false, null, cell0,
        cell3, null);
    cell3 = new Cell(1, 1, Color.BLUE, false, cell2, cell1,
        null, null);
    board1 = new ArrayList<Cell>(Arrays.asList(
        cell0, cell1, cell2, cell3));
    world2 = new FloodItWorld(2, 2, new Random(2),
        board1, false, 0, 0.0, Color.BLUE, Color.RED);
  }

  // test the method DrawCell
  void testDrawCell(Tester t) {
    Cell c1 = new Cell(0, 0, Color.BLACK, false);
    Cell c2 = new Cell(4, 6, Color.BLUE, false);
    Cell c3 = new Cell(2, 3, Color.CYAN, true);
    t.checkExpect(c1.drawCell(200), new RectangleImage(200, 200, OutlineMode.SOLID, Color.BLACK));
    t.checkExpect(c2.drawCell(160), new RectangleImage(160, 160, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(c3.drawCell(80), new RectangleImage(80, 80, OutlineMode.SOLID, Color.CYAN));
  }

  // test constructor to see whether cells in the list are connected
  void testConnectness(Tester t) {
    t.checkExpect(world.board.size(), 9);
    t.checkExpect(world.board.get(0).right, world.board.get(1));
    t.checkExpect(world.board.get(0).left, null);
    t.checkExpect(world.board.get(0).top, null);
    t.checkExpect(world.board.get(0).bottom, world.board.get(3));
    t.checkExpect(world.board.get(1).left, world.board.get(0));
    t.checkExpect(world.board.get(4).top, world.board.get(1));
  }

  // test Illegal Argument 
  boolean testValidNumColors(Tester t) {
    return t.checkConstructorException(new IllegalArgumentException("Maximum 8 colors"), 
        "FloodItWorld", 1, 10);
  }

  // test the method randomColor
  void testRandomColor(Tester t) {
    FloodItWorld world1 = new FloodItWorld(1, 1, new Random(2));
    t.checkExpect(world1.randColor(2), FloodItWorld.COLORLIST.get(0));
  }

  // test the method makeScene 
  void testMakeScene(Tester t) {
    init();

    WorldScene s = world2.getEmptyScene();

    int cellsize = FloodItWorld.BOARD_SIZE / world2.dimension;
    int boardSize = 20 * FloodItWorld.BOARD_SIZE + 180;
    int margin = 90;
    int numChance = (int)(world2.dimension * 1.64137 + world2.numColors * 4.16735
        - 23.54243);

    s.placeImageXY(world2.board.get(0).drawCell(cellsize), 
        world2.board.get(0).x * cellsize 
        + (cellsize / 2) + margin,
        world2.board.get(0).y * cellsize 
        + (cellsize / 2) + margin);
    s.placeImageXY(world2.board.get(1).drawCell(cellsize), 
        world2.board.get(1).x * cellsize 
        + (cellsize / 2) + margin,
        world2.board.get(1).y * cellsize 
        + (cellsize / 2) + margin);
    s.placeImageXY(world2.board.get(2).drawCell(cellsize), 
        world2.board.get(2).x * cellsize 
        + (cellsize / 2) + margin,
        world2.board.get(2).y * cellsize 
        + (cellsize / 2) + margin);
    s.placeImageXY(world2.board.get(3).drawCell(cellsize), 
        world2.board.get(3).x * cellsize 
        + (cellsize / 2) + margin,
        world2.board.get(3).y * cellsize 
        + (cellsize / 2) + margin);
    s.placeImageXY(new TextImage("Flood It!", 45 - 10, Color.black), boardSize / 2,
        45);
    s.placeImageXY(new TextImage(
        0 + "/" + numChance,  - 10, Color.black), 
        margin, boardSize - margin);
    s.placeImageXY(new AboveAlignImage(AlignModeX.LEFT,
        new TextImage("Up Down Keys: To Change Dimension", (margin / 4), Color.black),
        new TextImage("Left Right Keys: To Change NumColors", (margin / 4), Color.black)), 
        boardSize - margin * 2, boardSize - margin);
    s.placeImageXY(new TextImage("" + (int)world2.time, margin / 2 - 20, Color.black), 
        boardSize - margin * 2, margin);
    s.placeImageXY(new AboveAlignImage(AlignModeX.LEFT, 
        new TextImage(world2.dimension + " X " + world2.dimension, margin / 2, Color.black),
        new TextImage(world2.numColors + " Colors", margin / 2, Color.black)), 
        margin * 2, margin);

    t.checkExpect(world2.makeScene(), s);

    init();
    world2.board.get(0).color = Color.red;
    world2.board.get(2).color = Color.red;
    world2.board.get(3).color = Color.red;
    world2.numClicked = 2;
    s.placeImageXY(new RectangleImage(400, 100, OutlineMode.SOLID, Color.black), 300, 650);
    s.placeImageXY(new TextImage("You Win", 50, Color.cyan), 300, 650);
    s.placeImageXY(new TextImage("Press R to Restart", margin, Color.black), 
        boardSize / 2, boardSize / 2);
    t.checkExpect(world2.makeScene(), s);

    world2.numClicked = 10;
    s.placeImageXY(new RectangleImage(400, 100, OutlineMode.SOLID, Color.black), 300, 650);
    s.placeImageXY(new TextImage("You Lose", 50, Color.cyan), 300, 650);
    s.placeImageXY(new TextImage("Press R to Restart", margin, Color.black), 
        margin / 2, margin / 2);
    t.checkExpect(world2.makeScene(), s);
  }

  // test the method onTick
  void testOnTick(Tester t) {
    // test timer 
    init();
    t.checkExpect(world2.time, 0.0);
    world2.onTick();
    t.checkExpect(world2.time, 0.05);

    // test the flooding effect 
    FloodItWorld worldTest = new FloodItWorld(2, 2);

    worldTest.board.get(0).color = Color.blue;
    worldTest.board.get(1).color = Color.blue;
    worldTest.board.get(2).color = Color.red;
    worldTest.board.get(3).color = Color.blue;

    worldTest.before = Color.blue; 
    worldTest.after = Color.red;
    worldTest.isflooding = true;
    worldTest.onTick();

    t.checkExpect(worldTest.board.get(0).color, Color.blue);
    t.checkExpect(worldTest.board.get(0).flooded, false);
    t.checkExpect(worldTest.board.get(1).color, Color.red);
    t.checkExpect(worldTest.board.get(1).flooded, true);
    t.checkExpect(worldTest.board.get(2).color, Color.red);
    t.checkExpect(worldTest.board.get(2).flooded, false);
    t.checkExpect(worldTest.board.get(3).color, Color.BLUE);   

  }

  // test the method mouseClicked
  void testMouseClicked(Tester t) {
    int cellsize = 20 * FloodItWorld.BOARD_SIZE / 2;

    FloodItWorld worldTest = new FloodItWorld(2, 2);

    worldTest.board.get(0).color = Color.blue;
    worldTest.board.get(1).color = Color.blue;
    worldTest.board.get(2).color = Color.blue;
    worldTest.board.get(3).color = Color.red;
    worldTest.before = Color.blue;
    worldTest.after = Color.blue;

    worldTest.onMouseClicked(new Posn(420, 
        420));

    t.checkExpect(worldTest.isflooding, true);
    t.checkExpect(worldTest.after, Color.blue);
    t.checkExpect(worldTest.board.get(0).color, Color.blue);
    t.checkExpect(worldTest.board.get(0).flooded, true);
    t.checkExpect(worldTest.board.get(1).color, Color.blue);
    t.checkExpect(worldTest.board.get(1).flooded, false);
    t.checkExpect(worldTest.board.get(2).color, Color.blue);
    t.checkExpect(worldTest.board.get(2).flooded, false);
    t.checkExpect(worldTest.board.get(3).color, Color.red);
    t.checkExpect(worldTest.board.get(3).flooded, false);
  }

  // test the method init() 
  void testInit(Tester t) {
    init();
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.isflooding, false);
    t.checkExpect(world2.before, Color.blue);
    t.checkExpect(world2.after, Color.red);
    t.checkExpect(world2.numClicked, 0);
    t.checkExpect(world2.time, 0.0);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 2);
    world2.init();
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.isflooding, false);
    t.checkExpect(world2.before, world2.board.get(0).color);
    t.checkExpect(world2.after, world2.before);
    t.checkExpect(world2.numClicked, 0);
    t.checkExpect(world2.time, 0.0);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 2);
  }

  // test the method onKeyEvent
  void testOnKeyEvent(Tester t) {
    init();
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.isflooding, false);
    t.checkExpect(world2.before, Color.blue);
    t.checkExpect(world2.after, Color.red);
    t.checkExpect(world2.numClicked, 0);
    t.checkExpect(world2.time, 0.0);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 2);

    world2.onKeyEvent("r");
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.isflooding, false);
    t.checkExpect(world2.before, world2.board.get(0).color);
    t.checkExpect(world2.after, world2.before);
    t.checkExpect(world2.numClicked, 0);
    t.checkExpect(world2.time, 0.0);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 2);

    init();
    world2.onKeyEvent("R");
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.isflooding, false);
    t.checkExpect(world2.before, world2.board.get(0).color);
    t.checkExpect(world2.after, world2.before);
    t.checkExpect(world2.numClicked, 0);
    t.checkExpect(world2.time, 0.0);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 2);

    init();
    world2.onKeyEvent("up");
    t.checkExpect(world2.board.size(), 9);
    t.checkExpect(world2.dimension, 3);
    t.checkExpect(world2.numColors, 2);
    init();
    world2.dimension = 20;
    world2.onKeyEvent("up");
    t.checkExpect(world2.dimension, 20);
    t.checkExpect(world2.numColors, 2);

    init();
    world2.onKeyEvent("down");
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 2);
    init();
    world2.dimension = 4;
    world2.onKeyEvent("down");
    t.checkExpect(world2.board.size(), 9);
    t.checkExpect(world2.dimension, 3);
    t.checkExpect(world2.numColors, 2);

    init();
    world2.onKeyEvent("right");
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 3);
    init();
    world2.numColors = 8;
    world2.onKeyEvent("right");
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 8);

    init();
    world2.onKeyEvent("left");
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 2);
    init();
    world2.numColors = 6;
    world2.onKeyEvent("left");
    t.checkExpect(world2.board.size(), 4);
    t.checkExpect(world2.dimension, 2);
    t.checkExpect(world2.numColors, 5);
  }

  // test the method isFlooded 
  void testIsWin(Tester t) {
    init();
    t.checkExpect(world2.isWin(), false);
    world2.board.get(0).color = Color.red;
    world2.board.get(2).color = Color.red;
    world2.board.get(3).color = Color.red;
    t.checkExpect(world2.isWin(), true); 
  }

  // bigBang 
  void testBigBang(Tester t) {
    FloodItWorld w = new FloodItWorld(15, 3);
    w.bigBang(20 * FloodItWorld.BOARD_SIZE + 280, 
        20 * FloodItWorld.BOARD_SIZE + 180,
        FloodItWorld.TICKRATE);
  }
}