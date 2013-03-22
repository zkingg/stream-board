package thread;

import android.widget.Toast;

public class ToastExpander{

	//public static final String TAG = "ToastExpander";
	public static Thread t;
		
	public static void stop(){t.interrupt();}
	public static void showFor(final Toast aToast, final long durationInMilliseconds) {

		aToast.setDuration(Toast.LENGTH_SHORT);

		 t = new Thread() {
			long timeElapsed = 0l;

			public void run() {
				try {
					while (timeElapsed <= durationInMilliseconds) {
						long start = System.currentTimeMillis();
						aToast.show();
						sleep(2000);
						timeElapsed += System.currentTimeMillis() - start;
					}
				} catch (InterruptedException e) {}
			}
		};
		t.start();
	}
}