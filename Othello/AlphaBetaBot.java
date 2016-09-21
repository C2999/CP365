
import java.util.ArrayList;
import java.lang.Math;

public class AlphaBetaBot extends OthelloPlayer {
    
    public AlphaBetaBot(Integer _color) {
        super(_color);
    }

    /*public int boardScore( OthelloBoard b, int playerColor )
    {
        return b.getBoardScore();
        
    }*/

    //MiniMax algorithm with alpha beta pruning
    public int ABMMScore(int alpha, int beta, int depthLength, int player, OthelloBoard b)
    {

        if( (depthLength == 0) || b.gameOver() ) //if the recursive calls have hit the depth limit or the game is over
        {
            return b.getBoardScore(); //give the board score
        }
        else if( player == 1 ) //max Case
        {
            int bestScore = -1000; //default low score
            for(int i = 0; i <  b.legalMoves(playerColor).size(); i++) //for length of legal moves
            {
                OthelloBoard currBoard = new OthelloBoard(b.size, false); //deep clone of board
                currBoard = cloneBoard(b, currBoard);
                currBoard.addPiece(b.legalMoves(playerColor).get(i)); //perform move on deep clone
                int score = ABMMScore(bestScore, beta, depthLength - 1, 2, currBoard); //check move through recursive call
                bestScore = Math.max( score, bestScore ); //update alpha after returning the score
                if( beta <= bestScore ) //if beta is no longer Max+, prune and stop the recursive calls
                {
                    return bestScore;
                }
            }
            return bestScore;
        }
        else if( player == 2 )//min case
        {
            //same concept, except looking for lowest beta and pruning if alpha is no longer Min
            int bestScore = 1000;
            for(int i = 0; i <  b.legalMoves(playerColor).size(); i++)
            {
                OthelloBoard currBoard = new OthelloBoard(b.size, false);
                currBoard = cloneBoard(b, currBoard);
                currBoard.addPiece(b.legalMoves(playerColor).get(i));
                int score = ABMMScore(bestScore, beta, depthLength - 1, 1, currBoard);
                bestScore = Math.min( score, bestScore );
                if( bestScore <= alpha )
                {
                    return bestScore;
                }
            }
            return bestScore;
        }
        return 10101; //default return
        
    }
    
    public OthelloMove makeMove(OthelloBoard b) 
    {

        int highestScore = -1001;
        OthelloMove highestMove = b.legalMoves(playerColor).get(0); //initialize the highest move
        int currScore = highestScore;
        for( int j = 0; j < b.legalMoves(playerColor).size(); j++ )
        {
            OthelloBoard currBoard = new OthelloBoard(b.size, false); //deep clone of board
            currBoard = cloneBoard(b, currBoard); 
            currBoard.addPiece(b.legalMoves(playerColor).get(j));
            //find the best score by checking the game states 7 layers deep
            currScore = ABMMScore(Integer.MIN_VALUE, Integer.MAX_VALUE, 7, playerColor, currBoard);
            if( playerColor == 2 ) //look for min value if player two
            {
                 if( highestScore >= currScore  ) //update the best score
                {
                    highestScore = currScore; 
                    highestMove = b.legalMoves(playerColor).get(j);
                }
            }
            else if( playerColor == 1 ) //look for max value if player 1
            {
                 if( highestScore <= currScore  )
                {
                    highestScore = currScore; 
                    highestMove = b.legalMoves(playerColor).get(j);
                }
            }
           
        }
        return highestMove;
    }


    public OthelloBoard cloneBoard( OthelloBoard b, OthelloBoard newB ) 
    {
        //method for making deep copy
        for(int i = 0; i < b.size; i++)
        {
            for(int j = 0; j < b.size; j++)
            {
                newB.board[i][j] = b.board[i][j];
            }
        }

        return newB;
    }



}