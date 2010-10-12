/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.content;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import ceid.netcins.htmlparsing.HTMLParser;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 *
 * @author andy
 * 
 * This is a fundamental class which is used to build a set of 
 * fields which include the terms of a Content object!
 * Content Profile is represented by the Document that is being
 * created.
 * 
 */
public class ContentProfileFactory {
    
    public static final String DEFAULT_DELIMITER = " ";
    
    // Default length for a field. The max num of terms!
    public final static int DEFAULT_MAX_FIELD_LENGTH = 10000;
    
    // Our analyzer!
    StandardAnalyzer sa;
    
    // max num of terms permited to handle
    int maxFieldLength;
    
    /** Makes a document for a File. This document object is the Content Profile.
     *  The fields of the profile depends on the number of attributes extracted
     *  from the file. Some of the fields are indexed (and stored), this is translated to 
     *  network "insertions" with the correspoding field value being the 
     *  destination. Some other fields are only stored in the Catalogs (their values).
     *  And some of them are also tokenized (except for indexed, stored).
     * 
     *  We use "mimetype" information (e.g. text/html) to discover file content that is going
     *  to be tokenized. Specifically :
     *   
     *  TODO : In the Future develop a classes to tokenize application/pdf, text/csv, text/css, text/sgml!!!
     *  
     *  text/* : Always we use a FileReader to read file content, analyze, filter and tokenize!
     *  text/html, text/xml : Special handling to parse main content!
     * 
    <ul>
    <li><code>path</code>--containing the pathname of the file, as a stored,
    untokenized field;
    <li><code>modified</code>--containing the last modified date of the file as
    a field as created by <a
    href="lucene.document.DateTools.html">DateTools</a>; and
    <li><code>contents</code>--containing the full contents of the file, as a
    Reader field;
    </ul>
    */
  public ContentProfile buildContentProfile(File f)
       throws java.io.FileNotFoundException,java.io.IOException {
    
    if(!f.exists()){        
        throw new java.io.FileNotFoundException("File does not exist!");
    }
    if(f.isDirectory()){        
        throw new java.io.FileNotFoundException("File is a directory!");
    }
    if(!f.canRead()){        
        throw new java.io.FileNotFoundException("File cannot be read!");
    }

    // make a new, empty ContentProfile to fill in!
    ContentProfile cprof = new ContentProfile();
    
    // Map of candidate fields as given by the libextractor!
    Map<String,String> candfields = extractFile(f);
    
    if(candfields!=null){
        
        // Only the filename without the path 
        if(candfields.containsKey("filename") && candfields.get("filename")!=null){
            cprof.add(new TermField("filename", candfields.remove("filename")));
        }
        
        // Size computed in KB, MB or GB
        if(candfields.containsKey("filesize") && candfields.get("filesize")!=null){
            cprof.add(new StoredField("filesize", candfields.remove("filesize")));
        }
        
        // SHA-1 160bit checksum to identify the total content of the file
        if(candfields.containsKey("SHA-1") && candfields.get("SHA-1")!=null){
            cprof.add(new StoredField("SHA-1", candfields.remove("SHA-1")));
        }
        
        if(candfields.containsKey("modification date") && candfields.get("modification date")!=null){
            candfields.remove("modification date");
        }
        // Add the last modified date of the file a field named "modified".  Use 
        // a field that is indexed (i.e. searchable), but don't tokenize the field
        // into words.
        cprof.add(new StoredField("modified",
            DateTools.timeToString(f.lastModified(), DateTools.Resolution.MINUTE)));    
        
        if(candfields.containsKey("mimetype") && candfields.get("mimetype")!=null){
            
            // The case we can tokenize using a char encoding stream (FileReader)
            if(candfields.get("mimetype").equals("text/html")){ // HTML
                
                FileInputStream fis = new FileInputStream(f);
                HTMLParser parser = new HTMLParser(fis);
                Reader reader = parser.getReader();
                if(reader!=null){
                    TreeMap<String,Integer> tfm = termFrequencies(reader);
                
                    if(tfm!=null){
                        // Add the tag-stripped contents as a Reader-valued Text field so it will
                        // get tokenized and indexed.
                        cprof.add(new TokenizedField("contents", tfm));
                    }
                }
                
            }else if(candfields.get("mimetype").startsWith("text")){ // Other TEXT
                
                // Add the contents of the file to a field named "contents".  Specify a Reader,
                // so that the text of the file is tokenized and indexed, but not stored.
                // Note that FileReader expects the file to be in the system's default encoding.
                // If that's not the case searching for special characters will fail.
                Reader reader = new FileReader(f);
                if(reader!=null){
                    TreeMap<String,Integer> tfm = termFrequencies(reader);
                
                    if(tfm!=null){
                        
                        cprof.add(new TokenizedField("contents", tfm));
                    }
                }    
            }else{
                
                // TODO : Develop handlers for other mimetypes such as pdf,html,xml etc.
            }
            
            cprof.add(new StoredField("mimetype", candfields.remove("mimetype")));
        }
        
        // The rest of Field/Value pairs that libextractor provided us.
        // TODO : Determine which of these are TermFields or TokenizedFields!
        Iterator<String> it = candfields.keySet().iterator();
        String field;
        while(it.hasNext()){
            field = it.next();
            if(field!=null){
                cprof.add(new StoredField(field, candfields.get(field)));
            }
        }
        
    }

    // return the content profile
    return cprof;
  }

