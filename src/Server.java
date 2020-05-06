import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) throws IOException{
        int port = 6868;
        try{
            ServerSocket server = new ServerSocket(port, 100);
            System.out.println("Server is now listening on port " + port);
            while (true){
                new ServerThread(server.accept());  // this is the client being accepted, must be multi-threaded
            }
        } catch(IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port);
            System.out.println(e.getMessage());
        }
    }
}

class ServerThread implements Runnable{

    //IO and attributes for  each thread
    Socket client;
    PrintWriter output;
    Scanner input;

    public ServerThread(Socket client){
        try {
            this.client = client;
            this.output = new PrintWriter(client.getOutputStream(), true);
            this.input = new Scanner(client.getInputStream());
            (new Thread(this)).start(); // run the threads
        } catch (IOException e) {
            System.out.println("Exception caught when trying to set up IO.");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try{
            System.out.println("New Client Connected");
            String command = this.input.nextLine();  // read the message/request sent to the server from client
            int menuOp = Integer.parseInt(command); // this is the option to determine which Linux command to run
            System.out.println("menu op is " + menuOp);

            if(menuOp == 1){
                Date d1 = new Date();
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
                String formattedDate = df.format(d1);
                this.output.println(formattedDate);
            } else if(menuOp == 2){
                RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
                long uptime = rb.getUptime();
                long minutes = (uptime/1000) /60;
                long seconds = (uptime/1000) %60;
                String formatedTime = minutes + " Minutes" + " and " + seconds + " Seconds";
                this.output.println("The Uptime is: " + formatedTime);
            } else if (menuOp == 3){
                Process userProcess = Runtime.getRuntime().exec("vmstat");
                BufferedReader usersReader = new BufferedReader(new InputStreamReader(userProcess.getInputStream()));
                String user;
                while ((user = usersReader.readLine()) != null){
                    this.output.println(user);
                }
            } else if (menuOp == 4){
                Process userProcess = Runtime.getRuntime().exec("netstat");
                BufferedReader usersReader = new BufferedReader(new InputStreamReader(userProcess.getInputStream()));
                String user;
                while ((user = usersReader.readLine()) != null){
                    this.output.println(user);
                }
            } else if (menuOp == 5){
                Process userProcess = Runtime.getRuntime().exec("who");
                BufferedReader usersReader = new BufferedReader(new InputStreamReader(userProcess.getInputStream()));
                String user;
                while ((user = usersReader.readLine()) != null) {
                    this.output.println(user);
                }
            } else if (menuOp == 6){
                Process process = Runtime.getRuntime().exec("ps -e -o command");
                BufferedReader usersReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String user;
                while ((user = usersReader.readLine()) != null) {
                    this.output.println(user);
                }
            }
            this.input.close();
            this.output.close();
            this.client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}