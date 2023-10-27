package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.index.CorruptIndexException;

public class Indexer {
	
	private IndexWriter writer;
	private String fields = "";
	
	public Indexer(String indexDirectoryPath, String fields) throws IOException {
		//this directory will contain the indexes
		Path indexPath = Paths.get(indexDirectoryPath);
		this.fields = fields;
        if(!Files.exists(indexPath)) {
            Files.createDirectory(indexPath);
        }
        Directory indexDirectory = FSDirectory.open(indexPath);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(OpenMode.CREATE);
        writer = new IndexWriter(indexDirectory, config);
	}
	
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}
	
	private Document getDocument(File file) throws IOException {
		Document document = new Document();
		//index file contents
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str = "";
		ArrayList<Article> art = new ArrayList<Article>();
		List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Data\\" + file.getName()), StandardCharsets.UTF_8);
		
		for(String l:lines) {
			//if(l.contains("article")==false && l.contains("inproceedings")==false)
			str = str + l + "+";
		}
		String[] artic = str.split("},");
		String totalstr = "";
		for(String st : artic) {
			if( st.contains("author")==true || st.contains("title")==true || st.contains("journal")==true && st.contains("journals")!=true) {
				totalstr = totalstr + st;
			}
		}
		totalstr = totalstr.replace("+", "\n");
		//System.out.println(totalstr + "--");
		
		String[] s = totalstr.split(" = ");
		for(int i = 0; i+3<s.length;i = i+3) {
			art.add(new Article(s[i+1], s[i+2],s[i+3]));
		}
		
		int i = 0;
		for(Article a:art) {
			String content = "";
			String author = a.getAuthor();
			String title = a.getTitle();
			String booktitle_Journal = a.getBooktitle_Journal();
						
			if(fields.contains(LuceneConstants.AUTHOR)==true)
				content = content + author + "\n";
			if(fields.contains(LuceneConstants.TITLE)==true) 
				content = content + title + "\n";
			if(fields.contains(LuceneConstants.BOOKTITLE_JOURNAL)==true) 
				content = content + booktitle_Journal + "\n";
			Field authorField = new Field(LuceneConstants.AUTHOR, a.getAuthor(), TextField.TYPE_STORED);
			Field titleField = new Field(LuceneConstants.TITLE, a.getTitle(), TextField.TYPE_STORED);
			Field bookjournalField = new Field(LuceneConstants.BOOKTITLE_JOURNAL, a.getBooktitle_Journal(), TextField.TYPE_STORED);
			Field contentField = new Field(LuceneConstants.CONTENTS, content, TextField.TYPE_STORED);
			//index file name
			Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), StringField.TYPE_STORED);
			//index file path
			Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), StringField.TYPE_STORED);
			document.add(authorField);
			document.add(titleField);
			document.add(bookjournalField);
			document.add(contentField);
			document.add(fileNameField);
			document.add(filePathField);
		}
		br.close();
		return document;
		
	}
	
	private void indexFile(File file) throws IOException {
		System.out.println("Indexing " + file.getCanonicalPath());
		Document document = getDocument(file);
		writer.addDocument(document);
	}
	
	public int createIndex(String dataDirPath) throws IOException {
		//get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();
		for (File file : files) {
			//System.out.println(filter.accept(file));
			if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() /*&& filter.accept(file)*/){
				indexFile(file);
			}
		}
		return writer.numRamDocs();
	}
	
}