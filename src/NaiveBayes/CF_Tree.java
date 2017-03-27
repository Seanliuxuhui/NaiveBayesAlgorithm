package NaiveBayes;

import java.util.HashMap;
import java.util.Map;

public class CF_Tree {
	private Map<String, HashMap<String, Integer>> cf_tree = null;
	
	/**
	 * initialize the CF_Tree object
	 */
	public void initialize(){
		cf_tree = new HashMap<String, HashMap<String, Integer>>();
	}
	
	/**
	 * insert node into tree
	 * @param firstAttr
	 * @param secondAttr
	 * @param count
	 */
	public void insert(String firstAttr, String secondAttr, Integer count){
		if(cf_tree.containsKey(firstAttr)){
			cf_tree.get(firstAttr).put(secondAttr, count);
		}else{
			HashMap<String, Integer> innerMap = new HashMap<String, Integer>();
			innerMap.put(secondAttr, count);
			cf_tree.put(firstAttr, innerMap);
		}
	}
	
	/**
	 * retrieve the occurrence for the given attributes. 
	 * @param firstAttr
	 * @param secondAttr
	 * @return
	 */
	public int getOccurrence(String firstAttr, String secondAttr){
		if(cf_tree.get(firstAttr) != null){
			if(cf_tree.get(firstAttr).get(secondAttr) != null){
				return cf_tree.get(firstAttr).get(secondAttr); 
			}else{
				return 0;
			}
		}
		return 0;
	}
}
