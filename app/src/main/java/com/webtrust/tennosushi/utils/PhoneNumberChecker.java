package com.webtrust.tennosushi.utils;

/**
 * Класс, который занимается проверкой номера телефона
 * на корректность.
 */
public class PhoneNumberChecker {

    /**
     * Проверяет номер телефона на правильность.
     * @param number Номер телефона
     * @return Результат проверки
     */
    public static boolean checkNumber(String number) {
        // убираем пробелы
        number = number.replace(" ", "");

        // считаем цифры
        int digitCount = 0;
        for (int i = 0; i < number.length(); i++)
            if (Character.isDigit(number.charAt(i))) digitCount++;

        // проверяем правильность номера согласно правилам
        switch (number.charAt(0)) {
            case '+':
                return ((number.length() - 1) == digitCount && digitCount == 11);
            case '8':
                return ((number.length()) == digitCount && digitCount == 11);
            case '9':
                return ((number.length()) == digitCount && digitCount == 10);
        }

        // если номер неверен
        return false;
    }
}
