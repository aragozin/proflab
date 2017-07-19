package info.ragozin.proflab;

import java.io.DataInputStream;
import java.io.IOException;

public class DeathWatch {

	public static void start() {
		new Thread() {
			@Override
			public void run() {
				try {
					while(true) {
						new DataInputStream(System.in).readByte();
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		};
	}	
}
