package com.crescentflare.datainjector.condition;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorDataDetector;
import com.crescentflare.datainjector.utility.InjectorDataType;
import com.crescentflare.datainjector.utility.InjectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data injector condition: a full condition
 * Contains condition items parsed from a string and can be checked if the condition is met
 */
public class InjectorCondition
{
    // ---
    // Members
    // ---

    private List<ConditionItem> items = new ArrayList<>();


    // ---
    // Initialization
    // ---

    public InjectorCondition(String condition)
    {
        this(condition, null, null);
    }

    public InjectorCondition(String condition, Map<String, Object> fullRefData)
    {
        this(condition, fullRefData, null);
    }

    public InjectorCondition(String condition, Map<String, Object> fullRefData, Map<String, Object> subRefData)
    {
        if (condition.length() == 0)
        {
            return;
        }
        int curPos = 0;
        boolean findingData = true;
        while (findingData)
        {
            // Reset state
            findingData = false;
            curPos = findNonSpace(condition, curPos);

            // Check for condition operator or comparison type
            if (curPos >= 0 && curPos < condition.length())
            {
                char curChr = condition.charAt(curPos);
                if (InjectorConditionType.isReservedCharacter(curChr))
                {
                    InjectorConditionType foundType = InjectorConditionType.Unknown;
                    if (curPos + 2 < condition.length())
                    {
                        String checkTypeString = condition.substring(curPos, curPos + 2);
                        for (InjectorConditionType type : InjectorConditionType.values())
                        {
                            if (type.toString().length() == 2 && checkTypeString.startsWith(type.toString()))
                            {
                                foundType = type;
                                break;
                            }
                        }
                        if (foundType == InjectorConditionType.Unknown)
                        {
                            for (InjectorConditionType type : InjectorConditionType.values())
                            {
                                if (type.toString().length() == 1 && checkTypeString.startsWith(type.toString()))
                                {
                                    foundType = type;
                                    break;
                                }
                            }
                        }
                    }
                    if (foundType != InjectorConditionType.Unknown)
                    {
                        items.add(new ConditionItem(foundType, InjectorDataType.Unknown, null));
                        curPos += foundType.toString().length();
                    }
                    else
                    {
                        break;
                    }
                    if (curPos >= 0 && curPos < condition.length())
                    {
                        findingData = true;
                        continue;
                    }
                    else
                    {
                        break;
                    }
                }
            }

            // Check for value type
            InjectorDataType valueType = InjectorDataDetector.detectFromString(condition, curPos);
            if (valueType != InjectorDataType.Unknown)
            {
                int endPos = InjectorDataDetector.endOfTypeTypeString(valueType, condition, curPos);
                if (endPos < 0)
                {
                    endPos = findReservedCharacter(condition, curPos);
                }
                if (endPos < 0)
                {
                    break;
                }
                Object valueObject = obtainObject(valueType, condition, curPos, endPos, fullRefData, subRefData);
                items.add(new ConditionItem(InjectorConditionType.Unknown, InjectorDataDetector.detectFromObject(valueObject), valueObject));
                if (endPos >= 0 && endPos < condition.length())
                {
                    curPos = endPos;
                    findingData = true;
                }
            }
        }
    }


    // ---
    // Condition checking
    // ---

