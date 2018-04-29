package ru.nchernetsov.geometry;

/**
 * Константы Мировой Геодезической Системы (базового геодезического датума) WGS-84
 */
public class WGS84 {
    public static final double A = 6378137.0;              //большая полуось эллипса a
    private static final double InverseF = 298.257223563;  //1/f - обратное сжатие эллипсоида (f=(a-b)/a)
    private static final double A_B = 1.0/(1.0 - 1.0/InverseF);  //отношение полуосей эллипса a/b
    public static final double A_B2 = A_B*A_B;                   //(a*b)^2
    public static final double A_B4 = A_B2*A_B2;                 //(a*b)^4
    public static final double E2 = (2.0 - 1.0/InverseF)/InverseF;  //f*(2-f) = 1-(b^2/a^2) - квадрат первого численного
    //эксцентриситета эллипсоида
    public static final double OneMinusE2 = 1.0 - E2;  // = b^2/a^2
    public static final double KMperDegM = 111.0;
}
