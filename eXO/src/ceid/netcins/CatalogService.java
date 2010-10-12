/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins;

import ceid.netcins.catalog.Catalog;
import ceid.netcins.catalog.CatalogEntry;
import ceid.netcins.catalog.ContentCatalogEntry;
import ceid.netcins.catalog.ScoreCatalog;
import ceid.netcins.catalog.SocialCatalog;
import ceid.netcins.catalog.URLCatalogEntry;
import ceid.netcins.catalog.UserCatalogEntry;
import ceid.netcins.content.ContentField;
import ceid.netcins.content.ContentProfile;
import ceid.netcins.content.ContentProfileFactory;
import ceid.netcins.content.StoredField;
import ceid.netcins.content.TermField;
import ceid.netcins.content.TokenizedField;
import ceid.netcins.messages.FriendApprMessage;
import ceid.netcins.messages.FriendReqMessage;
import ceid.netcins.messages.FriendReqPDU;
import ceid.netcins.messages.InsertPDU;
import ceid.netcins.messages.QueryMessage;
import ceid.netcins.messages.QueryPDU;
import ceid.netcins.messages.ResponsePDU;
import ceid.netcins.messages.RetrieveContMessage;
import ceid.netcins.messages.RetrieveContPDU;
import ceid.netcins.messages.SocialQueryMessage;
import ceid.netcins.messages.SocialQueryPDU;
import ceid.netcins.messages.TagContentMessage;
import ceid.netcins.messages.TagContentPDU;
import ceid.netcins.similarity.Scorer;
import ceid.netcins.similarity.SimilarityRequest;
import ceid.netcins.social.SocService;
import ceid.netcins.social.SocialBookMark;
import ceid.netcins.social.TagCloud;
import ceid.netcins.social.URLBookMark;
import ceid.netcins.user.Friend;
import ceid.netcins.user.FriendRequest;
import ceid.netcins.user.User;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import rice.Continuation;
import rice.Continuation.MultiContinuation;
import rice.Continuation.NamedContinuation;
import rice.Continuation.StandardContinuation;
import rice.environment.logging.Logger;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.IdFactory;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.NodeHandleSet;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastException;
import rice.p2p.past.messaging.CacheMessage;
import rice.p2p.past.messaging.FetchHandleMessage;
import rice.p2p.past.messaging.FetchMessage;
import rice.p2p.past.messaging.InsertMessage;
import rice.p2p.past.messaging.LookupHandlesMessage;
import rice.p2p.past.messaging.LookupMessage;
import rice.p2p.past.messaging.PastMessage;
import rice.persistence.StorageManager;

/**
 *
 * @author andy
 */
public class CatalogService extends DHTService implements SocService {

    // Factory which used to obtain the content profile data
    private ContentProfileFactory cpf;
    
    // User data for the specific node
    private User user;
    
    // Types of printing results
    public static final int CONTENT = 0;
    public static final int USER = 1;
    public static final int HYBRID = 2;

    /**
     * Constructor pushes the appropriate arguments to DHTService 
     * and initializes the Service.
     * 
     */
    public CatalogService(Node node, StorageManager manager, int replicas, String instance, User user){
        super(node,manager,replicas,instance);
        cpf = new ContentProfileFactory();
        this.user = user;
        scorer = new Scorer();
        
        // Start the "Scorer" thread to be waiting!
        // One thread per node!
        scorerThread = new Thread(new Runnable(){
            
            public void run() {
                // Begin the wait-serving loop
                scorer.startScorer();
            }
            
        },"Scorer");
        
        scorerThread.start();

    }
    
    /**
     * Simulator Costructor ONLY
     * Constructor pushes the appropriate arguments to DHTService 
     * and initializes the Service.
     * 
     */
    public CatalogService(Node node, StorageManager manager, int replicas, String instance, Scorer scorer){
        this(node,manager,replicas,instance,null,scorer);
    }
    
    /**
     * Simulator Constructor ONLY!
     * Constructor pushes the appropriate arguments to DHTService 
     * and initializes the Service.
     * 
     */
    public CatalogService(Node node, StorageManager manager, int replicas, String instance, User user, Scorer scorer){
        super(node,manager,replicas,instance);
        cpf = new ContentProfileFactory();
        this.user = user;
        this.scorer = scorer;
    }
    
    /**
     * This is used to create a new user profile. In order to update the user
     * profile a update function is better to be used.
     * 
     * @param m A map with the fields and the corresponding terms
     * @throws java.io.IOException
     */
    public void createUserProfile(Map<String,String> m) throws IOException{
        createUserProfile(m, ContentProfileFactory.DEFAULT_DELIMITER);
    }
    
    /**
     * This is used to create a new user profile. In order to update the user
     * profile a update function is better to be used.
     * 
     * @param m A map with the fields and the corresponding terms
     * @param The delimiter used to split the terms
     * @throws java.io.IOException
     */
    public void createUserProfile(Map<String,String> m, String delimiter) throws IOException{
        user.setUserProfile(cpf.buildProfile(m,delimiter));
    }
    
    /**
     * This is used to create a url profile. 
     * 
     * @param m A map with the fields and the corresponding terms
     * @throws java.io.IOException
     */
    public ContentProfile createURLProfile(Map<String,String> m) throws IOException{
        return cpf.buildProfile(m);
    }
    
    /**
     * This is used to create a content profile when the terms are given in a Map
     * data structure. 
     * 
     * @param m A map with the fields and the corresponding terms
     * @throws java.io.IOException
     */
    public ContentProfile createContentProfile(Map<String,String> m, String delimiter) throws IOException{
        if(m.containsKey("Identifier")){
            String checksum = this.factory.buildId(m.get("Identifier")).toStringFull();
            m.put("SHA-1", checksum);
        }
        return cpf.buildProfile(m,delimiter);
    }
    
    /**
     * This is used to create the tag tokens and profile. 
     * This is used to filter the tags within the tokenizer (StopWords etc.)
     * 
     * @param m A string with all tags we want to apply on some object
     * @throws java.io.IOException
     */
    public String[] tokenizeTags(String tags) throws IOException{
        TreeSet<String> ts = cpf.termSet(new StringReader(tags));
        return (String[]) ts.toArray(new String[ts.size()]);
    }
    
    /**
     * This method sends a request for friendship to an other user of the
     * network specified by the user name string. Message is a custom string
     * formed by the source user to say hello.
     * Warning! In order to operate properly nodes must be assigned nodeIDs
     * created by the user unique names (e.g. email address).
     * Warning! This method should not be used in real deployment. 
     * Instead use the next version with uid.
     * 
     * @deprecated
     * @param userUniqueName
     * @param message
     */
    public void friendRequest(String userUniqueName, String message, final Continuation command){
         
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not be registered yet!");
            return;
        }
        
        final Id destuid = factory.buildId(userUniqueName);
        
        FriendReqPDU frPDU = new FriendReqPDU(message, this.user.getUsername());
        
