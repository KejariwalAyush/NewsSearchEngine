package searchEngine;

// Ayush Kejariwal
// 1941012408
// CSE-D

class UrlData {
    /*
     * this class includes all the data that is required for the BST class to fetch
     * data and give our output.
     */
    final String url;
    final String title;
    int points;

    UrlData(String url, String title, int p) {

        /*
         * constructor that takes in data from the urls that are being fetched. This
         * includes the title of the link/site(if no title has been specefied we assign
         * it under "No title"), the link itself and the points that will be assigned to
         * the url based on specifications mentioned in "Search Engine" class
         */

        this.points = p;
        this.title = title == null ? "No Title" : title;
        this.url = url;
    }

    @Override
    public String toString() {
        return title + "\n" + url + "\n";
    }
}