import java.awt.*;
import java.util.Date;
import java.awt.event.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import net.proteanit.sql.DbUtils;

import java.sql.Connection;

public class CheckInList extends JFrame
{
	JFrame mainFrame;
	JPanel controlPanel;
	static Connection conn=null;
	int row=0;
	
	JTable table;
	
	CheckInList(String isbn, String card, String borrower) throws SQLException
	{
		prepareGUI(isbn,card,borrower);
	}
	
	
	void prepareGUI(String isbn, String card, String borrower) throws SQLException
	{
		mainFrame=new JFrame("Library System");
		mainFrame.setSize(425,500);
		mainFrame.setLocation(700, 100);
		mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		controlPanel=new JPanel();
		controlPanel.setBorder(new EmptyBorder(10,10,10,10));
		setContentPane(controlPanel);
		GridBagLayout gbl_controlPanel=new GridBagLayout();
		gbl_controlPanel.columnWidths=new int[]{0,0};
		gbl_controlPanel.rowHeights = new int[]{0, 0, 0};
		gbl_controlPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_controlPanel.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		controlPanel.setLayout(gbl_controlPanel);
                
		JLabel details=new JLabel("Double-click a row to check in the selected book",JLabel.LEFT);
		details.setFont(new Font("Ariel", Font.PLAIN,16));
		GridBagConstraints gbc_details=new GridBagConstraints();
		gbc_details.insets=new Insets(0,0,5,0);
		gbc_details.gridx=0;
		gbc_details.gridy=0;
		gbc_details.gridwidth=2;
		controlPanel.add(details, gbc_details);
                
		JButton close= new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.setVisible(false); 
				mainFrame.dispose();
			}
		});
		
		JLayeredPane layeredPane=new JLayeredPane();
		GridBagConstraints gbc_layeredPane = new GridBagConstraints();
		gbc_layeredPane.insets = new Insets(0, 0, 5, 0);
		gbc_layeredPane.fill = GridBagConstraints.BOTH;
		gbc_layeredPane.gridx = 0;
		gbc_layeredPane.gridy = 0;
		controlPanel.add(layeredPane, gbc_layeredPane);
		
		JScrollPane scrollPane=new JScrollPane();
		scrollPane.setBounds(6,6,1200,600);
		layeredPane.add(scrollPane);
		
		table=new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(true);
		scrollPane.setViewportView(table);
		
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent evnt)
			{
				if(evnt.getClickCount() ==1 || evnt.getClickCount()==2)
				{
					int loan_id=(int)table.getModel().getValueAt(table.rowAtPoint(evnt.getPoint()), 0);
					String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
					try
					{
						conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Library?useSSL=false", "root", "password123");
						Statement stmt1=conn.createStatement();
						stmt1.executeUpdate("Update library.book_loans set Date_in='"+date+"' where Loan_id="+loan_id+";");
						
						Statement stmt2=conn.createStatement();
						ResultSet rs= stmt2.executeQuery("Select * from library.book_loans where Loan_id="+loan_id+" and Due_date<Date_in;");
						
						if(rs.next())
						{
							JOptionPane.showMessageDialog(null, "Book is overdue. There are fines.");
							mainFrame.setVisible(false);
							new FinesPage();
						}
						else
	                    {
	                    	JOptionPane.showMessageDialog(null, "Checked in. No fines applied");
	                    	mainFrame.dispose();
	                    	new CheckIn();
	                    }
					}
					catch(SQLException e)
					{
						System.out.println("Sql Error : "+e.getMessage());
					} /*catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				}
			}
		});
			
		//Close Button
		GridBagConstraints gbc_btnClose = new GridBagConstraints();
		gbc_btnClose.fill=GridBagConstraints.HORIZONTAL;
		gbc_btnClose.insets = new Insets(0, 0, 5, 0);
		gbc_btnClose.gridx = 0;
		gbc_btnClose.gridy = 2;
		gbc_btnClose.anchor=GridBagConstraints.PAGE_END;
		controlPanel.add(close, gbc_btnClose);		
		try{		
			//jdbc connection to database
			conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/library?useSSL=false", "root", "password123");
			Statement stmt=conn.createStatement();
			ResultSet rs= null;
			
			if(card.equals("") && borrower.equals(""))//only isbn
			{
				rs=stmt.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Isbn="+isbn+" and Date_in is null;");
			}
			else if(isbn.equals("") && borrower.equals(""))//only card_id
			{
				rs=stmt.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Card_id="+card+" and Date_in is null;");
			}
			else if(isbn.equals("") && card.equals(""))//only borrower name
			{
				rs=stmt.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Card_id in (Select Card_id from library.borrower where Bname like '%"+borrower+"%') and Date_in is null;");
			}
			else if(borrower.equals(""))//isbn and card_id
			{
				rs=stmt.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Isbn="+isbn+" and Card_id="+card+" and Date_in is null;");
			}
			else if(isbn.equals(""))//card_id and borrower name
			{
				rs=stmt.executeQuery("(Select Loan_id,Isbn,Card_id from library.book_loans where Card_id="+card+" and Date_in is null)");
			}
			else if(card.equals(""))//isbn and borrower name
			{
				rs=stmt.executeQuery("(Select Loan_id,Isbn,Card_id from library.book_loans where Isbn="+isbn+" and Card_id in (Select Card_id from library.borrower where Bname like '%"+borrower+"%') and Date_in is null)");
			}
			else//all 3 given
			{
				rs=stmt.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Isbn="+isbn+" and Card_id="+card+" and Date_in is null;");
			}
			
			table.setModel(DbUtils.resultSetToTableModel(rs));
			table.setEnabled(false);
			
			table.getColumnModel().getColumn(1).setPreferredWidth(150);
			table.getColumnModel().getColumn(2).setPreferredWidth(150);
			
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
			}
		
		mainFrame.add(controlPanel);
		mainFrame.setVisible(true);
		
	}
	}

