package searchEngine;

class UrlData {
    final String url;
    final String title;
    int points;

    UrlData(String url, String title, int p) {
        this.points = p;
        this.title = title == null ? "No Title" : title;
        this.url = url;
    }

    @Override
    public String toString() {
        return title + "\n" + url + "\n";
    }
}