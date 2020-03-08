package de.kisirisoft.tools.db.oracle.client;
import static de.kisirisoft.tools.db.oracle.client.ClassHelper.scanner;

/**
 * @author Ahmed Fikri
 *
 */
public class OracleExecuter {

	public static void main(String[] args)  {
		Utils u = new Utils();
		String line = "begin" ;
		System.out.println("enter help for help");
		while(!line.equals("exit")) {
			line = scanner.nextLine();
			u.executeCommandLine(line);
			
		}
		scanner.close();

		
					
	


	
	}
}
