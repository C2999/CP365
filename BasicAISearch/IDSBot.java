import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

class StackNode {

  public SlidingBoard board;
  public ArrayList<SlidingMove> path;

  public StackNode(SlidingBoard b, ArrayList<SlidingMove> p) {
    board = b;
    path = p;
  }
}


class IDSBot extends SlidingPlayer {

    ArrayList<SlidingMove> path;
    int move_number = -1;

    // The constructor gets the initial board
    public IDSBot(SlidingBoard _sb) {
        super(_sb);
        path = findPath(_sb);
    }

    public ArrayList<SlidingMove> findPath(SlidingBoard board) {
      HashSet<String> seen = new HashSet<String>(); //set of seen nodes so no identical games are checked
      LinkedList<StackNode> stack = new LinkedList<StackNode>();
      StackNode currNode = new StackNode(board, new ArrayList<SlidingMove>());
      int depthLim = 1;
      while (!currNode.board.isSolved()) //keeps checking if the board is not solved
      {
        ArrayList<SlidingMove> legal = currNode.board.getLegalMoves();
        for (SlidingMove move : legal) 
        {
          SlidingBoard childBoard = new SlidingBoard(currNode.board.size);
          childBoard.setBoard(currNode.board); //creates deep clone and performs the move
          childBoard.doMove(move);
          if (!seen.contains(childBoard.toString()) && (currNode.path.size() <= depthLim)) 
          { //adds child node to the stack unless the depth limit is reached
            seen.add(childBoard.toString());
            ArrayList<SlidingMove> childPath = (ArrayList<SlidingMove>)currNode.path.clone();
            childPath.add(move);
            stack.add(new StackNode(childBoard, childPath));
          }

        }
        if(stack.size() != 0) currNode = stack.pop(); //pops the stack if there is an item to pop
        else 
        {
          currNode = new StackNode(board, new ArrayList<SlidingMove>());
          seen.clear();
          depthLim++;
        }
        
      }
      System.out.println(currNode.board);
      return currNode.path;
    }

    // Perform a single move based on the current given board state
    public SlidingMove makeMove(SlidingBoard board) {
      move_number++;
      return path.get(move_number);
    }
}