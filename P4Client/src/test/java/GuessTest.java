import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

class GuessTest {

	int MaxLetters =12; 
	GameInfo a;
	GameInfo b;
	GameInfo c;
	ArrayList<String> answers; 
	@BeforeEach
	void init() {
		answers = new ArrayList<String>(3);
		answers.add("banna");
		answers.add("nevada");
		answers.add("hamburger");
		
		
		 a = new GameInfo();
		a.playerID =1;
		 b = new GameInfo();
		b.playerID =2;
		c = new GameInfo();
		c.playerID =3;
		
	}
	
	@Test
	void testMid() {
		int[] a= new int[2];
 		updateLaters(9,a);
		assertEquals(a[0],2);
		assertEquals(a[1],11);
		
		updateLaters(6,a);
		assertEquals(a[0],3);
		assertEquals(a[1],9);
	 }
	@Test 
	void checkWord_oneWord() {
		a.letter='n';
		b.letter='v';
		c.letter='e';
		assertEquals(sendResultToClient(a.playerID,a.message,a).message," 3 4") ;
		assertEquals(sendResultToClient(b.playerID,b.message,b).message," 3") ;
		assertEquals(sendResultToClient(c.playerID,c.message,c).message," 8");
		a.letter='v';
		b.letter='e';
		c.letter='m';
		assertEquals(sendResultToClient(a.playerID,a.message,a).message,"") ;
		assertEquals(sendResultToClient(b.playerID,b.message,b).message," 2") ;
		assertEquals(sendResultToClient(c.playerID,c.message,c).message," 3");
	
	}
	@Test 
	void checkWord_moreWord(){
		a.letter='a';
		b.letter='a';
		c.letter='a';
		assertEquals(sendResultToClient(a.playerID,a.message,a).message," 2 5") ;
		assertEquals(sendResultToClient(b.playerID,b.message,b).message," 4 6") ;
		assertEquals(sendResultToClient(c.playerID,c.message,c).message," 2");
		a.letter='b';
		b.letter='b';
		c.letter='b';
		assertEquals(sendResultToClient(a.playerID,a.message,a).message," 1") ;
		assertEquals(sendResultToClient(b.playerID,b.message,b).message,"") ;
		assertEquals(sendResultToClient(c.playerID,c.message,c).message," 4");
	}
	@Test
	void comboTest1() {
		int[] pos= new int[2];
		String s =""; 
		String exp ="____________";
		updateLaters(answers.get(1).length(),pos);
		for(int i=0;i<MaxLetters;i++) {
			if(i >=pos[0] && i <pos[1]) {
				s+= answers.get(1).substring(i-pos[0],i-pos[0]+1);
		
			}
			else {
				s+="_";
			}
		}
		assertTrue(s.equals("___nevada___"));
		b.letter ='a';
		System.out.println(pos[0]);
		
		GameInfo z= sendResultToClient(b.playerID,b.message,b); 	
		 System.out.println(z.positions.get(0));
		System.out.println(z.positions.get(1));
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		
		assertTrue(exp.equals("______a_a___"));
		b.letter ='d';
		z = sendResultToClient(b.playerID,b.message,b); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		assertTrue(exp.equals("______ada___"));
		b.letter ='e';
		z=sendResultToClient(b.playerID,b.message,b); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		assertTrue(exp.equals("____e_ada___"));
		b.letter ='n';
		z=sendResultToClient(b.playerID,b.message,b); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		assertTrue(exp.equals("___ne_ada___"));
		
	}
	@Test
	void comboTest2() {
		
		int[] pos= new int[2];
		String s ="";
		String exp ="____________";
		updateLaters(answers.get(2).length(),pos);
		for(int i=0;i<MaxLetters;i++) {
			if(i >=pos[0] && i <pos[1]) {
				s+= answers.get(2).substring(i-pos[0],i-pos[0]+1);
		
			}
			else {
				s+="_";
			}
		}
		assertTrue(s.equals("__hamburger_"));
		c.letter ='r';
		GameInfo z= sendResultToClient(c.playerID,c.message,c); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);	
		assertTrue(exp.equals("_______r__r_"));
		c.letter ='m';
		z=sendResultToClient(c.playerID,c.message,c); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		assertTrue(exp.equals("____m__r__r_"));
		c.letter ='a';
		z=sendResultToClient(c.playerID,c.message,c); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		assertTrue(exp.equals("___am__r__r_"));
		c.letter ='g';
		z=sendResultToClient(c.playerID,c.message,c); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		assertTrue(exp.equals("___am__rg_r_"));
		
		c.letter ='u';
		z=sendResultToClient(c.playerID,c.message,c); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		assertTrue(exp.equals("___am_urg_r_"));
		
		c.letter ='b';
		z=sendResultToClient(c.playerID,c.message,c); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		assertTrue(exp.equals("___amburg_r_"));

		c.letter ='e';
		z=sendResultToClient(c.playerID,c.message,c); 		 
		exp = updateLetters(z.letter,z.positions,pos[0],exp);
		assertTrue(exp.equals("___amburger_"));
		
	}
	
	
	
	
	
	public String updateLetters(char letter, ArrayList<Integer> positions,int startPosition,String s) {
		String ori=s;
		for(int i:positions) {	
			StringBuilder b  =  new StringBuilder(ori);
			b.setCharAt(i+startPosition,letter);
			ori = b.toString();
			System.out.println(i+startPosition);
		}
		return ori;
	}
	
	
	
	public void updateLaters(int length,int[] a) { 
		int mid = MaxLetters/2;
		 a[0]= mid - (length/2);
		a[1]= mid + (length - (length/2));
	}
		
	public GameInfo sendResultToClient(int clientID, String msg, GameInfo gameInfo) {

		GameInfo newgameInfo = new GameInfo();
		newgameInfo.playerID = gameInfo.playerID;
		newgameInfo.message = "";
		newgameInfo.letter = gameInfo.letter;
		newgameInfo.positions = new ArrayList<Integer>();
		//update position
		int i = answers.get(gameInfo.playerID - 1).indexOf(gameInfo.letter);
		while(i >= 0) {
		     newgameInfo.positions.add(i);
		     i = answers.get(gameInfo.playerID - 1).indexOf(gameInfo.letter, i + 1);
		     
		}
		if( newgameInfo.positions.size()>0) {
			
			for(Integer a: newgameInfo.positions){
		    
			newgameInfo.message += " "+Integer.valueOf(a+1);
			}
		}
		
		return newgameInfo;
	
	}
	
	
	
	

}
