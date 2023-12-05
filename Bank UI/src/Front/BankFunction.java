package Front;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;


import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;
import com.sun.javacard.apduio.CadTransportException;

public class BankFunction {
	
	private final static byte INS_GET_BALANCE = (byte) 0x01;
	private final static byte INS_INITIALISER_COMPTE = (byte) 0x04;
    static Apdu apdu ;
    static CadT1Client cad;
    public static int balance;
    private static String FILE_PATH = "Balance.txt";
    private static boolean notCreated=true;

    public BankFunction() {
    	try {
    	this.balance = readFromFile(FILE_PATH);
    	}
    catch(IOException | NumberFormatException e) {
    		createFile ("Balance.txt","1000");
    		this.balance=1000;
    }
    	
    	
    }
    public Apdu Msg(byte ins, byte lc, byte[] data,byte le) throws IOException, CadTransportException{
        apdu = new Apdu();
        apdu.command[Apdu.CLA] = (byte) 0xB0;
        apdu.command[Apdu.P1] = 0x00;
        apdu.command[Apdu.P2] = 0x00;
        apdu.command[Apdu.INS] = ins;
        //apdu.setLe(0x7f);
        apdu.setLe(le);
        if (data!=null)
            apdu.setDataIn(data);
        cad.exchangeApdu(apdu);
        return apdu;
    }

    
    void updateBalance() throws IOException, CadTransportException {
     	Apdu apdu = null;
     	// transition_buffer Size defined in Bank.java (JavaCard Applet)
    	byte size = 127;
    	// Data Array transfered in APDU message
    	byte[] data = new byte[size];
    	// Formule: (a + b + ... + z)*127+rest
    	int a_z;
    	byte rest;
    	rest = (byte) ( balance % 127);
    	a_z = (balance) / 127;
    	for(byte i= 0; i<size - 1 ; i++) {
    		if(a_z > 127) {
    			data[i] = 127;
    			a_z -= 127;
        	}
        	else {
        		data[i] = (byte) a_z;
        		a_z = 0;
        	}
    		
    	}
    	data[size-1] = rest;

    	apdu = Msg(INS_INITIALISER_COMPTE, (byte) size, data, (byte) 0x7F);
    }
    
    public void Connect(){
        Socket sckCarte;

        try {
            sckCarte = new Socket("localhost", 9025);
            sckCarte.setTcpNoDelay(true);
            BufferedInputStream input = new BufferedInputStream(sckCarte.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(sckCarte.getOutputStream());
            cad = new CadT1Client(input, output);
        } catch (Exception e) {
            System.out.println("Erreur : impossible de se connecter a la Javacard");
            return;
        }
        /* Mise sous tension de la carte */
        try {
            cad.powerUp();
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi de la commande Powerup a la Javacard");
            return;
        }
    }

    public void select() throws IOException, CadTransportException{

        /* Sélection de l'applet :création du commande SELECT APDU */
        apdu = new Apdu();
        apdu.command[Apdu.CLA] = (byte) 0x00;
        apdu.command[Apdu.INS] = (byte) 0xA4;
        apdu.command[Apdu.P1] = 0x04;
        apdu.command[Apdu.P2] = 0x00;
        byte[] appletAID = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00 };
        apdu.setDataIn(appletAID);
        cad.exchangeApdu(apdu);
        if (apdu.getStatus() != 0x9000) {
            System.out.println("Erreur lors de la sélection de l'applet");
            System.exit(1);
        }

    }

    public void Deselect(){
        /* Mise hors tension de la carte */
        try {
        	getBalance();
        	int balance_what = this.balance;
        	writeToFile(FILE_PATH, balance);
            cad.powerDown();
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi de la commande Powerdown a la Javacard");
            return;
        }
    }
    // Method to write a number to the file
    private static void writeToFile(String filePath, int number) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the number to the file
            writer.write(Integer.toString(number));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void writeToFile(String filePath, String data,boolean b) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,b))) {
            // Write the number to the file
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }    
    
    

    public static void createTransactionFile (String filePath) {
    	createFile(filePath,"");
    	writeToFile(filePath,"***********************************************************",false);
    	
    	writeToFile(filePath,"                    Transaction Ticket",true);
    	writeToFile(filePath,"",true);
    	
    	
    }
    
    public static void addToTransactionFile (String data) {
    	if (notCreated) {
    		createTransactionFile("Transaction.txt");
    		notCreated=false;
    	}
    	writeToFile("Transaction.txt",data,true);
    	
    }
    // Method to read a number from the file
    private static int readFromFile(String filePath)throws IOException,NumberFormatException {
        int number = 0;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
            // Read the first line from the file
            String line = reader.readLine();
            
            // Convert the content to an integer
            number = Integer.parseInt(line);
        
        return number;
    }
    
    private static void createFile (String filePath,String data) {

        // Create a File object
        File file = new File(filePath);

        try {
            // Create the file
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(data);
            bufferedWriter.close();
            writer.close();
        } catch (IOException e) {
            System.err.println("Error creating the file: " + e.getMessage());
        }
    }
    
    
    
    public void getBalance() throws IOException, CadTransportException {
    	Apdu apdu=null;
		apdu = Msg(INS_GET_BALANCE, (byte) 0x00, null, (byte) 0x7f);
		BigInteger one= new BigInteger(apdu.dataOut);
        balance = one.intValue();
		System.out.println();
		System.out.println(apdu);
		
    }
    

}
