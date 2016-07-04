import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class SpellCorrector {
	public static void main(String[] args) {
		SpellCorrector sp = SpellCorrector.getInstance();
		out.println(sp.correct("americ"));
	}

	private HashMap<String, Integer> NWORDS;
	
	private SpellCorrector(){
		NWORDS = train();
	}

	private static SpellCorrector spellCorrector;
	public static SpellCorrector getInstance(){
		synchronized (SpellCorrector.class) {
			if (spellCorrector == null) spellCorrector = new SpellCorrector();
		}
		return spellCorrector;
	}
	
	public String correct(String in){
		if(NWORDS.containsKey(in))
			return in;
		HashSet<String> fromEdit1 = known(edit1(in));
		if(fromEdit1.size()>0){
			return maxFreq(fromEdit1);
		}
		HashSet<String> fromEdit2 = known(edit2(in));
		if(fromEdit2.size()>0){
			return maxFreq(fromEdit2);
		}
		return in;
	}
	
	private String maxFreq(HashSet<String> rawSet){
		String maxString = "";
		int maxFreq = 0;
		for(String s : rawSet)
			if(NWORDS.get(s) > maxFreq){
				maxString = s;
				maxFreq = NWORDS.get(s);
			}
		return maxString;
	}
	//the words with one edit distance
	private HashSet<String> edit1(String in){
		HashSet<String> set = new HashSet<String>();
		ArrayList<String[]> splits = new ArrayList<String[]>();
		char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		//splits
		for(int i = 0; i< in.length()+1; i++){
			String[] tmpString = {in.substring(0, i), in.substring(i)};
			splits.add(tmpString);
		}
		//deletes
		for(int i = 0; i< splits.size()-1; i++){
			String[] tmpString = splits.get(i);
			set.add(tmpString[0]+tmpString[1].substring(1));
		}
		//transposes
		for(int i = 0; i< splits.size()-2; i++){
			String[] tmpString = splits.get(i);
			set.add(tmpString[0]+tmpString[1].substring(1,2)+tmpString[1].substring(0,1)+tmpString[1].substring(2));
		}
		//replaces
		for(int i = 0; i< splits.size()-1; i++){
			String[] tmpString = splits.get(i);
			for(int j = 0; j<alphabet.length; j++){
				set.add(tmpString[0]+alphabet[j]+tmpString[1].substring(1));
			}
		}
		//inserts
		for(int i = 0; i< splits.size(); i++){
			String[] tmpString = splits.get(i);
			for(int j = 0; j<alphabet.length; j++){
				if(tmpString[1] == "")
					set.add(tmpString[0]+alphabet[j]);
				else
					set.add(tmpString[0]+alphabet[j]+tmpString[1]);
			}
		}
		return set;
	}
	//the words with one edit distance
	private HashSet<String> edit2(String in){
		HashSet<String> set2 = new HashSet<String>();
		HashSet<String> tmpSet = edit1(in);
		for(String in2 : tmpSet)
			for(String atom : edit1(in2))
				set2.add(atom);
		return set2;
	}
	
	private HashSet<String> known(HashSet<String> rawSet){
		HashSet<String> knownSet = new HashSet<String>();
		for(String rawString : rawSet)
			if(NWORDS.containsKey(rawString))
				knownSet.add(rawString);
		return knownSet;
	}
	//return the frequency of word in the training data
	private HashMap<String, Integer> train(){
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		try{
			//specify appropriate path of the training data in the following line
			BufferedReader reader = new BufferedReader(new FileReader("big.txt"));
			String line;
			Pattern pattern = Pattern.compile("[a-z]+");
			while ((line = reader.readLine()) != null){
				Matcher matcher = pattern.matcher(line);
				while (matcher.find()){
					String match = matcher.group();
					if(result.containsKey(match))
						result.put(match, result.get(match)+1);
					else result.put(match, 1);
				}
			}
		}
		catch ( Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