  /**
   * 
   * @param f The file to be extracted.
   * @return This function returns a Map object with keys corresponding to field
   * names and values to field values
   */
  public Map<String,String> extractFile(File f) throws IOException{
            if(!f.exists() || f.isDirectory() || !f.canRead()){
                return null;
            }
            String extrargs[] = {"/usr/bin/extract","-f","-Hsha1",f.getAbsolutePath()};
            Process p = Runtime.getRuntime().exec(extrargs);
            BufferedReader instr = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));
            String line = null;
            String [] keyvalue;
            Map<String,String> tempcontainer=new HashMap<String,String>();
            while ((line = instr.readLine()) != null) {
                line = line.trim();
                // Split at - delimiter for only once per line
                keyvalue = line.split(" - ",2);
                if(keyvalue!=null && keyvalue.length==2 && keyvalue[0]!=null && keyvalue[1]!=null)
                    tempcontainer.put(keyvalue[0], keyvalue[1]);
            }
            return tempcontainer;
  }
  
  /**
   * 
   * @param reader a source for obtaining the tokens
   * @return null if no reader can be obtained, or a Map with the set of terms
   * the corresponding term occurences in the field.
   * Map<String,Integer> = Map < Term, TF >
   */
  public TreeMap<String,Integer> termFrequencies(Reader reader) throws IOException{
       
      // TODO : compute global Doc term frequency
        if(reader != null){
      
            TokenStream ts = sa.tokenStream("contents", reader); // Field name is not used at all :-) in LUCENE!!!

            Token reusableToken = new Token();
            int length = 0; // Number of terms seen!
            TreeMap<String,Integer> tfv = new TreeMap<String,Integer>(); // TreeMap to sort terms
            Integer i=null;
            
            // reset the TokenStream to the first token
            ts.reset();

              try {
                  
                for(;;) {
                  Token token = ts.next(reusableToken);
                  // Debugging only!
                  //if(token!=null)
                  //System.out.println("Token : "+token.toString());
                  if (token == null) break;
                  String str = token.termText();
                  
                  if(str!=null){ 
                       
                      // Already met this term, +1 to term frequency
                      if(tfv.containsKey(str) && (i=tfv.get(str))!=null){
                          tfv.put(str, new Integer(i.intValue()+1));
                          i=null;    
                          
                      }else{ // First term occurence
                          tfv.put(str, new Integer(1));
                      }
                  }else{
                      System.out.println("Null token.termText!");
                      break;
                  }
                  if (++length >= maxFieldLength) {
                      System.out.println("maxFieldLength " +maxFieldLength+ " reached for field, ignoring following tokens");
                    break;
                  }
                }
                
              } finally {
                ts.close();
              }
              return tfv;
        }
        return null;
  }
  
  /**
   * 
   * @param reader a source for obtaining the tokens (It could be a StringReader).
   * @return null if no reader can be obtained, or a Set with the terms
   * in the field.
   */
  public TreeSet<String> termSet(Reader reader) throws IOException{
      
        if(reader != null){
      
            TokenStream ts = sa.tokenStream("contents", reader); // Field name is not used at all :-) in LUCENE!!!

            Token reusableToken = new Token();
            int length = 0; // Number of terms seen!
            TreeSet<String> tv = new TreeSet<String>(); // TreeSet to sort terms
            
            // reset the TokenStream to the first token
            ts.reset();

              try {
                  
                for(;;) {
                  Token token = ts.next(reusableToken);
                  // Debugging only!
                  //if(token!=null)
                  //System.out.println("Token : "+token.toString());
                  if (token == null) break;
                  String str = token.termText();
                  
                  if(str!=null){
                      tv.add(str);
                  }else{
                      System.out.println("Null token.termText!");
                      break;
                  }
                  if (++length >= maxFieldLength) {
                      System.out.println("maxFieldLength " +maxFieldLength+ " reached for field, ignoring following tokens");
                    break;
                  }
                }
                
              } finally {
                ts.close();
              }
              return tv;
        }
        return null;
  }
  
  /**This is a utility method to obtain a set of ContentProfiles 
   * for a whole directory of files.
   * 
   * @param root
   * @return
   * @throws java.io.FileNotFoundException
   * @throws java.io.IOException
   */
  public Map<File,ContentProfile> buildFromDir(File root) throws FileNotFoundException, IOException{
      
      Map<File,ContentProfile> map = new HashMap<File,ContentProfile>();
      LinkedList dir = new LinkedList();
      dir.add(root);
      while (!dir.isEmpty()) {
          
          File innerf = (File) dir.removeFirst();
          if (innerf.isDirectory()) {
            File[] subs = innerf.listFiles();
            if (subs.length == 0) {
              continue;
            } else {
              dir.addAll(Arrays.asList(subs));
              //dir.addLast(innerf);
            }
          } else {
            map.put(innerf,this.buildContentProfile(innerf));
          }
      }  
      return map;
  }
  
  /**
   * Utility function to compose the user/content profile set of terms.
   * 
   * @param candfields
   * @return
   */
  public ContentProfile buildProfile(Map<String,String> candfields) throws IOException{
      return buildProfile(candfields,DEFAULT_DELIMITER);
  }
  
  /**
   * Utility function to compose the user/content profile set of terms.
   * 
   * @param candfields
   * @param delimiter The delimiter which separates the terms
   * @return
   */
  public ContentProfile buildProfile(Map<String,String> candfields, String delimiter) throws IOException{
      
      // make a new, empty ContentProfile to fill in!
      ContentProfile cprof = new ContentProfile();
    
      // TODO : Determine which of these are TermFields or TokenizedFields!
      Iterator<String> it = candfields.keySet().iterator();
      String field;
      Reader reader;
      TreeSet<String> terms;
      while(it.hasNext()){
          field = it.next();
          if(field!=null){
              
              if(field.equals("SHA-1")){
                  cprof.add(new StoredField(field, candfields.get(field)));
                  
              }else if(field.equals("Identifier")){
                  cprof.add(new TermField(field, candfields.get(field)));
                  
              } else {
              
                  // TODO : a proper tokenizer-filter such as Lucene's must be used
                  // This is used to treat whole phrases like a single term!!!
                  if(!delimiter.equals(DEFAULT_DELIMITER)){
                      terms = new TreeSet<String>();
                      String[] splitTerms = candfields.get(field).split(delimiter);
                      for(String st : splitTerms)
                          terms.add(st.trim()); // Trimming to remove control and whitespaces!
                      cprof.add(new TokenizedField(field, terms));
                      
                  }else{
                      reader = new StringReader(candfields.get(field));

                      if(reader!=null){
                          terms = termSet(reader);
                          if(terms!=null){
                              cprof.add(new TokenizedField(field, terms));
                          }  
                      }
                  }
              }
          }
      }
      return cprof;
  }
  
  
  public ContentProfileFactory() {
    this(DEFAULT_MAX_FIELD_LENGTH);
  }
  
  public ContentProfileFactory(int maxFieldLength){
      sa = new StandardAnalyzer();
      this.maxFieldLength = maxFieldLength;
  }

}