    public boolean isMet()
    {
        // First simplify the item list by changing comparisons into booleans
        List<ConditionItem> simplifiedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++)
        {
            ConditionItem item = items.get(i);
            if (i + 1 < items.size())
            {
                ConditionItem comparisonItem = items.get(i + 1);
                if (comparisonItem.getConditionType().isComparison())
                {
                    if (i + 2 < items.size())
                    {
                        ConditionItem compareItem = items.get(i + 2);
                        simplifiedItems.add(new ConditionItem(InjectorConditionType.Unknown, InjectorDataType.Boolean, compareItems(item, compareItem, comparisonItem.getConditionType())));
                        i += 2;
                        continue;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            simplifiedItems.add(item);
        }

        // Finalize the comparison with operators
        boolean currentCondition = true;
        InjectorConditionType currentOperator = InjectorConditionType.And;
        for (ConditionItem item : simplifiedItems)
        {
            if (currentOperator != InjectorConditionType.Unknown)
            {
                if (item.getConditionType().isOperator())
                {
                    return false;
                }
                Boolean result = InjectorConv.toBoolean(item.getValue());
                boolean checkValue = result != null ? result : false;
                if (currentOperator == InjectorConditionType.And)
                {
                    currentCondition = currentCondition && checkValue;
                }
                else
                {
                    currentCondition = currentCondition || checkValue;
                }
                currentOperator = InjectorConditionType.Unknown;
            }
            else
            {
                currentOperator = item.getConditionType();
                if (!currentOperator.isOperator())
                {
                    return false;
                }
            }
        }
        return currentCondition;
    }

    private boolean compareItems(ConditionItem firstItem, ConditionItem secondItem, InjectorConditionType type)
    {
        switch (type)
        {
            case Equals:
                if (secondItem.getValueType() == InjectorDataType.String)
                {
                    String firstString = InjectorConv.toString(firstItem.getValue());
                    String secondString = InjectorConv.toString(secondItem.getValue());
                    if (firstString != null && secondString != null)
                    {
                        return firstString.equals(secondString);
                    }
                }
                else if (secondItem.getValueType() == InjectorDataType.Number)
                {
                    Integer firstInteger = InjectorConv.toInteger(firstItem.getValue());
                    Integer secondInteger = InjectorConv.toInteger(secondItem.getValue());
                    if (firstInteger != null && secondInteger != null)
                    {
                        return firstInteger.equals(secondInteger);
                    }
                }
                else if (secondItem.getValueType() == InjectorDataType.DecimalNumber)
                {
                    Double firstDouble = InjectorConv.toDouble(firstItem.getValue());
                    Double secondDouble = InjectorConv.toDouble(secondItem.getValue());
                    if (firstDouble != null && secondDouble != null)
                    {
                        return firstDouble.equals(secondDouble);
                    }
                }
                else if (secondItem.getValueType() == InjectorDataType.Boolean)
                {
                    Boolean firstBoolean = InjectorConv.toBoolean(firstItem.getValue());
                    Boolean secondBoolean = InjectorConv.toBoolean(secondItem.getValue());
                    if (firstBoolean != null && secondBoolean != null)
                    {
                        return firstBoolean.equals(secondBoolean);
                    }
                }
                else if (secondItem.getValueType() == InjectorDataType.Empty)
                {
                    if (firstItem.getValueType() == InjectorDataType.String)
                    {
                        String firstString = InjectorConv.toString(firstItem.getValue());
                        if (firstString == null)
                        {
                            firstString = "";
                        }
                        return firstString.length() == 0;
                    }
                    return firstItem.getValueType() == InjectorDataType.Empty;
                }
                return false;
            case NotEquals:
                if (secondItem.getValueType() == InjectorDataType.String)
                {
                    String firstString = InjectorConv.toString(firstItem.getValue());
                    String secondString = InjectorConv.toString(secondItem.getValue());
                    if (firstString != null && secondString != null)
                    {
                        return !firstString.equals(secondString);
                    }
                }
                else if (secondItem.getValueType() == InjectorDataType.Number)
                {
                    Integer firstInteger = InjectorConv.toInteger(firstItem.getValue());
                    Integer secondInteger = InjectorConv.toInteger(secondItem.getValue());
                    if (firstInteger != null && secondInteger != null)
                    {
                        return !firstInteger.equals(secondInteger);
                    }
                }
                else if (secondItem.getValueType() == InjectorDataType.DecimalNumber)
                {
                    Double firstDouble = InjectorConv.toDouble(firstItem.getValue());
                    Double secondDouble = InjectorConv.toDouble(secondItem.getValue());
                    if (firstDouble != null && secondDouble != null)
                    {
                        return !firstDouble.equals(secondDouble);
                    }
                }
                else if (secondItem.getValueType() == InjectorDataType.Boolean)
                {
                    Boolean firstBoolean = InjectorConv.toBoolean(firstItem.getValue());
                    Boolean secondBoolean = InjectorConv.toBoolean(secondItem.getValue());
                    if (firstBoolean != null && secondBoolean != null)
                    {
                        return !firstBoolean.equals(secondBoolean);
                    }
                }
                else if (secondItem.getValueType() == InjectorDataType.Empty)
                {
                    if (firstItem.getValueType() == InjectorDataType.String)
                    {
                        String firstString = InjectorConv.toString(firstItem.getValue());
                        if (firstString == null)
                        {
                            firstString = "";
                        }
                        return firstString.length() > 0;
                    }
                    return firstItem.getValueType() != InjectorDataType.Empty;
                }
                return false;
            case Bigger:
            {
                Double firstDouble = InjectorConv.toDouble(firstItem.getValue());
                Double secondDouble = InjectorConv.toDouble(secondItem.getValue());
                if (firstDouble != null && secondDouble != null)
                {
                    return firstDouble > secondDouble;
                }
                return false;
            }
            case Smaller:
            {
                Double firstDouble = InjectorConv.toDouble(firstItem.getValue());
                Double secondDouble = InjectorConv.toDouble(secondItem.getValue());
                if (firstDouble != null && secondDouble != null)
                {
                    return firstDouble < secondDouble;
                }
                return false;
            }
            case BiggerOrEquals:
            {
                Double firstDouble = InjectorConv.toDouble(firstItem.getValue());
                Double secondDouble = InjectorConv.toDouble(secondItem.getValue());
                if (firstDouble != null && secondDouble != null)
                {
                    return firstDouble >= secondDouble;
                }
                return false;
            }
            case SmallerOrEquals:
            {
                Double firstDouble = InjectorConv.toDouble(firstItem.getValue());
                Double secondDouble = InjectorConv.toDouble(secondItem.getValue());
                if (firstDouble != null && secondDouble != null)
                {
                    return firstDouble <= secondDouble;
                }
                return false;
            }
        }
        return false;
    }


    // ---
    // Parse helpers
    // ---

    private Object obtainObject(InjectorDataType type, String condition, int start, int end, Map<String, Object> fullRefData, Map<String, Object> subRefData)
    {
        String item = condition.substring(start, end).trim();
        if (type == InjectorDataType.String)
        {
            char quoteChr = item.charAt(0);
            if (quoteChr == '\'' || quoteChr == '"')
            {
                if (item.endsWith("" + quoteChr))
                {
                    item = item.substring(1, item.length() - 1);
                }
            }
        }
        return obtainConvertedObject(type, item, fullRefData, subRefData);
    }

    private Object obtainConvertedObject(InjectorDataType type, String item, Map<String, Object> fullRefData, Map<String, Object> subRefData)
    {
        switch (type)
        {
            case String:
                return item;
            case Number:
                return InjectorConv.toInteger(item);
            case DecimalNumber:
                return InjectorConv.toDouble(item);
            case Boolean:
                return InjectorConv.toBoolean(item);
            case Empty:
                return null;
            case Reference:
                if (fullRefData != null)
                {
                    return InjectorUtil.itemFromMap(fullRefData, item.substring(1));
                }
                break;
            case SubReference:
                if (fullRefData != null)
                {
                    return InjectorUtil.itemFromMap(subRefData, item.substring(2));
                }
                break;
        }
        return null;
    }

    private int findNonSpace(String string, int start)
    {
        if (start < 0)
        {
            return -1;
        }
        int len = string.length();
        for (int i = start; i < len; i++)
        {
            if (string.charAt(i) != ' ')
            {
                return i;
            }
        }
        return -1;
    }

    private int findReservedCharacter(String string, int start)
    {
        if (start < 0)
        {
            return -1;
        }
        int len = string.length();
        for (int i = start; i < len; i++)
        {
            if (InjectorConditionType.isReservedCharacter(string.charAt(i)))
            {
                return i;
            }
        }
        return len;
    }


    // ---
    // Object for storing condition sequence items
    // ---

    private static class ConditionItem
    {
        private InjectorConditionType conditionType;
        private InjectorDataType valueType;
        private Object value;

        public ConditionItem(InjectorConditionType conditionType, InjectorDataType valueType, Object value)
        {
            this.conditionType = conditionType;
            this.valueType = valueType;
            this.value = value;
        }

        public InjectorConditionType getConditionType()
        {
            return conditionType;
        }

        public InjectorDataType getValueType()
        {
            return valueType;
        }

        public Object getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return "InjectorConditionItem{" +
                    "conditionType=" + conditionType +
                    ", valueType=" + valueType +
                    ", value=" + value +
                    '}';
        }
    }
}
