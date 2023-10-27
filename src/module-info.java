module Test {
	requires javafx.controls;
	requires javafx.graphics;
	requires java.desktop;
	requires lucene.core;
	requires lucene.queryparser;
	requires javafx.base;
	
	opens application to javafx.graphics, javafx.fxml;
}
