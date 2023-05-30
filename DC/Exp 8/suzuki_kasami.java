/*
/ * @author: Manish Singh
 * 
 */


package tokenExchange;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class suzuki_kasami {

	public static void exitCS(site localsite, int thissiteNumber, int number_of_nodes, String[] ip_addr, int[] port,
			int no_of_sites) {

		localsite.LN[thissiteNumber - 1] = localsite.RN[thissiteNumber - 1];

		// Send updated LN value to all sites
		String message = "ln," + thissiteNumber + "," + localsite.LN[thissiteNumber - 1];

		for (int i = 0; i < no_of_sites; i++) {
			
			if(i==thissiteNumber-1) {
				continue;
			}

			try {
				Socket skt = new Socket(ip_addr[i], port[i]);

				// System.out.println(skt.getPort());

				OutputStream os = skt.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				bw.write(message);
				bw.flush();
				skt.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < number_of_nodes; i++) {
			if (localsite.RN[i] == localsite.LN[i] + 1) {
				if (!localsite.token_queue.contains(i + 1)) {
					localsite.token_queue.add(i + 1);
				}
			}
		}

		if (localsite.token_queue.size() > 0) {
			localsite.sendToken(localsite.token_queue.poll());
		}

	}

	public static void main(String[] args) {

	

		// Read config of nodes

		BufferedReader br = null;
		File fFile = new File("");
		String cwd = fFile.getAbsolutePath();

		File nodes = new File(cwd + "\\nodes.config");
		try {
			br = new BufferedReader(new FileReader(nodes));
			int number_of_nodes = 0;
			String node_addr = br.readLine();
			ArrayList<String> node_table = new ArrayList<String>();

			while (node_addr != null) {
				node_table.add(node_addr);
				number_of_nodes++;
				node_addr = br.readLine();

			}

			//System.out.println(number_of_nodes);

			int[] siteNumber = new int[number_of_nodes];
			String[] ip_addr = new String[number_of_nodes];
			int[] port = new int[number_of_nodes];

			String[] tmpAddress = null;

			for (int counter = 0; counter < number_of_nodes; counter++) {

				tmpAddress = node_table.get(counter).split(" ");
				siteNumber[counter] = Integer.parseInt(tmpAddress[0]);
				ip_addr[counter] = tmpAddress[1];
				port[counter] = Integer.parseInt(tmpAddress[2]);

			}
			
			

			// preparing this site
			
			Scanner scan = new Scanner(System.in);

			int thissiteNumber = 0;

			int inFlag = 0;

			do {

				System.out.print("Enter site number (1-" + number_of_nodes + "): ");
				thissiteNumber = Integer.parseInt(scan.nextLine());

				if (thissiteNumber >= 1 && thissiteNumber <= number_of_nodes) {
					inFlag = 1;
				} else {
					System.out.println("Please enter the correct site number i.e. from 1 to  " + number_of_nodes);
				}

			} while (inFlag == 0);
			
			int hasToken = 0;

			if (thissiteNumber == 1) {
				hasToken = 1;
			}

			site localsite = new site(number_of_nodes, thissiteNumber, hasToken, ip_addr, port);

			// Open a socket

			listenToBroadcast listenBcst = new listenToBroadcast(localsite, port[thissiteNumber - 1]);

			listenBcst.start();

			String input_query = "";

			while (!input_query.equalsIgnoreCase("quit")) {
				System.out.println("Press ENTER to enter CS: ");
				Scanner scan_query = new Scanner(System.in);
				input_query = scan_query.nextLine();
				// System.out.println("Query from keyboard:" + input_query);
				if (localsite.token == 1) {

					localsite.processingCS = 1;
					System.out.println("Site has token. Executing CS.");

					Thread.sleep(15000);

					localsite.processingCS = 0;

					System.out.println("Exiting CS.");

					exitCS(localsite, thissiteNumber, number_of_nodes, ip_addr, port, number_of_nodes);

				} else {

					System.out.println("Requesting token");

					localsite.reqCS();
					System.out.println("Waiting for token..");
					
					localsite.processingCS = 1;

					while (localsite.token == 0) {
						Thread.sleep(100);
						
					}

					
					System.out.println("Site has received token. Executing CS.");

					Thread.sleep(15000);

					localsite.processingCS = 0;
					System.out.println("Exiting CS.");

					exitCS(localsite, thissiteNumber, number_of_nodes, ip_addr, port, number_of_nodes);

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

class site {

	String[] ip_addr = null;
	int[] port = null;

	int number_of_sites = 0;
	int site_number = 0;
	int token = 0;
	int seq_number = 0;
	int processingCS = 0;
	Queue<Integer> token_queue = new LinkedList<>();
	int RN[];
	int LN[];

	site(int numberofsites, int siteNumber, int hasToken, String[] ipAddr, int[] portno) {
		this.number_of_sites = numberofsites;
		this.site_number = siteNumber;
		this.token = hasToken;

		this.ip_addr = ipAddr;
		this.port = portno;

		RN = new int[number_of_sites];
		LN = new int[number_of_sites];
		for (int i = 0; i < numberofsites; i++) {
			RN[i] = 0;
			LN[i] = 0;
		}

	}

	void print() {
		System.out.println(number_of_sites + " " + site_number + " " + token);
	}
	
	void updateLN(int thissiteNumber, int value) {
		LN[thissiteNumber-1]=value;
	}

	void reqCS() {
		RN[site_number - 1]++;

		String message = "request," + site_number + "," + RN[site_number - 1];

		System.out.println("Broadcasting request to " + (number_of_sites - 1) + " sites.");

		for (int i = 0; i < number_of_sites; i++) {

			if (i != site_number - 1) {
				Socket skt = null;

				try {
					// InetAddress address= InetAddress.getByName(ip_addr[i]);
					skt = new Socket(ip_addr[i], port[i]);

					System.out.println(skt.getPort());

					OutputStream os = skt.getOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osw);
					bw.write(message);
					bw.flush();
					// InputStream is = skt.getInputStream();
					// InputStreamReader isr = new InputStreamReader(is);
					// BufferedReader br = new BufferedReader(isr);
					// String message1 = br.readLine();
					// System.out.println(message1);
					os.close();
					osw.close();
					bw.close();

				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {

						skt.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}

	}

	void processCSReq(int site, int sn) {
		if (RN[site - 1] < sn) {
			RN[site - 1] = sn;
		}

		if (processingCS == 0 && token == 1) {
			sendToken(site);

		} else {
			token_queue.add(site);
		}

	}

	void sendToken(int site) {

		if (this.token == 1) {
			if (RN[site - 1] == LN[site - 1] + 1) {
				System.out.println("Sending token to site " + site);

				try {
					Socket skt = new Socket(ip_addr[site - 1], port[site - 1]);
					String message = "token";
					int tokenQueuelen=token_queue.size();
					for(int i=0;i<tokenQueuelen;i++) {
						message+=","+token_queue.poll();
					}
					
					OutputStream os = skt.getOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osw);
					bw.write(message);
					bw.flush();
					skt.close();
					this.token = 0;
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} // -end sending
		}

	}
}

class listenToBroadcast extends Thread {

	int port = 0;
	site localSite = null;

	public listenToBroadcast(site thisSite, int port) {
		this.port = port;
		this.localSite = thisSite;
	}

	public void run() {

		try {
			ServerSocket serverSckt = new ServerSocket(port);
			while (true) {
				Socket skt = serverSckt.accept();
				new processRq(skt, localSite).start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

class processRq extends Thread {

	Socket lSocket = null;
	site localSite = null;

	public processRq(Socket lSocket, site localSitePass) {
		this.lSocket = lSocket;
		this.localSite = localSitePass;
	}

	public void run() {

		BufferedReader in = null;
		PrintWriter out = null;

		// System.out.println(lSocket.getInetAddress().getHostAddress());

		try {
			in = new BufferedReader(new InputStreamReader(lSocket.getInputStream()));
			// out = new PrintWriter(
			// new OutputStreamWriter(lSocket.getOutputStream()));

			// out.println("Welcome");
			// out.flush();

			String command = "";
			String[] message = null;

			command = in.readLine();
			System.out.println(command);
			if (null != command) {
				if (command.charAt(0) == 'r') {

					message = command.split(",");
					localSite.processCSReq(Integer.parseInt(message[1]), Integer.parseInt(message[2]));

					// synchronized (localSite) {
					//
					// localSite.sendToken(Integer.parseInt(message[1]));
					// }

				}

				if (command.charAt(0) == 't') {

					message = command.split(",");
					localSite.token_queue.clear();
					int length=message.length;
					for(int i=1;i<length;i++) {
						localSite.token_queue.add(Integer.parseInt(message[i]));
					}
					localSite.token = 1;

				}
				
				if (command.charAt(0) == 'l') {

					message = command.split(",");
					localSite.updateLN(Integer.parseInt(message[1]), Integer.parseInt(message[2]));

				}
			}

			// while (!command.equalsIgnoreCase("quit")) {
			// command = in.readLine();
			// System.out.println(command);
			// if (null != command) {
			// if (command.charAt(0) == 'r') {
			// message = command.split(",");
			// // localSite.processCSReq(Integer.parseInt(message[1]),
			// // Integer.parseInt(message[2]));
			//
			// }
			// }else {
			// command="";
			// }
			// }
			//
			// out.println("Bye");
			// out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				in.close();
				// out.close();
				lSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
