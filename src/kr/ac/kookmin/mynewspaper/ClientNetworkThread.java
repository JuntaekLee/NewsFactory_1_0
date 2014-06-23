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
 * ������ : Ŭ���̾�Ʈ�� �������� Request�� ������ Response�� �޴� ������
 **/
public class ClientNetworkThread extends Thread {
	private static final String TAG = "ClientNetworkThread";
	// Host Address
	private String mHostAddress;
	// Client Socket
	private Socket mClientSocket;
	// ���ϴ� �۾�
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
			Log.d(TAG, "writeObject(mRequestData) ��");

			// ���� �ޱ�
			ObjectInputStream ois = new ObjectInputStream(
					mClientSocket.getInputStream());
			msg.obj = ois.readObject();
			// Interrupt �˻��ؼ� Thread ���� (ȭ�� ������ ���´�.)
			if (Thread.currentThread().isInterrupted() == false) {
				Log.d(TAG, "���� Thread�� interrupted ���� �ʾ���... ȭ�� ���� �����ϰ���");
				mClientNetworkHandler.sendMessage(msg);
				Log.d(TAG, "Response ���� �Ϸ�");
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
