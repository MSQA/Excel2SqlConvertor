import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

public class Csv2SqlGenerator {
	private static final String CSVPATH = "/path/to/csv.csv";
	private static final String OUTPUTNAME = "InsertScriptsGeneratedFromExcel.sql";
	private static final String TABLENAME = "schema.tablename";
	private static final int IGNORECOUNT = 1;
	private static List<Column> columnList;
	
	public static void main(String[] args) {
		try {
			configureColumns();
			generateSqlInsertScripts();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void configureColumns(){
		columnList = new ArrayList<>();
		columnList.add(new Column("ILKAYITNO",Column.TYPE_NUMBER));
		columnList.add(new Column("ILADI",Column.TYPE_STRING));
	}
	
	private static void generateSqlInsertScripts() throws FileNotFoundException {
		Scanner scan = new Scanner(new File(CSVPATH));
		StringBuffer generatedSql = new StringBuffer();
		int ignore = 0;
	    while(scan.hasNextLine()){
	        String line = scan.nextLine();
	        String insertScript = "INSERT INTO "+TABLENAME+" (";
			for (Column column : columnList) {
				insertScript+=column.name+",";
			}
			insertScript = insertScript.substring(0, insertScript.length()-1);
			insertScript+=") values (";
	        String[] cellValues = StringUtils.splitPreserveAllTokens(line,",");
	        int columnIndex = 0;
	        while (columnIndex<columnList.size()) {
				String cellVal=cellValues[columnIndex];
				Column currColumn = columnList.get(columnIndex);
				if(currColumn.type.equals(Column.TYPE_NUMBER)){
					if(cellVal==null||cellVal.equals(""))
						insertScript += 0;
					else
						insertScript += cellVal;
				}else if(currColumn.type.equals(Column.TYPE_STRING)){
					if(cellVal==null||cellVal.equals(""))
						insertScript += "''";
					else
						insertScript += "'" + cellVal + "'";
				}else{
					//unknown type
				}
				if (columnIndex != columnList.size() - 1)
					insertScript += ",";
				columnIndex++;
	        }
	        insertScript += ");";
			System.out.println(insertScript);
			if(++ignore>IGNORECOUNT)
				generatedSql.append(insertScript + "\n");
	    }
	    writeScriptsToFile(generatedSql.toString());
	}
	
	private static void writeScriptsToFile(String sql){
		try {
			System.out.println("Creating file...");
			// Create file
			FileWriter fstream = new FileWriter(OUTPUTNAME);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(sql);
			// Close the output stream
			out.close();
			System.out.println(OUTPUTNAME+ " was created succesfully!");
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	static class Column{
		public static final String TYPE_STRING = "string";
		public static final String TYPE_NUMBER = "number";
		String name;
		String type;
		public Column(String name, String type) {
			super();
			this.name = name;
			this.type = type;
		}
	}
}
