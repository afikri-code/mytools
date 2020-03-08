package de.kisirisoft.tools.db.oracle.client;

/**
 * @author Ahmed Fikri
 *
 */
public class DbConnection {
	private String host;
	private int port;
	private String serviceName;
	private String user;
	private String pwd;
	
	private String connUrl;
	
	
	
	public DbConnection(int port) {
		this.port = port;
	}
	
	
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public void setConnUrl(String connUrl) {
		this.connUrl = connUrl;
	}
	
	public String getConnUrl() {
		if(connUrl == null) {
		  return "jdbc:oracle:thin:@"+host+":"+port+"/"+serviceName;
		}else {
			return "jdbc:oracle:thin:@"+this.connUrl;
		}
	}

	

	public void setUser(String user) {
		this.user = user;
	}

	

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}



	public String getPwd() {
		return this.pwd;
	}



	public String getUser() {
		return this.user;
	}

}
