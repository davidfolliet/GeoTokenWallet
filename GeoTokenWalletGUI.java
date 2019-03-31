/* GT WALLET
 * GeoToken
 * David Folliet
 * Copyright @ March 31, 2019
 * GRAPHICAL USER INTERFACE
 */

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import javax.swing.filechooser.FileSystemView;
import org.web3j.crypto.CipherException;
import org.web3j.tx.ChainId;

//GEOTOKEN GRAPHICAL USER INTERFACE
public class GeoTokenWalletGUI {
	//CONSTRUCTOR
	public GeoTokenWalletGUI() {	
		isTestNet = false;
		isLocalNode = false;
		converter = new UnitConverter();
		host = new Host();
		frame = new JFrame("GeoToken");
		//There is no way to replace default JRE icon for exported executable jar files
		//default icon image can be set in a .exe file which contains the .jar file
		//frame.setIconImage(new ImageIcon(getClass().getResource("GT_logo_s.png")).getImage());			//works iff running from IDE (Eclipse)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(frameWidth,frameHeight); 
		frame.setLocation(frameHorizontalOffset,frameVeritcalOffset); 	
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new CancelBtnListener());
		status = new JLabel();
		status.setBorder(BorderFactory.createLineBorder(Color.black));
		displayMainScreen();
	}
	
	//private member functions
	private void displayMainScreen(){          																//shows logo, user to interact with menu to load or create keys										
		if(panel != null)
			frame.remove(panel);
		setDefaultUserMenu();
		panel = new JPanel(new GridLayout(1,1));
		panel.add(new JLabel(new ImageIcon(getClass().getResource("GT_Logo_Banner1.png"))));
		frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
	}
	
	private void setDefaultUserMenu() {																		//Menu Bar
		menuBar = new JMenuBar();
		//Menu - Load or create keys
		JMenu menu = new JMenu("Menu"); 
	
		JMenuItem newWallet = new JMenuItem("Create a New Wallet"); 
		newWallet.addActionListener(new NewWalletListener());
		menu.add(newWallet);
		
		JMenuItem loadPrivateKey = new JMenuItem("Load Wallet Using Ethereum Private Key"); 
		loadPrivateKey.addActionListener(new LoadPrivateKeyListener());
		menu.add(loadPrivateKey);
		
		JMenuItem loadPrivateKeyFile = new JMenuItem("Load Wallet Using Ethereum Private Key File"); 
		loadPrivateKeyFile.addActionListener(new LoadPrivateKeyFileListener());
		menu.add(loadPrivateKeyFile);
		
		menuBar.add(menu);
	
		if(!connectionMenuOptions) {
			frame.setJMenuBar(menuBar);
			return;
		}
		
		/*Additional Menus - Show if private key has already been loaded*/
		
		//Connection Options
		options = new JMenu("Connection Options"); 
		 
		changeNetwork = new JMenuItem(); 
		if(isTestNet)
			changeNetwork.setText("Change To Ethereum Network");
		else
			changeNetwork.setText("Change To Ropsten Test Network");
		changeNetwork.addActionListener(new ChangeNetworkListener());
		 
		changeHost = new JMenuItem(); 
		if(isLocalNode)
			changeHost.setText("Change To Infura Node");
		else
			changeHost.setText("Change To Local Node");
		changeHost.addActionListener(new ChangeHostListener());
		
		options.add(changeNetwork);
		options.add(changeHost);
		menuBar.add(options);
		
		
		//Send
		JMenu sendMenu = new JMenu("Send");
		JMenuItem sendGT = new JMenuItem("Send GT");
		JMenuItem sendETH = new JMenuItem("Send ETH");
		sendGT.addActionListener(new sendGTListener());
		sendETH.addActionListener(new sendETHListener());
		sendMenu.add(sendGT);
		sendMenu.add(sendETH);
		menuBar.add(sendMenu);
		
		//Transaction History
		JMenu transactionsMenu = new JMenu("Transactions");
		JMenuItem geoTokenTransactions = new JMenuItem("GeoToken Transactions");
		JMenuItem ethereumTransactions = new JMenuItem("Ethereum Transactions");
		geoTokenTransactions.addActionListener(new geoTokenTransactionsListener());
		ethereumTransactions.addActionListener(new ethereumTransactionsListener());
		transactionsMenu.add(geoTokenTransactions);
		transactionsMenu.add(ethereumTransactions);
		menuBar.add(transactionsMenu);
		
		frame.setJMenuBar(menuBar);
	}
	
	private void createNewWallet(){																		//Menu Option					
		frame.remove(panel);
		panel = new JPanel(new GridLayout(4,1));
		status.setText("Enter a password to protect your private key file");
		panel.add(status);
		JTextField keyFilePassword = new JTextField();
		keyFilePassword.addActionListener(new GenerateKeyPairListener(keyFilePassword));               	//use keyboard enter button
		panel.add(keyFilePassword);
		JButton generateKeyPair = new JButton("Generate key Pair");			
		generateKeyPair.addActionListener(new GenerateKeyPairListener(keyFilePassword));				//use GUI button
		panel.add(generateKeyPair); 
		panel.add(cancelBtn);	                           												//cancel button
		frame.add(panel);
		frame.setVisible(true);	
	}
	 
	private void generateKeyPair(String keyFilePassword) {												//called from Listener
		String destinationDirectory = null;
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Choose a directory to save your private key file: ");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnValue = jfc.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			if (jfc.getSelectedFile().isDirectory()) 
				destinationDirectory = jfc.getSelectedFile().getPath();
		}
		Account.createNewWalletFile(keyFilePassword, destinationDirectory);
		JOptionPane.showMessageDialog (null,"Wallet file created, Returning to main screen","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
		displayMainScreen();
	}
	
	private void loadPrivateKey() {																		//...
		frame.remove(panel);
		panel = new JPanel(new GridLayout(4,1));
		status.setText("Enter Private Key");
		panel.add(status);
		JTextField privateKey = new JTextField();
		privateKey.addActionListener(new LoadPrivateKeyBtnListener(privateKey));
		panel.add(privateKey);
		JButton loadPrivateKeyBtn=new JButton("Load Private Key");
		loadPrivateKeyBtn.addActionListener(new LoadPrivateKeyBtnListener(privateKey));
		panel.add(loadPrivateKeyBtn);
		panel.add(cancelBtn);	
		frame.add(panel);
		frame.setVisible(true);	
	}
	
	private void loadPrivateKeyFile() {																	
		String privateKeyFilePath = null;
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) { 
			File selectedFile = jfc.getSelectedFile();
			privateKeyFilePath =selectedFile.getAbsolutePath().toString();
		}
		frame.remove(panel);
		panel = new JPanel(new GridLayout(4,1));
		status.setText("Enter password for private key file");
		panel.add(status);
		JTextField password = new JTextField();
		password.addActionListener(new LoadPrivateKeyFileBtnListener(password, privateKeyFilePath));
		panel.add(password);
		JButton loadPrivateKeyFileBtn=new JButton("Load Private Key File");
		loadPrivateKeyFileBtn.addActionListener(new LoadPrivateKeyFileBtnListener(password, privateKeyFilePath));
		panel.add(loadPrivateKeyFileBtn);
		panel.add(cancelBtn);	
		frame.add(panel);
		frame.setVisible(true);	
	}
	
	private boolean loadWallet(String privateKey) {
		if(!isPrivateKeySyntaxValid(privateKey)) {
			JOptionPane.showMessageDialog (null,"Invalid private key, keyboard input not valid","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			displayMainScreen();
			return false;
		}
		try {
			wallet = new Wallet(ChainId.MAINNET, host.getInfuraHostMainNet(), privateKey, host);	
		} catch (CipherException e) {
			JOptionPane.showMessageDialog (null,"Invalid private key","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			displayMainScreen();
			return false;
		}
		connectionMenuOptions = true;
		walletScreen();
		return true;
	}
	
	private boolean loadWallet(String password, String privateKeyFilePath) {
		try {
			wallet = new Wallet(ChainId.MAINNET, host.getInfuraHostMainNet(), password, privateKeyFilePath, host);
		} catch (CipherException e) {
			JOptionPane.showMessageDialog (null,"Invalid private key or private key file password","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			displayMainScreen();
			return false;
		}
		connectionMenuOptions = true;
		walletScreen();
		return true;
	}
	
	private void walletScreen() {
		setDefaultUserMenu();
		frame.remove(panel);
		panel = new JPanel(new GridLayout(7,1));
		status.setText("Public Adress: " + wallet.getAccount().getCredentials().getAddress());
		panel.add(status);
		String network;
		if(isTestNet)
			network = "Ropsten Test Network";
		else
			network = "Ethereum Main Network";
		panel.add(new JLabel("Network: " + network));
		String nodeHost;
		if(isLocalNode)
			nodeHost = Host.LOCAL_HOST;
		else
			if(isTestNet)
				nodeHost = Host.INFURA_HOST_TESTNET;
			else
				nodeHost = Host.INFURA_HOST_MAINNET;
		panel.add(new JLabel("Node Host: " + nodeHost));
		panel.add(new JLabel("Client Version: " + wallet.getNode().getClientVersion()));
		try {
			panel.add(new JLabel("GT: " + wallet.getGeoTokenBalance()));
		} catch (ExecutionException e) {									
			e.printStackTrace();
		}
		panel.add(new JLabel("ETH: " + wallet.getEthereumBalance()));
		converter.setEthApiHandler();
		panel.add(new JLabel("ETH USD Value: $" +   converter.getUsdValue(wallet.getEthereumBalance())));
		frame.add(panel);
		frame.setVisible(true);
	}
	
	private void sendGT() {																								//TODO CUSTOM GAS PRICE AND GAS LIMIT  (PROVIDED BY USER INPUT)
		frame.remove(panel);
		panel = new JPanel(new GridLayout(4,2));
		//balances
		try {
			panel.add(new JLabel("GT: " + wallet.getGeoTokenBalance()));
		} catch (ExecutionException e) {									
			e.printStackTrace();
		}
		panel.add(new JLabel("ETH: " + wallet.getEthereumBalance()));
		//to address and amount
		panel.add(new JLabel("To Address"));
		JTextField toAddress = new JTextField();
		panel.add(toAddress);
		panel.add(new JLabel("Amount"));
		JTextField amount = new JTextField();
		panel.add(amount);
		//gas price and gas limit
		
		
		
		
		
		
		
		
		
		//send and cancel buttons
		JButton sendBtn = new JButton("Continue");
		sendBtn.addActionListener(new ConfirmTransactionListener(false, toAddress, amount));

		panel.add(cancelBtn);
		panel.add(sendBtn);
		
		//show gui
		frame.add(panel);
		frame.setVisible(true);
	}
	
	private void sendETH() {
		frame.remove(panel);
		panel = new JPanel(new GridLayout(4,2));
		//balances
		try {
			panel.add(new JLabel("GT: " + wallet.getGeoTokenBalance()));
		} catch (ExecutionException e) {									
			e.printStackTrace();
		}
		panel.add(new JLabel("ETH: " + wallet.getEthereumBalance()));
		//to address and amount
		panel.add(new JLabel("To Address"));
		JTextField toAddress = new JTextField();
		panel.add(toAddress);
		panel.add(new JLabel("Amount"));
		JTextField amount = new JTextField();
		panel.add(amount);
		//gas price and gas limit
		
		
		
		
		
		
		
		
		
		//send and cancel buttons
		JButton sendBtn = new JButton("Continue");
		sendBtn.addActionListener(new ConfirmTransactionListener(true, toAddress, amount));

		panel.add(cancelBtn);
		panel.add(sendBtn);
		
		//show gui
		frame.add(panel);
		frame.setVisible(true);
	}
	
	private boolean confirmTransaction(boolean isEthTransaction, String toAddress, String amount) {		
		wallet.initiateTransaction(isEthTransaction);																//TODO NEED TO CALL DIFERRENT INIT FUNCTION IF USING USER SPECIFIES GAS PRICE AND LIMIT
		//check for sufficient balance
		if(isEthTransaction && !wallet.isSufficientBalanceEthereumTransfer(new BigDecimal(amount))) {
			return false;
		}else {
			try {
				if(!wallet.isSufficientBalanceGeotokenTransfer(new BigDecimal(amount))) {
					//JOptionPane.showMessageDialog (null,"INSUFICENT BALANCE(S)","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
					if(!wallet.isSufficientEthereumBalanceGeotokenTransfer(new BigDecimal(amount)))
						JOptionPane.showMessageDialog (null,"INSUFICENT ETH BALANCE (GAS) FOR SENDING TRANSACTION","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);					
					else
						JOptionPane.showMessageDialog (null,"INSUFICENT GT BALANCE","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
					walletScreen();
					return false;
				}
			} catch (ExecutionException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		
		//force user to confirm transaction
		frame.remove(panel);
		panel = new JPanel(new GridLayout(4,1));
		panel.add(new JLabel("To: " + toAddress));
		panel.add(new JLabel("Amount: " + amount));
		
		//send and cancel buttons
		JButton sendBtn = new JButton("Send");
		sendBtn.addActionListener(new SendTransactionListener(isEthTransaction, toAddress, amount));

		panel.add(cancelBtn);
		panel.add(sendBtn);
		
		//show gui
		frame.add(panel);
		frame.setVisible(true);
		return true;	
	}
	private void sendTransaction(boolean isEthTransaction ,String toAddress, String amount) {                                   //TODO HANDLE EXCEPTION
		Transaction outgoingTransaction = null;
		if(isEthTransaction)
			outgoingTransaction = wallet.sendEthTransaction(toAddress, new BigDecimal(amount));
		else 
			try {
				outgoingTransaction = wallet.sendGeoTokenTransaction(toAddress, new BigDecimal(amount));
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		walletScreen();
	}

	
	private void geoTokenTransactions() {
		try {
			Desktop.getDesktop().browse(new URL("https://etherscan.io/token/0xb5a00e30602ee03074f4f8e996adaf9dc96224a4?a="+ wallet.getAccount().getCredentials().getAddress()).toURI());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void ethereumTransactions() {
		try {
			Desktop.getDesktop().browse(new URL("https://etherscan.io/address/"+ wallet.getAccount().getCredentials().getAddress()).toURI());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//static helper functions
	private static boolean isPrivateKeySyntaxValid(String privateKey) {
		char[] chars = privateKey.toCharArray();
		if(chars.length==1 && chars[0]=='0')
			return false;
		for(char ch : chars) {
			if(ch < '0')
				return false;
			if(ch>'9' && ch <'A')
				return false;
			if(ch>'F' && ch <'a')
				return false;
			if(ch > 'f')
				return false;
		}
		return true;
	}
	
	
	//private members
	private Wallet wallet;
	private UnitConverter converter;
	private Host host;
	
	private boolean isTestNet;
	private boolean isLocalNode;
	
	
	
	
	//GUI
	private JMenuBar menuBar;
	private JFrame frame;
	private JPanel panel;
	private JLabel status;
	private JButton cancelBtn;
	private boolean connectionMenuOptions;
	private JMenu options;
	private JMenuItem changeNetwork; 
	private JMenuItem changeHost; 
	
	
	//GUI constants
	private static final int frameWidth = 600; 
	private static final int frameHeight = 400; 
	private static final int frameHorizontalOffset = 250; 
	private static final int frameVeritcalOffset = 250; 
	
	
	//MAIN
	public static void main(String args[]){
		new GeoTokenWalletGUI();				//calls constructor
	}

		
	
	//Listeners (inner classes)
	private class CancelBtnListener implements ActionListener {
		  public void actionPerformed(ActionEvent e) {
			  if(wallet == null) {
				  JOptionPane.showMessageDialog (null,"Returning to main screen","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
				  displayMainScreen();
			  }else {
				  JOptionPane.showMessageDialog (null,"Returning to wallet screen","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
				  walletScreen();
			  }
		  }
	}
	private class NewWalletListener implements ActionListener {
		  public void actionPerformed(ActionEvent e) {
			  JOptionPane.showMessageDialog (null,"Navigating to wallet creation screen","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			  createNewWallet();			  
		  }
	}
	
	private class GenerateKeyPairListener implements ActionListener {
		public GenerateKeyPairListener(JTextField keyFilePassword) {
			this.keyFilePassword = keyFilePassword;
		}
		  public void actionPerformed(ActionEvent e) {
			  JOptionPane.showMessageDialog (null,"Specify where to save your wallet file","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			  generateKeyPair(keyFilePassword.getText());			  
		  }
		  private JTextField keyFilePassword;
	}
	
	
	private class LoadPrivateKeyListener implements ActionListener {
		 public void actionPerformed(ActionEvent e) {
			  JOptionPane.showMessageDialog (null,"Navigating to wallet loading screen","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			  loadPrivateKey();			  
		  }
	}
	private class LoadPrivateKeyBtnListener implements ActionListener {
		public LoadPrivateKeyBtnListener(JTextField privateKey) {
			this.privateKey = privateKey;
		}
		  public void actionPerformed(ActionEvent e) {
			  if(privateKey.getText() == null || privateKey.getText().equals("")) {
				  JOptionPane.showMessageDialog (null,"Invalid private key","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
				  displayMainScreen();
			  }else if(loadWallet(privateKey.getText()))
				  JOptionPane.showMessageDialog (null,"Wallet loaded successfully","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			  else
				  JOptionPane.showMessageDialog (null,"Error loading wallet","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
		  }
			private JTextField privateKey;
	}

	private class LoadPrivateKeyFileListener implements ActionListener {
		 public void actionPerformed(ActionEvent e) {
			  JOptionPane.showMessageDialog (null,"Navigating to wallet loading screen, specify key file location","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			  loadPrivateKeyFile();			  
		  }
	}
	private class LoadPrivateKeyFileBtnListener implements ActionListener {
		public LoadPrivateKeyFileBtnListener(JTextField password, String privateKeyFile) {
			this.password = password;
			this.privateKeyFile = privateKeyFile;
		}
		 public void actionPerformed(ActionEvent e) {
			 if(loadWallet(password.getText(), privateKeyFile))
				 JOptionPane.showMessageDialog (null,"Wallet loaded successfully","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);		  
		  }
		 private JTextField password;
		 private String privateKeyFile;
	}
	private class ChangeNetworkListener implements ActionListener {
		  public void actionPerformed(ActionEvent e) {
			  if(isTestNet){
				  wallet.setNetwork(ChainId.MAINNET);
				  isTestNet = false;
			  }else {
				  wallet.setNetwork(ChainId.ROPSTEN);
				  isTestNet = true;
			  }
			  walletScreen();
		  } 
	}
	private class ChangeHostListener implements ActionListener { 
		  public void actionPerformed(ActionEvent e) {
			  if(isLocalNode){										//change to Infura node
				  if(isTestNet)
					  wallet.setHostName(host.getInfuraHostTestNet());
				  else
					  wallet.setHostName(host.getInfuraHostMainNet());
				  isLocalNode = false;
			  }else {												//change to local node
				  try {
					  wallet.setHostName(host.getLocalHost());
					  wallet.getGeoTokenBalance();					//used only to try to throw exception so it can be handled here
				} catch (ExecutionException e1) {					//exception detected so switch back to Infura 
					e1.printStackTrace();
					if(isTestNet){
						JOptionPane.showMessageDialog (null,"Failed to connect to ropsten test network on local node, check your localhost node conection (parity/geth)","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
						wallet.setHostName(host.getInfuraHostTestNet());
					}else {
						JOptionPane.showMessageDialog (null,"Failed to connect to local node, check your localhost node conection (parity/geth)","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
						wallet.setHostName(host.getInfuraHostMainNet());	
					}
					walletScreen();
					return;
				}
				  isLocalNode = true;
				  JOptionPane.showMessageDialog (null,"You are now using your own node, you must synch your node(parity/geth) to the network you are using (mainnet/ropsten testnet)","Notification",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			  }
			  walletScreen();
		  }
	}
	private class sendGTListener implements ActionListener {
		  public void actionPerformed(ActionEvent e) {
			 sendGT();
		  } 
	}
	private class sendETHListener implements ActionListener {
		  public void actionPerformed(ActionEvent e) {
			 sendETH();
		  } 
	}
	private class ConfirmTransactionListener implements ActionListener {
		  public ConfirmTransactionListener(boolean isEthTranaction, JTextField toAddress, JTextField amount) {
			  this.toAddress = toAddress;
			  this.amount = amount;
			  this.isEthTranaction = isEthTranaction;
		  }
		  public void actionPerformed(ActionEvent e) {
			 confirmTransaction(isEthTranaction, toAddress.getText(), amount.getText());
		  } 
		  private JTextField toAddress;
		  private JTextField amount;
		  private boolean isEthTranaction;
	}
	private class SendTransactionListener implements ActionListener {
		  public SendTransactionListener(boolean isEthTransaction, String toAddress, String amount) {
			  this.isEthTransaction = isEthTransaction;
			  this.toAddress = toAddress;
			  this.amount = amount;
		  }
		  public void actionPerformed(ActionEvent e) {
			 sendTransaction(isEthTransaction, toAddress, amount);
		  } 
		  private boolean isEthTransaction;
		  private String toAddress;
		  private String amount;
	}
	private class geoTokenTransactionsListener implements ActionListener {
		  public void actionPerformed(ActionEvent e) {
			 geoTokenTransactions();
		  } 
	}
	private class ethereumTransactionsListener implements ActionListener {
		  public void actionPerformed(ActionEvent e) {
			 ethereumTransactions();
		  } 
	}
	
	
}