        // Issue a lookup request to the uderline DHT service
        lookup(destuid, false, frPDU,
                  new NamedContinuation("FriendReqMessage (FriendReqPDU) for " + destuid, command){

                public void receiveResult(Object result) {
                    if(result instanceof Boolean){
                            System.out.println("\n\nFriend request sent to user with UID : "+destuid+", result code : "+result);
                            
                            // Now we can add the UID to the queue for pending approvals!
                            user.addPendingFAppr(destuid);
                            
                            parent.receiveResult(result);
                    }else{
                        System.out.println("\n\nFriend request sent to user with UID : "+destuid+", but something went wrong to the storing process");
                    }
                }

                public void receiveException(Exception result) {
                    System.out.println("\n\nFriend request sent to user with UID : "+destuid+", result (exception) code : "+result.getMessage());
                    parent.receiveException(result);
                }
          });
        
    }
    
    /**
     * This method sends a request for friendship to an other user of the
     * network specified by the uid. Message is a custom string
     * formed by the source user to say hello.
     * Warning! In order to operate properly nodes must be assigned nodeIDs
     * created by the user unique names (e.g. email address).
     * 
     * @param userUniqueName
     * @param message
     */
    public void friendRequest(Id uid, String message, final Continuation command){
         
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not be registered yet!");
            return;
        }
        
        final Id destuid = uid;
        
        FriendReqPDU frPDU = new FriendReqPDU(message, this.user.getUsername());
        
        // Issue a lookup request to the uderline DHT service
        lookup(destuid, false, frPDU,
                  new NamedContinuation("FriendReqMessage (FriendReqPDU) for " + destuid, command){

                public void receiveResult(Object result) {
                    if(result instanceof Boolean){
                            System.out.println("\n\nFriend request sent to user with UID : "+destuid+", result code : "+result);
                            
                            // Now we can add the UID to the queue for pending approvals!
                            user.addPendingFAppr(destuid);
                            
                            parent.receiveResult(result);
                    }else{
                        System.out.println("\n\nFriend request sent to user with UID : "+destuid+", but something went wrong to the storing process");
                    }
                }

                public void receiveException(Exception result) {
                    System.out.println("\n\nFriend request sent to user with UID : "+destuid+", result (exception) code : "+result.getMessage());
                    parent.receiveException(result);
                }
          });
        
    }
    
    
    /**
     * This method sends an approval message for friendship to an other user 
     * of the network specified by the uid. Message is an empty message
     * formed by the source user to say "I approve your friendship request".
     * Warning! In order to operate properly nodes must be assigned nodeIDs
     * created by the user unique names (e.g. email address).
     * 
     * @param freq The friend request for approval
     * @param command The callback that must be executed when we return
     */
    public void friendApproval(final FriendRequest freq, final Continuation command){
         
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not be registered yet!");
            return;
        }
        
        final Id destuid = freq.getUID();
        
        // Issue a lookup request to the uderline DHT service
        lookup(destuid, false, FriendApprMessage.TYPE,
                  new NamedContinuation("FriendApprMessage for " + destuid, command){

                public void receiveResult(Object result) {
                    if(result instanceof Boolean){
                            System.out.println("\n\nFriend approval sent to user with UID : "+destuid+", result code : "+result);
                            
                            // Now we know that message of approval has sent successfully and we can remove the freq!
                            user.removePendingFReq(freq);
                            
                            // Users are now FRIENDS!
                            // TODO : Maybe include IP address.
                            user.addFriend(new Friend(freq.getUID(),freq.getFriendReqPDU().getScreenName()));
                            
                            parent.receiveResult(result);
                    }else{
                        System.out.println("\n\nFriend approval sent to user with UID : "+destuid+", but something went wrong to the dest node process");
                    }
                }

                public void receiveException(Exception result) {
                    System.out.println("\n\nFriend approval sent to user with UID : "+destuid+", result (exception) code : "+result.getMessage());
                    parent.receiveException(result);
                }
          });
    }
    
    
    /**
     * This method is used to retrieve the original (or pseydodata for simulation)
     * and maybe some tagclouds from a user node. In order to know where we should
     * travel to fetch the data a nodeId and a data checksum must have been obtained
     * previously. This can be done for exmple by using a search request.
     * 
     * @param uid User unique id (destination node).
     * @param contentId The Id of the taging content (checksum, SHA-1 of synonyms etc.).
     * @param clouds This handles whether tag clouds will be returned or not.
     * @param command An asychronous command which will be executed on return.
     */
    public void retrieveContent(Id uid, Id contentId, final boolean clouds, final Continuation command){
         
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not be registered yet!");
            return;
        }
        
        final Id destuid = uid;
        
        // Issue a lookup request to the uderline DHT service
        lookup(destuid, false, new RetrieveContPDU(contentId),
                  new NamedContinuation("RetrieveContMessage (RetrieveContPDU) for " + destuid, command){

                public void receiveResult(Object result) {
                    if(result instanceof TagCloud){
                            TagCloud ve = (TagCloud) result;
                            System.out.println("\n\nRetrieve Content request sent to user with UID : "+destuid+", result code : success");
                            // Present The Tag Clouds
                            System.out.println(ve.toString());
                            parent.receiveResult(result);       
                           
                    }else if(result instanceof Boolean){
                        System.out.println("\n\nRetrieve Content request sent to user with UID : "+destuid+", result code : success");
                        parent.receiveResult(result); 
                    }else{
                        System.out.println("\n\nRetrieve Content request sent to user with UID : "+destuid+", but something went wrong to the process");
                    }
                }

                public void receiveException(Exception result) {
                    System.out.println("\n\nRetrieve Content request sent to user with UID : "+destuid+", result (exception) code : "+result.getMessage());
                    parent.receiveException(result);
                }
          });
        
    }
       
    /**
     * This method sends a set of tags for a specific content object
     * (contentId) to its owner's node specified by the uid.
     * Warning! In order to operate properly nodes must be assigned nodeIDs
     * created by the user unique names (e.g. email address).
     * 
     * @param uid User unique id (destination node).
     * @param contentId The Id of the taging content (checksum, SHA-1 of synonyms etc.).
     * @param tags An array of the tags which will be applied to the content object.
     * @param command An asychronous command which will be executed on return.
     */
    public void tagContent(Id uid, Id contentId, final String[] tags, final Continuation command){
         
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not be registered yet!");
            return;
        }
        
        final Id destuid = uid;
        
        TagContentPDU tcPDU = new TagContentPDU(contentId, tags);
        
        // Issue a lookup request to the uderline DHT service
        lookup(destuid, false, tcPDU,
                  new NamedContinuation("TagContentMessage (TagContentPDU) for " + destuid, command){

                public void receiveResult(Object result) {
                    if(result instanceof ContentCatalogEntry){
                            ContentCatalogEntry cce = (ContentCatalogEntry) result;
                            
                            // debugging only System.out.println("\n\nTagContent request sent to user with UID : "+destuid+", result code : "+result);
                            
                            // Now we can add the ContentCatalogEntry to each SocialCatalog (for each tag)
                            // Tagers inverted list.
                            for(String tag : tags){
                                SocialCatalog scat = user.getTagContentList().get(tag);
                                if(scat == null)
                                    scat = new SocialCatalog(tag);
                                Vector ccatEntries = scat.getContentCatalogEntries();
                                if(!ccatEntries.contains(cce))
                                    scat.addContentCatalogEntry(cce);
                                user.addTagContentList(tag, scat);
                            }
                            parent.receiveResult(result);
                    }else{
                        System.out.println("\n\nTagContent request sent to user with UID : "+destuid+", but something went wrong to the storing process");
                    }
                }

                public void receiveException(Exception result) {
                    System.out.println("\n\nTagContent request sent to user with UID : "+destuid+", result (exception) code : "+result.getMessage());
                    parent.receiveException(result);
                }
          });
        
    }
    
    /**
     * Index fuction for the specified URL profile (set of tags).
     * It is translated into an insert request to the underline network.
     * An URLCatalogEntry is added to the catalog node for the URL's TID.
     * 
     * @param url The destination address obtained by the URL's TID
     * @param tags The description keywords in a ContentProfile object
     * @param command Asynchronous commands to be executed after return
     */ 
    public void indexURL(URL url, ContentProfile tags, final Continuation command){
         
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not be registered yet!");
            return;
        }
        
        final Id tid = factory.buildId(url.toString());
        
        // Check the social bookmarks in the local user space first.
        // 1.if there exist, update it
        ContentProfile oldtags = tags;
        Map<Id,SocialBookMark> bookMarks = user.getBookMarks();
        if(bookMarks.containsKey(tid) && bookMarks.get(tid) instanceof URLBookMark){
            SocialBookMark ubm = bookMarks.get(tid);
            oldtags = ubm.getTags();
            List<ContentField> oldfields = oldtags.getFields();
            List<ContentField> fields = tags.getFields();
            Iterator<ContentField> it1 = oldfields.listIterator();
            Iterator<ContentField> it2 = fields.listIterator();
            ContentField cf1,cf2;
            // Old profile loop
            while(it1.hasNext()){
                cf1 = it1.next();
                // new tags loop
                while(it2.hasNext()){
                    cf2 = it2.next();
                    if(cf1.getFieldName().equals(cf2.getFieldName())){
                        // TODO : Check THIS 
                        // TokenizedField should be more flexible (switch to vectors instead of static tables)
                        if(cf1 instanceof TokenizedField && cf2 instanceof TokenizedField){ // merge the tags!!!!!
                            TokenizedField tf1 = (TokenizedField) cf1;
                            TokenizedField tf2 = (TokenizedField) cf2;
                            // TODO : The tf array now will contain arbitrary positions of term freqs
                            TreeSet<String> tags1 = new TreeSet<String>(Arrays.asList(tf1.getTerms()));
                            TreeSet<String> tags2 = new TreeSet<String>(Arrays.asList(tf2.getTerms()));
                            tags1.addAll(tags2);
                            tf1.setTerms(tags1.toArray(tf1.getTerms()));
                        }else {
                            // TODO : IMPLEMENT THIS 
                        }
                    }else{
                        oldtags.add(cf2);
                    }
                }
            }
        // 2.if there is not, create it    
        }else{
            user.addBookMark(tid, new URLBookMark(url,tags));
        }
        
        // Our data which will travel through the network!!!    
        URLCatalogEntry ue = new URLCatalogEntry(user.getUID(), oldtags, user.getUserProfile(), url);
        PastContent pdu = new InsertPDU(tid,ue);
        // Here is the message post
        // Issue an insert request to the uderline DHT service
        insert(pdu,
                  new NamedContinuation("InsertMessage (InsertPDU - URLCatalogEntry) for " + tid, command){

                public void receiveResult(Object result) {
                    if(result instanceof Boolean[]){
                            System.out.println("\n\nBookmark URL index process, result code : "+result);
                            
                            parent.receiveResult(result);
                    }else{
                        System.out.println("\n\nBookmark URL index process, result : something went wrong to the storing process");
                    }
                }

                public void receiveException(Exception result) {
                    System.out.println("\n\nBookmark URL index process, result (exception) code : "+result.getMessage());
                    parent.receiveException(result);
                }
          });
        
    }
    
    /**
     * This method uses a ContentProfileFactory functions to
     * form a set of indexing terms for the specific File
     * and inserts a ContentCatalogEntry to every node which is 
     * responsible for the specific TID.
     * 
     * @param file
     */
    public void indexContent(final File file, final Continuation command){
        
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not be registered yet!");
            return;
        }
        
        try{
            // Metadata extraction, Field creation, Analysis, Tokenization, TF computation
            final ContentProfile cp = cpf.buildContentProfile(file);
            if(cp == null){
                System.out.println("The content profile is empty!");
                return;
            }
            
            // Check in the user's sharedContent map to see if we have already indexed it!
            List<ContentField> fields = cp.getFields();
            Iterator<ContentField> it = fields.listIterator();
            ContentField cf;
            Id checksum=null;
            while(it.hasNext()){
                cf = it.next();
                if(cf.getFieldName().equals("SHA-1")){
                    checksum = factory.buildIdFromToString( ((StoredField)cf).getFieldData() );
                    if(user.getSharedContent().containsKey(checksum)){
                        System.out.println("File "+file.toString()+" has already been indexed!");
                        return;
                    }
                }
            }
            // Convinience for use in MultiContinuation inner Class
            final Id chsum = checksum;
            
            // Our data which will travel through the network!!!
            ContentCatalogEntry cce = new ContentCatalogEntry(user.getUID(), cp, user.getUserProfile());

            // Create MultiContinuation
            // TODO : In order to index once every term we should use a Set instead of Vector!!!
            Vector<String> indexingTerms = new Vector<String>(); // holds the indexing terms (Strings)
            it = fields.listIterator();
            while(it.hasNext()){
                cf = it.next();
                if(cf instanceof TermField){ // These fields are indexed as whole
                    TermField termf = (TermField)cf;
                    indexingTerms.add(termf.getFieldData());

                }else if(cf instanceof TokenizedField){ // These fields are indexed
                    TokenizedField tokf = (TokenizedField) cf;
                    String[] terms = tokf.getTerms();
                    for(int i=0; i<terms.length; i++){
                        indexingTerms.add(terms[i]);
                    }    
                }
            }

            int termscount = indexingTerms.size();
            if( termscount == 0){

                // TODO : Maybe throw some Exception 
                System.out.println("No terms to index!");
                return;
            }else{

                MultiContinuation multi = new MultiContinuation(command, termscount) {

                    public boolean isDone() throws Exception {
                        int numSuccess = 0;
                        for (int i=0; i<haveResult.length; i++)
                            if ((haveResult[i]) && (result[i] instanceof Boolean[]))
                                numSuccess++;

                        if (numSuccess >= (SUCCESSFUL_INSERT_THRESHOLD * haveResult.length))
                            return true;

                        if (super.isDone()) {
                            for (int i=0; i<result.length; i++)    
                                if (result[i] instanceof Exception)
                                    if (logger.level <= Logger.WARNING) logger.logException("result["+i+"]:",(Exception)result[i]);
                            throw new PastException("Had only " +  numSuccess + " successful inserted indices out of " + result.length + " - aborting.");
                        }

                        return false;
                    }

                    // This is called once we have sent all the messages (signaling Success).
                    public Object getResult() {
                        Boolean[] b = new Boolean[result.length];
                        for (int i=0; i<b.length; i++)
                            b[i] = new Boolean((result[i] == null) || result[i] instanceof Boolean[]);
                        
                        // As we have sent all the necessary data, add the file to the user Map
                        if(chsum!=null){
                            user.addSharedContent(chsum, file);
                            user.addSharedContentProfile(chsum, cp);
                        }
                        
                        return b;
                    }
                };

                int index = 0;
                Id tid;
                String term;

                while(!indexingTerms.isEmpty()) {

                    term = indexingTerms.remove(0);
                    tid = factory.buildId(term);                
                    PastContent pdu = new InsertPDU(tid,cce);
                    Continuation c = new NamedContinuation("InsertMessage (InsertPDU) for " + tid, multi.getSubContinuation(index));
                    index++;
                    insert(pdu,c); // Here is the message post
                }
                // End of Multicontinuation           
            }   
        }catch(IOException e){
            e.getStackTrace();
        }
    }
    
        /**
     * This function indexes the user's profile keywords around the network.
     * 
     * @param command
     */
    public void indexUser(final Continuation command){
        
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not been registered yet!");
            return;
        }
        
        // Fetch the user profile
        ContentProfile cp = user.getUserProfile();
        if(cp == null){
            System.out.println("User has an empty profile!");
            return;
        }

        // Our data which will travel through the network!!!
        UserCatalogEntry uce = new UserCatalogEntry(user.getUID(), cp);

        // Create MultiContinuation
        List<ContentField> fields = cp.getFields();
        Iterator<ContentField> it = fields.iterator();
        ContentField cf;
        Vector<String> indexingTerms = new Vector<String>(); // holds the indexing terms (Strings)
        while(it.hasNext()){
            cf = it.next();
            if(cf instanceof TermField){ // These fields are indexed as whole
                TermField termf = (TermField)cf;
                indexingTerms.add(termf.getFieldData());

            }else if(cf instanceof TokenizedField){ // These fields are indexed
                TokenizedField tokf = (TokenizedField) cf;
                String[] terms = tokf.getTerms();
                for(int i=0; i<terms.length; i++){
                    indexingTerms.add(terms[i]);
                }    
            }
        }

        int termscount = indexingTerms.size();
        if( termscount == 0){

            // TODO : Maybe throw some Exception 
            System.out.println("No terms to index!");
            return;
        }else{

            MultiContinuation multi = new MultiContinuation(command, termscount) {

                public boolean isDone() throws Exception {
                    int numSuccess = 0;
                    for (int i=0; i<haveResult.length; i++)
                        if ((haveResult[i]) && (result[i] instanceof Boolean[]))
                            numSuccess++;

                    if (numSuccess >= (1.0 * haveResult.length))
                        return true;

                    if (super.isDone()) {
                        for (int i=0; i<result.length; i++)    
                            if (result[i] instanceof Exception)
                                if (logger.level <= Logger.WARNING) logger.logException("result["+i+"]:",(Exception)result[i]);
                        throw new PastException("Had only " +  numSuccess + " successful inserted indices out of " + result.length + " - aborting.");
                    }

                    return false;
                }

                public Object getResult() {
                    Boolean[] b = new Boolean[result.length];

                    for (int i=0; i<b.length; i++){
                        if(result[i]!=null && result[i] instanceof Boolean[]){
                            // Check the replicas' return values for true to return true or false
                            Boolean temp[] = (Boolean[])result[i];
                            b[i] = new Boolean(false);
                            // We want all the replicas to have been indexed (so all true)
                            int j;
                            for(j=0; j<temp.length; j++){
                                if(temp[j]==false)
                                    break;
                            }
                            // HERE WE WANT 100% SUCCESS IN REPLICATION INDEXING
                            if(j==temp.length)
                                b[i] = new Boolean(true);
                        }else{ // If it is null or it is not of type Boolean[]
                            b[i] = new Boolean(false);
                        }
                    }
                    return b;
                }
            };

            int index = 0;
            Id tid;
            String term;

            while(!indexingTerms.isEmpty()) {

                term = indexingTerms.remove(0);
                tid = factory.buildId(term);                
                PastContent pdu = new InsertPDU(tid,uce);
                Continuation c = new NamedContinuation("InsertMessage (InsertPDU) for " + tid, multi.getSubContinuation(index));
                index++;
                insert(pdu,c); // Here is the message post
            }
            // End of Multicontinuation           
        }
    }
    
    /**
     * This function indexes the content profile keywords around the network.
     * It is used when pseydo content is going to be indexed.
     * 
     * @param command
     */
    public void indexPseydoContent(final ContentProfile cp, final Continuation command){
        
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not been registered yet!");
            return;
        }
   
        // Handle the content profile, check if it is already indexed
        if(cp == null){
            System.out.println("The content profile is empty!");
            return;
        }

        // Check in the user's sharedContent map to see if we have already indexed it!
        List<ContentField> fields = cp.getFields();
        Iterator<ContentField> it = fields.listIterator();
        ContentField cf;
        Id checksum=null;
        String identifier = "";
        while(it.hasNext()){
            cf = it.next();
            if(cf.getFieldName().equals("Identifier")){
                identifier = ((TermField)cf).getFieldData();
            }
            if(cf.getFieldName().equals("SHA-1")){
                checksum = factory.buildIdFromToString( ((StoredField)cf).getFieldData() );
                if(user.getSharedContent().containsKey(checksum)){
                    System.out.println("File has already been indexed!");
                    return;
                }
            }
        }
        // Convinience for use in MultiContinuation inner Class
        final Id chsum = checksum;

        // Our data which will travel through the network!!!
        ContentCatalogEntry cce = new ContentCatalogEntry(user.getUID(), cp, user.getUserProfile());

        // Create MultiContinuation
        // TODO : In order to index once every term we should use a Set instead of Vector!!!
        Vector<String> indexingTerms = new Vector<String>(); // holds the indexing terms (Strings)
        it = fields.listIterator();
        while(it.hasNext()){
            cf = it.next();
            if(cf instanceof TermField){ // These fields are indexed as whole
                TermField termf = (TermField)cf;
                indexingTerms.add(termf.getFieldData());

            }else if(cf instanceof TokenizedField){ // These fields are indexed
                TokenizedField tokf = (TokenizedField) cf;
                String[] terms = tokf.getTerms();
                for(int i=0; i<terms.length; i++){
                    indexingTerms.add(terms[i]);
                }    
            }
        }

        int termscount = indexingTerms.size();
        if( termscount == 0){

            // TODO : Maybe throw some Exception 
            System.out.println("No terms to index!");
            return;
        }else{

            final String filename = identifier;
            MultiContinuation multi = new MultiContinuation(command, termscount) {

                public boolean isDone() throws Exception {
                    int numSuccess = 0;
                    for (int i=0; i<haveResult.length; i++)
                        if ((haveResult[i]) && (result[i] instanceof Boolean[]))
                            numSuccess++;

                    if (numSuccess >= (1.0 * haveResult.length))
                        return true;

                    if (super.isDone()) {
                        for (int i=0; i<result.length; i++)    
                            if (result[i] instanceof Exception)
                                if (logger.level <= Logger.WARNING) logger.logException("result["+i+"]:",(Exception)result[i]);
                        throw new PastException("Had only " +  numSuccess + " successful inserted indices out of " + result.length + " - aborting.");
                    }

                    return false;
                }

                // This is called once we have sent all the messages (signaling Success).
                public Object getResult() {
                    Boolean[] b = new Boolean[result.length];

                    for (int i=0; i<b.length; i++){
                        if(result[i]!=null && result[i] instanceof Boolean[]){
                            // Check the replicas' return values for true to return true or false
                            Boolean temp[] = (Boolean[])result[i];
                            b[i] = new Boolean(false);
                            // We want all the replicas to have been indexed (so all true)
                            int j;
                            for(j=0; j<temp.length; j++){
                                if(temp[j]==false)
                                    break;
                            }
                            // HERE WE WANT 100% SUCCESS IN REPLICATION INDEXING
                            if(j==temp.length)
                                b[i] = new Boolean(true);
                        }else{ // If it is null or it is not of type Boolean[]
                            b[i] = new Boolean(false);
                        }
                    }

                    // As we have sent all the necessary data, add the file to the user Map
                    if(chsum!=null){
                        user.addSharedContent(chsum, new File(filename));
                        user.addSharedContentProfile(chsum, cp);
                    }

                    return b;
                }
            };

            int index = 0;
            Id tid;
            String term;

            while(!indexingTerms.isEmpty()) {

                term = indexingTerms.remove(0);
                tid = factory.buildId(term);                
                PastContent pdu = new InsertPDU(tid,cce);
                Continuation c = new NamedContinuation("InsertMessage (InsertPDU) for " + tid, multi.getSubContinuation(index));
                index++;
                insert(pdu,c); // Here is the message post
            }
            // End of Multicontinuation           
        }   
    }
    
    /**
     * This method uses a query to search in the overlay network for occurences
     * of the specific query terms. These terms may refer to a Content object
     * indexed in the network or some User or even both of these types.
     * Respectively, we have three query types : CONTENTQUERY, USERQUERY, 
     * HYBRIDQUERY.
     * New Feature: URLQUERY type is also offered now
     * 
     * @param queryType
     * @param queryOld
     * @param k The number of results which are going to be returned as a list.
     * @param command
     */
    public void searchQuery(final int queryType, final String queryOld, final int k, final Continuation command){
        searchQuery(queryType, queryOld, k, ContentProfileFactory.DEFAULT_DELIMITER, command);
    }
   
    /**
     * This method uses a query to search in the overlay network for occurences
     * of the specific query terms. These terms may refer to a Content object
     * indexed in the network or some User or even both of these types.
     * Respectively, we have three query types : CONTENTQUERY, USERQUERY, 
     * HYBRIDQUERY.
     * New Feature: URLQUERY type is also offered now
     * 
     * @param queryType
     * @param queryOld
     * @param k The number of results which are going to be returned as a list.
     * @param delimiter The delimiter for query terms
     * @param command
     */
    public void searchQuery(final int queryType, final String queryOld, final int k, final String delimiter, final Continuation command){
        
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not be registered yet!");
            return;
        }
        
        // Query Parsing 
        // TODO : This should be done with more proffesional Classes using JavaCC
        String query = queryOld;//.trim();
        if(query ==null || query.equals("")){
            System.out.println("I got an empty Query!");
            return;
        }
        String[] qterms = query.split(delimiter);
        
        MultiContinuation multi = new MultiContinuation(command, qterms.length) {

            public boolean isDone() throws Exception {
                int numSuccess = 0;
                for (int i=0; i<haveResult.length; i++)
                    if ((haveResult[i]) && (result[i] == null) || (result[i] instanceof ResponsePDU))  // The check "instanceof" is important
                        numSuccess++;

                if (numSuccess >= (1.0 * haveResult.length))
                    return true;

                if (super.isDone()) {
                    for (int i=0; i<result.length; i++)    
                        if (result[i] instanceof Exception)
                            if (logger.level <= Logger.WARNING) logger.logException("result["+i+"]:",(Exception)result[i]);
                    throw new PastException("Had only " +  numSuccess + " successful lookups out of " + result.length + " - aborting.");
                }
                return false;
            }

            public Object getResult() {
                return result;
            }
        };
        
        Id querytid = null;
        // Iterate to lookup for every term in query!
        for(int i=0; i<qterms.length; i++){
            // Compute each terms TID
            querytid = factory.buildId(qterms[i]);
            
            final int num = i;
            final Id tid = querytid;
            final QueryPDU qPDU;
            if(queryType == QueryPDU.CONTENT_ENHANCEDQUERY || queryType == QueryPDU.USER_ENHANCEDQUERY 
                    || queryType == QueryPDU.HYBRID_ENHANCEDQUERY ){
                qPDU = new QueryPDU(qterms, queryType, k, this.user.getUserProfile());
            }else{
                qPDU = new QueryPDU(qterms, queryType, k);
            }
                
            // Issue a lookup request to the uderline DHT service
            lookup(querytid, false, qPDU,
                      new NamedContinuation("QueryMessage (QueryPDU) for " + querytid, multi.getSubContinuation(i)){

                    public void receiveResult(Object result) {
                        // DEBUGGING System.out.println("\n\nQuery : "+queryOld+", #"+num+" result (success) for TID = "+tid);
                        if(result instanceof ResponsePDU){
                           // System.out.println(printQueryResults((Catalog)result));
                        }else    
                            System.out.println("Result : "+result);
                        parent.receiveResult(result);
                    }

                    public void receiveException(Exception result) {
                        System.out.println("Query : "+queryOld+", #"+num+" result (error) "+result.getMessage());
                        parent.receiveException(result);
                    }
              });
        }
    }
    
     
    /**
     * This method uses a query of terms to search in every visited node. 
     * Social Tags are the target. Specifically, in the set of nodes we 
     * visit (e.g. friends, neighbors etc.), Social Catalogs of every tag
     * term are checked for Content or User entries, and the most relevant
     * in some manner are returned.
     * 
     * 
     * Respectively, we have three query types : CONTENTQUERY, USERQUERY, 
     * HYBRIDQUERY.
     * New Feature: URLQUERY type is also offered now
     * 
     * @param queryType
     * @param queryOld
     * @param command
     */
    public void searchSocialTagsQuery(final int queryType, final String[] tags, final String[] userIds, final Continuation command){
        
        // TODO : maybe an Exception is needed here to be thrown
        if(this.user == null){
            System.out.println("User has not be registered yet!");
            return;
        }
        
        MultiContinuation multi = new MultiContinuation(command, userIds.length) {

            public boolean isDone() throws Exception {
                int numSuccess = 0;
                for (int i=0; i<haveResult.length; i++)
                    if ((haveResult[i]) && (result[i] instanceof Vector))  // The check "instanceof" is important, The vector of social catalogs
                        numSuccess++;

                if (numSuccess >= (SUCCESSFUL_INSERT_THRESHOLD * haveResult.length))
                    return true;

                if (super.isDone()) {
                    for (int i=0; i<result.length; i++)    
                        if (result[i] instanceof Exception)
                            if (logger.level <= Logger.WARNING) logger.logException("result["+i+"]:",(Exception)result[i]);
                    throw new PastException("Had only " +  numSuccess + " successful lookups out of " + result.length + " - aborting.");
                }
                return false;
            }

            public Object getResult() {
                Boolean[] b = new Boolean[result.length];
                for (int i=0; i<b.length; i++)
                    b[i] = new Boolean((result[i] == null) || result[i] instanceof Vector);    // The check "instanceof" is important
                return b;
            }
        };
        
        Id destId = null;
        // Iterate to lookup for every node we want to visit!
        for(int i=0; i<userIds.length; i++){
            // Compute each terms TID
            destId = factory.buildIdFromToString(userIds[i]);
            
            final int num = i;
            final Id uid = destId;
            final SocialQueryPDU sqPDU;
            if(queryType == QueryPDU.CONTENT_ENHANCEDQUERY || queryType == QueryPDU.USER_ENHANCEDQUERY 
                    || queryType == QueryPDU.HYBRID_ENHANCEDQUERY ){
                sqPDU = new SocialQueryPDU(tags,queryType, this.user.getUserProfile());
            }else{
                sqPDU = new SocialQueryPDU(tags,queryType);
            }
                
            // Issue a lookup request to the uderline DHT service
            lookup(destId, false, sqPDU,
                      new NamedContinuation("SocialQueryMessage (SocialQueryPDU) for " + destId, multi.getSubContinuation(i)){

                    public void receiveResult(Object result) {
                        System.out.println("\n\nSocialTagsQuery  : "+tags.toString()+", #"+num+" result (success) for destination ID = "+uid);
                        if(result instanceof Vector){
                            Vector<SocialCatalog> v = (Vector)result;
                            Iterator<SocialCatalog> it = v.iterator();
                            while(it.hasNext())
                                System.out.println(it.next());
                                //printQueryResults(it.next())
                        }else    
                            System.out.println("Result : "+result);
                        parent.receiveResult(result);
                    }

                    public void receiveException(Exception result) {
                        System.out.println("SocialTagsQuery : "+tags.toString()+", #"+num+" result (error) "+result.getMessage());
                        parent.receiveException(result);
                    }
              });
        }
    }
    
    
    
    /**
     * Utility function to print the results returned by the search query.
     * Each Catalog contains userCatalogEntries and contentCatalogEntries,
     * so we must check both parts. Caution! Only one of these vectors can
     * be non null!
     * 
     * @param cat
     * @return
     */
    private String printQueryResults(Catalog cat){
        
        StringBuffer buffer=new StringBuffer();
        if(cat.getURLCatalogEntries()!=null && !cat.getURLCatalogEntries().isEmpty()){ // URLContentCatalogEntry PART
            Iterator<URLCatalogEntry> it = cat.getURLCatalogEntries().iterator();
            URLCatalogEntry uce;
            ContentProfile cprof;
            List<ContentField> listFields;
            Iterator<ContentField> itf;
            ContentField cf;
            buffer.append("\n\n*** URL Tags/Descriptions ***\n");
            int i=0;
            // For every CatalogEntry
            while(it.hasNext()){
                buffer.append("\n\n*** Entry "+i+" ***\n");
                uce = it.next();
                cprof = uce.getContentProfile();
                listFields = cprof.getFields();
                itf = listFields.iterator();
                // Fore every ContentField
                while(itf.hasNext()){
                    cf = itf.next();
                    if(cf instanceof TermField){
                        TermField tf = (TermField)cf;
                        buffer.append(""+tf.getFieldName()+" : "+tf.getFieldData()+", \n");
                    }else if(cf instanceof StoredField){
                        StoredField sf = (StoredField)cf;
                        buffer.append(""+sf.getFieldName()+" : "+sf.getFieldData()+", \n");
                    }else{
                        // TODO : Fill in the tokenized fields we want to display
                        // debuging only
                        TokenizedField tf = (TokenizedField) cf;
                        String [] tfs = tf.getTerms();
                        buffer.append(""+tf.getFieldName()+" : (");
                        for(int j=0; j<tfs.length; j++){
                            buffer.append(", "+tfs[j]);
                        }
                        buffer.append("), \n");
                    }
                }
                buffer.append("User : "+uce.getUID()+"\n");
                if(cat instanceof ScoreCatalog)
                    buffer.append("Score : "+((ScoreCatalog)cat).getScores().get(i).toString());
                i++;
            }
            
        }else if(cat.getContentCatalogEntries()!=null && !cat.getContentCatalogEntries().isEmpty()){ // ContentCatalogEntry PART
            Iterator<ContentCatalogEntry> it = cat.getContentCatalogEntries().iterator();
            ContentCatalogEntry cce;
            ContentProfile cprof;
            List<ContentField> listFields;
            Iterator<ContentField> itf;
            ContentField cf;
            
            int i=0;
            // For every CatalogEntry
            while(it.hasNext()){
                buffer.append("\n\n*** Entry "+i+" ***\n");
                cce = it.next();
                cprof = cce.getContentProfile();
                listFields = cprof.getFields();
                itf = listFields.iterator();
                // Fore every ContentField
                while(itf.hasNext()){
                    cf = itf.next();
                    if(cf instanceof TermField){
                        TermField tf = (TermField)cf;
                        buffer.append(""+tf.getFieldName()+" : "+tf.getFieldData()+", \n");
                    }else if(cf instanceof StoredField){
                        StoredField sf = (StoredField)cf;
                        buffer.append(""+sf.getFieldName()+" : "+sf.getFieldData()+", \n");
                    }else{
                        // TODO : Fill in the tokenized fields we want to display
                        // debuging only
                        TokenizedField tf = (TokenizedField) cf;
                        String [] tfs = tf.getTerms();
                        buffer.append(""+tf.getFieldName()+" : (");
                        for(int j=0; j<tfs.length; j++){
                            buffer.append(", "+tfs[j]);
                        }
                        buffer.append("), \n");
                    }
                }
                buffer.append("User : "+cce.getUID()+"\n");
                if(cat instanceof ScoreCatalog)
                    buffer.append("Score : "+((ScoreCatalog)cat).getScores().get(i).toString());
                i++;
            }
            
        }else if(cat.getUserCatalogEntries()!=null && !cat.getUserCatalogEntries().isEmpty()){   // UserCatalogEntry PART
            Iterator<UserCatalogEntry> it = cat.getUserCatalogEntries().iterator();
            UserCatalogEntry uce;
            ContentProfile cprof;
            List<ContentField> listFields;
            Iterator<ContentField> itf;
            ContentField cf;
            
            int i=0;
            // For every CatalogEntry
            while(it.hasNext()){
                buffer.append("\n\n"+i+". ");
                uce = it.next();
                buffer.append("User : "+uce.getUID().toStringFull()+"\n");
                cprof = uce.getUserProfile();
                listFields = cprof.getFields();
                itf = listFields.iterator();
                // Fore every ContentField
                while(itf.hasNext()){
                    cf = itf.next();
                    if(cf instanceof TermField){
                        TermField tf = (TermField)cf;
                        buffer.append("  "+tf.getFieldName()+" : "+tf.getFieldData()+",");
                    }else if(cf instanceof StoredField){
                        StoredField sf = (StoredField)cf;
                        buffer.append("  "+sf.getFieldName()+" : "+sf.getFieldData()+",");
                    }else{
                        // TODO : Fill in the tokenized fields we want to display
                        // debuging only
                        TokenizedField tf = (TokenizedField) cf;
                        String [] tfs = tf.getTerms();
                        buffer.append(" "+tf.getFieldName()+" : [");
                        for(int j=0; j<tfs.length; j++){
                            buffer.append(tfs[j]);
                            if(j!=tfs.length-1)
                                buffer.append(", ");
                        }
                        buffer.append("],");
                    }
                }
                if(cat instanceof ScoreCatalog)
                    buffer.append("\nScore : "+((ScoreCatalog)cat).getScores().get(i).toString());
                i++;
            }
            
        }
        return buffer.toString();
    }
    
    
    /**
     * Utility function to print the results returned by the search query.
     * Each Catalog contains userCatalogEntries and contentCatalogEntries,
     * so we must check both parts. Caution! Only one of these vectors can
     * be non null! This focus to print the top K results.
     * 
     * @param result The array of results, result[i] could be null or Catalog instance.
     * @param type 0=CONTENT, 1=USER, 2=HYBRID
     * @param k The number of results to be returned.
     * @return 
     */
    public static String printTopKQueryResults(Object[] result,int type, int k){
        
        StringBuffer buffer=new StringBuffer();
        ScoreCatalog sc = null;
        boolean done = false;
        ContentProfile cprof;
        List<ContentField> listFields;
        Iterator<ContentField> itf;
        ContentField cf;
                        
        if(type==CONTENT){
            float max = 0, current_max = 0;
            int point_vector = 0, resnum = 1;
            // To include only the discreet results (not the duplicates)
            Vector<ContentCatalogEntry> printed = new Vector<ContentCatalogEntry>();
            // Corresponding Scores to be printed
            Vector<Float> scoresPrinted = new Vector<Float>();
            // Here we put the CCEs which will be chosen in a random manner
            Vector<ContentCatalogEntry> randomSet = new Vector<ContentCatalogEntry>();
            while(!done){
                point_vector = -1;
                max = 0;
                for(int i=0; i<result.length; i++){
                    if(result[i]!=null && result[i] instanceof ScoreCatalog){
                        sc = (ScoreCatalog) result[i];
                        if(sc.getScores()!=null 
                                && !sc.getScores().isEmpty() 
                                && !sc.getContentCatalogEntries().isEmpty()
                                && sc.getScores().firstElement().floatValue()>=max){
                            max=sc.getScores().firstElement().floatValue();
                            point_vector = i;
                        }  
                    }else{ // TODO : Handle the case we dont have SCORECATALOG
                    }
                    // Now remove the topmost as it is the maximum and check if we finished
                    if(i==(result.length-1)){
                        // We reach the end of the results
                        if(point_vector == -1){
                            // FIll in the printed vector and break the loop
                            if(k!=QueryPDU.RETURN_ALL && printed.size()+randomSet.size()>=k){ 
                                    // How many to print from the randomSet?
                                    int answer = k-printed.size();
                                    int choice;
                                    // Pick radomly a CCE and put it in the printed
                                    Random random = new Random();
                                    for(int l = 0; l<answer; l++){
                                        choice = random.nextInt(randomSet.size());
                                        printed.add(randomSet.get(choice));
                                        randomSet.remove(choice);
                                        scoresPrinted.add(current_max);
                                    }
                                    done = true;
                                    break;
                                }else{
                                    // Add the pending results
                                    printed.addAll(randomSet);
                                    // Put the corresponding score values
                                    for(int l = 0; l < randomSet.size(); l++ ){
                                        scoresPrinted.add(current_max);
                                    }
                                }
                            
                            done = true;
                            break;
                        }
                        sc = (ScoreCatalog) result[point_vector];
                        // Only if the ContentCatalogEntries are non null, non empty
                        if(sc.getContentCatalogEntries()!=null && !sc.getContentCatalogEntries().isEmpty()){
                            
                            ContentCatalogEntry cce = sc.getContentCatalogEntries().firstElement();
                            
                            // If we have a different score then we reset randomSet 
                            // and check if we reach the end of the top k list
                            if(max != current_max){
                                // Finish our list and dont include the new CCE
                                // as it is out of the important top k!
                                if(k!=QueryPDU.RETURN_ALL && printed.size()+randomSet.size()>=k){ 
                                    // How many to print from the randomSet?
                                    int answer = k-printed.size();
                                    int choice;
                                    // Pick radomly a CCE and put it in the printed
                                    Random random = new Random();
                                    for(int l = 0; l < answer; l++){
                                        choice = random.nextInt(randomSet.size());
                                        printed.add(randomSet.get(choice));
                                        randomSet.remove(choice);
                                        // Put the corresponding score values
                                        scoresPrinted.add(current_max);
                                    }
                                    done = true;
                                    break;
                                }else{
                                    // Add the pending results
                                    printed.addAll(randomSet);
                                    // Put the corresponding score values
                                    for(int l = 0; l < randomSet.size(); l++ ){
                                        scoresPrinted.add(current_max);
                                    }
                                    current_max = max;
                                    // and reset the randomSet
                                    randomSet.clear();
                                    // Now add the new CCE
                                    randomSet.add(cce);
                                }
                            // If we have the same score add the cce to the randomSet
                            }else{
                                // Except for duplicates!
                                if(!randomSet.contains(cce))
                                    randomSet.add(cce);
                            }

                            // Remove the touched entry
                            sc.getScores().remove(0);
                            sc.getContentCatalogEntries().remove(0);
                        }
                    }
                }
            }
            
            // Print (append) the printed Vector ///////////////////////////////////////////////////////
            Iterator<ContentCatalogEntry> it = printed.iterator();
            Iterator<Float> it2 = scoresPrinted.iterator();
            ContentCatalogEntry tempCCE;
            while(it.hasNext()){
                tempCCE = it.next();
                buffer.append("\n\n"+ resnum++ +". ");
                buffer.append("User : "+tempCCE.getUID().toStringFull()+"\n");
                buffer.append("   Content Profile : {");
                cprof = tempCCE.getContentProfile();
                listFields = cprof.getFields();
                itf = listFields.iterator();
                // Fore every ContentField
                while(itf.hasNext()){
                    cf = itf.next();
                    if(cf instanceof TermField){
                        TermField tf = (TermField)cf;
                        buffer.append(" "+tf.getFieldName()+" : "+tf.getFieldData()+", ");
                    }else if(cf instanceof StoredField){
                        StoredField sf = (StoredField)cf;
                        buffer.append(" "+sf.getFieldName()+" : "+sf.getFieldData()+", ");
                    }else{
                        // TODO : Fill in the tokenized fields we want to display
                        // debuging only
                        TokenizedField tf = (TokenizedField) cf;
                        String [] tfs = tf.getTerms();
                        buffer.append(" "+tf.getFieldName()+" : [");
                        for(int j=0; j<tfs.length; j++){
                            buffer.append(tfs[j]);
                            if(j!=tfs.length-1)
                                buffer.append(", ");
                        }
                        buffer.append("], ");
                    }
                }
                buffer.append(" }\n");
                // The corresponding score value
                buffer.append("   Score : "+it2.next());
            }
        /////////////////////////////////////////////////////////////////////
            
            
        }else if(type==USER){
            float max = 0, current_max = 0;
            int point_vector = 0, resnum = 1;
            // To include only the discreet results (not the duplicates)
            Vector<UserCatalogEntry> printed = new Vector<UserCatalogEntry>();
            // Corresponding Scores to be printed
            Vector<Float> scoresPrinted = new Vector<Float>();
            // Here we put the CCEs which will be chosen in a random manner
            Vector<UserCatalogEntry> randomSet = new Vector<UserCatalogEntry>();
            while(!done){
                point_vector = -1;
                max = 0;
                for(int i=0; i<result.length; i++){
                    if(result[i]!=null && result[i] instanceof ScoreCatalog){
                        sc = (ScoreCatalog) result[i];
                        if(sc.getScores()!=null 
                                && !sc.getScores().isEmpty() 
                                && !sc.getUserCatalogEntries().isEmpty()
                                && sc.getScores().firstElement().floatValue()>=max){
                            max=sc.getScores().firstElement().floatValue();
                            point_vector = i;
                        }  
                    }else{ // TODO : Handle the case we dont have SCORECATALOG
                    }
                    // Now remove the topmost as it is the maximum and check if we finished
                    if(i==(result.length-1)){
                        // We reach the end of the results
                        if(point_vector == -1){
                            // FIll in the printed vector and break the loop
                            if(k!=QueryPDU.RETURN_ALL && printed.size()+randomSet.size()>=k){ 
                                    // How many to print from the randomSet?
                                    int answer = k-printed.size();
                                    int choice;
                                    // Pick radomly a CCE and put it in the printed
                                    Random random = new Random();
                                    for(int l = 0; l<answer; l++){
                                        choice = random.nextInt(randomSet.size());
                                        printed.add(randomSet.get(choice));
                                        randomSet.remove(choice);
                                        scoresPrinted.add(current_max);
                                    }
                                    done = true;
                                    break;
                                }else{
                                    // Add the pending results
                                    printed.addAll(randomSet);
                                    // Put the corresponding score values
                                    for(int l = 0; l < randomSet.size(); l++ ){
                                        scoresPrinted.add(current_max);
                                    }
                                }
                            
                            done = true;
                            break;
                        }
                        sc = (ScoreCatalog) result[point_vector];
                        // Only if the ContentCatalogEntries are non null, non empty
                        if(sc.getUserCatalogEntries()!=null && !sc.getUserCatalogEntries().isEmpty()){
                            
                            UserCatalogEntry uce = sc.getUserCatalogEntries().firstElement();
                            
                            // If we have a different score then we reset randomSet 
                            // and check if we reach the end of the top k list
                            if(max != current_max){
                                // Finish our list and dont include the new CCE
                                // as it is out of the important top k!
                                if(k!=QueryPDU.RETURN_ALL && printed.size()+randomSet.size()>=k){ 
                                    // How many to print from the randomSet?
                                    int answer = k-printed.size();
                                    int choice;
                                    // Pick radomly a UCE and put it in the printed
                                    Random random = new Random();
                                    for(int l = 0; l < answer; l++){
                                        choice = random.nextInt(randomSet.size());
                                        printed.add(randomSet.get(choice));
                                        randomSet.remove(choice);
                                        // Put the corresponding score values
                                        scoresPrinted.add(current_max);
                                    }
                                    done = true;
                                    break;
                                }else{
                                    // Add the pending results
                                    printed.addAll(randomSet);
                                    // Put the corresponding score values
                                    for(int l = 0; l < randomSet.size(); l++ ){
                                        scoresPrinted.add(current_max);
                                    }
                                    current_max = max;
                                    // and reset the randomSet
                                    randomSet.clear();
                                    // Now add the new CCE
                                    randomSet.add(uce);
                                }
                            // If we have the same score add the cce to the randomSet
                            }else{
                                // Except for duplicates!
                                if(!randomSet.contains(uce))
                                    randomSet.add(uce);
                            }

                            // Remove the touched entry
                            sc.getScores().remove(0);
                            sc.getUserCatalogEntries().remove(0);
                        }
                    }
                }
            }
            
            // Print (append) the printed Vector ///////////////////////////////////////////////////////
            Iterator<UserCatalogEntry> it = printed.iterator();
            Iterator<Float> it2 = scoresPrinted.iterator();
            UserCatalogEntry tempUCE;
            while(it.hasNext()){
                tempUCE = it.next();
                buffer.append("\n\n"+ resnum++ +". ");
                buffer.append("User : "+tempUCE.getUID().toStringFull()+"\n");
                buffer.append("   User Profile : {");
                cprof = tempUCE.getUserProfile();
                listFields = cprof.getFields();
                itf = listFields.iterator();
                // Fore every ContentField
                while(itf.hasNext()){
                    cf = itf.next();
                    if(cf instanceof TermField){
                        TermField tf = (TermField)cf;
                        buffer.append(" "+tf.getFieldName()+" : "+tf.getFieldData()+", ");
                    }else if(cf instanceof StoredField){
                        StoredField sf = (StoredField)cf;
                        buffer.append(" "+sf.getFieldName()+" : "+sf.getFieldData()+", ");
                    }else{
                        // TODO : Fill in the tokenized fields we want to display
                        // debuging only
                        TokenizedField tf = (TokenizedField) cf;
                        String [] tfs = tf.getTerms();
                        buffer.append(" "+tf.getFieldName()+" : [");
                        for(int j=0; j<tfs.length; j++){
                            buffer.append(tfs[j]);
                            if(j!=tfs.length-1)
                                buffer.append(", ");
                        }
                        buffer.append("], ");
                    }
                }
                buffer.append(" }\n");
                // The corresponding score value
                buffer.append("   Score : "+it2.next());
            }
        /////////////////////////////////////////////////////////////////////      
        
        }else if(type==HYBRID){
                    // TODO : Implement this body
        
        }
        return buffer.toString();
    }
    
    /**
     * For quick setup through simulator only!
     * 
     * @param user
     * @deprecated
     */
    public void registerUser(User user){
       this.user = user;
    }
    
    /**
     * Getter for the user private object.
     * Caution! This should be checked for
     * security holes in a real deployment.
     * 
     * @return
     */
    public User getUser(){
        return user;
    }
 
    public IdFactory getFactory(){
        return this.factory;
    }
    
    /**
   * This method is called on the application at the destination node
   * for the given id.
   *
   * @param id The destination id of the message
   * @param message The message being sent
   */
    @Override
  public void deliver(Id id, Message message) {
    final PastMessage msg = (PastMessage) message;

    if (msg.isResponse()) {
      handleResponse((PastMessage) message);
    } else {
      if (logger.level <= Logger.INFO) logger.log("Received message " + message + " with destination " + id);
      
      if (msg instanceof InsertMessage) {
        final InsertMessage imsg = (InsertMessage) msg;        
        
        // make sure the policy allows the insert
        if (policy.allowInsert(imsg.getContent())) {
          inserts++;
          final Id msgid = imsg.getContent().getId();
          
          lockManager.lock(msgid, new StandardContinuation(getResponseContinuation(msg)) {

            public void receiveResult(Object result) {
              storage.getObject(msgid, new StandardContinuation(parent) {
                public void receiveResult(Object o) {
                  try {
                    // allow the object to check the insert, and then insert the data
                    PastContent content = imsg.getContent().checkInsert(msgid, (PastContent) o);
                    storage.store(msgid, null, content, new StandardContinuation(parent) {
                      public void receiveResult(Object result) {
                        getResponseContinuation(msg).receiveResult(result);
                        lockManager.unlock(msgid);
                      }
                    });
                  } catch (PastException e) {
                    parent.receiveException(e);
                  }
                }
              });
            }
          });
        } else {
          getResponseContinuation(msg).receiveResult(new Boolean(false));
        }
      } else if (msg instanceof QueryMessage) {
        final QueryMessage qmsg = (QueryMessage) msg;
        lookups++;
        
        // if the data is here, we send the reply, as well as push a cached copy
        // back to the previous node
        storage.getObject(qmsg.getId(), new StandardContinuation(getResponseContinuation(qmsg)) {
          public void receiveResult(Object o) {
            if (logger.level <= Logger.FINE) logger.log("Received object " + o + " for id " + qmsg.getId());

            // Do the similarity computation and scoring of terms and return a mini ScoredCatalog (PastContent)
            if(o instanceof Catalog){   
                // Leave the job to be done asynchronously by the Scorer thread
                scorer.addRequest(new SimilarityRequest(
                        (Catalog)o, 
                        qmsg.getQueryPDU().getData(), 
                        qmsg.getQueryPDU().getType(),  
                        qmsg.getQueryPDU().getK(), 
                        qmsg.getQueryPDU().getSourceUserProfile(), 
                        parent,
                        qmsg.getHops()));
                scorer.doNotify();
            }else{
                //debugging only
                System.out.println("Error: o is not Catalog (in deliver)");
                // send result back
                parent.receiveResult(new ResponsePDU(qmsg.getHops()));
            }
            
            
//            // if possible, pushed copy into previous hop cache
//            if ((lmsg.getPreviousNodeHandle() != null) &&
//                (o != null) &&
//                (! ((PastContent) o).isMutable())) {
//              NodeHandle handle = lmsg.getPreviousNodeHandle();
//              if (logger.level <= Logger.FINE) logger.log("Pushing cached copy of " + ((PastContent) o).getId() + " to " + handle);
//              
//              CacheMessage cmsg = new CacheMessage(getUID(), (PastContent) o, getLocalNodeHandle(), handle.getId());    
//              //endpoint.route(null, cmsg, handle);
//            }
          }
        });
      
      } else if (msg instanceof SocialQueryMessage) {
        final SocialQueryMessage sqmsg = (SocialQueryMessage) msg;
        lookups++;
        
        // Here we are in Tager's side. 
        SocialQueryPDU sqpdu = sqmsg.getQueryPDU();
        String[] qtags = sqpdu.getData();
        
        // The Vector we are going to fill in
        Vector<SocialCatalog> vec = new Vector<SocialCatalog>();
        
        //Search Social Catalogs corresponding to our query terms.
        Map<String,SocialCatalog> invertedMap = user.getTagContentList();
        SocialCatalog scat=null;
        for(String term : qtags){
            if(invertedMap.containsKey(term)){
                vec.add(invertedMap.get(term));
            }
        }
        
        if (logger.level <= Logger.FINER) logger.log("Returning response for social content query message " + sqmsg.getId() + " from " + endpoint.getId());
                
        // All was right! Now let's return the ContentCatalogEntry
        getResponseContinuation(msg).receiveResult(vec);
        
      } else if (msg instanceof FriendApprMessage) {  
        final FriendApprMessage famsg = (FriendApprMessage) msg;
        lookups++;
        
        // TODO : check "famsg.getSource().getId()" if NodeHandle calls some socket to retrieve ID
        Id fid = famsg.getSource().getId();
        user.removePendingFAppr(fid);
        
        // Now they are FRIENDS!
        // TODO : Find a way to get the screen name!
        user.addFriend(new Friend(fid,"n/a"));
        
        if (logger.level <= Logger.FINER) logger.log("Returning response for FriendApprMessage " + famsg.getId() + " from " + endpoint.getId());
                
        // All was right!
        getResponseContinuation(msg).receiveResult(new Boolean(true));
        
        
      } else if (msg instanceof FriendReqMessage) {  
        final FriendReqMessage frmsg = (FriendReqMessage) msg;
        lookups++;
        
        // TODO : check "frmsg.getSource().getId()" if NodeHandle calls some socket to retrieve ID
        user.addPendingFReq(new FriendRequest(frmsg.getFriendReqPDU(), frmsg.getSource().getId()));
        if (logger.level <= Logger.FINER) logger.log("Returning response for friendrequest message " + frmsg.getId() + " from " + endpoint.getId());
                
        // All was right!
        getResponseContinuation(msg).receiveResult(new Boolean(true));
        
       
      } else if (msg instanceof TagContentMessage) {  
        final TagContentMessage tcmsg = (TagContentMessage) msg;
        lookups++;
        
        // Now update the TagCloud +1 term freqs.
        TagContentPDU tpdu = tcmsg.getTagContentPDU();
        Id contentId = tpdu.getContentId();
        Map<Id,TagCloud> mapCloud = user.getContentTagClouds();
        TagCloud cloud;
        if(mapCloud.containsKey(contentId)){
            cloud = mapCloud.get(contentId);
        }else{  // If the TagCloud does not exist, we create it
            cloud = new TagCloud();
            mapCloud.put(contentId, cloud);
        }
        // +1
        // TODO : Implement association with User-tagers in the TagCloud
        String[] tags = tpdu.getTags();
        for(String tag : tags){
            cloud.addTagTFMap(tag);
        }
        
        if (logger.level <= Logger.FINER) logger.log("Returning response for tagcontent message " + tcmsg.getId() + " from " + endpoint.getId());
                
        // All was right! Now let's return the ContentCatalogEntry
        getResponseContinuation(msg).receiveResult(new ContentCatalogEntry(user.getUID(),user.getSharedContentProfile().get(tpdu.getContentId()),user.getUserProfile()));
      
      } else if (msg instanceof RetrieveContMessage) {  
        final RetrieveContMessage rcmsg = (RetrieveContMessage) msg;
        lookups++;
        
        Object result;
        
        // Now get the tagclouds if requested
        RetrieveContPDU rcpdu = rcmsg.getRetrieveContPDU();
        Id contentId = rcpdu.getCheckSum();
        if(rcpdu.getCloudFlag()){
            Map<Id,TagCloud> mapCloud = user.getContentTagClouds();
            TagCloud cloud;
            cloud = mapCloud.get(contentId);
            result = cloud;
        }else{
            
            result = new Boolean("true");
        }
        
        // TODO : Here we must handle the downloading of the content
        
        if (logger.level <= Logger.FINER) logger.log("Returning response for retrieve content message " + rcmsg.getId() + " from " + endpoint.getId());
                
        // All was right! Now return the TagCloud or the success indicator Boolean
        getResponseContinuation(msg).receiveResult(result);
  
           
      } else if (msg instanceof LookupMessage) {
        final LookupMessage lmsg = (LookupMessage) msg;
        lookups++;
        
        // if the data is here, we send the reply, as well as push a cached copy
        // back to the previous node
        storage.getObject(lmsg.getId(), new StandardContinuation(getResponseContinuation(lmsg)) {
          public void receiveResult(Object o) {
            if (logger.level <= Logger.FINE) logger.log("Received object " + o + " for id " + lmsg.getId());

            // send result back
            parent.receiveResult(o);
            
            // if possible, pushed copy into previous hop cache
            if ((lmsg.getPreviousNodeHandle() != null) &&
                (o != null) &&
                (! ((PastContent) o).isMutable())) {
              NodeHandle handle = lmsg.getPreviousNodeHandle();
              if (logger.level <= Logger.FINE) logger.log("Pushing cached copy of " + ((PastContent) o).getId() + " to " + handle);
              
              CacheMessage cmsg = new CacheMessage(getUID(), (PastContent) o, getLocalNodeHandle(), handle.getId());    
              //endpoint.route(null, cmsg, handle);
            }
          }
        });
        
      } else if (msg instanceof LookupHandlesMessage) {
        LookupHandlesMessage lmsg = (LookupHandlesMessage) msg;
        NodeHandleSet set = endpoint.replicaSet(lmsg.getId(), lmsg.getMax());
        if (logger.level <= Logger.FINER) logger.log("Returning replica set " + set + " for lookup handles of id " + lmsg.getId() + " max " + lmsg.getMax() + " at " + endpoint.getId());
        getResponseContinuation(msg).receiveResult(set);
      } else if (msg instanceof FetchMessage) {
        FetchMessage fmsg = (FetchMessage) msg;
        lookups++;

        Continuation c;
//        c = getResponseContinuation(msg);
        c = getFetchResponseContinuation(msg); // has to be special to determine how to send the message
        
        storage.getObject(fmsg.getHandle().getId(), c);
      } else if (msg instanceof FetchHandleMessage) {
        final FetchHandleMessage fmsg = (FetchHandleMessage) msg;
        fetchHandles++;
        
        storage.getObject(fmsg.getId(), new StandardContinuation(getResponseContinuation(msg)) {
          public void receiveResult(Object o) {
            PastContent content = (PastContent) o;

            if (content != null) {
              if (logger.level <= Logger.FINE) logger.log("Retrieved data for fetch handles of id " + fmsg.getId());
              parent.receiveResult(content.getHandle(CatalogService.this));
            } else {
              parent.receiveResult(null);
            }
          }
        });
      } else if (msg instanceof CacheMessage) {
        cache(((CacheMessage) msg).getContent());
      } else {
        if (logger.level <= Logger.SEVERE) logger.log("ERROR - Received message " + msg + "of unknown type.");
      }
    }
  }
    
}