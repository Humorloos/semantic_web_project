package model;

/**
 * This class contains some more information about a certain topic, that can be
 * displayed in the UI.
 */
public class TopicInfo {

	private String resourceUrl; // url of the resource of this topic
	private String topicLabel; // readable label for the user
	private String propertyLabel; // label for the property connecting this resource
	private String typeLabel; // label for the type of resource
	private String wikiUri; // uri to the wikipedia page of the object
	private String previousResource; // The previous resource

	/**
	 * @param resourceUrl The URL, specifying the location of the resource, as a
	 *                    {@link String}.
	 * @param label       The label of the resource in english as a {@link String}.
	 */
	public TopicInfo(String resourceUrl, String label, String typeLabel, String propertyLabel, String previousResource) {
		this.resourceUrl = resourceUrl;
		this.topicLabel = label.split("@")[0];
		this.propertyLabel = propertyLabel;
		this.typeLabel = typeLabel;
		this.previousResource = previousResource;
		this.wikiUri = "https://en.wikipedia.org/wiki/"+resourceUrl.replaceAll("http://dbpedia.org/resource/", ""); // TODO: construct from resourceUrl and wikipedia-url
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public String getType() {
		return typeLabel;
	}

	public String getWikiUri() {
		return wikiUri;
	}

	public String getLabel() {
		return topicLabel;
	}

	public String getPropertyLabel() {
		return propertyLabel;
	}

	public String getPreviousResource() {
		return previousResource;
	}

}
