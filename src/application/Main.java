package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;


public class Main extends Application {
	Lucene srch = new Lucene();
	@Override
	public void start(Stage primaryStage) {
		try {
			 
			AnchorPane root = new AnchorPane();
			TextField search_bar = new TextField();
			TextField num_bar = new TextField();
			
			CheckBox  researchers_cb = new CheckBox("Researchers");
			CheckBox title_cb = new CheckBox("Title");
			CheckBox booktitle_journal_cb = new CheckBox("BookTitle/Journal");
	        HBox hb = new HBox(5);
	        hb.getChildren().addAll(researchers_cb, title_cb, booktitle_journal_cb);
	        root.getChildren().addAll(hb);
    		root.setTopAnchor(hb, 50.0);
	        root.setLeftAnchor(hb, 55.0);
	        root.setRightAnchor(hb, 55.0);

			search_bar.setPrefWidth(470);
			search_bar.setPrefHeight(30);
			search_bar.setPromptText("Search");
			search_bar.setFocusTraversable(false);
	        root.getChildren().addAll(search_bar);
	        root.setTopAnchor(search_bar, 10.0);
	        root.setLeftAnchor(search_bar, 55.0);
	        
	        num_bar.setPrefWidth(60);
	        num_bar.setPrefHeight(30);
	        num_bar.setPromptText("Number of Results");
	        num_bar.setFocusTraversable(false);
	        root.getChildren().addAll(num_bar);
	        root.setTopAnchor(num_bar, 10.0);
	        root.setLeftAnchor(num_bar, 535.0);
	        
	        Button search_btn = new Button("Search");
	        search_btn.setPrefWidth(60);
	        search_btn.setPrefHeight(30);
	        root.getChildren().addAll(search_btn);
	        root.setTopAnchor(search_btn, 10.0);
	        root.setLeftAnchor(search_btn, 615.0);
	        search_btn.setOnAction(event -> {
				if(search_bar.getText().isEmpty()==false && num_bar.getText().isEmpty()==false && ( researchers_cb.isSelected() || title_cb.isSelected() || booktitle_journal_cb.isSelected() )) {
					File f = new File("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Articles");
        			File[] files = f.listFiles();
        			
        			if(files.length>0) {
        				String num = search_bar.getText();
        				String cb = "";		//value of checkbox
        				if(researchers_cb.isSelected()) 
        					cb = cb + LuceneConstants.AUTHOR + ",";
        				if(title_cb.isSelected()) 
        					cb = cb + LuceneConstants.TITLE + ",";
        				if(booktitle_journal_cb.isSelected()) 
        					cb = cb + LuceneConstants.BOOKTITLE_JOURNAL + ",";
        				System.out.println("CB = " + cb);
        				srch = new Lucene(search_bar.getText(),Integer.parseInt(num_bar.getText()),cb);
        			}else {
        				Alert alert = new Alert(AlertType.ERROR);
	        			alert.setTitle("ERROR");
	        			alert.setContentText("There is NO DATA");
	        			alert.show();
        			}
        			int j = 0;
        			ObservableList<Document> items = FXCollections.observableArrayList(srch.docum);
        			ListView<String> list = new ListView<>();
        			for(Document res : items) {
        				//String cont = res.get(LuceneConstants.AUTHOR) + "\n" + res.get(LuceneConstants.TITLE) + "\n" + res.get(LuceneConstants.BOOKTITLE_JOURNAL);
           				String fl = res.get(LuceneConstants.FILE_NAME);
           				
           				ArrayList<Article> art = new ArrayList<Article>();
           				
        				List<String> lines;
						try {
							lines = Files.readAllLines(Paths.get("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Data\\" + fl.toString()), StandardCharsets.UTF_8);
							String str = "";
	        				for(String l:lines) {
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
	        				
	        				String[] s = totalstr.split(" = ");
	        				for(int i = 0; i+3<s.length ; i = i+3) 
	        					art.add(new Article(s[i+1], s[i+2],s[i+3]));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        				for(int i = 0; i<=9; i++)
        					fl = fl.replace(String.valueOf(i), "");
        				fl = fl.replace(".txt", "");
        				fl = fl.substring(0, 1).toUpperCase() + fl.substring(1);
        				for(Article a:art) {
        					String content = a.getAuthor() + "\n" + a.getTitle() + "\n" + a.getBooktitle_Journal() + "\n";
        					//if(j<srch.score.size()) {
        						list.getItems().add("score: " + srch.score.get(j++) + "\n" + fl + "\n" + content);
        					//}
        				}
        				
        			}
        			
	        		root.setTopAnchor(list, 80.0);
                    root.setLeftAnchor(list, 55.0);
                    root.setRightAnchor(list, 55.0);
                    root.setBottomAnchor(list, 80.0);
                    root.getChildren().addAll(list);
                    
                    Button open = new Button("Open Article");
	        		open.setPrefWidth(100);
	    	        open.setPrefHeight(30);
	    	        root.setBottomAnchor(open, 25.0);
	    	        root.setRightAnchor(open, 55.0);
                    root.getChildren().addAll(open);
                    
                    open.setOnAction(eventViewArticle -> {
        				Stage article = new Stage();
        				AnchorPane ap = new AnchorPane(); 
        				
        				Scene articleScene = new Scene(ap, 230, 100);
        				if(list.getSelectionModel().getSelectedIndex()>=0) {
        					int n = list.getSelectionModel().getSelectedIndex();
	        				Document d = items.get(n);
	        				String str = d.get(LuceneConstants.CONTENTS);
	        				String[] contents = str.split("\n");
	        				
	        				if (contents.length>2) {
		        				Label auth = new Label(contents[0]);
		        				Label ttl = new Label(contents[1]);
		        				Label booktitle_journal = new Label(contents[2]);
		        				Label score = new Label("Score: " + String.valueOf(srch.score.get(n)));
		        				String title = d.get(LuceneConstants.FILE_NAME);
		        				for(int i = 0; i<=9; i++)
		        					title = title.replace(String.valueOf(i), "");
		        				title = title.replace(".txt", "");
		        				title = title.substring(0, 1).toUpperCase() + title.substring(1);
		        				
		        				ap.setTopAnchor(auth, 15.0);
		    	    	        ap.setLeftAnchor(auth, 10.0);
		                        ap.getChildren().addAll(auth);
		        				ap.setTopAnchor(ttl, 35.0);
		    	    	        ap.setLeftAnchor(ttl, 10.0);
		                        ap.getChildren().addAll(ttl);
		                        ap.setTopAnchor(booktitle_journal, 55.0);
		    	    	        ap.setLeftAnchor(booktitle_journal, 10.0);
		                        ap.getChildren().addAll(booktitle_journal);
		                        ap.setTopAnchor(score, 75.0);
		    	    	        ap.setLeftAnchor(score, 10.0);
		                        ap.getChildren().addAll(score);
		                        
		        				article.setTitle(title);
		        				article.setScene(articleScene);
		        				article.setHeight(450);
		        				article.setMinWidth(600);
		        				article.setMaxHeight(450);
		        				article.setMaxWidth(600);
		        				article.show();
	        				}
        				}
        			});
				}else if(num_bar.getText().isEmpty()==true && search_bar.getText().isEmpty()==true) {
					Alert alert = new Alert(AlertType.ERROR);
        			alert.setTitle("ERROR");
        			alert.setContentText("Please enter a query and the number of the results!");
        			alert.show();
				}else if(search_bar.getText().isEmpty()==true) {
					Alert alert = new Alert(AlertType.ERROR);
        			alert.setTitle("ERROR");
        			alert.setContentText("Please enter a query!");
        			alert.show();
				}else if(num_bar.getText().isEmpty()==true) {
					Alert alert = new Alert(AlertType.ERROR);
        			alert.setTitle("ERROR");
        			alert.setContentText("Please enter the number of the results!");
        			alert.show();
				}else if(researchers_cb.isSelected()==false && title_cb.isSelected()==false && booktitle_journal_cb.isSelected()==false ) {
					Alert alert = new Alert(AlertType.ERROR);
        			alert.setTitle("ERROR");
        			alert.setContentText("Please select a field to search!");
        			alert.show();
				}
		    });
	        
	        Button add_btn = new Button("Add");
	        add_btn.setPrefWidth(60);
	        add_btn.setPrefHeight(30);
	        root.getChildren().addAll(add_btn);
	        root.setTopAnchor(add_btn, 10.0);
	        root.setLeftAnchor(add_btn, 685.0);
	        add_btn.setOnAction(add_event -> {
	        	Stage currentStage = (Stage) add_btn.getScene().getWindow();
	        	
	            FileChooser fileChooser = new FileChooser();
	            fileChooser.setTitle("Choose files");
	            fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("TEXT files (*.bib)", "*.bib"));

	            File f = new File("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Dit Uop Professors");
	            fileChooser.setInitialDirectory(f);
	            List<File> selectedFile = fileChooser.showOpenMultipleDialog(currentStage);
	            
	            if(selectedFile!=null) {
		            for(File file : selectedFile) {
		            	Path source = Paths.get(file.toString());
				        Path fileName = source.getFileName();
				        Path target = Paths.get("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Articles\\" + fileName.toString());
		
				        /*if(file.exists()==true) {
							Alert alert = new Alert(AlertType.ERROR);
		        			alert.setTitle("ERROR");
		        			alert.setContentText("This file " + fileName + " already exists");
		        			alert.show();
						}else { */
					        
					        try {
								Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
								System.out.println(fileName.toString() + " Added to Data");
							} catch (IOException e) {
								System.out.println("Exception while moving file: " + e.getMessage());
								e.printStackTrace();
							}
					        String str = "";
					        List<String> lines = new ArrayList<String>();  
					    	try {
								lines = Files.readAllLines(Paths.get("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Articles\\" + fileName.toString()), StandardCharsets.UTF_8);
								
								for(String s:lines) {
						    		str = str + s + "\n";
								}
								str = str.replace("@article", "@art");
								str = str.replace("@inproceedings", "@art");
								
								System.out.println(str);
									
								String[] s = str.split("@art");
								str = "";
								int i = 0;
								for(String st:s) {
									if(st.compareTo("")!=0) {
										String fname = fileName.toString() + String.valueOf(++i);
										if(st.contains("booktitle"))
											st = "@inproceedings" + st;
										if(st.contains("journal") && st.contains("booktitle")==false)
											st = "@article" + st;
										BibTexParser parser = new BibTexParser(st);
										if(parser.getRes()) {
											fname = fname.replace(".bib", "");
											Path path = Paths.get("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Data\\" + fname + ".txt");
											Files.writeString(path, st,  StandardCharsets.UTF_8);
										}else {
											System.out.println(fname);
										}
											
									}
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    	
			            //}
		            }
	            }
	        });
	        
	        Button delete_btn = new Button("Delete");
	        delete_btn.setPrefWidth(60);
	        delete_btn.setPrefHeight(30);
	        root.getChildren().addAll(delete_btn);
	        root.setTopAnchor(delete_btn, 10.0);
	        root.setLeftAnchor(delete_btn, 755.0);
	        delete_btn.setOnAction(delete_event -> {
	        	Stage currentStage = (Stage) add_btn.getScene().getWindow();
	        	
	            FileChooser fileChooser = new FileChooser();
	            fileChooser.setTitle("Choose files");
	            fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("BibTex files (*.bib)", "*.bib"));

	            File f = new File("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Articles\\");
	            fileChooser.setInitialDirectory(f);
	            List<File> selectedFile = fileChooser.showOpenMultipleDialog(currentStage);
	            
	            if(selectedFile!=null) {
		            for(File file : selectedFile) {
		            	Path source = Paths.get(file.toString());
				        Path fileName = source.getFileName();
				        String fname = fileName.toString();
				        fname = fname.replace(".bib", "");
				        Path target = Paths.get("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Articles\\" + fileName.toString());
				        
		
				        if(file.delete()) {
		        			File ff = new File("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Data");
		        			File[] files = ff.listFiles();
		        			for(File fl:files) {
		        				if(fl.getName().contains(fname))
		        					fl.delete();
		        			}
		        			Alert alert = new Alert(AlertType.INFORMATION);
		        			alert.setTitle("FILE DELETED");
		        			alert.setContentText( fileName + " deleted successfully");
		        			alert.show();
				        }
		            }
	            }
		    });
	          
	        Button data_btn = new Button("Data");
	        data_btn.setPrefWidth(60);
	        data_btn.setPrefHeight(30);
	        root.getChildren().addAll(data_btn);
	        root.setTopAnchor(data_btn, 10.0);
	        root.setLeftAnchor(data_btn, 825.0);
	        data_btn.setOnAction(data_event -> {
	        	AnchorPane ap = new AnchorPane();
	        	ObservableList<String> items = FXCollections.observableArrayList();
	        	
	        	File file = new File("C:\\Users\\giorg\\eclipse-workspace\\ASTERAS\\Articles\\");
	        	File[] files = file.listFiles();
	        	ListView<String> list = new ListView<>();
    			for(File fl:files) {
    				list.getItems().add(fl.getName().toString());
    			}
    			
    			ap.setTopAnchor(list, 80.0);
    			ap.setLeftAnchor(list, 55.0);
    			ap.setRightAnchor(list, 55.0);
    			ap.setBottomAnchor(list, 80.0);
    			ap.getChildren().addAll(list);
                
                Scene scene2 = new Scene(ap,450,300);
                Stage stg = new Stage();
                stg.setTitle("Data");
                stg.setScene(scene2);
                stg.setHeight(300);
                stg.setMinWidth(450);
                stg.setMaxHeight(300);
                stg.setMaxWidth(450);
                stg.show();
                
	        });
	        
	        Scene scene = new Scene(root,940,650);
	        primaryStage.setTitle("Search");
			primaryStage.setScene(scene);
			primaryStage.setHeight(650);
			primaryStage.setMinWidth(940);
			primaryStage.setMaxHeight(650);
			primaryStage.setMaxWidth(940);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}