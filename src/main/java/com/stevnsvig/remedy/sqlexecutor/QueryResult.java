package com.stevnsvig.remedy.sqlexecutor;

import java.util.ArrayList;

/**
 * Class to handle result from SQL in a format that can be sent to a table
 *
 * @author <a href="mailto:jonas.stumph@stevnsvig.com"> Jonas Stumph Stevnsvig</a>
 *
 */
public abstract class QueryResult {

    /**
     * data
     */
    private ArrayList<ArrayList<String>> result;
    /**
     * column headers
     */
    private ArrayList<String> headers;

    /**
     *
     * @param data
     * @param colHeaders
     */
    public QueryResult(ArrayList<ArrayList<String>> data, ArrayList<String> colHeaders) {
        this.result = data;
        this.headers = colHeaders;
    }

    public ArrayList<ArrayList<String>> getResult() {
        return result;
    }

    public void setResult(ArrayList<ArrayList<String>> result) {
        this.result = result;
    }

    public ArrayList<String> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<String> headers) {
        this.headers = headers;
    }

}
