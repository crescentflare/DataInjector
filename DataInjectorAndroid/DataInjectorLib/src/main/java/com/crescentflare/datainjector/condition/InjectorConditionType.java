package com.crescentflare.datainjector.condition;

/**
 * Data injector condition: condition type enum
 * Determines the type of the condition item in a sequence
 */
public enum InjectorConditionType
{
    // ---
    // Enum values
    // ---

    Unknown(""),
    Equals("=="),
    NotEquals("!="),
    Bigger(">"),
    Smaller("<"),
    BiggerOrEquals(">="),
    SmallerOrEquals("<="),
    And("&&"),
    Or("||");


    // ---
    // Link string to enum
    // ---

    private String string;

    InjectorConditionType(String string)
    {
        this.string = string;
    }

    public String toString()
    {
        return this.string;
    }

    public static InjectorConditionType fromString(String string)
    {
        for (InjectorConditionType type : values())
        {
            if (type.string.length() > 0 && type.string.equals(string))
            {
                return type;
            }
        }
        return Unknown;
    }


    // ---
    // Helpers
    // ---

    public boolean isComparison()
    {
        return this == Equals || this == NotEquals || this == Bigger || this == Smaller || this == BiggerOrEquals || this == SmallerOrEquals;
    }

    public boolean isOperator()
    {
        return this == And || this == Or;
    }

    public static boolean isReservedCharacter(char chr)
    {
        for (InjectorConditionType type : values())
        {
            if (type.string.length() > 0 && type.string.charAt(0) == chr)
            {
                return true;
            }
        }
        return false;
    }
}
