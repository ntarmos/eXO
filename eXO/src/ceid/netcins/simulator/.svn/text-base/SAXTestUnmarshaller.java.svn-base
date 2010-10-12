/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.simulator;

import ceid.netcins.IndexContentRequest;
import ceid.netcins.IndexPseydoContentRequest;
import ceid.netcins.IndexUserRequest;
import ceid.netcins.RandomQueriesRequest;
import ceid.netcins.Request;
import ceid.netcins.ScenarioRequest;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;

/**
 *
 * @author andy
 */
public class SAXTestUnmarshaller extends DefaultHandler {

    private Vector<Request> scenarios;

    private Stack stack;
    private boolean isStackReadyForText;

    // Points to the last place an event occurred
    private Locator locator;

    String lastFieldName;

    // -----

    public SAXTestUnmarshaller() {
	stack = new Stack();
    scenarios = new Vector();
	isStackReadyForText = false;
    lastFieldName = null;
    }

    public Vector getScenarios() { return scenarios; }

    // ----- callbacks: -----

    public void setDocumentLocator( Locator rhs ) { locator = rhs; }

    // -----

    public void startElement( String uri, String localName, String qName,
			      Attributes attribs ) {

	isStackReadyForText = false;

	// if next element is complex, push a new instance on the stack
	// if element has attributes, set them in the new instance
	if( localName.equals( "scenario" ) ) {
	    stack.push( new ScenarioRequest() );

	}else if( localName.equals( "index_pcontent" ) ) {
	    stack.push( new IndexPseydoContentRequest() );

	}else if( localName.equals( "index_user" ) ) {
        IndexUserRequest i = new IndexUserRequest();
        i.setDelimiter("::");
	    stack.push( i );

	}else if( localName.equals( "random_queries" ) ) {
	    stack.push( new RandomQueriesRequest() );

	}
	// if next element is simple, push StringBuffer
	// this makes the stack ready to accept character text
	else if( localName.equals( "source" ) || localName.equals( "keywords" ) ||
		 localName.equals( "identifier"  ) || localName.equals( "name"  ) ||
         localName.equals( "num_type" ) || localName.equals( "num_keywords" ) ||
         localName.equals( "num_queries" ) || localName.equals( "num_results" ) ||
         localName.equals( "user_address" )) {
	    stack.push( new StringBuffer() );
	    isStackReadyForText = true;
	}
	// if none of the above, it is an unexpected element
	else{
	    // do nothing
	}
    }

    // -----

    public void endElement( String uri, String localName, String qName ) {

	// recognized text is always content of an element
	// when the element closes, no more text should be expected
	isStackReadyForText = false;

	// pop stack and add to 'parent' element, which is next on the stack
	// important to pop stack first, then peek at top element!
    if(stack.empty())
        return;
    Object tmp = stack.pop();

	if( localName.equals( "scenario" ) ) {
	    scenarios.add((ScenarioRequest)tmp);

	}else if( localName.equals( "index_pcontent" ) ) {
	    ((ScenarioRequest)stack.peek()).index_submitted.add( (IndexPseydoContentRequest)tmp );

	}else if( localName.equals( "index_user" ) ) {
	    ((ScenarioRequest)stack.peek()).index_submitted.add( (IndexUserRequest)tmp );
        

	}else if( localName.equals( "random_queries" ) ) {
	    ((ScenarioRequest)stack.peek()).randomQueries_submitted.add( (RandomQueriesRequest)tmp );
	}
	// for simple elements, pop StringBuffer and convert to String
	else if( localName.equals( "source" ) ) {
        Object obj = stack.peek();
        if(obj instanceof IndexPseydoContentRequest){
            ((IndexPseydoContentRequest)obj).setSource( Integer.parseInt(tmp.toString()) );
        }else if(obj instanceof IndexUserRequest){
            ((IndexUserRequest)obj).setSource( Integer.parseInt(tmp.toString()) );
        }

	}else if( localName.equals( "keywords" ) ) {
        Object obj = stack.peek();
        if(obj instanceof IndexPseydoContentRequest){
            if(this.lastFieldName!=null){
                ((IndexPseydoContentRequest)obj).addToContentProfile( this.lastFieldName,tmp.toString() );
                lastFieldName=null;
            }else{
                ((IndexPseydoContentRequest)obj).addToContentProfile( "content description",tmp.toString() );
            }
        }else if(obj instanceof IndexUserRequest){
            if(this.lastFieldName!=null){
                ((IndexUserRequest)obj).addToUserProfile(this.lastFieldName,tmp.toString() );
                lastFieldName=null;
            }else{
                ((IndexUserRequest)obj).addToUserProfile("user description",tmp.toString() );
            }
        }

    }else if( localName.equals( "user_address" ) ) {
	    ((IndexUserRequest)stack.peek()).addToUserProfile( "user address",tmp.toString() );

    }else if( localName.equals( "name" ) ) {
        this.lastFieldName = tmp.toString();

	}else if( localName.equals( "identifier" ) ) {
	    ((IndexPseydoContentRequest)stack.peek()).addToContentProfile( "Identifier",tmp.toString() );
        ((IndexPseydoContentRequest)stack.peek()).setIdentifier( tmp.toString() );

	}else if( localName.equals( "num_type" ) ) {
	    ((RandomQueriesRequest)stack.peek()).setType( Integer.parseInt(tmp.toString()) );
    }else if( localName.equals( "num_keywords" ) ) {
	    ((RandomQueriesRequest)stack.peek()).setKeywords( Integer.parseInt(tmp.toString()) );
    }else if( localName.equals( "num_queries" ) ) {
	    ((RandomQueriesRequest)stack.peek()).setQueries( Integer.parseInt(tmp.toString()) );
    }else if( localName.equals( "num_results" ) ) {
	    ((RandomQueriesRequest)stack.peek()).setK( Integer.parseInt(tmp.toString()) );
	}
	// if none of the above, it is an unexpected element:
	// necessary to push popped element back!
	else{
	    stack.push( tmp );
	}
    }

    // -----

    public void characters( char[] data, int start, int length ) {

	// if stack is not ready, data is not content of recognized element
	if( isStackReadyForText == true ) {
	    ((StringBuffer)stack.peek()).append( data, start, length );
	}else{
	    // read data which is not part of recognized element
	}
    }

    // -----

    private String resolveAttrib( String uri, String localName,
			          Attributes attribs, String defaultValue ) {

	String tmp = attribs.getValue( uri, localName );
	return (tmp!=null)?(tmp):(defaultValue);
    }
}
