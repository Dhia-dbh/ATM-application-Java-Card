package Front;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

import javax.swing.JPasswordField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadTransportException;



import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class BankUI implements ActionListener{

    private JFrame frame;
    private JPasswordField pinCode;
    private JLabel soldeValeurLabel;
    private JButton returnBtn,exitBtn,retryBtn;
    private JButton validerBtn;
    private JButton soldeViewBtn,creditBtn,debitBtn,transactionsBtn;
    private JButton credit10_btn,credit20_btn,credit30_btn,credit100_btn,credit50_btn,credit200_btn,credit300_btn,credit500_btn;
    private JButton debit10_btn,debit20_btn,debit30_btn,debit100_btn,debit50_btn,debit200_btn,debit300_btn,debit500_btn;
    private JLabel checkedLabel;
    private JLabel operationSucessLabel;
    private JLabel errorPNGLabel;
    private JLabel errorMessageLabel;

    private JPanel MenuPanel,PINPanel,balancePanel,crediterPanel,debiterPanel,Succes_panel,errorPanel,pinErrorPanel,blockCardPanel;
    private static final byte INS_GET_BALANCE = 0x01;
    private static final byte INS_INCREMENTER_BALANCE = 0x02;
    private static final byte INS_DECREMENTER_BALANCE = 0x03;
    private static final byte INS_INITIALISER_BALANCE = 0x04;
    private static final byte INS_VERIF_PIN = 0x00;
    private static final int MAX_ROUGE=1000;

    private static BankFunction client;
    
    public static void main(String[] args) throws IOException, CadTransportException {

    	client = new BankFunction();
		client.Connect();
		try {
			client.select();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CadTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	//Get Balance from file and Sync with Balance in Card	
    	client.updateBalance();
    	EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    BankUI window = new BankUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
  
    	});
    
    	
    
    
    }


    private BankUI() {

        frame = new JFrame();
        frame.setTitle("distributeur Bank");
        frame.setBounds(100, 100, 900, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        showPINPanel();
        //showMenuPanel();
        //AI logo maker
    }


    private void showPINPanel (){

        PINPanel=new JPanel();
        frame.setContentPane(PINPanel);
        PINPanel.setLayout(null);
        
        JLabel logoLabel = new JLabel("");
        logoLabel.setBounds(0, 10, 886, 150);
        logoLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/LogoBank.png")));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 39));
        PINPanel.add(logoLabel);

        JLabel codePinLabel = new JLabel("S'il vous plait entrez votre code PIN:");
        codePinLabel.setBounds(0, 200, 886, 40);
        codePinLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
        codePinLabel.setHorizontalAlignment(SwingConstants.CENTER);
        PINPanel.add(codePinLabel);

        pinCode = new JPasswordField(4);
        pinCode.setDocument(new PlainDocument(){
            @Override
             public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                if (str == null) {
                    return;
                }

                // Check if the string contains only numeric characters
                if (str.matches("\\d*")) {
                    // Check if the total length of the text won't exceed the maximum size
                    if ((getLength() + str.length()) <= 4) {
                        super.insertString(offset, str, attr);
                    }
                    if((getLength() + str.length()) == 5) {
                    	validerPIN();
                    }
                }
            }
        });

        pinCode.setFont(new Font("Tahoma", Font.PLAIN, 30));
        pinCode.setBounds(210, 270, 470, 60);
        pinCode.setHorizontalAlignment(SwingConstants.CENTER);
        pinCode.setMaximumSize(new Dimension(470, 60));
        pinCode.setMinimumSize(new Dimension(470, 60));
        PINPanel.add(pinCode);

        exitBtn = new JButton("Annuler");
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        exitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/log-out.png")));
        exitBtn.setBounds(133, 386, 180, 60);
        PINPanel.add(exitBtn);

        validerBtn = new JButton("Valider");
        validerBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        validerBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/forward.png")));
        validerBtn.setBounds(578, 386, 180, 60);
        validerBtn.requestFocus();
        
        PINPanel.add(validerBtn);
        
        frame.validate();
        frame.repaint();

        exitBtn.addActionListener(this);
        validerBtn.addActionListener(this);

    }


    private void showMenuPanel (){
        MenuPanel=new JPanel();
        frame.setContentPane(MenuPanel);
        MenuPanel.setLayout(null);

        JLabel logoLabel = new JLabel("");
        logoLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/LogoBank.png")));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBounds(0, 20, 886, 150);
        logoLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 39));
        MenuPanel.add(logoLabel);

        debitBtn = new JButton("Débiter");
        debitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        debitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/deposit.png")));
        debitBtn.setBounds(706, 200, 180, 70);
        MenuPanel.add(debitBtn);

        exitBtn = new JButton("Quitter");
        exitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/log-out.png")));
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        exitBtn.setBounds(706, 350, 180, 70);
        MenuPanel.add(exitBtn);

        soldeViewBtn = new JButton("Solde");
        soldeViewBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/money.png")));
        soldeViewBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        soldeViewBtn.setBounds(0, 200, 180, 70);
        MenuPanel.add(soldeViewBtn);

        creditBtn = new JButton("Créditer");
        creditBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/credit.png")));
        creditBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        creditBtn.setBounds(0, 350, 180, 70);
        MenuPanel.add(creditBtn);

        transactionsBtn = new JButton("Imprimer mes Transactions");
        transactionsBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/transaction.png")));
        transactionsBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        transactionsBtn.setBounds(240, 420, 410, 50);
        MenuPanel.add(transactionsBtn);

        
        
        JLabel mainLabel = new JLabel("Choisir une Transaction");
        mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        mainLabel.setBounds(278, 275, 330, 86);
        MenuPanel.add(mainLabel);

        JLabel visaLabel = new JLabel("");
        visaLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/visa.png")));
        visaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        visaLabel.setBounds(30, 440, 80, 150);
        visaLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 39));
        MenuPanel.add(visaLabel);

        JLabel masterCardLabel = new JLabel("");
        masterCardLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/MasterCard.png")));
        masterCardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        masterCardLabel.setBounds(100, 440, 80, 150);
        masterCardLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 39));
        MenuPanel.add(masterCardLabel);

        JLabel maestroLabel = new JLabel("");
        maestroLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/Maestro.png")));
        maestroLabel.setHorizontalAlignment(SwingConstants.CENTER);
        maestroLabel.setBounds(175, 440, 80, 150);
        maestroLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 39));
        MenuPanel.add(maestroLabel);


        JLabel copyright = new JLabel("Créer par Yassine EL KAMEL et Dhia BEN HAMOUDA");
        copyright.setFont(new Font("Tahoma", Font.PLAIN, 15));
        copyright.setHorizontalAlignment(SwingConstants.CENTER);
        copyright.setBounds(535, 480, 350, 150);
        MenuPanel.add(copyright);
        

        frame.validate();
        frame.repaint();

        transactionsBtn.addActionListener(this);
        debitBtn.addActionListener(this);
        exitBtn.addActionListener(this);
        soldeViewBtn.addActionListener(this);
        creditBtn.addActionListener(this);

    }


    private void showBalancePanel (int solde){
        balancePanel=new JPanel();
        frame.setContentPane(balancePanel);
        balancePanel.setLayout(null);


        JLabel logoLabel = new JLabel("");
        logoLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/LogoBank.png")));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBounds(0, 20, 886, 150);
        logoLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 39));
        balancePanel.add(logoLabel);

        JLabel soldeLabel = new JLabel("Votre solde: ");
        soldeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        soldeLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
        soldeLabel.setBounds(0, 159, 886, 96);
        balancePanel.add(soldeLabel);

        soldeValeurLabel = new JLabel();
        soldeValeurLabel.setHorizontalAlignment(SwingConstants.CENTER);
        soldeValeurLabel.setText(solde+ "TND");
        soldeValeurLabel.setFont(new Font("Tahoma", Font.ITALIC, 30));
        soldeValeurLabel.setBounds(129, 257, 606, 81);
        if (solde<0)
        	soldeValeurLabel.setForeground(Color.red);

        balancePanel.add(soldeValeurLabel);

        returnBtn = new JButton("Retourner");
        returnBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/return.png")));
        returnBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        returnBtn.setBounds(129, 408, 220, 70);
        balancePanel.add(returnBtn);

        exitBtn = new JButton("Quitter");
        exitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/log-out.png")));
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        exitBtn.setBounds(555, 408, 220, 70);
        balancePanel.add(exitBtn);

        frame.validate();
        frame.repaint();

        returnBtn.addActionListener(this);
        exitBtn.addActionListener(this);

    }

    private void showCrediterPanel (){
        crediterPanel=new JPanel();
        frame.setContentPane(crediterPanel);
        crediterPanel.setLayout(null);


        JLabel logoLabel = new JLabel("");
        logoLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/LogoBank.png")));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBounds(0, 20, 886, 150);
        logoLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 39));
        crediterPanel.add(logoLabel);

        JLabel crediterLabel = new JLabel("Créditer");
        crediterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        crediterLabel.setFont(new Font("Tahoma", Font.BOLD, 35));
        crediterLabel.setBounds(0, 140, 886, 123);
        crediterPanel.add(crediterLabel);

        credit10_btn = new JButton("10 TND");
        credit10_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        credit10_btn.setBounds(0, 60, 202, 70);
        crediterPanel.add(credit10_btn);

        credit20_btn = new JButton("20 TND");
        credit20_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        credit20_btn.setBounds(0, 160, 202, 70);
        crediterPanel.add(credit20_btn);

        credit30_btn = new JButton("30 TND");
        credit30_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        credit30_btn.setBounds(0, 260, 202, 70);
        crediterPanel.add(credit30_btn);

        credit50_btn = new JButton("50 TND");
        credit50_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        credit50_btn.setBounds(0, 360, 202, 70);
        crediterPanel.add(credit50_btn);

        credit100_btn = new JButton("100 TND");
        credit100_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        credit100_btn.setBounds(685, 60, 202, 70);
        crediterPanel.add(credit100_btn);

        credit200_btn = new JButton("200 TND");
        credit200_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        credit200_btn.setBounds(685, 160, 202, 70);
        crediterPanel.add(credit200_btn);

        credit300_btn = new JButton("300 TND");
        credit300_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        credit300_btn.setBounds(685, 260, 202, 70);
        crediterPanel.add(credit300_btn);

        credit500_btn = new JButton("500 TND");
        credit500_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        credit500_btn.setBounds(685, 360, 202, 70);
        crediterPanel.add(credit500_btn);

        returnBtn = new JButton("Retourner");
        returnBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/return.png")));
        returnBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        returnBtn.setBounds(0, 460, 202, 70);
        crediterPanel.add(returnBtn);

        exitBtn = new JButton("Quitter");
        exitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/log-out.png")));
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        exitBtn.setBounds(685, 460, 202, 70);
        crediterPanel.add(exitBtn);

        frame.validate();
        frame.repaint();
        
        credit10_btn.addActionListener(this);
        credit20_btn.addActionListener(this);
        credit30_btn.addActionListener(this);
        credit50_btn.addActionListener(this);
        credit100_btn.addActionListener(this);
        credit200_btn.addActionListener(this);
        credit300_btn.addActionListener(this);
        credit500_btn.addActionListener(this);
        returnBtn.addActionListener(this);
        exitBtn.addActionListener(this);

    }

    private void showDebiterPanel (){
        debiterPanel=new JPanel();
        frame.setContentPane(debiterPanel);
        debiterPanel.setLayout(null);

        JLabel logoLabel = new JLabel("");
        logoLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/LogoBank.png")));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBounds(0, 20, 886, 150);
        logoLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 39));
        debiterPanel.add(logoLabel);

        JLabel debiterLabel = new JLabel("Débiter");
        debiterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        debiterLabel.setFont(new Font("Tahoma", Font.BOLD, 35));
        debiterLabel.setBounds(0, 140, 886, 123);
        debiterPanel.add(debiterLabel);

        debit10_btn = new JButton("10 TND");
        debit10_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        debit10_btn.setBounds(0, 60, 202, 70);
        debiterPanel.add(debit10_btn);

        debit20_btn = new JButton("20 TND");
        debit20_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        debit20_btn.setBounds(0, 160, 202, 70);
        debiterPanel.add(debit20_btn);

        debit30_btn = new JButton("30 TND");
        debit30_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        debit30_btn.setBounds(0, 260, 202, 70);
        debiterPanel.add(debit30_btn);

        debit50_btn = new JButton("50 TND");
        debit50_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        debit50_btn.setBounds(0, 360, 202, 70);
        debiterPanel.add(debit50_btn);

        debit100_btn = new JButton("100 TND");
        debit100_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        debit100_btn.setBounds(685, 60, 202, 70);
        debiterPanel.add(debit100_btn);

        debit200_btn = new JButton("200 TND");
        debit200_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        debit200_btn.setBounds(685, 160, 202, 70);
        debiterPanel.add(debit200_btn);

        debit300_btn = new JButton("300 TND");
        debit300_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        debit300_btn.setBounds(685, 260, 202, 70);
        debiterPanel.add(debit300_btn);

        debit500_btn = new JButton("500 TND");
        debit500_btn.setFont(new Font("Tahoma", Font.PLAIN, 22));
        debit500_btn.setBounds(685, 360, 202, 70);
        debiterPanel.add(debit500_btn);

        returnBtn = new JButton("Retourner");
        returnBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/return.png")));
        returnBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        returnBtn.setBounds(0, 460, 202, 70);
        debiterPanel.add(returnBtn);

        exitBtn = new JButton("Quitter");
        exitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/log-out.png")));
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        exitBtn.setBounds(685, 460, 202, 70);
        debiterPanel.add(exitBtn);
        frame.validate();
        frame.repaint();

        debit10_btn.addActionListener(this);
        debit20_btn.addActionListener(this);
        debit30_btn.addActionListener(this);
        debit50_btn.addActionListener(this);
        debit100_btn.addActionListener(this);
        debit200_btn.addActionListener(this);
        debit300_btn.addActionListener(this);
        debit500_btn.addActionListener(this);
        returnBtn.addActionListener(this);
        exitBtn.addActionListener(this);

    }

    private void showSucessPanel (){
        Succes_panel=new JPanel();
        frame.setContentPane(Succes_panel);
        Succes_panel.setLayout(null);

        checkedLabel = new JLabel("");
        checkedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        checkedLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/checked.png")));
        checkedLabel.setBounds(0, 120, 886, 170);
        Succes_panel.add(checkedLabel);

        operationSucessLabel = new JLabel("Opération effectuée");
        operationSucessLabel.setFont(new Font("Tahoma", Font.BOLD, 34));
        operationSucessLabel.setHorizontalAlignment(SwingConstants.CENTER);
        operationSucessLabel.setBounds(0, 311, 886, 71);
        Succes_panel.add(operationSucessLabel);

        returnBtn = new JButton("Retourner");
        returnBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/return.png")));
        returnBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        returnBtn.setBounds(125, 431, 220, 70);
        Succes_panel.add(returnBtn);

        exitBtn = new JButton("Quitter");
        exitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/log-out.png")));
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        exitBtn.setBounds(576, 431, 220, 70);
        Succes_panel.add(exitBtn);
        frame.validate();
        frame.repaint();

        returnBtn.addActionListener(this);
        exitBtn.addActionListener(this);

    }
    private void showErrorPanel (String error,String img){
        errorPanel=new JPanel();
        frame.setContentPane(errorPanel);
        errorPanel.setLayout(null);

        errorPNGLabel = new JLabel("");
        errorPNGLabel.setIcon(new ImageIcon(BankUI.class.getResource(img)));
        errorPNGLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorPNGLabel.setBounds(0, 120, 886, 170);
        errorPanel.add(errorPNGLabel);

        errorMessageLabel = new JLabel(error);
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorMessageLabel.setFont(new Font("Tahoma", Font.BOLD, 34));
        errorMessageLabel.setBounds(0, 311, 886, 71);
        errorPanel.add(errorMessageLabel);

        returnBtn = new JButton("Retourner");
        returnBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/return.png")));
        returnBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        returnBtn.setBounds(125, 431, 220, 70);
        errorPanel.add(returnBtn);

        exitBtn = new JButton("Quitter");
        exitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/log-out.png")));
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        exitBtn.setBounds(576, 431, 220, 70);
        errorPanel.add(exitBtn);

        frame.validate();
        frame.repaint();

        returnBtn.addActionListener(this);
        exitBtn.addActionListener(this);
    }
    
    void showBlockCardPanel (){
        blockCardPanel=new JPanel();
        frame.setContentPane(blockCardPanel);
        blockCardPanel.setLayout(null);

        JLabel blockedCardPNGLabel = new JLabel("");
        blockedCardPNGLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/blockedCard.png")));
        blockedCardPNGLabel.setHorizontalAlignment(SwingConstants.CENTER);
        blockedCardPNGLabel.setBounds(0, 75, 886, 215);
        blockCardPanel.add(blockedCardPNGLabel);

        JLabel blockCardLabel = new JLabel("Vous avez dépassé la limite de tentatives.");
        blockCardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        blockCardLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        blockCardLabel.setBounds(0, 280, 886, 86);
        blockCardPanel.add(blockCardLabel);

        JLabel blockCardLabel_2 = new JLabel("Votre carte est bloquée !");
        blockCardLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        blockCardLabel_2.setFont(new Font("Tahoma", Font.BOLD, 28));
        blockCardLabel_2.setBounds(0, 327, 886, 86);
        blockCardPanel.add(blockCardLabel_2);

        exitBtn = new JButton("Quitter");
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        exitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/log-out.png")));
        exitBtn.setBounds(358, 450, 220, 70);

        frame.validate();
        frame.repaint();

        blockCardPanel.add(exitBtn);
        exitBtn.addActionListener(this);

    }
    
    void showPinErrorPanel (){
        pinErrorPanel=new JPanel();
        frame.setContentPane(pinErrorPanel);
        pinErrorPanel.setLayout(null);

        JLabel errorPNGLabel = new JLabel("");
        errorPNGLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorPNGLabel.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/error.png")));
        errorPNGLabel.setBounds(0, 71, 886, 166);
        pinErrorPanel.add(errorPNGLabel);

        JLabel wrongPinLabel = new JLabel("Erreur: Code Pin est incorrect");
        wrongPinLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wrongPinLabel.setFont(new Font("Tahoma", Font.BOLD, 26));
        wrongPinLabel.setBounds(0, 210, 886, 181);
        pinErrorPanel.add(wrongPinLabel);

        exitBtn = new JButton("Quitter");
        exitBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        exitBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/log-out.png")));
        exitBtn.setBounds(135, 386, 220, 70);
        pinErrorPanel.add(exitBtn);

        retryBtn = new JButton("Réessayer");
        retryBtn.setFont(new Font("Tahoma", Font.BOLD, 22));
        retryBtn.setIcon(new ImageIcon(BankUI.class.getResource("/Icons/reload.png")));
        retryBtn.setBounds(548, 386, 220, 70);
        pinErrorPanel.add(retryBtn);

        frame.validate();
        frame.repaint();

        retryBtn.addActionListener(this);
        exitBtn.addActionListener(this);

    }
    
    
	private void validerPIN() {
		String code= pinCode.getText();
        int a=0;
        try {
            a= Integer.parseInt(code);
        }catch(NumberFormatException Nfe) {}
        int a1=a/1000;
        int a2=(a/100)%10;
        int a3=(a/10)%10;
        int a4=a%10;
        byte[] pin_ok= {(byte) a1, (byte) a2, (byte) a3, (byte) a4};
        
        
        Apdu apdu = null;
        try {
            apdu = client.Msg(INS_VERIF_PIN, (byte) 0x04, pin_ok, (byte) 0x7f);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            showErrorPanel("Erreur Systeme !","/Icons/error.png");                
        	e1.printStackTrace();
        } 
       
        catch (CadTransportException e1) {
            // TODO Auto-generated catch block
            showErrorPanel("Erreur Systeme !","/Icons/error.png");
            e1.printStackTrace();
        }
       
        if (apdu.getStatus() == 0x6300) {
            showPinErrorPanel();
        } else if(apdu.getStatus()== 0x6321) {
            showBlockCardPanel();
        }
        else
        {
            showMenuPanel();
        }
    
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==validerBtn) {
        	validerPIN();
        }
        
        if(e.getSource()==soldeViewBtn) {
            Apdu apdu = null;
            
            try {
                apdu = client.Msg(INS_GET_BALANCE, (byte) 0x00, null, (byte) 0x7f);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (CadTransportException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (apdu.getStatus() != 0x9000) {
                showErrorPanel("Erreur SYSTEME","/Icons/error.png");
                //System.out.println("Erreur : status word different de 0x9000");
            } else {

                BigInteger one;
                one= new BigInteger(apdu.dataOut);
                
                showBalancePanel(one.intValue()-MAX_ROUGE);
            }

        }

        if(e.getSource()==exitBtn) {
            client.Deselect();
            System.exit(0);
        }

        if (e.getSource()==retryBtn) {
            showPINPanel();
        }

        if(e.getSource()==returnBtn) {
            showMenuPanel();
        }

        if(e.getSource()==creditBtn) {
            showCrediterPanel();
        }


        if(e.getSource()==debitBtn) {
            showDebiterPanel();
        }

        if (e.getSource()==transactionsBtn) {
        	try {
				client.getBalance();
			} catch (IOException | CadTransportException e1) {
				// TODO Auto-generated catch block
			}
        	BankFunction.addToTransactionFile("");
        	BankFunction.addToTransactionFile("***********************************************************");
        	BankFunction.addToTransactionFile("");
        	BankFunction.addToTransactionFile("                    Balance :"+(BankFunction.balance-1000)+"DT");
        	BankFunction.addToTransactionFile("");
        	BankFunction.addToTransactionFile("***********************************************************");
        	
        	printTextFile("Transaction.txt");
        
        }


        if(e.getSource()==credit10_btn) {
            this.crediter_action(10);
        }
        if(e.getSource()==credit20_btn) {
            this.crediter_action(20);
        }
        if(e.getSource()==credit30_btn) {
            this.crediter_action(30);
        }
        if(e.getSource()==credit50_btn) {
            this.crediter_action(50);
        }
        if(e.getSource()==credit100_btn) {
            this.crediter_action(100);
        }
        if(e.getSource()==credit200_btn) {
            this.crediter_action(200);
        }
        if(e.getSource()==credit300_btn) {
            this.crediter_action(300);
        }
        if(e.getSource()==credit500_btn) {
            this.crediter_action(500);
        }

        if(e.getSource()==debit10_btn) {
            this.debiter_action(10);
        }

        if(e.getSource()==debit20_btn) {
            this.debiter_action(20);
        }

        if(e.getSource()==debit30_btn) {
            this.debiter_action(30);
        }

        if(e.getSource()==debit50_btn) {
            this.debiter_action(50);
        }

        if(e.getSource()==debit100_btn) {
            this.debiter_action(100);
        }

        if(e.getSource()==debit200_btn) {
            this.debiter_action(200);
        }

        if(e.getSource()==debit300_btn) {
            this.debiter_action(300);
        }

        if(e.getSource()==debit500_btn) {
            this.debiter_action(500);
        }


    }

    private void crediter_action(int a) {
    	byte[] montant = new byte[2];
    	if(a < 127) {
	        montant[0] = 0;
	        montant[1] = (byte)a;
    	}
    	else {
    		montant[0] = (byte) (a >> 8 & 0xFF);
    		montant[1] = (byte) (a & 0xFF);
    	}
    	Apdu apdu = null;
        
        try {
            apdu = client.Msg(INS_INCREMENTER_BALANCE, (byte) 0x02, montant, (byte) 0x7f);
            //System.out.println(apdu);
        } catch (IOException e1) {
        	showErrorPanel("Erreur: Erreur Système","/Icons/error.png");
            e1.printStackTrace();
        } catch (CadTransportException e1) {
        	showErrorPanel("Erreur: Lecture de Carte impossible","/Icons/error.png");
            e1.printStackTrace();
        }
        if (apdu.getStatus() != 0x9000) {
            showErrorPanel("Erreur: Vous Avez dépasser le montant spécifié","/Icons/error.png");
        } else {
            showSucessPanel();
            BankFunction.addToTransactionFile("   ATM Credit in "+Calendar.getInstance().getTime()+" Amount "+a);
        
        }
       
    }

    private void debiter_action(int a) {
    	byte[] montant = new byte[2];
    	if(a < 127) {	        
	        montant[0] = 0;
	        montant[1] = (byte)a;
    	}
    	else {
    		montant[0] = (byte) (a >> 8 & 0xFF);
    		montant[1] = (byte) (a & 0xFF);
    	}
        Apdu apdu = null;
        try {
            apdu = client.Msg(INS_DECREMENTER_BALANCE, (byte) 0x02, montant, (byte) 0x7f);
            //System.out.println(apdu);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CadTransportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (apdu.getStatus() == 0x6A85) {
            showErrorPanel("Erreur: Solde Insuffisant","/Icons/error.png");
        } 
        else if (apdu.getStatus() == 0x6320) {
            showErrorPanel("Attention votre compte est dans le rouge","/Icons/red-flag.png");
            BankFunction.addToTransactionFile("   ATM Debit  in "+Calendar.getInstance().getTime()+" Amount "+a);
        } 
        
        else {
        	BankFunction.addToTransactionFile("   ATM Debit  in "+Calendar.getInstance().getTime()+" Amount "+a);
        	showSucessPanel();
        }
    }
    
    public static void printTextFile(String filePath) {
    	 try {
             File file = new File(filePath);
             Desktop.getDesktop().print(file);
         } catch (IOException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
         }
    }
}
    



