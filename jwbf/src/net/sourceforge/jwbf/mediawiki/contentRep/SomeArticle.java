package net.sourceforge.jwbf.mediawiki.contentRep;

//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Properties;
//import java.util.TreeSet;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class Article implements ContentAccessable {
//
//	public static final class CatLinkCompare implements Comparator<WikiLink> {
//
//		public int compare(WikiLink arg0, WikiLink arg1) {
//			return arg0.category.compareTo(arg1.category);
//		}
//	}
//
//	StringBuffer text = new StringBuffer();
//
//	static Properties defaultProps = new Properties();
//
//	Properties props = new Properties(defaultProps);
//
//	String catLinkPatternString ="\\[\\[("+props.getProperty("ns.category")+":([\\w\\s_\\- ]*))(\\|([^\\]]*))?\\]\\]"; 
//		
//		//"\\[\\[(" + props.getProperty("ns.category")
//		//	+ ":([\\w\\s_\\- ]*))(\\|([\\w\\s_\\- ]*))?\\]\\]";
//
//	// \[\[Kategorie:(\w*)(\|(\w*))\]\]
//
//	Pattern catLinkPattern = Pattern.compile(catLinkPatternString);
//
//	Pattern sectionPatter = Pattern.compile("^(=+)([^=]*)(=+)$",Pattern.MULTILINE);
//
//	TreeSet<WikiLink> catSet = new TreeSet<WikiLink>(new CatLinkCompare());
//
//	ArrayList<String> sections = new ArrayList<String>();
//
//	private String editSummary;
//
//	private String editor;
//
//	private String label;
//
//	private boolean minorEdit;
//
//	static {
//		defaultProps.setProperty("ns.main", "");
//		defaultProps.setProperty("ns.talk", "Talk");
//		defaultProps.setProperty("ns.user", "User");
//		defaultProps.setProperty("ns.user_talk", "User talk");
//		defaultProps.setProperty("ns.meta", "Meta");
//		defaultProps.setProperty("ns.meta_talk", "Meta talk");
//		defaultProps.setProperty("ns.image", "Image");
//		defaultProps.setProperty("ns.mediawiki", "MediaWiki");
//		defaultProps.setProperty("ns.mediawiki_talk", "MediaWiki talk");
//		defaultProps.setProperty("ns.template", "Template");
//		defaultProps.setProperty("ns.template_talk", "Template talk");
//		defaultProps.setProperty("ns.help", "Help");
//		defaultProps.setProperty("ns.help_talk", "Help talk");
//		defaultProps.setProperty("ns.category", "Category");
//		defaultProps.setProperty("ns.category_talk", "Category talk");
//
//		/*
//		 * German:
//		 */
//		defaultProps.setProperty("ns.talk", "Diskussion");
//		defaultProps.setProperty("ns.user", "Benutzer");
//		defaultProps.setProperty("ns.user_talk", "Benutzer Diskussion");
//		defaultProps.setProperty("ns.meta", "Wiki Aventurica");
//		defaultProps.setProperty("ns.meta_talk", "Wiki Aventurica Diskussion");
//		defaultProps.setProperty("ns.image", "Bild");
//		defaultProps.setProperty("ns.mediawiki", "MediaWiki");
//		defaultProps.setProperty("ns.mediawiki_talk", "MediaWiki Diskussion");
//		defaultProps.setProperty("ns.template", "Vorlage");
//		defaultProps.setProperty("ns.template_talk", "Vorlage Diskussion");
//		defaultProps.setProperty("ns.help", "Hilfe");
//		defaultProps.setProperty("ns.help_talk", "Hilfe Diskussion");
//		defaultProps.setProperty("ns.category", "Kategorie");
//		defaultProps.setProperty("ns.category_talk", "Kategorie Diskussion");
//
//	}
//
//	public Article(ContentAccessable a) {
//		editor=a.getEditor();
//		editSummary=a.getEditSummary();
//		label=a.getLabel();
//		setText(a.getText());
//		
//	}
//
//	private void init() {
//		parseCategories();
//		parseSections();
//	}
//
//	private void parseSections() {
//		sections.clear();
//		Matcher m = sectionPatter.matcher(text.toString());
//		int start = 0;
//		
//		while (m.find()) {
//			String section = new String();
//			section = text.toString().substring(start, m.start());
//			sections.add(section);
//			
//			start = m.start();
//		}
//		
//		sections.add(text.toString().substring(start,
//				text.toString().length()));
//
//	}
//
//	private void parseCategories() {
//
//		Matcher m = catLinkPattern.matcher(text.toString());
//		while (m.find()) {
//			WikiLink link = new WikiLink();
//			link.category = m.group(1);
//			if (m.groupCount() > 3) {
//				link.linkname = m.group(4);
//
//			}
//			catSet.add(link);
//		}
//	}
//
//	private void rebuildCategories() {
//		Matcher m = catLinkPattern.matcher(text.toString());
//		StringBuffer newText = new StringBuffer(m.replaceAll(""));
//		for (Iterator iter = catSet.iterator(); iter.hasNext();) {
//			WikiLink link = (WikiLink) iter.next();
//			newText.append('\n');
//			
//			newText.append(link.toString());
//		}
//		newText.append('\n');
//		setText(newText);
//	}
//
//	
//	
//	private void setText(StringBuffer newText) {
//		text=newText;
//		
//		init();
//	}
//
//	public void setText(String newText) {
//		setText(new StringBuffer(newText));
//		
//	}
//
//	public String getText() {
//		StringBuffer internal = new StringBuffer();
//		for (int i=0;i<sections.size();i++){
//			String section=sections.get(i);
//			internal.append(section);
//			if (!section.endsWith("\n")){
//				internal.append('\n');
//			}
//		}
//		text=internal;
//		rebuildCategories();
//
//		return text.toString();
//
//	}
//
//	public List<String> getSections() {
//		return sections;
//	}
//
//	public String getEditSummary() {
//
//		return editSummary;
//	}
//
//	public String getEditor() {
//		// TODO Auto-generated method stub
//		return editor;
//	}
//
//	public String getLabel() {
//		// TODO Auto-generated method stub
//		return label;
//	}
//
//	public boolean isMinorEdit() {
//		// TODO Auto-generated method stub
//		return minorEdit;
//	}
//
//	public String getCatLinks() {
//		StringBuffer res=new StringBuffer();
//		for (Iterator iter = catSet.iterator(); iter.hasNext();) {
//			WikiLink link = (WikiLink) iter.next();
//			res.append(link);
//			res.append('\n');
//		}
//		return res.toString();
//	}
//
//}
