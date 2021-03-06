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
	private Integer nRelatedPreviousResources;

	public Integer getnRelatedPreviousResources() {
		return nRelatedPreviousResources;
	}

	/**
	 * @param resourceUrl The URL, specifying the location of the resource, as a
	 *                    {@link String}.
	 * @param label       The label of the resource in english as a {@link String}.
	 */
	public TopicInfo(String resourceUrl, String label, String typeLabel, String propertyLabel, String previousResource,
			final Integer nRelatedPreviousResources) {
		this.resourceUrl = resourceUrl;
		this.topicLabel = label.split("@")[0];
		this.propertyLabel = propertyLabel.split("@")[0];
		if (propertyLabel.contains("http:") && this.propertyLabel.lastIndexOf('/') >= 0) {
			this.propertyLabel = this.propertyLabel.substring(this.propertyLabel.lastIndexOf('/') + 1);
		}
		if (propertyLabel.length() > 1) { // First letter uppercase for better readability
			this.propertyLabel = this.propertyLabel.substring(0, 1).toUpperCase() + this.propertyLabel.substring(1);
		}
		this.typeLabel = typeLabel.split("@")[0];
		if (typeLabel.contains("http:") && this.typeLabel.lastIndexOf('/') >= 0) {
			this.typeLabel = this.typeLabel.substring(this.typeLabel.lastIndexOf('/') + 1);
		}
		if (typeLabel.length() > 1) { // First letter uppercase for better readability
			this.typeLabel = this.typeLabel.substring(0, 1).toUpperCase() + this.typeLabel.substring(1);
		}
		this.previousResource = previousResource;
		this.wikiUri = "https://en.wikipedia.org/wiki/" + resourceUrl.replaceAll("http://dbpedia.org/resource/", "");
		this.nRelatedPreviousResources = nRelatedPreviousResources;
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
