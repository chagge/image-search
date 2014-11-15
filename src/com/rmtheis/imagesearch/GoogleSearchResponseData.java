package com.rmtheis.imagesearch;

import com.google.gson.annotations.SerializedName;

/** Represents a response to a Google image search */
public class GoogleSearchResponseData {

    // TODO Remove the fields we're not using
    
    /** Array of search results contained in a response to a Google image search request */
    @SerializedName("items")
    private GoogleSearchResult[] searchResults;

    @SerializedName("queries")
    private Queries queries;

    public NextPage getNextPage() {
        return queries.getNextPage();
    }
    
    public GoogleSearchResult[] getSearchResults() {
        return searchResults;
    }

    public static class Queries {
        @SerializedName("nextPage")
        private NextPage[] nextPage;
        
        public NextPage getNextPage() {
            return nextPage[0];
        }
    }
    
    public static class NextPage {
        @SerializedName("title")
        private String title;
        
        @SerializedName("startIndex")
        private int startIndex;
        
        @SerializedName("totalResults")
        private int numTotalResults;
        
        public String getTitle() {
            return title;
        }
        
        public int getStartIndex() {
            return startIndex;
        }
        
        public int getNumTotalResults() {
            return numTotalResults;
        }
    }
    
    public static class ImageData {
        @SerializedName("thumbnailLink")
        private String thumbnailLink;
        
        public String getThumbnailLink() {
            return thumbnailLink;
        }
    }
    
    /** Represents a single search result for an image search. */
    public static class GoogleSearchResult {

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
        
        public String getThumbnailLink() {
            return imageData.getThumbnailLink();
        }

    }
}
