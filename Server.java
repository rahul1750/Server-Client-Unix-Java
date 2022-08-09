import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	static String strFromClient = "";
	static String strToClient = "";
	static BufferedReader br = null;
	static DataOutputStream dout = null;

	public static void main(String[] args) {
		DataInputStream din = null;
		ServerSocket serverSocket = null;

		String exception = "";
		while (true) {
			try {
				/*
				 * Creates a server socket, bound to the specified port.
				 */
				serverSocket = new ServerSocket(6666);
				System.out.println("Server is Waiting for New Client... ");

				/*
				 * Listens for a connection to be made to this socket and accepts it. The method
				 * blocks until a connection is made.
				 */
				Socket socket = serverSocket.accept();
				System.out.println("Client Successfully Connected... ");
				din = new DataInputStream(socket.getInputStream());

				OutputStream outputStream = socket.getOutputStream();
				dout = new DataOutputStream(outputStream);

				//br = new BufferedReader(new InputStreamReader(System.in));
				strFromClient = "";
				strToClient = "";
				while (!strFromClient.equalsIgnoreCase("quit")) {
					strFromClient = din.readUTF();
					System.out.println("Message from Client: " + strFromClient);
					Runnable r = new Runnable() {

						@Override
						public void run() {
							try {
								Runtime run = Runtime.getRuntime();
								Process pr = run.exec(strFromClient);
								pr.waitFor();
								BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
								String line = "";
								strToClient = "";
								while ((line = buf.readLine()) != null) {
									strToClient += line + "\n";
								}
								
								if(strToClient.equals(""))
									strToClient = "Command executed";
								dout.writeUTF(strToClient);
								dout.flush();
							} catch (Exception e) {
//								System.out.println(e);
							}
						}
					};

					r.run();

				}
				dout.writeUTF("Client Successfully Disconnected");
				dout.flush();
			} catch (Exception exe) {
				exe.printStackTrace();
			} finally {
				try {
					if (br != null) {
						br.close();
					}

					if (din != null) {
						din.close();
					}

					if (dout != null) {
						dout.close();
					}
					if (serverSocket != null) {
						/*
						 * closes the server socket.
						 */
						serverSocket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}