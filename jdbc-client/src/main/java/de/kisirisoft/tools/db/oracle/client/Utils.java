package de.kisirisoft.tools.db.oracle.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static de.kisirisoft.tools.db.oracle.client.ClassHelper.*;

/**
 * @author Ahmed Fikri
 *
 */
public class Utils {
	Map<String,Doit> commands = new HashMap<>();
	List<String> keys = new ArrayList<>();
	DbConnection con = new DbConnection(1521);
	Map<String,String> directories = new HashMap<>();
	String workDir;
	Connection connection = null;
	
	
	
	/**
	 * 
	 */
	Doit defineConn = (Utils u) -> {
		writeOut("defining connection");
		String connUrl = readInValue("enter easyconnect url (or just enter):");
		if(connUrl!=null) {
			con.setConnUrl(connUrl);
			con.setUser(readInValue("enter username:"));
			con.setPwd(readInValue("enter password:"));
			connect();
			return;
		}
		con.setHost(readInValue("enter host:"));
		con.setPort(Integer.parseInt(readInValue("enter port:")));
		con.setServiceName(readInValue("enter SID:"));
		con.setUser(readInValue("enter username:"));
		con.setPwd(readInValue("enter password:"));
		connect();
	};
	
	/**
	 * 
	 */
	Doit showWorkdir =(Utils u) -> {		
		writeOut("Work directory:");
		writeOut(workDir + " : " + directories.get(workDir));
		
	};
	
	/**
	 * 
	 */
	Doit defineDir =(Utils u) -> {		
		writeOut("defining directory");
		directories.put(readInValue("enter directory name:"), readInValue("enter directory path:"));
	};
	
	/**
	 * 
	 */
	Doit setWorkDir =(Utils u) -> {		
		if(directories.isEmpty()) {
			writeOut("no direcotory is defined. Please define directory");
		}else {
		  String tmp = readInValue("set workdir:");
		  if(directories.containsKey(tmp)) {
			  workDir = tmp;
			  writeOut("set workdir done");
		  }else {
			  writeOut("directory name is not defined");
		  }
		}
		
	};
	
	/**
	 * 
	 */
	Doit help =(Utils u) -> {for(int i = 0;i< keys.size();i++) {
		writeOut("[ "+i+" ] : "+keys.get(i));}
	};
	/**
	 * 
	 */
	Doit listDir =(Utils u) -> {for(String k:u.directories.keySet()) {
		writeOut(k + " : " + directories.get(k));}
	};
	
	/**
	 * 
	 */
	Doit listDirContent =(Utils u) -> {
		String tmp = readInValue("enter dir name:[ "+u.workDir+" ]");
		if(tmp.isEmpty()) {
			tmp = u.workDir;
		}
		u.listDirContent(tmp);
	};
	
