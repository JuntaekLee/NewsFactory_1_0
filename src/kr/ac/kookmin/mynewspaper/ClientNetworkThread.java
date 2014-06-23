/**
 * 
 */
package kr.ac.kookmin.mynewspaper;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import kr.ac.kookmin.mynewspaper.protocol.RequestData;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 스레드 : 클라이언트가 서버에게 Request를 보내고 Response를 받는 스레드
 **/
public class ClientNetworkThread extends Thread {
	private static final String TAG = "ClientNetworkThread";
	// Host Address
	private String mHostAddress;
	// Client Socket
	private Socket mClientSocket;
	// 원하는 작업
	private RequestData mRequestData;

	private Handler mClientNetworkHandler;

	public ClientNetworkThread(String mHostAddress, RequestData mRequestData,
			Handler mClientNetworkHandler) {
		super();
		this.mHostAddress = mHostAddress;
		this.mRequestData = mRequestData;
		this.mClientNetworkHandler = mClientNetworkHandler;

	}

	@Override
	public void run() {
		Message msg = Message.obtain();
		try {
			mClientSocket = new Socket(mHostAddress, 3307);
			ObjectOutputStream oos = new ObjectOutputStream(
					mClientSocket.getOutputStream());

			Log.d(TAG, "mRequestData.getWork : " + mRequestData.getRequestCode());
			oos.writeObject(mRequestData);
			oos.flush();
			Log.d(TAG, "writeObject(mRequestData) 후");

			// 응답 받기
			ObjectInputStream ois = new ObjectInputStream(
					mClientSocket.getInputStream());
			msg.obj = ois.readObject();
			// Interrupt 검사해서 Thread 종료 (화면 갱신을 막는다.)
			if (Thread.currentThread().isInterrupted() == false) {
				Log.d(TAG, "현재 Thread가 interrupted 되지 않았음... 화면 갱신 진행하겠음");
				mClientNetworkHandler.sendMessage(msg);
				Log.d(TAG, "Response 수신 완료");
			}

			mClientSocket.close();
			oos.close();
			ois.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

}
