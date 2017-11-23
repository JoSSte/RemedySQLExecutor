package com.stevnsvig.remedy.sqlexecutor;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.IARRowIterator;
import java.util.ArrayList;

/**
 * Simple class to hold the results of a query.
 *
 * @author jss
 */
public class ResultEntryList extends ArrayList<Entry> implements IARRowIterator {

	private static final long serialVersionUID = 0L;
	
    /**
     * counter for the number of entries in the list.
     */
    private int numEntries = 0;

    /**
     * 0-arg constructor
     *
     */
    public ResultEntryList() {
    }

    /**
     * Callback method to handle addition of objects
     *
     * @param entry
     */
    @Override
    public void iteratorCallback(Entry entry) {
        this.add(entry);

        numEntries++;
    }

    @Override
    public int size() {
        return super.size();
    }

    /**
     * returns 0 if called before iteratorCallback
     *
     * @return number of entries in the object.
     */
    public int getNumEntries() {
        return numEntries;
    }
}