	/**
	 * 
	 */
	Doit connInfo =(Utils u) -> {
		writeOut(con.getConnUrl());
		try {
			if(u.connection != null && !u.connection.isClosed()) {
				writeOut("connection is open");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	};
	/**
	 * 
	 */
	Doit exportQueryToFile =(Utils u) -> {
		if(workDir!=null && workDir.isEmpty()) {
			writeOut("no work dir defined");
			return;
		}
		String filename = readInValue("enter filename:");
		File f = new File(directories.get(workDir)+"/"+filename);
		if(!f.exists() ) { 
		    writeOut("file doesn''t exist!");
		}
		if(f.isDirectory()) {
			writeOut("entred file is a directory");	
		}
		
		String outputfile = readInValue("enter output file:");
		exportSQLasCSV(filename,outputfile);
		
		
		
	};
	
	/**
	 * constructor
	 */
	public Utils() {
		addKeys("define connection",defineConn);
		addKeys("define directory",defineDir);
		addKeys("set workdirectory",setWorkDir);
		addKeys("list directories",listDir);
		addKeys("connection info",connInfo);
		addKeys("show workdir",showWorkdir);
		addKeys("list dir content",listDirContent);
		addKeys("export query to csv",exportQueryToFile);
		addKeys("help",help);
	}
	
  
	
	/**
	 * @param filename
	 * @param outputfile
	 */
	private void exportSQLasCSV(String filename, String outputfile) {
		Statement stmt = null;
		ResultSet rs = null;
		String path = getPath(workDir);
		String sql = readAllBytesJava7(path+"/"+filename);
		File fout = new File(path+"/"+outputfile);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		
	 
		
		try {
		   stmt = connection.createStatement();
		   stmt.execute(sql);
		   rs = stmt.getResultSet();
		   int n = rs.getMetaData().getColumnCount();
		   writeOut(null);
		   StringBuilder line = new StringBuilder();
		   for(int i =1;i<=n;i++) {
			   line = line.append(rs.getMetaData().getColumnName(i)).append(";");		
			   
		   }
		   bw.write(line.toString());
		   bw.newLine();
		   while(rs.next()) {
			   line = new StringBuilder();
			   for(int i =1;i<=n;i++) {
				  
				   line = line.append(rs.getString(i)).append(";");		
				   
			   }
			   bw.write(line.toString());
			   bw.newLine();
			   
		   }
		   
		}
		catch (SQLException e) {
			writeOut(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
		   try {
			   rs.close();
			stmt.close();
			bw.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		
	}



	/**
	 * @param workDir2
	 * @return
	 */
	private String getPath(String workDir2) {
		return directories.get(workDir2);
	}


	/**
	 * @param tmp
	 */
	private void listDirContent(String tmp) {
		if(!directories.containsKey(tmp)) {
			writeOut("directory not defined");
			return;
		}
			try {
				Files.list(new File(directories.get(tmp)).toPath())
				.limit(10)
				.forEach(path -> {
				    System.out.println(path.getFileName());
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
	}



	/**
	 * @param key
	 * @param d
	 */
	void addKeys(String key,Doit d) {
		if(!commands.containsKey(key)){
			keys.add(key);
		}
		commands.put(key, d);
	}
	
	
	
	
	 
	
	
	 /**
	 * 
	 */
	void  connect() {
		try {
			writeOut("Load the jdbc driver");
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection(con.getConnUrl(), con.getUser(), con.getPwd());
				writeOut("connected");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	 
	
	/**
	 * @param s
	 * @param sql
	 * @return
	 */
	static boolean enableDisableOutput(Statement s, String sql) {
		boolean ret = false;
		try {
			s.executeUpdate(sql);
			ret = true;
		} catch (SQLException e) {
		}
		return ret;
	}
	
	/**
	 * @param s
	 * @return
	 */
	public static boolean enableOutput(Statement s) {
		return enableDisableOutput(s, "begin dbms_output.enable(); end;");
	}
	
	/**
	 * @param s
	 * @return
	 */
	public static boolean disableOutput(Statement s) {
		return enableDisableOutput(s, "begin dbms_output.disable(); end;");
	}
	
	/**
	 * @param s
	 * @param path
	 * @return
	 */
	public static boolean executeFile(Statement s, String path) {
		boolean ret = false;
		if(!fileExists(path)) {
			writeOut("no file " + path);
		}else {
			try {
				s.execute( new String(Files.readAllBytes(Paths.get(path))) );
				ret = true;
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					writeOutputBuffer(s.getConnection());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param c
	 */
	public static void writeOutputBuffer(Connection c) {
		CallableStatement call = null;
		try {
			 call = c.prepareCall(
			        "declare "
			      + "  num integer := 1000;"
			      + "begin "
			      + "  dbms_output.get_lines(?, num);"
			      + "end;"
			    );
			 call.registerOutParameter(1, Types.ARRAY,
		                "DBMSOUTPUT_LINESARRAY");
		            call.execute();
		 
		            Array array = null;
		            try {
		                array = call.getArray(1);
		                Stream.of((Object[]) array.getArray())
		                      .forEach(System.out::println);
		            }
		            finally {
		                if (array != null)
		                    array.free();
		            }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				call.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param s
	 * @param paths
	 * @return
	 */
	public static boolean executeFileList(Statement s, List<String> paths) {
		boolean ret = false;
		for(String path:paths) {
			if(!executeFile(s,path)) {
				writeOut("we stop here");
				ret = false;
				break;
			}else {
				ret = true;
			}
			
		}
		return ret;
	}
	
	/**
	 * @param s
	 * @param path
	 * @return
	 */
	public static boolean executeFiles(Statement s, String path) {
		boolean ret = false;
		if(!fileExists(path)) {
			writeOut("input file doesn't exist! We stop here.");
		}else {
			try {
				ret = executeFileList(s, Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	
	/**
	 * @param line
	 */
	public  void executeCommandLine(String line) {
//		String[] mykeys = line.split("\\s+");
		try {
			int x = Integer.parseInt(line);
			commands.get(keys.get(x)).doIt(this);
			return;
		}catch(Exception e) {
			
		}
		
		if(commands.containsKey(line)) {
			commands.get(line).doIt(this);
		}
		
		
		
	}	
	

}
