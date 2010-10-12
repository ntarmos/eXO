/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.client;

import ceid.netcins.FriendsRequest;
import ceid.netcins.IndexContentRequest;
import ceid.netcins.IndexUserRequest;
import ceid.netcins.Request;
import ceid.netcins.SearchContentRequest;
import ceid.netcins.SearchUserRequest;
import ceid.netcins.StatsRequest;
import ceid.netcins.content.ContentProfileFactory;
import ceid.netcins.simulator.HttpServerHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import rice.environment.Environment;

/**
 *
 * This is the Main class for the client Frontend. Here operates the Frontend 
 * thread which initiates user requests and sends them to the Dispatcher thread
 * in ClientDriver class.
 * 
 * @author andy
 */
public class ClientMain {
    
    
// Basic Commands' numcodes
    
public static final int INDEXCONTENT = 1;    
public static final int SEARCHCONTENT = 2;
public static final int SEARCHUSER = 3;
public static final int CREATENODES = 4;
public static final int IMPORTTEST = 5;
public static final int STOPTEST = 6;
public static final int NODE = 7;
public static final int HELP = 8;
public static final int EXIT = 9;
public static final int FRIENDS = 10;
    
    /** Creates a new instance of SimMain */
    public ClientMain() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ClientMain frontend = null;
        try{
        Thread.currentThread().setName("FrontendThread");
        
        frontend = new ClientMain();
        
        frontend.preRunning();
        frontend.triggerRunning(args);
        frontend.postRunning();
        
        }catch(Exception e){
            System.out.println("Error occured!\n");
            e.printStackTrace();
        }
    }
    
    /**timeSource
     * Here all the pre- issues must be covered.
     */
    public void preRunning(){
        
        // Deletes all the files in the FreePastry-Storage-Root dir
//        LinkedList delme = new LinkedList();
//        delme.add(new File("FreePastry-Storage-Root"));
//        while (!delme.isEmpty()) {
//          File f = (File) delme.removeFirst();
//          if (f.isDirectory()) {
//            File[] subs = f.listFiles();
//            if (subs.length == 0) {
//              f.delete();
//            } else {
//              delme.addAll(Arrays.asList(subs));
//              delme.addLast(f);
//            }
//          } else {
//            f.delete();
//          }
//        }
    }
    
    /**
     * Triggers the running through the driver object.
     */
    public void triggerRunning(String[] args) throws Exception{
        env = parseArgs(args);
        
        // Allocate Memory for the ClientDriver in order both Threads to be
        // able to set important variables
        if(this.bindPort!=0)
            driver = new ClientDriver(env,bindPort);
        else
            driver = new ClientDriver(env);
        
        cpf = new ContentProfileFactory();

        if(webEnv){
            cws = new ClientWebServer(driver,null);
            cws.startUIServer();
        }
        // Use cli if the user has requested it!
        if(cliEnv){
            in = null;
            in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            if(in!=null)
                cli(in);
        }else{
            parseXMLFile(new File(lastarg));
        }
    }
    
    /**
     * Here must be put all the post- processing.
     */
    public void postRunning(){
        
    }
    
    
    void cli(BufferedReader in) {
                // CLI for user commands!
        while (true) {
            try{
            
            System.out.println("< :-) >"); // prompt the user
            
            String line = in.readLine();
            
            if (line == null || line.length() == -1)
                break;

            line = line.trim();
            
            switch(parseLine(line)){
                
                case CREATENODES:
                    // The first time this command is called, 
                    // we create the "requestDispather" Thread to monitor!
                    createRequestDispatcher();
                    
                    if(webEnv)
                        cws.setDispatcher(requestDispatcher);
                    
                    break;
                    
                case INDEXCONTENT:
                    if(requestDispatcher == null){
                        System.out.println("Nodes must have been created first.");
                        break;
                    }
                    
  
                    // For debugging
//                    Map<File,ContentProfile> wholecpf = cpf.buildFromDir(new File(lastarg));
//                    Iterator<File> it = wholecpf.keySet().iterator();
//                    while(it.hasNext()){
//                        File f = it.next();
//                        System.out.println("\n\n For File ---> "+f+" <---\n"+wholecpf.get(f).toString());
//                    }
                    
                    // For debugging
//                    Iterator i = tfv.keySet().iterator();
//                    while(i.hasNext()){
//                        String str = (String) i.next();
//                        System.out.println("Term "+str+" has tf="+tfv.get(str));
//                    }
                    
                    if(lastarg.startsWith("-s"))
                    {
                        int sourceNum = IndexContentRequest.RANDOMSOURCE;
                        String sargs[] = lastarg.split(" ", 3);
                        sourceNum = Integer.parseInt(sargs[1]);
                        File f = new File(sargs[2]);
                        if(f.isDirectory()){
                            directoryIndexing(f, sourceNum);
                        }else
                            // Add the Request for indexing the file
                            this.driver.execRequests.add(new IndexContentRequest(sargs[2],sourceNum));
                    }else{
                        File f = new File(lastarg);
                        if(f.isDirectory()){
                            directoryIndexing(f);
                        }else
                            // Add the Request for indexing the file
                            this.driver.execRequests.add(new IndexContentRequest(lastarg));
                    }
                    
                    // Once for ALL :-)
                    doNotify();
                    lastarg=null;
                    break;    
                    
                case SEARCHCONTENT:  
                    if(requestDispatcher == null){
                        System.out.println("Nodes must have been created first.");
                        break;
                    }
                    
                    if(lastarg.startsWith("-s"))
                    {
                        int sourceNum = IndexContentRequest.RANDOMSOURCE;
                        String sargs[] = lastarg.split(" ", 3);
                        sourceNum = Integer.parseInt(sargs[1]);
                        // Add the Request for search
                        this.driver.execRequests.add(new SearchContentRequest(sargs[2],sourceNum));
                    }else{
                        // Add the Request for search
                        this.driver.execRequests.add(new SearchContentRequest(lastarg)); // Feed the whole query!
                    }
                    
                    doNotify();
                    break;
                    
                case SEARCHUSER:  
                    if(requestDispatcher == null){
                        System.out.println("Nodes must have been created first.");
                        break;
                    }
                    
                    if(lastarg.startsWith("-s"))
                    {
                        int sourceNum = IndexContentRequest.RANDOMSOURCE;
                        String sargs[] = lastarg.split(" ", 3);
                        sourceNum = Integer.parseInt(sargs[1]);
                        // Add the Request for search
                        this.driver.execRequests.add(new SearchUserRequest(sargs[2],sourceNum));
                    }else{
                        // Add the Request for search
                        this.driver.execRequests.add(new SearchUserRequest(lastarg)); // Feed the whole query!
                    }
                    
                    doNotify();
                    break;
                    
                case IMPORTTEST:  
                    parseXMLFile(new File(lastarg));
                    //doNotify();
                    break;
                    
                case STOPTEST:
                    doNotify();
                    break;
                    
                case NODE: 
                    if(requestDispatcher == null){
                        System.out.println("Nodes must have been created first.");
                        break;
                    }
                    Request req = parseNodeCommand(lastarg);
                    if(req == null){
                        System.out.println("Please use the correct syntax :\n\n node (add [<uniquename>])|" +
                                "(delete <uniquename>|<nodenumber>)|" +
                                "(show pending|friends|shared <node number>)\n");
                        break;
                    }else{
                        // Add the StatsRequest
                        this.driver.execRequests.add(req);
                    }
                    doNotify();
                    break;
                    
                case HELP:
                    break;
                    
                case EXIT:
                    if(requestDispatcher != null)
                        driver.cleanUp();
                    System.exit(0);
                    break;
                    
                case FRIENDS:
                    if(requestDispatcher == null){
                        System.out.println("Nodes must have been created first.");
                        break;
                    }
                    
                    if(lastarg == null){
                        this.driver.execRequests.add(new FriendsRequest(null,FriendsRequest.RANDOMSOURCE,FriendsRequest.RANDOMSOURCE));    
                    }else if(lastarg.startsWith("-s"))
                    {
                        int sourceNum = FriendsRequest.RANDOMSOURCE;
                        String sargs[] = lastarg.split(" ", 3);
                        sourceNum = Integer.parseInt(sargs[1]);
                        if(sargs[2].startsWith("-d")){
                            int destNum = FriendsRequest.RANDOMSOURCE;
                            String sargs2[] = sargs[2].split(" ", 3);
                            destNum = Integer.parseInt(sargs2[1]);
                            if(sargs2.length>2){
                                // Add the Request for friendship
                                this.driver.execRequests.add(new FriendsRequest(sargs2[2],sourceNum,destNum));
                            }else{
                                // Add the Request for friendship
                                this.driver.execRequests.add(new FriendsRequest(null,sourceNum,destNum));
                            }
                        }else if(sargs.length>2){
                                // Add the Request for friendship
                                this.driver.execRequests.add(new FriendsRequest(sargs[2],sourceNum,FriendsRequest.RANDOMSOURCE));
                        }else{
                                // Add the Request for friendship
                                this.driver.execRequests.add(new FriendsRequest(null,sourceNum,FriendsRequest.RANDOMSOURCE));
                        }
                    }else if(lastarg.startsWith("-d")){
                        int destNum = FriendsRequest.RANDOMSOURCE;
                        String sargs[] = lastarg.split(" ", 3);
                        destNum = Integer.parseInt(sargs[1]);
                        if(sargs.length>2){
                            // Add the Request for search
                            this.driver.execRequests.add(new FriendsRequest(sargs[2],FriendsRequest.RANDOMSOURCE,destNum));        
                        }else{
                            // Add the Request for search
                            this.driver.execRequests.add(new FriendsRequest(null,FriendsRequest.RANDOMSOURCE,destNum));    
                        }
                        
                    }else if(!lastarg.equals("")){
                        // Add the Request for search
                        this.driver.execRequests.add(new FriendsRequest(lastarg,FriendsRequest.RANDOMSOURCE,FriendsRequest.RANDOMSOURCE));
                    }
                    doNotify();
                    break;
                    
                default:
                    System.out.println("Bad command or filename! Use \"help\"!");
                    break;
            }
            
            System.out.println("Got input: " +line );
            
            }catch(Exception e){            
                System.out.println("\n\nError occured in while loop, during the \"TRIGGER TEST\" phase!\n");
                e.printStackTrace();
                
            }finally{
                System.out.println("...Continue in CLI!");
            }
        }
    }
    
  /**
   * Processes command line arguments and sets the global Environment
   * and environment variables
   * 
   * 
   */
  protected Environment parseArgs(String args[]) throws IOException {
    // process command line arguments  
      
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-help")) {
        System.out.println("Usage: java ClientMain [-help] [-port <portnumber>] [-web] [-test <xml_test_file>]");
        System.exit(0);
      }
    }
    
    // Loads pastry settings, and sets up the Environment for simulation
    Environment env = new Environment();
    
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-test") && i+1 < args.length) {
          File file= new File(args[i+1]);
          if(file.exists() && file.isFile() && file.canRead()){
              cliEnv = false;
              System.out.println("\nNon cli based output has been selected!\n");
              
              System.out.println("\nA new test scenario is going to be used...\n");
              // Parse xml test and feed requestDispatcher with the requests
              this.lastarg = args[i+1];
          }
        break;
      }
    }
    
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-port") && i+1 < args.length) {
          this.bindPort = Integer.parseInt(args[i+1]);
        break;
      }
    }
    
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-web")) {
          this.webEnv = true;
        break;
      }
    }

    return env;
  }
  
  /**
   * process user input line args
   */
  int parseLine(String line){
      String args[] = line.split(" ",2);
      for (int i=0; i < args.length; i++){
          if(args[i].equals("help")){   
              if(i+1 < args.length){
                  
              }else{
                  System.out.println("Type \"help <command>\" to print more information about the specific command\n");
                  System.out.println("********************** COMMANDS ********************************************");
                  System.out.println("*                                                                          *");
                  System.out.println("*  1.indexcontent [-s <source number>] <content file>|<content directory>  *");
                  System.out.println("*                                                                          *");
                  System.out.println("*  2.searchuser [-s <source number>] <query>                               *");
                  System.out.println("*                                                                          *");
                  System.out.println("*  3.importtest [<xml file>]                                               *");
                  System.out.println("*                                                                          *");
                  System.out.println("*  4.stoptest                                                              *");
                  System.out.println("*                                                                          *");
                  System.out.println("*  5.node (add [<uniquename>])|(delete <uniquename>|<nodenumber>)          *");
                  System.out.println("*    |(show pending|friends|shared <node number>)                          *");
                  System.out.println("*                                                                          *");
                  System.out.println("*  6.exit                                                                  *");
                  System.out.println("*                                                                          *");
                  System.out.println("*  7.help [<command>]                                                      *");
                  System.out.println("*                                                                          *");
                  System.out.println("*  8.createnodes                                                           *");
                  System.out.println("*                                                                          *");
                  System.out.println("*  9.searchcontent [-s <source number>] <query>                            *");
                  System.out.println("*                                                                          *");
                  System.out.println("* 10.friends [-s <source number>][-d <destination num>][<message>]         *");
                  System.out.println("*                                                                          *");
                  System.out.println("****************************************************************************");
                  return HELP;
              }
              
          }
              
      }
      
      for (int i=0; i < args.length; i++){
          if(args[i].equals("exit")){   
              System.out.println("...Terminating Simulator\n\n");
              return EXIT;
          }
      }
      
      for (int i=0; i < args.length; i++){
          if(args[i].equals("createnodes")){   
              return CREATENODES;
          }
      }
      
      for (int i=0; i < args.length; i++){
          if(args[i].equals("importtest")&& i+1 < args.length) {
              lastarg = args[i+1];
              return IMPORTTEST;
          }
      }
      
      for (int i=0; i < args.length; i++){
          if(args[i].equals("stoptest")){   
              return STOPTEST;
          }
      }
      
      for (int i = 0; i < args.length; i++) {
          if (args[i].equals("indexcontent") && i+1 < args.length) {
              lastarg = args[i+1];
              return INDEXCONTENT;
          }
      }
      
      for (int i = 0; i < args.length; i++) {
          if (args[i].equals("searchcontent") && i+1 < args.length) {
              lastarg = args[i+1]; // THE WHOLE QUERY
              return SEARCHCONTENT;
          }
      }
      
      for (int i = 0; i < args.length; i++) {
          if (args[i].equals("searchuser") && i+1 < args.length) {
              lastarg = args[i+1]; // THE WHOLE QUERY
              return SEARCHUSER;
          }
      }
      
      for (int i=0; i < args.length; i++){
          if(args[i].equals("node") && i+1 < args.length){
              lastarg = args[i+1]; // THE WHOLE QUERY
              return NODE;
          }
      }
      
      for (int i=0; i < args.length; i++){
          if(args[i].equals("friends")){
              if(i+1 < args.length)
                lastarg = args[i+1]; // THE WHOLE QUERY
              else
                  lastarg = null;
              return FRIENDS;
          }
      }
      
      return -1;
  }
  
  
  // TODO : Fix this part!
  public boolean parseXMLFile(File f){
    try{
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        
        // We use DTD for XML validation (see the current directory)
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder=factory.newDocumentBuilder();
        Document doc=builder.parse(f);

        Element root;
        Vector<org.w3c.dom.Node> scenarios = new Vector<org.w3c.dom.Node>();
        org.w3c.dom.Node tmp,nodes=null;
        
        root=doc.getDocumentElement();
        NodeList children=root.getChildNodes();
        
        int i=0;
        // First Level
        while(i<children.getLength()){
            tmp=children.item(i);
            if(((Element)tmp).getTagName().equals("scenario"))
                scenarios.add(tmp);
            else if(((Element)tmp).getTagName().equals("nodes"))
                nodes = tmp;
            i++;
        }
        
        // Create the network only if it is not exist!
        if(requestDispatcher==null)
            createRequestDispatcher();
        
        if(nodes!=null){    
            // This case is used to manage nodes of the underlying network.
            // For example we can ADD, DELETE, UPDATE nodes etc.
            // TODO : Add functionality
        }
        
        if(scenarios.isEmpty())
            return true;
        
        System.out.println("Ready to issue requests");
        
        // These will hold the requests
        Vector<org.w3c.dom.Node> index = new Vector<org.w3c.dom.Node>();
        Vector<org.w3c.dom.Node> search = new Vector<org.w3c.dom.Node>();
        Vector<org.w3c.dom.Node> retrieve = new Vector<org.w3c.dom.Node>();
        Vector<org.w3c.dom.Node> tag = new Vector<org.w3c.dom.Node>();
        
        // Iterate through each scenario
        for(org.w3c.dom.Node tmp2 : scenarios){
            children = tmp2.getChildNodes();
            if(children == null)
                continue;
            i=0;
            // Second Level
            while(i<children.getLength()){
                tmp=children.item(i);
                if(((Element)tmp).getTagName().equals("index"))
                    index.add(tmp);
                else if(((Element)tmp).getTagName().equals("search"))
                    search.add(tmp);
                else if(((Element)tmp).getTagName().equals("retrieve"))
                    retrieve.add(tmp);
                else if(((Element)tmp).getTagName().equals("tag"))
                    tag.add(tmp);
                i++;
            }
            
            // Feed the requests
            executeScenario(index,search,retrieve,tag);
        }
        
    }catch(Exception e){
        e.printStackTrace();
        return false;
    }
    return true;
  }

  /**
   * This method creates the RequestDispatcher thread which starts the
   * network creation and initialization.
   * 
   */  
  private void createRequestDispatcher() {
        
            if(driver==null){
                if(this.bindPort==0)
                    driver = new ClientDriver(env);
                else
                    driver =new ClientDriver(env,bindPort);
            }
            if (requestDispatcher == null) {
                requestDispatcher = new Thread() {

                    @Override
                    public void run() {
                        try {
                            Thread.currentThread().setName("RequestDispatcher");
                            driver.start();
                        } catch (Exception e) {
                            System.out.println("Error occured in thread \"RequestDispatcher\"!\n");
                            e.printStackTrace();
                        }
                    }
                };
                requestDispatcher.start();
            }
    }
  
     
  /**
   * Gets the vectors for each request type and transfers the requests for the specific
   * scenario to the requestDispatcher to execute them.
   * 
   * @param index
   * @param search
   * @param retrieve
   * @param tag
   */
  private void executeScenario(Vector<org.w3c.dom.Node> index, Vector<org.w3c.dom.Node> search, Vector<org.w3c.dom.Node> retrieve, Vector<org.w3c.dom.Node> tag) {
 
      NodeList children;
      
      // INDEXING
      for(org.w3c.dom.Node tmp : index){
          children = tmp.getChildNodes();
          if(children == null)
              continue;
          org.w3c.dom.Node currentNode = children.item(0);
          // source
          // TODO : Include the choice of source in a request
          if(((Element)currentNode).getTagName().equals("source")){
              
              int sourceNum = Integer.parseInt(((Text)currentNode.getFirstChild()).getData().trim());
              
              currentNode = children.item(1);
              if(((Element)currentNode).getTagName().equals("directory")){
                  String dir = ((Text)currentNode.getFirstChild()).getData().trim();
                  File directory = new File(dir);
                  if(directory.exists() && directory.isDirectory()){
                      System.out.println("Directory "+dir+ " is being indexed now!");
                      directoryIndexing(directory,sourceNum);
                      doNotify();
                  }else
                      System.out.println("Directory is not valid!");
              }else if(((Element)currentNode).getTagName().equals("single_file")){
                  String sf = ((Text)currentNode.getFirstChild()).getData().trim();
                  File sfile = new File(sf);
                  if(sfile.exists() && sfile.isFile()){
                      System.out.println("File "+sf+ " is being indexed now!");
                      this.driver.execRequests.add(new IndexContentRequest(sfile.getAbsolutePath(),sourceNum));
                      doNotify();
                  }else
                      System.out.println("Single File is not valid!");
                  
              // user    
              }else if(((Element)currentNode).getTagName().equals("user")){
                  children = currentNode.getChildNodes();
                  
                  Map<String, String> profile = new HashMap<String, String>();
                  // user_address
                  currentNode = children.item(0);
                  String udata = ((Text)currentNode.getFirstChild()).getData().trim();
                  profile.put("user address", udata);
                  // user_profile
                  currentNode = children.item(1);
                  children = currentNode.getChildNodes();
                  
                  // <!ELEMENT user_profile (field*|keywords)>
                  currentNode = children.item(0);
                  // keywords
                  if(((Element)currentNode).getTagName().equals("keywords")){
                      udata = ((Text)currentNode.getFirstChild()).getData().trim();
                      // default field
                      profile.put("user description", udata);   
                  // field    
                  }else if(((Element)currentNode).getTagName().equals("field")){
                      String name;
                      for(int i=0; i<children.getLength(); i++){
                          currentNode = children.item(i);
                          // name 
                          name = ((Text)currentNode.getFirstChild().getFirstChild()).getData().trim();
                          // keywords
                          udata = ((Text)currentNode.getLastChild().getFirstChild()).getData().trim();
                          profile.put(name, udata);
                      }
                  }
                  if(!profile.isEmpty()){
                      System.out.println("A user is being indexed now!");
                      this.driver.execRequests.add(new IndexUserRequest(profile,sourceNum));
                      doNotify();
                  }else
                      System.out.println("User profile is empty!");
              }
              
          }else if(((Element)currentNode).getTagName().equals("directory")){
              String dir = ((Text)currentNode.getFirstChild()).getData().trim();
              File directory = new File(dir);
              if(directory.exists() && directory.isDirectory()){
                  System.out.println("Directory "+dir+ " is being indexed now!");
                  directoryIndexing(directory);
                  doNotify();
              }else
                  System.out.println("Directory is not valid!");
          }else if(((Element)currentNode).getTagName().equals("single_file")){
              String sf = ((Text)currentNode.getFirstChild()).getData().trim();
              File sfile = new File(sf);
              if(sfile.exists() && sfile.isFile()){
                  System.out.println("File "+sf+ " is being indexed now!");
                  this.driver.execRequests.add(new IndexContentRequest(sfile.getAbsolutePath()));
                  doNotify();
              }else
                  System.out.println("Single File is not valid!");
              
          // user    
          }else if(((Element)currentNode).getTagName().equals("user")){
          
              children = currentNode.getChildNodes();
              Map<String, String> profile = new HashMap<String, String>();
              
              // user_address
              currentNode = children.item(0);
              String udata = ((Text)currentNode.getFirstChild()).getData().trim();
              profile.put("user address", udata);
              
              // user_profile
              currentNode = children.item(1);
              children = currentNode.getChildNodes();
              
              // <!ELEMENT user_profile (field*|keywords)>
              currentNode = children.item(0);
              
              // keywords
              if(((Element)currentNode).getTagName().equals("keywords")){
                  udata = ((Text)currentNode.getFirstChild()).getData().trim();
                  // default field
                  profile.put("user description", udata);   
                  
                  // field    
              }else if(((Element)currentNode).getTagName().equals("field")){
                  String name;
                  for(int i=0; i<children.getLength(); i++){
                      currentNode = children.item(i);
                      // name 
                      name = ((Text)currentNode.getFirstChild().getFirstChild()).getData().trim();
                      // keywords
                      udata = ((Text)currentNode.getLastChild().getFirstChild()).getData().trim();        
                      profile.put(name, udata); 
                  }   
              }
              
              if(!profile.isEmpty()){
                  System.out.println("A user is being indexed now!");
                  this.driver.execRequests.add(new IndexUserRequest(profile));
                  doNotify();
              }else
                  System.out.println("User profile is empty!");
              }            
              
      }
      
      // Searching 
      // <!ELEMENT search (source?,query,number_of_results)>
      for(org.w3c.dom.Node tmp : search){
          children = tmp.getChildNodes();
          if(children == null)
              continue;
          org.w3c.dom.Node currentNode = children.item(0);
          // source
          // TODO : Include the choice of source in a request
          if(((Element)currentNode).getTagName().equals("source")){
              
              int sourceNum = Integer.parseInt(((Text)currentNode.getFirstChild()).getData().trim());
              
              // query
              // <!ELEMENT query (user,content)>
              currentNode = children.item(1);
              NodeList qchildren = currentNode.getChildNodes();
              
              // user
              currentNode = qchildren.item(0);
              NodeList uchildren = currentNode.getChildNodes();
              // user_address
              currentNode = uchildren.item(0);
              // user_profile
              currentNode = uchildren.item(1);
              
              //content
              currentNode = qchildren.item(1);
              NodeList cchildren = currentNode.getChildNodes();
              // keywords
              currentNode = cchildren.item(0);
              String queryTerms = ((Text)currentNode.getFirstChild()).getData().trim();
              
              // number_of_results
              currentNode = children.item(2);
              int k = Integer.parseInt(((Text)currentNode.getFirstChild()).getData().trim());
              
              // Add the Request for search
              this.driver.execRequests.add(new SearchContentRequest(queryTerms, sourceNum)); // Feed the whole query!
              doNotify();
              
          }else if(((Element)currentNode).getTagName().equals("query")){
              // query
              // <!ELEMENT query (user,content)>
              NodeList qchildren = currentNode.getChildNodes();
              
              // user
              currentNode = qchildren.item(0);
              NodeList uchildren = currentNode.getChildNodes();
              // user_address
              currentNode = uchildren.item(0);
              // user_profile
              currentNode = uchildren.item(1);
              
              //content
              currentNode = qchildren.item(1);
              NodeList cchildren = currentNode.getChildNodes();
              // keywords
              currentNode = cchildren.item(0);
              String queryTerms = ((Text)currentNode.getFirstChild()).getData().trim();
              
              // number_of_results
              currentNode = children.item(1);
              int k = Integer.parseInt(((Text)currentNode.getFirstChild()).getData().trim());
              
              // Add the Request for search
              this.driver.execRequests.add(new SearchContentRequest(queryTerms)); // Feed the whole query!
              doNotify();
          }
      }
      
      for(org.w3c.dom.Node tmp : retrieve){
          children = tmp.getChildNodes();
          if(children == null)
              continue;
      }
      
      for(org.w3c.dom.Node tmp : tag){
          children = tmp.getChildNodes();
          if(children == null)
              continue;
      }
      
  }
  
  
  private Request parseNodeCommand(String lastarg) {
      
      String args[] = lastarg.trim().split(" ");
      for (int i=0; i < args.length; i++){
          if(args[i].equals("show")&& i+2 < args.length) {
              if(args[i+1]==null || args[i+2]==null){
                  return null;
              }else if(args[i+1].equals("pending")){
                  int source = Integer.parseInt(args[i+2]);
                  return new StatsRequest(StatsRequest.PENDING, source);
              }else if(args[i+1].equals("shared")){
                  int source = Integer.parseInt(args[i+2]);
                  return new StatsRequest(StatsRequest.SHARED, source);
              }else if(args[i+1].equals("profiles")){
                  int source = Integer.parseInt(args[i+2]);
                  return new StatsRequest(StatsRequest.PROFILES, source);
              }else{
                  return null;
              }
          }else if(args[i].equals("add")&& i+1 < args.length) {
              // TODO : Implement this!
          }else if(args[i].equals("delete")&& i+1 < args.length) {
              // TODO : Implement this!
          }
      }
    
      return null;
  }
      
      
  /**
   * Used to recursively index every single file in a dir and its subdirs!
   * 
   * @param f
   */
  private void directoryIndexing(File f){
      this.directoryIndexing(f, IndexContentRequest.RANDOMSOURCE);
  }
  
  private void directoryIndexing(File f,int sourceNum){
      File innerf;
      LinkedList dir = new LinkedList();
      dir.add(f);
      while (!dir.isEmpty()) {

          innerf = (File) dir.removeFirst();
          if (innerf.isDirectory()) {
            File[] subs = innerf.listFiles();
            if (subs.length == 0) {
              continue;
            } else {
              dir.addAll(Arrays.asList(subs));
              //dir.addLast(innerf);
            }
          } else {
              // Add the Request for indexing the file
              this.driver.execRequests.add(new IndexContentRequest(innerf.getAbsolutePath(), sourceNum));
          }
      }
  }
  
  /**
   * notify the RequestDispatcher Thread that a request has been received 
   */
  public void doNotify(){
    if(driver!=null){
        // TODO: Check if the monitorobject creates deadlock when multiple requests
        // are issued simultaneously
        synchronized(driver){ 
            driver.wasSignalled = true;
            driver.notify();
        }    
    }
  }
  
  /**
   * PRIVATE CLASS VARIABLES
   */
  private BufferedReader in;
  private Environment env;
  private ContentProfileFactory cpf;
  private String lastarg;
  private ClientDriver driver;
  private Thread requestDispatcher;
  private boolean cliEnv = true;
  private boolean webEnv = false;
  private ClientWebServer cws;
  private int bindPort;
}
