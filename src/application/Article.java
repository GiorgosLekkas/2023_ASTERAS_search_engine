package application;

import java.util.List;

public class Article {
	
	private String title = "";
	private String booktitle_journal = "";
	private String author = "";
	
	public Article() {
		
	}
	
	public Article(String author,String title, String booktitle_journal) {
		
		/*System.out.println("AUTH = " + author);
		System.out.println("TITLE = " + title);
		System.out.println("BJ = " + booktitle_journal);*/
		
		author = author.replace("title", "");
		author = author.substring(1);
		author = author.replaceAll("\\s{2,}", " ").trim();
		String[] str = author.split(" and ");
		String auth = " ";
		for(String s : str)
			auth = auth + s + ",";
		auth = auth.substring(0, auth.length() - 1);
		this.author = auth;	
		
		title = title.replace("journal", "");
		title = title.replace("booktitle", "");
		title = title.replaceAll("\\s{2,}", " ").trim();
		title = title.substring(1);
		this.title = title;
		
		booktitle_journal = booktitle_journal.substring(1);
		booktitle_journal = booktitle_journal.replaceAll("\\s{2,}", " ").trim();
		this.booktitle_journal = booktitle_journal;
	}
	
	public String getAuthor() {
		
		return author;
	}
	
	public String getTitle() {
		
		return title;
	}
	
	public String getBooktitle_Journal() {
		
		return booktitle_journal;
	}

}
