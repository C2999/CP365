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


class BFSBot extends SlidingPlayer {

    ArrayList<SlidingMove> path;
    int move_number = -1;

    // The constructor gets the initial board
    public BFSBot(SlidingBoard _sb) {
        super(_sb);
        path = findPath(_sb);
    }

    public ArrayList<SlidingMove> findPath(SlidingBoard board) 
    {
      System.out.println(board);
      HashSet<String> seen = new HashSet<String>();
      LinkedList<StackNode> queue = new LinkedList<StackNode>();
      StackNode currNode = new StackNode(board, new ArrayList<SlidingMove>());

      while (!currNode.board.isSolved()) 
      {
        ArrayList<SlidingMove> legal = currNode.board.getLegalMoves();

  		  for (SlidingMove move : legal) 
  		  {
  		    	SlidingBoard childBoard = new SlidingBoard(currNode.board.size);
  	        childBoard.setBoard(currNode.board);
  	        childBoard.doMove(move);
  	        if (!seen.contains(childBoard.toString())) 
            {
              seen.add(childBoard.toString());
              ArrayList<SlidingMove> childPath = (ArrayList<SlidingMove>)currNode.path.clone();
              childPath.add(move);
              queue.addLast(new StackNode(childBoard, childPath));
            }		
  		  }
  		  currNode = queue.removeFirst();

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