

package ceid.netcins.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import ceid.netcins.IndexContentRequest;
import ceid.netcins.SearchContentRequest;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * This class represents an HttpHandler for the user side web server. This
 * server is responsible to handle end to end user traffic, as well as UI
 * requests. This enables the use of web interfaces, rich web based frontends
 * which communicate by using GET or POST in normal or asynchronous http
 * packets. A usefull choice is AJAX.
 * 
 * @author Andreas Loupasakis
 */
public class ClientWebServer implements HttpHandler {

	// Code numbers of every UI Request type
	public final int PAGEREQUEST = 0; // default
	public final int INDEXCONTENTREQUEST = 1;
	public final int INDEXUSERREQUEST = 2;
	public final int SEARCHCONTENTREQUEST = 3;
	public final int SEARCHUSERREQUEST = 4;
	public final int FRIENDREQUEST = 5;

	// Just like in SimMain, the frontends where we issue requests
	private ClientDriver driver;
	private Thread requestDispatcher;

	public ClientWebServer(ClientDriver driver, Thread requestDispatcher) {
		this.driver = driver;
		this.requestDispatcher = requestDispatcher;
	}

	private ClientWebServer() {

	}

	public void handle(HttpExchange t) throws IOException {
		// Body of request http (GET or POST)
		InputStream is = t.getRequestBody();
		// read(is); // .. read the request body
		Scanner sc = new Scanner(is);
		while (sc.hasNextLine()) {
			System.out.println(" Body : " + sc.nextLine());
		}

		// Headers of request http (GET or POST)
		Headers h = t.getRequestHeaders();
		Iterator<String> it = h.keySet().iterator();
		while (it.hasNext()) {
			System.out.println(" Header : " + h.get(it.next()));
		}

		// Requested URI Path
		System.out.println(" Requested URI : " + t.getRequestURI()
				+ ", Requested Path : " + t.getRequestURI().getPath());

		// Response Preparation
		String query = t.getRequestURI().getQuery();
		int type = PAGEREQUEST;
		if (query == null || query.isEmpty() || !query.startsWith("type")) {
			type = PAGEREQUEST;
		} else if (query.startsWith("type=0")) {
			// Always we want the first as denoted by the protocol
			// (http://bla?type=2&...)
			// type = Integer.parseInt(query.split("&", 2)[0].split("=",
			// 2)[1]);
			type = INDEXCONTENTREQUEST;
		} else if (query.startsWith("type=1")) {
			type = SEARCHCONTENTREQUEST;
		}

		switch (type) {

		case INDEXCONTENTREQUEST:
			if (requestDispatcher == null) {
				String msg = "Nodes must have been created first.";
				System.out.println(msg);
				t.sendResponseHeaders(200, msg.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(msg.getBytes());
				os.close();
				break;
			}
			// Add the Request for indexing the file
			driver.execRequests.add(new IndexContentRequest(query.split(
					"&", 2)[1].split("=", 2)[1], t));
			this.doNotify();
			break;

		case SEARCHCONTENTREQUEST:
			if (requestDispatcher == null) {
				String msg = "Nodes must have been created first.";
				System.out.println(msg);
				t.sendResponseHeaders(200, msg.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(msg.getBytes());
				os.close();
				break;
			}
			// Add the Request for indexing the file
			driver.execRequests.add(new SearchContentRequest(query.split(
					"&", 2)[1].split("=", 2)[1],
					SearchContentRequest.RANDOMSOURCE, t));
			this.doNotify();
			break;

		default:
			// RESPONSE PAGE
			byte[] b = readBin(t.getRequestURI().getPath().substring(1));
			t.sendResponseHeaders(200, b.length);
			OutputStream os = t.getResponseBody();
			os.write(b);
			os.close();
			b = null;
			break;
		}

		sc = null;
	}

	/**
	 * notify the RequestDispatcher Thread that a request has been received
	 */
	public void doNotify() {
		if (driver != null) {
			// TODO: Check if the monitorobject creates deadlock when multiple
			// requests
			// are issued simultaneously
			synchronized (driver) {
				driver.wasSignalled = true;
				driver.notify();
			}
		}
	}

	/**
	 * Read the requested url file in memory, into a byte array. 
	 * TODO : A more efficient reading with nio
	 * 
	 * @param file
	 * @return
	 * @throws java.io.IOException
	 */
	private byte[] readBin(String file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[fis.available()];
		fis.read(data);
		fis.close();
		return data;
	}

	public void startUIServer() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(8000),
					2);
			server.createContext("/", this);
			server.setExecutor(null);
			server.start();
		} catch (IOException ex) {
			Logger.getLogger(ClientWebServer.class.getName()).log(Level.SEVERE,
					null, ex);
			ex.printStackTrace();
		}
	}

	public void setDispatcher(Thread dispatcher) {
		this.requestDispatcher = dispatcher;
	}

	public static void main(String[] args) {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(8000),
					2);
			server.createContext("/", new ClientWebServer());
			server.setExecutor(null);
			server.start();
		} catch (IOException ex) {
			Logger.getLogger(ClientWebServer.class.getName()).log(Level.SEVERE,
					null, ex);
			ex.printStackTrace();
		}
	}

}
