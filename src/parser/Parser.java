package parser;

/**
 * @author Victor Yuste Vara
 * Main class
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class Parser {
	
	/**
	 * Attributes
	 */

	private ArrayList<String> translation;
	private String statement;
	private String thenLabel;
	int currentLabel;
	int returnTemporalLabel;
	private String elseLabel;
	private String endLabel;
	private int nextLabelAt;
	private ArrayList<String> tokens;
	private ArrayList<String> tokensComplete;
	int endAndBlock;
	private String expression;
	ArrayList<String> temporals;

	
	/**
	 * Public constructor
	 */
	public Parser(String expression) {
		
		this.currentLabel = 4;
		this.expression = expression;
		this.translation = new ArrayList<>();
		this.statement = ""; 
		this.thenLabel = "L1: " + expression.substring(expression.indexOf("then") + 4, expression.indexOf("else")).trim() + " goto L3";
		this.elseLabel = "L2: " + expression.substring(expression.indexOf("else") + 4).trim();	
		this.endLabel = "L3: ";
		this.nextLabelAt = 0;
		this.endAndBlock = 0;
		this.returnTemporalLabel = 0;

		
	}
	
	/**
	 * Finds the first symbol String after a given one
	 * @param tokens ArrayList of tokens
	 * @param startSymbol Starting point of the search
	 * @param searchedSymbol Symbol we'll try to find
	 */
	public int firstSymbolAfter(ArrayList<String> tokens, String startSymbol, String searchedSymbol) {
		
		int index;
		
		for(int i = 0; i < tokens.size(); i++) {
			if(tokens.get(i).equals(startSymbol)) { 			//We get the first occurrence of the startSymbol
				for(int j = i; j < tokens.size(); j++) { 		//After we get it, We look for the first occurrence of the Searched Symbol
					if(tokens.get(j).equals(searchedSymbol)) {
						index = j;
						return index;							//We return it if found, or '-1' if not
					}
					/*else {
						return -1;		//Old version
					}*/
				}
				return -1;
			}
		}
		
		return -1;
	}
	
	/**
	 * Finds the index where a given AND block ends
	 * @param tokens ArrayList of tokens
	 */
	public int findEndAndBlock(ArrayList<String> tokens) {
		
		int index = 0;

		for(int i = 0; i < tokens.size(); i++) {
			if(tokens.get(i).equals("&&")) {
				index = i;
				if(((i+2) < tokens.size()) && tokens.get(index+2).equals("||")) {
					return index;
				}
			}
		}
		
		return index;
	}
	
	/**
	 * Translates the expression stored in the parser
	 * @return and ArrayList with the translation
	 */
	public ArrayList<String> translate() {
		
		String editedExpression = expression.substring(4, expression.indexOf("then")-2);
		
		ArrayList<String> temporals = new ArrayList<>();
		
		//We remove parenthsesis
		while(hasParenthesis(editedExpression)) {
			
			String extracted = extractParenthesis(editedExpression);
			temporals.add(extracted);
			String target = "(" + extracted + ")";
			String replacement = "temporal_" + temporals.indexOf(temporals.getLast());
			editedExpression = editedExpression.replace(target, replacement);
			
		}
		
		tokens = new ArrayList<>(Arrays.asList(editedExpression.split(" ")));
		tokensComplete = new ArrayList<>(Arrays.asList(editedExpression.split(" ")));
		
		nextLabelAt = tokens.indexOf("||")+1; if(tokens.indexOf("||") == -1) { nextLabelAt = 999999; }
		
		translateFirstExpression(editedExpression);
		
		ListIterator<String> it = tokens.listIterator();
		
		while(it.hasNext()) {
			
			int nextAnd = tokens.indexOf("&&"); if(tokens.indexOf("&&") == -1) { nextAnd = 9999999; }
			int nextOr = tokens.indexOf("||"); if(tokens.indexOf("||") == -1) { nextOr = 9999999; }
			
			//If we are at the next element
			if(tokens.size() < 2) {
				
				String last = it.next();
				
				if(last.startsWith("temporal ")) {
					
				}
				
				if(tokensComplete.get(tokensComplete.size()-2).equals("||")) {
					
					if(nextLabelAt == 0) {
						statement = "L" + currentLabel + ": " + "ifFalse " + last + " goto " + elseLabel.substring(0, 2);
						translation.add(statement);
						currentLabel++;
						it.remove();
						continue;
					} else {
						statement = "ifFalse " + last + " goto " + elseLabel.substring(0, 2);
						translation.add(statement);
						currentLabel++;
						it.remove();
						continue;
					}
			
				} else {
					
					statement = "ifFalse " + last + " goto " + elseLabel.substring(0, 2);
					translation.add(statement);
					it.remove();
					continue;
					
				}
				
				
			}
			
			//We are in an AND expression
			else if(nextOr < 0 || nextAnd < nextOr || endAndBlock == 0) {
				
				String left = it.next();
				String operator = "";
				if(it.hasNext()) {operator = it.next();}
				
				/*if(left.contains("temporal")) {
					
					int temporalNumber = Integer.parseInt(left.replace("temporal", ""));   
					translateTemporal(temporalNumber);
					
					//We consume the tokens
					it.remove();
					it.previous();
					it.remove();
					
					nextLabelAt = tokens.indexOf("||")+1; if(tokens.indexOf("||") == -1) { nextLabelAt = 99999; }
					endAndBlock = tokens.indexOf("||")-1; if(tokens.indexOf("||") == -1) { endAndBlock = tokens.size()-1; }
					continue;
	
					
				}*/
				
				if(nextLabelAt == 0) {
					statement = "L" + currentLabel + ": " + "ifFalse " + left + " goto L" + ++currentLabel;
					translation.add(statement);
				} else {
					statement = "ifFalse " + left + " goto L" + currentLabel;
					translation.add(statement);
				}
				
				if(endAndBlock == 0) {
					statement = "goto " + thenLabel.substring(0, 2);
					translation.add(statement);
					
					//We consume the tokens
					it.remove();
					it.previous();
					it.remove();
				
					endAndBlock = findEndAndBlock(tokens)+1;
					nextLabelAt = 0;
					continue;
				}
				
				//We consume the tokens
				it.remove();
				it.previous();
				it.remove();			
				
				nextLabelAt = tokens.indexOf("||")+1; if(tokens.indexOf("||") == -1) { nextLabelAt = 99999; }
				endAndBlock = tokens.indexOf("||")-1; if(tokens.indexOf("||") == -1) { endAndBlock = tokens.size()-1; }
				
			//Next there is an OR expression
			} else {
				
				//Check nextlabelat
				String left = it.next();
				String operator = "";
				if(it.hasNext()) {operator = it.next();}
					
				if(tokens.get(1).equals("||")) {
					
					if(nextLabelAt == 0) {
						statement = "L" + currentLabel + ": " + "if " + left + " goto " + thenLabel.substring(0, 2);
						currentLabel++;
					} 
					
					nextLabelAt = firstSymbolAfter(tokens, "&&", "||");
					
				} else {
					
					if(nextLabelAt == 0) {
						statement = "L" + currentLabel + ": " + "ifFalse " + left + " goto L" + ++currentLabel;
					} 
					
				}
							
				translation.add(statement);
				
				//We consume the tokens
				it.remove();
				it.previous();
				it.remove();	
				
			}
		}
		
		translation.add(thenLabel);
		translation.add(elseLabel);
		translation.add(endLabel);
		fixEndLabels(currentLabel);
		
		if(temporals.size() > 0) {	
			translation.add("");
			translation.add("Temporals: ");
			for(String s : temporals) {
				String add = "temporal " + temporals.indexOf(s) + ": " + s;
				translation.add(add);
			}
		}
		
		return translation;
	}
	
	/**
	 * Translates the first 3 tokens of any expression
	 * @param expression Expression that needs translating
	 */
	public void translateFirstExpression(String expression) {
		
		
		int nextAnd = tokens.indexOf("&&"); if(tokens.indexOf("&&") == -1) { nextAnd = 9999999; }
		int nextOr = tokens.indexOf("||"); if(tokens.indexOf("||") == -1) { nextOr = 9999999; }
		
		//We are in an AND expression
		if(nextAnd < nextOr) {
			
			String left = tokens.get(0);
			String operator = tokens.get(1);
			String right = tokens.get(2);
			
			statement = "ifFalse " + left + " goto L" + currentLabel;
			translation.add(statement);
			statement = "ifFalse " + right + " goto L" + currentLabel;
			translation.add(statement);
			
			if(nextLabelAt == nextAnd + 3) {
				statement = "goto " + thenLabel.substring(0, 2);
				translation.add(statement);
				
				//We consume the tokens
				tokens.remove(0);
				tokens.remove(0);
				tokens.remove(0);
				if(tokens.size() > 0) tokens.remove(0);
				endAndBlock = tokens.indexOf("||")-1; if(tokens.indexOf("||") == -1) { endAndBlock = tokens.size()-1; }
				nextLabelAt = 0;
				return;
				
			}
			
			//We consume the tokens
			tokens.remove(0);
			tokens.remove(0);
			tokens.remove(0);
			if(tokens.size() > 0) tokens.remove(0);
			nextLabelAt = tokens.indexOf("||")+1; if(tokens.indexOf("||") == -1) { nextLabelAt = 9999999; }
			endAndBlock = tokens.indexOf("||")-1; if(tokens.indexOf("||") == -1) { endAndBlock = tokens.size()-1; }
			
			
		//We are in an OR expression
		} else {
			
			String left = tokens.get(0);
			String operator = tokens.get(1);
			String right = tokens.get(2);

			statement = "if " + left + " goto " + thenLabel.substring(0, 2);
			translation.add(statement);
			tokens.remove(0);
			tokens.remove(0);
			
			//If there is another OR after this one
			if((tokens.size() > 1) && tokens.get(1).equals("||")) {
				statement = "if " + right + " goto " + thenLabel.substring(0, 2);
				translation.add(statement);
			} else {
				statement = "ifFalse " + right + " goto L" + currentLabel;
				translation.add(statement);
			}
			
			tokens.remove(0);
			if(tokens.size() > 0) tokens.remove(0);

			nextLabelAt = tokens.indexOf("||")+1; if(tokens.indexOf("||") == -1) { nextLabelAt = 999999; }
			endAndBlock = tokens.indexOf("||")-1; if(tokens.indexOf("||") == -1) { endAndBlock = tokens.size()-1; }
		
		}
	}
	
	/**
	 * Translates a given temporary name (temporal)
	 * @param temporalNumber Number of the temporary name
	 */
	public void translateTemporal(int temporalNumber) {
		
		String expression = temporals.get(temporalNumber);
		temporals.remove(temporalNumber);
		
		ArrayList<String> temporalTokens = new ArrayList<>(Arrays.asList(expression.split(" ")));
		ArrayList<String> temporalTokensComplete = new ArrayList<>(Arrays.asList(expression.split(" ")));
	
		int nextAnd = temporalTokens.indexOf("&&"); if(temporalTokens.indexOf("&&") == -1) { nextAnd = 9999999; }
		int nextOr = temporalTokens.indexOf("||"); if(temporalTokens.indexOf("||") == -1) { nextOr = 9999999; }

		//We are in an AND expression
		if(nextAnd < nextOr) {
			
			String left = temporalTokens.get(0);
			String operator = temporalTokens.get(1);
			String right = temporalTokens.get(2);
			
			if(nextLabelAt == 0) {
				statement = "L" + currentLabel + ": " + "ifFalse " + left + " goto L" + ++currentLabel;
				translation.add(statement);
				statement = "ifFalse " + right + " goto L" + currentLabel;
				translation.add(statement);
			} else {
				statement = "ifFalse " + left + " goto L" + currentLabel;
				translation.add(statement);				
				statement = "ifFalse " + right + " goto L" + currentLabel;
				translation.add(statement);				
			}			
			
			nextLabelAt = temporalTokens.indexOf("||")+1; if(temporalTokens.indexOf("||") == -1) { nextLabelAt = 9999999; }
			endAndBlock = temporalTokens.indexOf("||")-1; if(temporalTokens.indexOf("||") == -1) { endAndBlock = temporalTokens.size()-1; }
		
			
			if(nextLabelAt == nextAnd + 3) {
				nextLabelAt = 0;				
			} 
			
			//We consume the tokens
			temporalTokens.remove(0);
			temporalTokens.remove(0);
			temporalTokens.remove(0);
			if(temporalTokens.size() > 0)  temporalTokens.remove(0);
			
			
		//We are in an OR expression
		} else {
			
			String left = temporalTokens.get(0);
			String operator = temporalTokens.get(1);
			String right = temporalTokens.get(2);

			if(nextLabelAt == 0) {
				statement = "L" + currentLabel + ": " + "if " + left + " goto R" + ++currentLabel;
				translation.add(statement);
			}else {
				statement = "if " + left + " goto R" + ++currentLabel;
				translation.add(statement);
			}
		
			temporalTokens.remove(0);
			temporalTokens.remove(0);
			
			//If there is another OR after this one
			if(tokens.get(1).equals("||")) {
				
			} else {
				statement = "ifFalse " + right + " goto L" + currentLabel;
				translation.add(statement);
			}
			
			temporalTokens.remove(0);
			if(temporalTokens.size() > 0) temporalTokens.remove(0);

			nextLabelAt = temporalTokens.indexOf("||")+1; if(temporalTokens.indexOf("||") == -1) { nextLabelAt = 999999; }
			endAndBlock = temporalTokens.indexOf("||")-1; if(temporalTokens.indexOf("||") == -1) { endAndBlock = temporalTokens.size()-1; }
			
		}
		
		ListIterator<String> it = temporalTokens.listIterator();
		
		while(it.hasNext()) {
			
			nextAnd = temporalTokens.indexOf("&&"); if(temporalTokens.indexOf("&&") == -1) { nextAnd = 9999999; }
			nextOr = temporalTokens.indexOf("||"); if(temporalTokens.indexOf("||") == -1) { nextOr = 9999999; }
			
			//If we are at the next element
			if(temporalTokens.size() < 2) {
				String nextSymbol = tokens.get(temporalNumber+1);
				
				//The expressions continues after the temporal
				if(nextSymbol != null) {
					
					String last = it.next();
							
					//It's and OR expression
					if(nextSymbol.equals("||")) {
						
						//Followed by AND expression
						if(nextSymbol.equals("&&")) {
							
							if(nextLabelAt == 0) {
								statement = "L" + currentLabel + ": " + "ifFalse " + last + " goto L" + ++currentLabel;
								translation.add(statement);
								it.remove();
								continue;
							} else {
								statement = "ifFalse " + last + " goto L" + currentLabel;
								translation.add(statement);
								it.remove();
								continue;
							}
							
						}
						
						//Followed by OR expression
						else {
							
							if(nextLabelAt == 0) {
								statement = "L" + currentLabel + ": " + "if " + last + " goto L" + thenLabel.substring(0, 2);
								translation.add(statement);
								currentLabel++;
								it.remove();
								continue;
							} else {
								statement = "ifFalse " + last + " goto " + thenLabel.substring(0, 2);;
								translation.add(statement);
								it.remove();
								continue;
							}
							
						}
										
					//It's and AND expression
					} else {
						//Followed by AND expression
						if(nextSymbol.equals("&&")) {
							
							statement = "ifFalse " + last + " goto L" + currentLabel;
							translation.add(statement);
							it.remove();
							continue;
							
						}
						
						//Followed by OR expression
						else {
											
							statement = "if " + last + " goto " + thenLabel.substring(0, 2);;
							translation.add(statement);
							it.remove();
							continue;
												
						}
						
					}
				} 
				
				//The expression ends with the temporal
				else {
					
					String last = it.next();
					
					//It's and OR expression
					if(temporalTokensComplete.get(temporalTokensComplete.size()-2).equals("||")) {
						
						if(nextLabelAt == 0) {
							statement = "L" + currentLabel + ": " + "ifFalse " + last + " goto " + elseLabel.substring(0, 2);
							translation.add(statement);
							currentLabel++;
							it.remove();
							continue;
						} else {
							statement = "ifFalse " + last + " goto " + elseLabel.substring(0, 2);
							translation.add(statement);
							it.remove();
							continue;
						}
				
					//It's and AND expression
					} else {
						
						statement = "ifFalse " + last + " goto " + elseLabel.substring(0, 2);
						translation.add(statement);
						it.remove();
						continue;
						
					}
					
				}
				
				
				
				
				
			}
			
			//We are in an AND expression
			else if(nextOr < 0 || nextAnd < nextOr || endAndBlock == 0) {
				
				String left = it.next();
				String operator = "";
				if(it.hasNext()) {operator = it.next();}
				
				if(nextLabelAt == 0) {
					statement = "L" + currentLabel + ": " + "ifFalse " + left + " goto L" + ++currentLabel;
					translation.add(statement);
				} else {
					statement = "ifFalse " + left + " goto L" + currentLabel;
					translation.add(statement);
				}
				
				if(endAndBlock == 0) {
					statement = "goto " + thenLabel.substring(0, 2);
					translation.add(statement);
					
					//We consume the tokens
					it.remove();
					it.previous();
					it.remove();
				
					endAndBlock = findEndAndBlock(temporalTokens)+1;
					nextLabelAt = 0;
					continue;
				}
				
				//We consume the tokens
				it.remove();
				it.previous();
				it.remove();			
				
				nextLabelAt = temporalTokens.indexOf("||")+1; if(nextLabelAt == 0) { nextLabelAt = 99999; }
				endAndBlock = temporalTokens.indexOf("||")-1; if(temporalTokens.indexOf("||") == -1) { endAndBlock = temporalTokens.size()-1; }
				
			//Next there is an OR expression
			} else {
				
				//Check nextlabelat
				String left = it.next();
				String operator = "";
				if(it.hasNext()) {operator = it.next();}
					
				if(temporalTokens.get(1).equals("||")) {
					
					if(nextLabelAt == 0) {
						statement = "L" + currentLabel + ": " + "if " + left + " goto " + thenLabel.substring(0, 2);
						currentLabel++;
					} 
					
					nextLabelAt = firstSymbolAfter(temporalTokens, "&&", "||");
					
				} else {
					
					if(nextLabelAt == 0) {
						statement = "L" + currentLabel + ": " + "ifFalse " + left + " goto L" + ++currentLabel;
					} 
					
				}
							
				translation.add(statement);
				
				//We consume the tokens
				it.remove();
				it.previous();
				it.remove();	
				
			}
		}
	}

	/**
	 * Finds the inner-most parenthesis-bound subexpression in an expression
	 * @param expression The expression to work with
	 * @return the String with the subexpression
	 */
	public String extractParenthesis(String expression) {
		
		int startIndex = 0;
		int endIndex = 0;
		String subExpression = "";	
		if(expression.contains("(")) {			//We find the inner-most parenthesis
			forLoop: for(int i = 0; i < expression.length(); i++) {
				if(expression.charAt(i) == '(') {
					startIndex = i;
				} 
				
				if(expression.charAt(i) == ')') {
					endIndex = i;
					break forLoop;
				} 
			}
		}
		subExpression = expression.substring(startIndex+1, endIndex);
		return subExpression;
	}
	
	/**
	 * Checks whether a String contains parenthesis
	 * @param s String to check 
	 * @return boolean with the answer
	 */
	public boolean hasParenthesis(String s) {
		
		if(s.contains("(")) {
			return true;
		}
			
		return false;
		
	}
	
	/**
	 * Creates a new ArrayList from a given one
	 * @param original ArrayList of copy
	 * @return the copy of the ArrayList
	 */
	public ArrayList<String> cloneList(ArrayList<String> original) {
        
		ArrayList<String> copy = new ArrayList<>();
		
        for (String s : original) {
            copy.add(s); // Usar el constructor de copia
        }
        return copy;
        
    }
	
	/**
	 * Changes the last labels if they point to non-existent labels
	 * @param currentLabel Number of the last assigned label
	 */
	public void fixEndLabels(int currentLabel) {
		
		boolean validLabel = false;
		String expectedLabel = "L" + currentLabel + ": ";
		
		for(int i = 0; i < translation.size(); i++) {			
			if(translation.contains(expectedLabel)) {		//The label has never been associated
				validLabel = true;
			}					
		}
		
		String target = "L" + currentLabel;
		
		if(!validLabel) {
			for(int i = 0; i < translation.size(); i++) {			
				String replacement = translation.get(i).replace(target, elseLabel.substring(0, 2));
				translation.set(i, replacement);
			}
		}
		
	
	}
}


