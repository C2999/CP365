import java.util.HashMap;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

class AStarNode implements Comparable {

  public SlidingBoard board;
  public ArrayList<SlidingMove> path;
  public int moveNum = 0;
  public int minMove;
  public int cost = 0;

  public AStarNode(SlidingBoard b, ArrayList<SlidingMove> p, int _min_move, int _move_num) {
    board = b; //special node keeping track of min move and move number
    path = p;
    moveNum = _move_num;
    minMove = _min_move; //based on heuristic for determining the min # of moves needed to reach goal
    cost = moveNum + minMove;


  }

  public int compareTo(Object otherNode) {
    AStarNode other = (AStarNode) otherNode;
    return new Double(cost).compareTo(new Double(other.cost));
  }

  public String toString() {
    return "ASTARNODE: " + path + "\t" + cost + "\n";
  }

}


class ASSBot extends SlidingPlayer {

    ArrayList<SlidingMove> path;
    int move_number = -1;

    // The constructor gets the initial board
    public ASSBot(SlidingBoard _sb) 
    {
        super(_sb);
        path = findPath(_sb);

    }

    public ArrayList<SlidingMove> findPath(SlidingBoard board) 
    {
      PriorityQueue<AStarNode> q = new PriorityQueue<>(); //priority queue for sorting and having the best node on top
      HashSet<String> seen = new HashSet<String>(); //for keeping track of seen states
      int min_move = findMinMove(board);
      AStarNode currNode = new AStarNode(board, new ArrayList<SlidingMove>(), min_move, 0);
      q.add(currNode); //adds current node to queue

      while(!currNode.board.isSolved())
      {
        System.out.println(currNode.board);
        currNode = q.poll(); //takes highest priority node
        ArrayList<SlidingMove> legal = currNode.board.getLegalMoves();
        for(SlidingMove move : legal)
        {
          SlidingBoard childBoard = new SlidingBoard(currNode.board.size);
          childBoard.setBoard(currNode.board); //deep clone
          childBoard.doMove(move);
          if (!seen.contains(childBoard.toString()))  //checks child board if hasn't been seen
          {
            seen.add(childBoard.toString());
            ArrayList<SlidingMove> childPath = (ArrayList<SlidingMove>)currNode.path.clone();
            childPath.add(move);
            int _min_move = findMinMove(childBoard);
            AStarNode newNode = new AStarNode(childBoard, childPath, _min_move, currNode.moveNum + 1 );
            q.add(newNode);
          }
        }
      }
      return currNode.path;

    }




    public int findMinMove(SlidingBoard board) 
    {//calculates manhattan distance 
        int expected = 0;
        int min_move = 0;
        for(int r = 0; r < board.size; r++)
        {
          for(int c = 0; c < board.size; c++)
          {
              min_move += Math.abs((board.board[r][c]%board.size)-c); //Zack's code for one line manhattan distance
              min_move += Math.abs((board.board[r][c]/board.size)-r);
          }          
        }
        return min_move - 1; //makes admissible
    }

    // Perform a single move based on the current given board state
    public SlidingMove makeMove(SlidingBoard board) 
    {
      move_number++;
      return path.get(move_number);
    }
}



