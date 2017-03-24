package NaiveBayes;

import java.util.HashMap;
import java.util.Map;

public class CF_Node {
	private String attr_id;
	private String attr;
	private String val;
	private int count;
	
	public CF_Node(String attr, String val){
		this.attr = attr;
		this.val = val;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getAttr_id() {
		return attr_id;
	}

	public void setAttr_id(String attr_id) {
		this.attr_id = attr_id;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
	
	
}
