package searchEngine;

class UrlData {
    final String url;
    final String title;
    int points;

    UrlData(String url, String title, int p) {
        this.points = p;
        this.title = title;
        this.url = url;
    }

    @Override
    public String toString() {
        return points + " | " + title + "\n" + url + "\n";
    }
}