import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/*
 * if there are problems with this class, find "tools.jar" 
 * form your installed jdk and add it to the project.
 * 
 * to generate javadoc add the following parameters to javadoc
 * 
 * -taglet CustomTaglets 
 * -tagletpath <absolute path to>/lib/tagletExt.jar
 */

public class CustomTaglets implements Taglet {
    
    private static final String NAME = "supportedBy";
    
    /**
     * Return the name of this custom tag.
     */
    public String getName() {
        return NAME;
    }
    

    public boolean inField() {
        return false;
    }

    public boolean inConstructor() {
        return false;
    }
    
    public boolean inMethod() {
        return true;
    }
    
    public boolean inOverview() {
        return true;
    }

    public boolean inPackage() {
        return false;
    }

    public boolean inType() {
        return true;
    }

    
    public boolean isInlineTag() {
        return false;
    }
    

    public static void register(Map tagletMap) {
       CustomTaglets tag = new CustomTaglets();
       Taglet t = (Taglet) tagletMap.get(tag.getName());
       if (t != null) {
           tagletMap.remove(tag.getName());
       }
       tagletMap.put(tag.getName(), tag);
    }


    public String toString(Tag tag) {
        return parseTag(tag);
    }
    
    private String parseTag(Tag... tags) {
    	
    	if (tags.length == 0) {
            return null;
        }
    	Hashtable<String, ByElement> byList = new Hashtable<String, ByElement>();
    	String result = "";
    	
    	for (int i = 0; i < tags.length; i++) {
	    	String tagText = tags[i].text();
	    	String [] tagWords = tagText.split(" ");
	    	String key = tagWords[0];
	    	ByElement bEl = null;
	    	if (byList.containsKey(key)) {
	    		bEl = byList.get(key);
	    		bEl.addValue(tagText);
	    		
	    		
	    	} else {
	    		
	    		bEl = new ByElement(key);
	    		bEl.addValue(tagText);
	    		
	    		
	    	}
	    	byList.put(key, bEl);
        }
    	
    	Iterator<ByElement> byElIt = byList.values().iterator();
    	while (byElIt.hasNext()) {
			ByElement elem = byElIt.next();
			result += elem.toString();
			
		}
    	
    	
    	return result;
    }
    

    public String toString(Tag[] tags) {
        
        return parseTag(tags);
    }
    
    private class ByElement {
    	

    	private String type = "";
    	private String result = "";
    	
    	public ByElement(String type) {
    		this.type = type;
    	}
    	
    	public void addValue(final String value) {

    		
    		String temp = value;
    		if(temp.contains(type)) {
            	temp = temp.substring(type.length()).trim();
            }
    		
    		result += "<li>"+temp+"</li>";

    	}
    	
    	public String toString() {
    		String typeOut = type;
    		
    		if(typeOut.contains("API")) {
    			
    			typeOut = typeOut.replace("API", "<span style=\"color: red\">API</span>");
    		}
    		
    		return "\n<DT><B>Supported by " + typeOut + ":</B>" +
    				"<ul style=\"-moz-column-count:3; column-count:3;\">" + result + "<ul>\n";
    	}
    }
}


