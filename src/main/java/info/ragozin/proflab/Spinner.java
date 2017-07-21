package info.ragozin.proflab;

public class Spinner {

	static double garbage = 0;

	
    public static double spinMicros(float micros) {
        double x = 1.000001;
        // very rough estimation of cycles to burn
        for(int r = 0; r != (int)(3*micros); ++r) {
            for(int i = 0; i != 1000; ++i) {
                x = x * x;
            }
        }
        garbage += x;
        return x;
    }

}
