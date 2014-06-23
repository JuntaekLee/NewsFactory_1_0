package kr.ac.kookmin.appengine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class NetworkThread extends Thread {

	private Handler netHandler;
	private String text;
	private String name;

	public NetworkThread(Handler netHandler, String text, String name) {
		super();
		this.netHandler = netHandler;
		this.text = text;
		this.name = name;
	}

	@Override
	public void run() {
		RestClient client = new RestClient(
				"http://iconic-iridium-619.appspot.com/test");
		Message msg = Message.obtain();
		Bundle bundle = new Bundle();
		String response;
		if (text == null || text == "") {
			client.AddParam("default", "null string");
			try {
				client.Execute(RequestMethod.GET);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response = client.getResponse();
			bundle.putString("board", response);
			msg.setData(bundle);
			netHandler.sendMessage(msg);
		} else {

			client.AddParam("board", name + " : " + text);
			try {
				client.Execute(RequestMethod.GET);
			} catch (Exception e) {
				e.printStackTrace();
			}

			response = client.getResponse();

			bundle.putString("board", response);
			msg.setData(bundle);
			netHandler.sendMessage(msg);
		}
	}

}
