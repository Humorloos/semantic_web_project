package model;

/**
 * --- Work in Progress! --- This class contains some more information about a
 * certain topic, that can be displayed in the UI.
 */
public class TopicInfo {

	private String resourceUrl; //url of the resource of this topic
	private String typeLabel; //label for the type of resource
	private String wikiUri; //uri to the wikipedia page of the object

	public TopicInfo(String resourceUrl, String typeLabel, String wikiUri) {
		this.typeLabel = typeLabel;
		this.wikiUri = wikiUri;
	}

}
