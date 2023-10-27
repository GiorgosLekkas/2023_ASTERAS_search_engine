package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class Lucene {
	
	public ArrayList<File> res = new ArrayList<>();
	public ArrayList<Float> score = new ArrayList<>();
	public ArrayList<Document> docum = new ArrayList<>();
	public ArrayList<String> str = new ArrayList<>();
	public ListView<String> list = new ListView<>();
	ObservableList<Document> items;

	
	public TopDocs hits;
	QueryParser queryParser;
	String indexDir = "C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Index";
	String dataDir = "C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Data";
	Indexer indexer;
	Searcher searcher;
	int search_number = 0;
	
	public Lucene() {
		
	}
	
	public Lucene(String s, int search_number, String fields) {
		Lucene tester;
		try {
			this.search_number = search_number;
			this.createIndex(fields);
			this.search(s, fields);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void createIndex(String fields) throws IOException {
		indexer = new Indexer(indexDir, fields);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed = indexer.createIndex(dataDir);
		long endTime = System.currentTimeMillis();
		indexer.close();
		System.out.println(numIndexed+" File(s) indexed, time taken: " + (endTime-startTime)+" ms");
	}

	private void search(String searchQuery, String fields) throws IOException, ParseException {
		searcher = new Searcher(indexDir, search_number, fields);
		long startTime = System.currentTimeMillis();
		
		hits = searcher.search(searchQuery);
		
		long endTime = System.currentTimeMillis();
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
            docum.add(searcher.getDocument(scoreDoc));
        }
		
		System.out.println(hits.totalHits +" documents found. Time :" + (endTime - startTime));
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            File f = new File(doc.get(LuceneConstants.FILE_PATH).toString());
			res.add(f);
			score.add(scoreDoc.score);
            
            System.out.print("Score: "+ scoreDoc.score + " ");
            System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
        items = FXCollections.observableArrayList(docum);
        searcher.close();
	}
}
