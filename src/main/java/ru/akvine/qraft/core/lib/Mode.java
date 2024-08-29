package ru.akvine.qraft.core.lib;

public interface Mode {

    int MODE_NUMBER = 1 << 0;

    int MODE_ALPHA_NUM = 1 << 1;

    int MODE_8BIT_BYTE = 1 << 2;

    int MODE_KANJI = 1 << 3;
}
