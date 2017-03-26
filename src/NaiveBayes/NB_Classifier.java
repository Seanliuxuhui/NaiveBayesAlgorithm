package NaiveBayes;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NB_Classifier {
	private Map<String, HashMap<String, String>> attrmap = new HashMap<>(); 
	private Map<String, HashMap<String, HashMap<String, Double>>> probTable = new Hashtable<>();
	private CF_Tree cf_t = new CF_Tree();
	public void classifier_build(int attrIndex, MyDB db){
		List<String> attrNames = db.getAttributes();
		String selectedAttr = attrNames.get(attrIndex - 1);
		cf_t.initialize();
		int i = 1;
		for(String attrName: db.getAttributes()){
			int j = 1;
			HashMap<String, String> attrValueMap = new HashMap<String, String>();
			for(String attrValue: db.getAttributeValue(attrName)){
				attrValueMap.put(attrValue, i + "." + j);
				attrmap.put(attrName, attrValueMap);
				j++;
			}
			i++;
		}
		
		int row = 1;
		while(db.getContent(row) != null){
			List<String> content = db.getContent(row);
			String first = this.attrmap.get(selectedAttr).get(content.get(attrIndex -1));
			
			for(int col = content.size() - 1; col >= 0; col--){
				String second = this.attrmap.get(attrNames.get(col)).get(content.get(col));
				int count = cf_t.getOccurrence(first, second) == 0 ? 1 : cf_t.getOccurrence(first, second) + 1;
				cf_t.insert( first, second, count);
			}
			row++;
		}
	}
	public void prob_table_build(int attrIndex, MyDB db){
		attrIndex = attrIndex - 1;
		for(String selectedAttrValue: this.attrmap.get(db.getAttributes().get(attrIndex)).values()){
			HashMap<String, HashMap<String, Double>> tmp_predValMap = new HashMap<>();
			for(String attrName: db.getAttributes()){
				HashMap<String, Double> tmp = new HashMap<String, Double>();
				for(String attrValue: db.getAttributeValue(attrName)){
					double numerator = 1;
					double denominator = 1;
					if(!attrName.equals(db.getAttributes().get(attrIndex))){
						numerator = this.cf_t.getOccurrence(selectedAttrValue, attrmap.get(attrName).get(attrValue)) + 1;
						denominator = this.cf_t.getOccurrence(selectedAttrValue, selectedAttrValue) + db.getAttributeValue(attrName).size();
						
					}else{
						numerator = this.cf_t.getOccurrence(selectedAttrValue, attrmap.get(attrName).get(attrValue)) + 1;
						denominator = db.rowCount() + db.getAttributeValue(attrName).size();
					}
					double prob = BigDecimal.valueOf(numerator/denominator).setScale(3, RoundingMode.HALF_UP).doubleValue();
					tmp.put(attrmap.get(attrName).get(attrValue),prob);
				}
				tmp_predValMap.put(attrName, tmp);
			}
			this.probTable.put(selectedAttrValue, tmp_predValMap);
			}
		}
	
	public void predict(int attrIndex, MyDB db) throws IOException{
		StringBuffer bf = new StringBuffer();
		String s = "";
		for(String attrName: db.getAttributes()){
			s += attrName + " ";
		}
		s += "Classification\n";
		bf.append(s);
		int accurate = 0;
		for(int row = 1; row < db.rowCount(); row++){
			s = new String();
			List<String> rowContent = db.getContent(row);
			for(String dataItem: rowContent){
				s += dataItem + " ";
			}
			String result = this.predict(rowContent, attrIndex, db.getAttributes(), db);
			if(rowContent.get(attrIndex - 1).equals(result)){
				accurate++;
			}
			s += result + "\n";
			bf.append(s);
		}
		
		s = "Accuracy: " + accurate + "/" + db.rowCount() + "\n";
		bf.append(s);
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter("Result.txt"))){
			bw.write(bf.toString());
			bw.close();
		}
		System.out.println("The result is in the file 'Result.txt'");
		
	}
	
	private String predict(List<String> items, int selectedIndex, List<String> attrs, MyDB db){
		selectedIndex = selectedIndex - 1;
		String selectedAttr = attrs.get(selectedIndex);
		
		Map<String, String> valueKeyPairs = new HashMap<>();
		for(String key: this.attrmap.get(selectedAttr).keySet()){
			valueKeyPairs.put(this.attrmap.get(selectedAttr).get(key), key);
		}
		
		Map<String, Double> predMap = new HashMap<>();
		for(String key: valueKeyPairs.keySet()){
			double sum = 0.0, prob = 0.0;
			for(int col = 0; col < items.size(); col++){
				String convectedString = this.attrmap.get(attrs.get(col)).get(items.get(col));
				if(!convectedString.isEmpty()){
					prob = this.probTable.get(key).get(attrs.get(col)).get(convectedString);
				}else{
					double numerator = 1.00;
					double denominator = this.cf_t.getOccurrence(selectedAttr, key) + db.getAttributeValue(attrs.get(col)).size();
					prob = BigDecimal.valueOf(numerator/denominator).setScale(3, RoundingMode.HALF_UP).doubleValue();
				}
				sum += Math.log(prob);
			}
			predMap.put(valueKeyPairs.get(key), sum);
		}
		
		double prob = -Double.MAX_VALUE; String predictedValue = "";
		for(String k: predMap.keySet()){
			if(predMap.get(k) > prob){
				prob = predMap.get(k);
				predictedValue = k;
			}
		}
		return predictedValue;
		
	}
	
	public MyDB readData(Scanner scan, String purpose){
		while(true){
			try {
				System.out.println("Please enter a " + purpose + " file:");
				String filename = scan.nextLine();
				MyDB db = new MyDB(filename);
				return db;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Cannot locate your file. ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) throws IOException{
		Scanner scan = new Scanner(System.in);
		NB_Classifier clf = new NB_Classifier();
		//C:\Users\Liu\Desktop\courses\CSCI 4144\Ass\Ass5\data1
		//C:\Users\Liu\Desktop\courses\CSCI 4144\Ass\Ass5\data2
		MyDB training_DB = clf.readData(scan, "training");
		MyDB test_DB = clf.readData(scan, "test");
		System.out.println("Please choose an attribute (by number):");
		training_DB.print_attrs();
		System.out.println();
		int attrIndex = scan.nextInt();
		clf.classifier_build(attrIndex, training_DB);
		clf.prob_table_build(attrIndex, training_DB);
		clf.predict(attrIndex, test_DB);
	}
}
