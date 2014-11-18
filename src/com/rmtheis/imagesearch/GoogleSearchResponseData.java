package com.rmtheis.imagesearch;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/** Represents a response to a Google image search */
public class GoogleSearchResponseData implements Serializable {

    /** Array of search results contained in a response to a Google image search request */
    @SerializedName("items")
    private GoogleSearchResult[] searchResults;

    public GoogleSearchResult[] getSearchResults() {
        return searchResults;
    }

    public static class ImageData implements Serializable {
        @SerializedName("contextLink")
        private String contextLink;

        @SerializedName("thumbnailLink")
        private String thumbnailLink;
    }

    /** Represents a single search result for an image search. */
    public static class GoogleSearchResult implements Serializable {

        /** A name for this search result */
        @SerializedName("title")
        private String title;

        /** A web link that points to the image */
        @SerializedName("link")
        private String link;

        /** A display-friendly link that shows the domain name */
        @SerializedName("displayLink")
        private String displayLink;

        @SerializedName("image")
        private ImageData imageData;

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getDisplayLink() {
            return displayLink;
        }

        public String getContextLink() {
            return imageData.contextLink;
        }
        
        public String getThumbnailLink() {
            return imageData.thumbnailLink;
        }

    }
}
