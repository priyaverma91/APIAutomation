package utils;

import io.cucumber.datatable.DataTable;
import jxl.write.DateFormats;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class DataTableUtils {
    private static String onlyCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static String onlyNumbers = "1234567890";
    private static String alphaNumeric = onlyCharacters + onlyNumbers;

    public static <K, V> Map<K, V> getDataMap(
            DataTable table, int keyCol, int valCol, Class<K> key, Class<V> value) {
        List<Map<String, String>> tableMap = table.transpose().asMaps();
        return new HashMap<>(
                tableMap.get(valCol - 1).entrySet().stream()
                        .filter(e -> Objects.nonNull(e.getValue()))
                        .collect(
                                Collectors.toMap(
                                        e -> key.cast(e.getKey()), e -> value.cast(parseValue(value, e)))));
    }

    private static <V> Object parseValue(Class<V> value, Map.Entry e) {
        Object object;
        if (value.isAssignableFrom(Integer.class)) {
            object = e.getValue().toString().isEmpty() ? 0 : Integer.parseInt(e.getValue().toString());
        } else if (value.isAssignableFrom(Boolean.class)) {
            object = !e.getValue().toString().isEmpty() && Boolean.parseBoolean(e.getValue().toString());
        } else {
            object = e.getValue().toString();
        }
        return object;
    }

    public static Map<String, String> getValueMap(DataTable table) {
        List<Map<String, String>> tableMap = table.transpose().asMaps();
        return new HashMap<>(tableMap.get(0));
    }

    public static Map<String, Integer> getLengthMap(DataTable table) {
        return new HashMap<>(getDataMap(table, 0, 2, String.class, Integer.class));
    }

    protected static String getRandomStringOfNumbers(int length) {
        StringBuilder randomString = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        while (randomString.length() < length) {
            int index = (int) (rnd.nextFloat() * onlyNumbers.length());
            randomString.append(onlyNumbers.charAt(index));
        }
        return randomString.toString();
    }

    protected static String getRandomStringOfNumbersWithoutZero(int length) {
        StringBuilder randomString = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        String withoutZero = onlyNumbers.substring(0, onlyNumbers.length() - 2);
        while (randomString.length() < length) {
            int index = (int) (rnd.nextFloat() * withoutZero.length());
            randomString.append(withoutZero.charAt(index));
        }
        return randomString.toString();
    }

    protected static String getRandomStringOfAlphabets(int length) {
        StringBuilder randomString = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        while (randomString.length() < length) {
            int index = (int) (rnd.nextFloat() * onlyCharacters.length());
            randomString.append(onlyCharacters.charAt(index));
        }
        return randomString.toString().toLowerCase();
    }

    protected static String getRandomString(int length) {
        StringBuilder randomString = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        while (randomString.length() < length) {
            int index = (int) (rnd.nextFloat() * alphaNumeric.length());
            randomString.append(alphaNumeric.charAt(index));
        }
        return randomString.toString().toLowerCase();
    }

    protected static String getTimeStamp(DateFormats format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

    public static String getFutureDate(DateFormats format, int daysToUpdate) {
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, daysToUpdate);
        Date updatedDate = cal.getTime();
        return dateFormat.format(updatedDate);
    }

    public static String getPastDate(DateFormats format, int daysToUpdate) {
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        LocalDateTime localDateTime =
                currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusDays(daysToUpdate);
        Date updatedDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return dateFormat.format(updatedDate);
    }

    public static void updateValues(
            Map<String, String> testData,
            String valueType,
            DateFormats format) {

        testData
                .keySet()
                .forEach(
                        key -> {
                            if (valueType.toUpperCase().contentEquals("VALID")
                                    || valueType.toUpperCase().contentEquals("VALIDATION")) {
                                setValues(testData, key, format);
                            } else if (valueType.toUpperCase().contentEquals("INVALID")
                                    || valueType.toUpperCase().contentEquals("MISSING")) {
                                setValues(testData, key, format);
                            }
                        });
    }

    private static void setValues(
            Map<String, String> testData, String key, DateFormats format) {
        if (Objects.nonNull(testData.get(key)) && !testData.get(key).isEmpty()) {
            switch (testData.get(key).toUpperCase().trim()) {
                case "BLANK":
                    testData.put(key, " ");
                    break;
                case "NULL":
                    testData.put(key, null);
                    break;
                case "NUMBERS":
                    testData.put(key, getRandomStringOfNumbersWithoutZero(10));
                    break;
                case "DECIMALS":
                    testData.put(
                            key,
                            getRandomStringOfNumbersWithoutZero(4)
                                    + "."
                                    + getRandomStringOfNumbersWithoutZero(2));
                    break;
                case "ALPHABETS":
                    testData.put(key, getRandomStringOfAlphabets(15));
                    break;
                case "ALPHANUM":
                    testData.put(key, getRandomString(15));
                    break;
                case "DATE":
                    testData.put(key, getTimeStamp(format));
                    break;
                case "FUTUREDATE":
                    testData.put(key, getFutureDate(format, 2));
                    break;
                case "PASTDATE":
                    testData.put(key, getPastDate(format, 2));
                    break;
                default:
                    break;
            }
        } else {
            testData.put(key, null);
        }
    }
}
