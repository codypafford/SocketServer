import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Client extends Thread{
    //IO and attributes for each thread
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;
    int option;

    //Time attributes
    static ArrayList<Long> timeArray = new ArrayList<Long>();
    long endTime;
    long startTime;

    //Client Thread Constuctor
    Client(String hostName, int portNumber, int option){
        try{
            this.clientSocket = new Socket(hostName, portNumber);
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.option = option;
        }catch (UnknownHostException e){
            System.err.println("Dont know about host " + hostName);
            System.exit(1);
        }catch (IOException e){
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    //The code that the threads execute when started
    public void run(){
        String response;
        this.startTime = System.currentTimeMillis();
        System.out.println(this.option + " is the option before being sent");
        out.println(this.option); //numbered option sent to server
        //Receive the server's response
        try{
            while ((response = in.readLine()) != null){
                //Uncomment the below print statement to view the data on your screen
                System.out.println(response); //reading from socket
                response = null; //get rid of any data left over in 'response'
            }
            this.in.close();
            this.out.close();
            this.clientSocket.close(); // close the socket so that the server can accept the next group of clients
        }catch (IOException e){
            System.err.println("I/O error with the connection");
            System.exit(1);
        }
        //calculate the time it took and add it to the timeArray list
        this.endTime = (System.currentTimeMillis() - startTime);
        timeArray.add(this.endTime);
    }

    public static void main(String[] args) throws IOException{
        String hostName;
        int portNumber;

        int menuOp;
        int clients = 0;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the hostname/IP address in which you would like to connect: ");
        hostName = scanner.nextLine();

        while (true) {
            System.out.println("Please enter the port number in which you would like to connect: ");
            String input = scanner.next();
            try {
                portNumber = Integer.parseInt(input);
                break; // break from the while loopmmand f
            } catch (NumberFormatException ne) {
                System.out.println("Input is not a valid number");
            }
        }

        //array for the client threads
        ArrayList<Client> clientThreads = new ArrayList<>();

        do{
            //menu
            System.out.println("Enter one of the following commands:");
            System.out.println("1. Host Current Date/Time");
            System.out.println("2. Host Uptime");
            System.out.println("3. Host Memory Use");
            System.out.println("4. Host Netstat");
            System.out.println("5. Host Current Users");
            System.out.println("6. Host Running Processes");
            System.out.println("7. Exit Program");

            menuOp = scanner.next().charAt(0);
            while (menuOp < 49 || menuOp > 55){  // ASCII Values
                System.out.println("Please enter a number between 1 - 7");
                menuOp = scanner.next().charAt(0);
            }

            menuOp = Character.getNumericValue(menuOp); // convert ASCII to integer


            while (true) {
                if (menuOp == 7){
                    break;
                }
                System.out.println("Please enter the number of clients: ");
                String input = scanner.next(); // returns the next token
                try {
                    clients = Integer.parseInt(input);
                    break;
                } catch (NumberFormatException ne) {
                    System.out.println("Input is not a valid number");
                }
            }

            System.out.println("menu op selected is " + menuOp);


            System.out.println("menuOp " + menuOp);
            System.out.println("Clients " + clients);

            //create the threads
            for (int i = 0; i < clients; i++){
                clientThreads.add(i, new Client(hostName, portNumber, menuOp));
            }

            //start the threads
            for (int i = 0;i < clients;i++){
                Client client = clientThreads.get(i);
                client.start();
            }

            //join after all threads have started so that the program waits for all of them to finish before returning to main
            for (int i = 0; i < clients; i++){
                try{
                    Client client = clientThreads.get(i);
                    // makes it so that each thread waits on the others to finish before we move on
                    client.join();
                }catch (InterruptedException ie){
                    System.out.println(ie.getMessage());
                }
            }
            //calculate the average server response time
            long sumOftimeArray = 0;
            for(long x: timeArray){
                sumOftimeArray += x;
            }
            double avgTime = sumOftimeArray / (double)clients;
            timeArray.clear();
            System.out.println("Average time of response = " + avgTime + "ms\n");
        }while (menuOp != 7);
        System.exit(1);
    }//end main
}

