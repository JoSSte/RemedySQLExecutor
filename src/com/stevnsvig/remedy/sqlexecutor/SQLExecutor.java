package com.stevnsvig.remedy.sqlexecutor;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.SQLResult;
import com.bmc.arsys.api.Value;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author <a href="mailto:jonas.stumph@stevnsvig.com"> Jonas Stumph Stevnsvig</a>
 */
public class SQLExecutor extends BaseClassWithARSConnection {

    public SQLExecutor(Properties prop) {
        super(prop);
    }

    /**
     * Execute the defined SQL query
     *
     * @param sql SQL string to be executed
     * @return
     * @throws ARException
     */
    public ArrayList<ArrayList<String>> executeSQL(String sql) throws ARException {
        ArrayList<ArrayList<String>> returnList = new ArrayList<>();

        int maxRows = 0;
        SQLResult sr = arsConnection.getListSQL(sql, maxRows, true);
        System.out.println("Executing\t" + sql);
        System.out.println("Number of matches:\t" + sr.getTotalNumberOfMatches());
        for (Iterator<List<Value>> iterator = sr.getContents().iterator(); iterator.hasNext();) {
            ArrayList<String> resultList = new ArrayList<>();
            ArrayList<Value> resultRow = (ArrayList<Value>) iterator.next();
            for (Value next : resultRow) {
                resultList.add(next.toString());
            }
            returnList.add(resultList);
        }
        return returnList;
    }
}
