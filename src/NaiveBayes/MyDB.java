package NaiveBayes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyDB {
	private Map<Integer, List<String>> dbContent = new HashMap<>();
	private Map<String, Set<String>> attrValuePairs = new HashMap<>();
	public MyDB(String filename) throws FileNotFoundException, IOException{
		this.readfile(filename);
	}
	
	/**
	 * process database 
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void readfile(String filename) throws FileNotFoundException, IOException{
		List<String> s = null;
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			int row = 0;
			String line = br.readLine();
			while(line != null){
				if(line.length()> 0){
					String[] words = line.split(" ");
					s = new ArrayList<String>();
					for(String word: words){
						if(!word.isEmpty()){
							s.add(word);
						}
					}
					
					dbContent.put(row++, s);
				}
				line = br.readLine();
			}
			br.close();
		}
		
		for(int row = 1; row < this.rowCount(); row++){
			List<String> data = this.getContent(row);
			for(int col = 0; col < data.size(); col++){
				if(this.attrValuePairs.get(this.getAttributes().get(col)) != null){
					Set<String> tmp = this.attrValuePairs.get(this.getAttributes().get(col));
					tmp.add(data.get(col));
				}else{
					Set<String> tmp = new HashSet<String>();
					this.attrValuePairs.put(this.getAttributes().get(col), tmp);
				}
			}
		}
	}
	
	/**
	 * return distinct values of the given attributes
	 * @param attr
	 * @return
	 */
	public Set<String> getAttributeValue(String attr){
		return this.attrValuePairs.get(attr);
	}
	
	/**
	 * return the number of rows 
	 * @return
	 */
	public int rowCount(){
		return dbContent.size() - 1;
	}
	
	/**
	 * return attributes names
	 * @return
	 */
	public List<String> getAttributes(){
		return dbContent.get(0);
	}
	
	/**
	 * print the attributes of the database
	 */
	public void print_attrs(){
		int attrIndex = 1;
		for(String attr: this.getAttributes()){
			System.out.println(attrIndex + ". " + attr);
			attrIndex++;
		}
		System.out.print("Attributes: ");
	}
	
	/**
	 * return the content of the given row
	 * @param rowIndex
	 * @return
	 */
	public List<String> getContent(int rowIndex){
		return this.dbContent.get(rowIndex);
	}
}
