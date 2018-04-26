//package DialPad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* *
 * This class is responsible for generating a combination of valid lottery numbers from a given input string.
 * Specifically, given a large series of number strings, this class returns each that might be suitable for a lottery ticket pick. 
 * A valid lottery ticket must have 7 unique numbers between 1 and 59, digits must be used in order, and every digit must be used exactly once.
 * */

public class ValidLotteryNumbers {
	
	/**
	 * @return : void
	 * @param : String[] args is the list of command line arguments
	 * The main method and entry point of the program. This method accepts one or more strings as command line arguments
	 * which are passed on to the function that generates the lottery numbers
	 * */
    public static void main(String[] args){
    	//call function that generates the lottery numbers
    	if(args.length == 0)
    		System.out.println("You have not entered any strings as input");
    	else
    		generateValidLotteryNumbers(args);
    }

    /**
     * The method prints the lottery numbers for a given input string. E.g input string is "1234567" out put will be
     *  1234567->[1 2 3 4 5 6 7]
     *  IF the string has non numerics, the out put is an error message
     * @return : void	 
     * @param : String[] inputStrings is the list of input lottery numbers, as strings.
     * */
    static void generateValidLotteryNumbers(String[] inputStrings){
    	//iterate through the array of strings 
        for(String s : inputStrings){
        	//check if the input string has any nun numeric characters. If it has only numeric characters
        	//then generate the lottery numbers, otherwise ignore it
        	if(validNumericString(s)){
        		//the following asks another method to generate a list of lottery numbers from a given input
        		//where the list of numbers is exactly 7, staring at index 0 of the string
        		List<String> myResult = generate(s.trim(), 0, 7);
                if(myResult.size() > 0){
                    System.out.println(s.trim()+"->"+myResult);
                }
        	}
        	else {
        		System.out.println(s+" contains non numeric characters");
        	}
        }
    }
    
    /**
     * This function checks if the input string contains only numeric characters or not
     * @return boolean : true if only numeric characters present, false if non numeric present
     * @param String : the input string being evaluated
     * */
    static boolean validNumericString(String input){
    	char[] digits = input.toCharArray();
    	//ASCII range for characters between 1 and 9
    	for(char c : digits){
    		if(c-'0' < 0 || c-'0' > 9){
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * This is the function which is responsible for creating the DFS tree recursively
     * Each call of the function generates a list of valid lottery numbers where the size
     * of the list is determined by the input 'size'.
     * 
     * @return List<String> : the list of strings where each strings represents a valid lottery combination
     * @param String s : the original input string
     * 		  int index: the index at which the string should be evaluated for generating a combination
     *        int size : the number of lottery numbers that should be a part of the list
     * */
    static List<String> generate(String s, int index, int size){
    	
    	//the output list of lottery numbers, stored as apace separated strings
        List<String> res = new ArrayList<>();
        
        //the condition for the recursion bottoming out. The recursion bottoms out when the
        //string left to be examined string has no characters left to be examined
        String examine = s.substring(index,s.length());
        if(examine.length() == 0) {
                res.add(s.substring(index,s.length()));
            return res;
        }

        //the endingIndex determines how many characters from the string, starting at position index
        //need to be 'chopped' of to examine if they represent a valid lottery number. This step is necessary
        //to take care of strings which have zeros in them.
        int endingIndex = Math.min(index+1, s.length());
        
        //as long as the substring being examined is between 0 and 59 (inclusive), it is a candidate to be part of the list.
        //Even though substrings with 0 are invalid lottery numbers, the 0 is required to fully check for all combinations
        while(Integer.parseInt(s.substring(index, endingIndex)) >= 0 && Integer.parseInt(s.substring(index, endingIndex)) <= 59){
            endingIndex++;
            if(endingIndex > s.length())
                break;
        }
        //move back the index one position to get the correct starting position of the string 
        endingIndex--;

        //the for loot extracts 1 or more characters starting at position index to evaluate if they
        //can form a valid number
        for(int i = index; i < Math.min(endingIndex, s.length()); i++){
        	
        	//this represents the substring being examined for a candidate lottery number
            String nextCombo = s.substring(index,i+1);
            
            //the remaining string
            String remainingStr = s.substring(i+1,s.length());

            //remove leading zeros and change it back toa string
            nextCombo = String.valueOf(Integer.parseInt(nextCombo));

            //if this is the last (7th) lottery number being added and there is no more string
            //left to be examined and the string is a valid lottery number, add it to the list
            if(size-1 == 0 && remainingStr.length() == 0 && validLotteryNumber(nextCombo)){
                res.add(nextCombo);
            }

            //if the current string, represented by variable next combo is a valid lottery number
            //and there is more string to be evaluated and the size of the combination has not yet 
            //been reached, recursively call the function i.e. for to the next level of the decision tree
            else if(remainingStr.length() >= 0 && size-1 > 0  && validLotteryNumber(nextCombo)){
            	
            	//future combos is valid lottery combinations of lenght  = size-1
                List<String> futureCombos = generate(s, i+1, size-1);
                
                //the current combos formed by prefixing nextCombo (evaluated as a candidate earlier) to the list.
                //nextCombo is added only if it is not a duplicate
                List<String> currentCombos;
                
                //itertae through the list
                for(String str : futureCombos){
                    currentCombos = new ArrayList<>();
                    
                    //create a hashset, to enable O(1) expected lookup, of the space separated lottery numbers
                    Set<String> oldNumbers = new HashSet<>(Arrays.asList(str.split(" ")));
                    
                    //add nextCombo only if it will not create a duplicate values and the number of
                    //lottery combinations does not exceed size
                    if(!oldNumbers.contains(nextCombo) && (str.split(" ").length+1) == size) {
                        if (!(str.trim().length() == 0)) {
                            currentCombos.add(nextCombo+" "+str);
                        }
                        else{
                            currentCombos.add(nextCombo);
                        }
                        res.addAll(currentCombos);
                    }
                }
            }
        }

        return res;
    }

    /**
     * This function checks to see if the numeric values of the string is between\
     * 1 and 59 (inclusive) and the leading character is not 0
     * 
     * @ return boolean : false is values < 0 or > 60 or if leading character is 0
     * @param String : string being evaluated
     * */
    static boolean validLotteryNumber(String s){
        if(!(Integer.parseInt(s) <= 59 && Integer.parseInt(s) > 0))
            return false;

        if(s.length() == 2){
            if(s.charAt(0) == '0')
                return false;
        }

        return true;
    }
}
