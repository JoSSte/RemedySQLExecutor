package com.stevnsvig.remedy.sqlexecutor;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Constants;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.SortInfo;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class with Remedy Connection to facilitate rapid development of java
 * with configuration file and remedy access.
 *
 * Extend the class, load the properties, and go!
 *
 * @author <a href="mailto:jonas.stumph@stevnsvig.com"> Jonas Stumph Stevnsvig</a>
 */
public abstract class BaseClassWithARSConnection {

    /**
     * Connection to Remedy Server
     */
    protected ARServerUser arsConnection = new ARServerUser();
    /**
     * username to use for connection
     */
    protected String arsUser;
    /**
     * password to connect with
     */
    protected String arsPassword;
    /**
     * RPC Port
     */
    protected int arsRPC = 0;
    /**
     * Server name
     */
    protected String arsServer = "";
    /**
     * port of AR Server (default is 5000)
     */
    protected int arsPort = 5000;
    /**
     * Max entries to retrieve (default is 50000)
     */
    protected int maxRetreive = 50000;
    /**
     * Name of the property file to load (default is properties.xml)
     */
    protected static String prefFileName = "properties.xml";
    /**
     * Program does not execute any delete/insert/update if this flag is set.
     * (Default is true (do nothing)
     */
    public static boolean doNothing = true;

    /**
     * Sample configuration file: see createPropertiesXML()
     *
     * @return {@link java.util.Properties} object with the options loaded from
     * the xml file
     * @throws java.io.FileNotFoundException
     */
    public static Properties loadPropertiesFromXML() throws FileNotFoundException {
        Properties prop = new Properties();
        InputStream is;

        if ((new File(prefFileName)).exists()) {
            try {
                is = new FileInputStream(prefFileName);

                try {
                    prop.loadFromXML(is);
                } catch (IOException ex) {
                    Logger.getLogger(BaseClassWithARSConnection.class.getName()).log(java.util.logging.Level.SEVERE, "IO Exception", ex);
                } finally {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Logger.getLogger(BaseClassWithARSConnection.class.getName()).log(java.util.logging.Level.SEVERE, "IO Exception on file close", ex);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(BaseClassWithARSConnection.class.getName()).log(java.util.logging.Level.SEVERE, "File Not Found", ex);
                System.err.println("FileNotFound: " + ex.getMessage());
                System.exit(1);
            }
        } else {
            throw new FileNotFoundException(prefFileName + " does not exist");
        }
        return prop;
    }

    public static void createPropertiesXML() throws FileNotFoundException {
        PrintWriter pOut = new PrintWriter(prefFileName);
        pOut.print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "      <!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n"
                + "      <properties>\n"
                + "      <comment>Configuration file for Something</comment>\n"
                + "      <!-- ARS server login settings -->\n"
                + "      <entry key=\"ars.server.dev\">somefqdnServerName</entry>\n"
                + "      <entry key=\"ars.server.test\">somefqdnServerName</entry>\n"
                + "      <entry key=\"ars.server.edu\">somefqdnServerName</entry>\n"
                + "      <entry key=\"ars.server.preprod\">somefqdnServerName</entry>\n"
                + "      <entry key=\"ars.server.prod\">somefqdnServerName</entry>\n"
                + "      <entry key=\"ars.environment\">dev</entry><!-- must match one of the above\n"
                + "      suffixes -->\n"
                + "      <entry key=\"ars.port\">5500</entry>\n"
                + "      <entry key=\"ars.user\">Demo</entry>\n"
                + "      <entry key=\"ars.password\"></entry>\n"
                + "     \n"
                + "      <!-- Script Settings -->\n"
                + "      <entry key=\"maxRetreive\">50000</entry> <!-- Default: 50000-->\n"
                + "     \n"
                + "      <!-- Script Control-->\n"
                + "      <entry key=\"doNothing\">true</entry><!-- does not delete/insert/update if\n"
                + "      this value is set to true -->\n"
                + "     \n"
                + "      </properties>");

    }

    /**
     * same as {@link loadPropertiesFromXML}, but takes a filename as argument
     *
     * @param fName
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static Properties loadPropertiesFromXML(String fName) throws FileNotFoundException {
        prefFileName = fName;
        return loadPropertiesFromXML();
    }

    public BaseClassWithARSConnection(Properties prop) {
        this(prop, "");
    }

    /**
     *
     * @param prop {@link java.util.Properties} loaded from XML file
     * @param label label for connection (e.g. fromServer)
     */
    public BaseClassWithARSConnection(Properties prop, String label) {
        String servString = "ars.environment";
        if (!"".equals(label)) {
            servString += "." + label;
        }
        System.out.println("Loading configuration file ...");
        //Remedy settings
        arsUser = prop.getProperty("ars.user");
        arsPassword = prop.getProperty("ars.password");
        arsServer = prop.getProperty("ars.server." + prop.getProperty(servString));
        if (arsServer == null || arsServer.length() == 0) {
            System.err.println("error loading server name");
            System.exit(1);
        }
        arsPort = Integer.parseInt(prop.getProperty("ars.port"));
        maxRetreive = Integer.parseInt(prop.getProperty("maxRetreive"));
        doNothing = Boolean.parseBoolean(prop.getProperty("doNothing"));
        if (doNothing) {
            Logger.getLogger(BaseClassWithARSConnection.class.getName()).log(java.util.logging.Level.WARNING, "DOING NOTHING - no Creates, Updates or Deletions are being executed");
        }
        System.out.println("Connecting  " + arsUser + "@" + arsServer + ":" + arsPort);
        if (arsPassword.equals("")) {
            Console console = System.console();
            console.printf("Please enter your password: ");
            char[] passwordChars = console.readPassword();
            String passwordString = new String(passwordChars);
            if (passwordString.length() == 0) {//TODO change this so this is not triggered for Demo(?)
                System.err.println("ERROR : blank password!");
                Logger.getLogger(BaseClassWithARSConnection.class.getName()).log(java.util.logging.Level.SEVERE, "Blank password!");
                System.exit(1);
            } else {
                arsPassword = String.valueOf(passwordChars);
            }
        }
        arsRPC = Integer.parseInt(prop.getProperty("ars.rpc", arsRPC + ""));
        arsConnection = new ARServerUser();
        arsConnection.setUser(arsUser);
        arsConnection.setPassword(arsPassword);
        arsConnection.setServer(arsServer);
        arsConnection.setPort(arsPort);
    }

