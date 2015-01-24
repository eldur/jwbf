package net.sourceforge.jwbf.mediawiki.actions.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryError {
    /* The code of the error */
    private final String code;
    /* The message of the error */
    private final String message;
    /* The detailed message about the error */
    private final String moreInfo;

    @JsonCreator
    public QueryError(@JsonProperty("code") String code, @JsonProperty("info") String message,
            @JsonProperty("*") String moreInfo) {
        this.code = code;
        this.message = message;
        this.moreInfo = moreInfo;
    }
    /**
     * Get the code of the error
     * @return the code
     */
    public String getCode() {
        return code;
    }
    /**
     * Get the message of the error
     * @return the message of the error
     */
    public String getMessage() {
        return message;
    }
    /**
     * Get the detailed message of the error
     * @return the detailed message of the error
     */
    public String getMoreInfo() {
        return moreInfo;
    }
}
