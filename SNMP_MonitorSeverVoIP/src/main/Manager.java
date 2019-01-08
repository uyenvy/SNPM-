package main;

import java.awt.BorderLayout;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.border.LineBorder;

import snmptool.SNMP;

import java.awt.Color;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.TextArea;

public class Manager extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JTextField txtIP;
	private JTextField txtComString;
	private JTextField txtOID;
	private JButton btnOk;
	private JComboBox<String> comboBox;
	private TextArea textArea ;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Manager frame = new Manager();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Manager() {
		setTitle("Monitor Sever VoIP - SNMP");
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\BunZyZY\\Downloads\\home-icon.png"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 773, 452);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblMonitorServerVoip = new JLabel("Monitor Server VoIP");
		lblMonitorServerVoip.setHorizontalAlignment(SwingConstants.CENTER);
		lblMonitorServerVoip.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblMonitorServerVoip.setBounds(10, 11, 737, 33);
		contentPane.add(lblMonitorServerVoip);
		
		JPanel panel_input = new JPanel();
		panel_input.setBorder(new LineBorder(Color.GRAY));
		panel_input.setBounds(10, 55, 352, 346);
		contentPane.add(panel_input);
		panel_input.setLayout(null);
		
		JLabel lbl = new JLabel("SNMP Method");
		lbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl.setBounds(10, 14, 112, 19);
		panel_input.add(lbl);
		
		 comboBox = new JComboBox();
		comboBox.setBounds(132, 11, 210, 22);
		comboBox.addItem("get-request");
		comboBox.addItem("get-next-request");
		panel_input.add(comboBox);
		
		JLabel lblAgentIp = new JLabel("Agent IP");
		lblAgentIp.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblAgentIp.setBounds(10, 59, 59, 19);
		panel_input.add(lblAgentIp);
		
		txtIP = new JTextField();
		txtIP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtIP.setBounds(132, 57, 210, 22);
		panel_input.add(txtIP);
		txtIP.setColumns(10);
		
		JLabel lblCommunityString = new JLabel("Community String");
		lblCommunityString.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblCommunityString.setBounds(10, 96, 131, 22);
		panel_input.add(lblCommunityString);
		
		txtComString = new JTextField();
		txtComString.setText("public");
		txtComString.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtComString.setColumns(10);
		txtComString.setBounds(132, 96, 210, 22);
		panel_input.add(txtComString);
		
		JLabel lblOid = new JLabel("OID");
		lblOid.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOid.setBounds(10, 141, 59, 19);
		panel_input.add(lblOid);
		
		txtOID = new JTextField();
		txtOID.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtOID.setColumns(10);
		txtOID.setBounds(132, 139, 210, 22);
		panel_input.add(txtOID);
		
		btnOk = new JButton("OK");
		btnOk.addActionListener(this);
		btnOk.setBackground(Color.LIGHT_GRAY);
		btnOk.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnOk.setBounds(132, 201, 89, 23);
		panel_input.add(btnOk);
				
		JPanel panel_output = new JPanel();
		panel_output.setBorder(new LineBorder(Color.GRAY));
		panel_output.setBounds(372, 55, 375, 346);
		contentPane.add(panel_output);
		panel_output.setLayout(null);
		
		textArea = new TextArea();
		textArea.setFont(new Font("Candara", Font.PLAIN, 18));
		textArea.setBounds(0, 0, 375, 346);
		panel_output.add(textArea);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.GREEN);
		//textArea.setEnabled(false);
	}

	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(btnOk)) {
			textArea.setText("");
			SNMP snmp = new SNMP(txtIP.getText(), txtOID.getText(), txtComString.getText(), comboBox.getSelectedItem().toString());
			snmp.getResponse();
			textArea.setText(snmp.getError() + "\n" + snmp.getValue());
			
		}
		
	}
	
	
}