    /**
     *
     * @return the name of the user you have used to connect to the server
     */
    public String getArsUser() {
        return arsUser;
    }

    /**
     *
     * @return the name of the server you are connected to
     */
    public String getArsServer() {
        return arsServer;
    }

    /**
     * Returns the number of records in a specific form;
     *
     * @param formName the name of the form
     * @return number of records in the form supplied
     */
    public int getNumrecordsInForm(String formName) {
        List<SortInfo> sortList = new ArrayList<>();
        int firstRetreive = 0;
        OutputInteger numMatches = new OutputInteger();
        ResultEntryList results = new ResultEntryList();
        QualifierInfo qiPlain = new QualifierInfo();

        // we only want to count - request ID only
        int[] fieldIds = {1};
        try {
            this.arsConnection.getListEntryObjects(formName, qiPlain, firstRetreive, maxRetreive, sortList, fieldIds, false, numMatches, results);
        } catch (ARException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return numMatches.intValue();
    }

    /**
     *
     * @param FieldDataType the integer representation of the field datatype
     * @return String representation of the fields datatype
     */
    public static String getFieldDataTypeString(int FieldDataType) {
        switch (FieldDataType) {
            case Constants.AR_DATA_TYPE_ATTACH:
                return "attach";
            case Constants.AR_DATA_TYPE_ATTACH_POOL:
                return "attachment pool";
            case Constants.AR_DATA_TYPE_BITMASK:
                return "bitmask";
            case Constants.AR_DATA_TYPE_BYTES:
                return "bytes";
            case Constants.AR_DATA_TYPE_CHAR:
                return "char";
            case Constants.AR_DATA_TYPE_COLUMN:
                return "column";
            case Constants.AR_DATA_TYPE_CONTROL:
                return "control";
            case Constants.AR_DATA_TYPE_COORDS:
                return "coordinates";
            case Constants.AR_DATA_TYPE_CURRENCY:
                return "currency";
            case Constants.AR_DATA_TYPE_DATE:
                return "date";
            case Constants.AR_DATA_TYPE_DECIMAL:
                return "decimal";
            case Constants.AR_DATA_TYPE_DIARY:
                return "diary";
            case Constants.AR_DATA_TYPE_DISPLAY:
                return "display";
            case Constants.AR_DATA_TYPE_ENUM:
                return "enum";
            case Constants.AR_DATA_TYPE_INTEGER:
                return "integer";
            case Constants.AR_DATA_TYPE_JOIN:
                return "join";
            case Constants.AR_DATA_TYPE_KEYWORD:
                return "keyword";
            /*
             * case Constants.AR_DATA_TYPE_MAX_TYPE: return "max type";
             */
            case Constants.AR_DATA_TYPE_NULL:
                return "NULL";
            case Constants.AR_DATA_TYPE_PAGE:
                return "page";
            case Constants.AR_DATA_TYPE_PAGE_HOLDER:
                return "page holder";
            case Constants.AR_DATA_TYPE_REAL:
                return "real";
            case Constants.AR_DATA_TYPE_TABLE:
                return "table";
            case Constants.AR_DATA_TYPE_TIME:
                return "time";
            case Constants.AR_DATA_TYPE_TIME_OF_DAY:
                return "time of day";
            case Constants.AR_DATA_TYPE_TRIM:
                return "trim";
            case Constants.AR_DATA_TYPE_ULONG:
                return "ulong";
            case Constants.AR_DATA_TYPE_UNKNOWN:
            default:
                return "UNKNOWN";
            case Constants.AR_DATA_TYPE_VALUE_LIST:
                return "value list";
            case Constants.AR_DATA_TYPE_VIEW:
                return "view";
        }
    }

    /**
     *
     * @param FTS
     * @return a String representing the FTS state of the field
     */
    public static String getFTSOption(int FTS) {
        switch (FTS) {
            case Constants.AR_FULLTEXT_OPTIONS_EXCLUDE_FIELD_BASED:
                //return "Field is not indexed";
                return "NOT_FTS_Indexed";
            case Constants.AR_FULLTEXT_OPTIONS_INDEXED:
                //return "Field is Full Text indexed.";
                return "FTS_Indexed";
            case Constants.AR_FULLTEXT_OPTIONS_LITERAL:
                //return "Field indexed for literal search if Full Text indexed.";
                return "Literal_FTS_Indexed";
            default:
            case Constants.AR_FULLTEXT_OPTIONS_NONE:
                return "FTS_&_MFS";
        }
    }
}
