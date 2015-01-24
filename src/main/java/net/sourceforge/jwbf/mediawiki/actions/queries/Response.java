package net.sourceforge.jwbf.mediawiki.actions.queries;

public class Response {
    private final String title;
    private final String id;
    private final int ns;

    public Response(String title, String id, int ns) {
        this.title = title;
        this.id = id;
        this.ns = ns;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public int getNs() {
        return ns;
    }

}
