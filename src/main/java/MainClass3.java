import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainClass3 {

    public String crypt(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] hash = md.digest();
            for (byte aHash : hash) {
                if ((0xff & aHash) < 0x10) {
                    hexString.append("0" + Integer.toHexString((0xFF & aHash)));
                } else {
                    hexString.append(Integer.toHexString(0xFF & aHash));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }

    public static void main(String[] args) {
        long N = 1000 * 100 * 100;
        if (args.length > 0) {
            N = Long.parseLong(args[0], 10);
        }
        MainClass3 main = new MainClass3();
        RandomStringUtils randomStringUtils = new RandomStringUtils();
        long ts,tf;
        long timer1 = 0;
        long timer2 = 0;
        for (long i = 0; i < N; i++) {
        	ts = System.nanoTime();
            String text = randomStringUtils.generate();
            tf = System.nanoTime();
            timer1 += tf - ts;
            ts = tf;
			main.crypt(text);
			tf = System.nanoTime();
			timer2 += tf - ts;
			ts = tf;
        }
        System.out.println("Done " + N);
        System.out.println("Generation: " + 100 * timer1 / (timer1 + timer2) + "%");
        System.out.println("Hasing: " + 100 * timer2 / (timer1 + timer2) + "%");
    }
}