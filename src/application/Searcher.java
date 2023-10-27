package application;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Searcher {
	IndexSearcher indexSearcher;
	Directory indexDirectory;
	IndexReader indexReader;
	QueryParser queryParser;
	Query query;
	int search_number = 0;

	public Searcher(String indexDirectoryPath, int search_number, String fields) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		this.search_number = search_number;
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
	}

	public TopDocs search(String searchQuery) throws IOException, ParseException {
		
		String[] str = searchQuery.split(" ");
		if(str.length>1) {
			if(searchQuery.contains(" AND ")==true || searchQuery.contains(" OR ")==true || searchQuery.contains(" NOT ")==true) {
				query = queryParser.parse(searchQuery);
				System.out.println("query: "+ query.toString());
				return indexSearcher.search(query, search_number);
			}else {
		        query = new PhraseQuery(0, LuceneConstants.CONTENTS, searchQuery);
		        query = queryParser.parse(searchQuery);
		        System.out.println("query: "+ query.toString());///Sort.RELEVANCE
		        return indexSearcher.search(query, search_number);
			}
		}
		query = queryParser.parse(searchQuery);
		System.out.println("query: "+ query.toString());
		return indexSearcher.search(query, search_number);
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}
	
	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
}
