package ru.akvine.qraft.core.lib;

public class QrConstants {

    private QrConstants() {}
    public static String FIRST_ELEMENT = "0";
    public static int INCLEMENTS_COUNT = 32;

    public static int[][] QR_CODE_LIMIT_LENGTH = {
        {17, 14, 11, 7},
        {32, 26, 20, 14},
        {53, 42, 32, 24},
        {78, 62, 46, 34},
        {106, 84, 60, 44},
        {134, 106, 74, 58},
        {154, 122, 86, 64},
        {192, 152, 108, 84},
        {230, 180, 130, 98},
        {271, 213, 151, 119},
        {321, 251, 177, 137},
        {367, 287, 203, 155},
        {425, 331, 241, 177},
        {458, 362, 258, 194},
        {520, 412, 292, 220},
        {586, 450, 322, 250},
        {644, 504, 364, 280},
        {718, 560, 394, 310},
        {792, 624, 442, 338},
        {858, 666, 482, 382},
        {929, 711, 509, 403},
        {1003, 779, 565, 439},
        {1091, 857, 611, 461},
        {1171, 911, 661, 511},
        {1273, 997, 715, 535},
        {1367, 1059, 751, 593},
        {1465, 1125, 805, 625},
        {1528, 1190, 868, 658},
        {1628, 1264, 908, 698},
        {1732, 1370, 982, 742},
        {1840, 1452, 1030, 790},
        {1952, 1538, 1112, 842},
        {2068, 1628, 1168, 898},
        {2188, 1722, 1228, 958},
        {2303, 1809, 1283, 983},
        {2431, 1911, 1351, 1051},
        {2563, 1989, 1423, 1093},
        {2699, 2099, 1499, 1139},
        {2809, 2213, 1579, 1219},
        {2953, 2331, 1663, 1273},
    };

    private static int id = 1;

    public static int[][] neighborOffsets = {
            {-1, 0},
            {0, -1},
            {1, 0},
            {0, 1}
    };

    public static int[][][] contours = {
            {
                    {0, 0},
                    {0, 1}
            },
            {
                    {0, 0},
                    {1, 0}
            },
            {
                    {1, 0},
                    {1, 1}
            },
            {
                    {0, 1},
                    {1, 1}
            }
    };

    public static String generateId() {
        String generatedId = String.valueOf(id);
        id++;
        return generatedId;
    }

    public static void resetId() {
        id = 1;
    }
}