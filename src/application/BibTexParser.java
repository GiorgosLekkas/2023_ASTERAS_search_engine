package application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class BibTexParser {
	
	private String file = "";
	
	public BibTexParser() {

	}
	
	public BibTexParser(String file) {
		this.file = file;
	}
	
	public Boolean getRes() {
		
		if(Character.compare(file.charAt(0),'@')!=0 && Character.compare(file.charAt(file.length()-3),'}')!=0) {
			System.out.println("1");
			return false;
		}
		if(file.substring(0,15).compareTo("@inproceedings{")!=0 && file.substring(0,9).compareTo("@article{")!=0) {// \{([^*]|\*+[^\/\*])*\}
			
			System.out.println("2 ");
			return false;
		}
		
		if(file.substring(0,15).compareTo("@inproceedings{")==0) {
			if( file.contains("  author    = ")==false || file.contains("  booktitle = ")==false || file.contains("  title     = ")==false ){
				System.out.println("3");
				return false;
			}
		}else if(file.substring(0,9).compareTo("@article{")!=0)
			if( file.contains("  author    = ")==false || file.contains("  journal   = ")==false || file.contains("  title     = ")==false ){
				System.out.println("4");
				return false;
			}
		
		return true;
	}
		
}
