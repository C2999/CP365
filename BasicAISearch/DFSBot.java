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


class DFSBot extends SlidingPlayer {

    ArrayList<SlidingMove> path;
    int move_number = -1;

    // The constructor gets the initial board
    public DFSBot(SlidingBoard _sb) {
        super(_sb);
        path = findPath(_sb);
    }

    public ArrayList<SlidingMove> findPath(SlidingBoard board) {
      HashSet<String> seen = new HashSet<String>(); //prevents identical states from being checked
      LinkedList<StackNode> stack = new LinkedList<StackNode>(); //stack for keeping track of the next ndoe to visit
      StackNode currNode = new StackNode(board, new ArrayList<SlidingMove>()); //current node being checked

      while (!currNode.board.isSolved()) { //continue checking if the game is not solved
        ArrayList<SlidingMove> legal = currNode.board.getLegalMoves();
        for (SlidingMove move : legal) { //for all legal moves
          SlidingBoard childBoard = new SlidingBoard(currNode.board.size);
          childBoard.setBoard(currNode.board); //deep clone of board
          childBoard.doMove(move);
          if (!seen.contains(childBoard.toString())) { //checks if state has already been seen
            seen.add(childBoard.toString()); //adds states to string representation of seen states
            ArrayList<SlidingMove> childPath = (ArrayList<SlidingMove>)currNode.path.clone(); //deep clone of path
            childPath.add(move);
            stack.add(new StackNode(childBoard, childPath)); //adds move to stack
          }
          else {
             System.out.println("Already seen!");
           }
        }
        currNode = stack.pop(); //pops from the stack and checks the most recent addition to the stack
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