import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainClass {

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
        long N = 1000 * 100;
        if (args.length > 0) {
            N = Long.parseLong(args[0], 10);
        }
        MainClass main = new MainClass();
        RandomStringUtils randomStringUtils = new RandomStringUtils();
        for (long i = 0; i < N; i++) {
            main.crypt(randomStringUtils.generate());
        }
        System.out.println("Done " + N);
    }
}