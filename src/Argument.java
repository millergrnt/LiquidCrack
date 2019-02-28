/**
 * Represents an argument on the command line
 */

/**
 * Simple enum to help interpret arguments
 */
enum Argtype{ FLAG,STRING,INTEGER }

/**
 * Represents an argument type
 * Author: Grant Miller <gem1086@rit.edu>
 * File: Argument.java
 * Date: 25 February 2019
 * Version
 */
public class Argument {

    // Instance variables
    private Argtype type;
    private Object val;
    private String argFullName;
    private String argShortName;
    private String description;
    private boolean required;

    /**
     * Argument constructor
     * @param type Type of the argument
     * @param argFullName Full name of the argument i.e. 'output'
     * @param argShortName Short name of the argument i.e 'o'
     * @param description Description of what this flag does
     */
    Argument(Argtype type, String argFullName, String argShortName, String description) {
        this.type = type;
        this.argFullName = argFullName;
        this.argShortName = argShortName;

        // if this argument is a flag it will be null by default
        if(type == Argtype.FLAG)
            this.val = false;
        else

            // Default values are null otherwise
            this.val = null;

        this.description = description;
        this.required = false;
    }

    /**
     * Argument constructor
     * @param type Type of the argument
     * @param argFullName Full name of the argument i.e. 'output'
     * @param argShortName Short name of the argument i.e 'o'
     */
    Argument(Argtype type, String argFullName, String argShortName) {
        this.type = type;
        this.argFullName = argFullName;
        this.argShortName = argShortName;

        // if this argument is a flag it will be null by default
        if(type == Argtype.FLAG)
            this.val = false;
        else

            // Default values are null otherwise
            this.val = null;

        this.description = "";
        this.required = false;
    }


    /**
     * Sets this argument as required. Will thrown an exception if not found
     * when parsing arguments
     * @param required Whether or not this Argument should be required
     */
    public void setRequired(boolean required) { this.required = required; }


    /**
     * Sets the description of this argument to the provided string
     * @param description Description of this argument
     */
    public void setDescription(String description) { this.description = description; }


    /**
     * Description getter
     * @return The description of this argument
     */
    public String getDescription() { return this.description; }


    /**
     * Full name getter
     * @return The full name of this argument
     */
    public String getArgFullName() {
        return argFullName;
    }


    /**
     * Short name getter
     * @return The short name of this argument
     */
    public String getArgShortName() {
        return argShortName;
    }

    /**
     * Val setter
     * @param val The value derived from the command line
     */
    public void setVal(Object val) {
        this.val = val;
    }


    /**
     * val getter
     * @return Value of this argument
     */
    public Object getVal() { return this.val; }


    /**
     * Gets the type of this argument
     * @return type of the argument
     */
    public Argtype getType() {
        return type;
    }


    /**
     * Required getter
     * @return Whether or not this argument is required
     */
    public boolean isRequired() {
        return required;
    }
}
