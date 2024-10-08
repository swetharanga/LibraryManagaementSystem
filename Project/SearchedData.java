import java.awt.*;
import java.awt.event.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import net.proteanit.sql.DbUtils;

import java.sql.Connection;

public class SearchedData extends JFrame
{
	JFrame mainFrame;
	JPanel controlPanel;
	static Connection conn=null;

	JTable table;
	
	SearchedData(String keyword) throws SQLException
	{
		prepareGUI(keyword);
	}
	
	void prepareGUI(String keyword) throws SQLException
	{
		mainFrame=new JFrame("Library System");
		mainFrame.setSize(1200,500);
		mainFrame.setLocation(20, 50);
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
		
		table=new JTable();
		JScrollPane scrollPane=new JScrollPane(table);
		scrollPane.setBounds(6,6,1100,600);
		layeredPane.add(scrollPane);
		
		
		scrollPane.setViewportView(table);
		
		GridBagConstraints gbc_btnClose = new GridBagConstraints();
		gbc_btnClose.fill=GridBagConstraints.NONE;
		gbc_btnClose.insets = new Insets(0, 0, 5, 0);
		gbc_btnClose.gridx = 0;
		gbc_btnClose.gridy = 2;
		gbc_btnClose.anchor=GridBagConstraints.PAGE_END;
		controlPanel.add(close, gbc_btnClose);		
		try{		
			//jdbc connection to database
			conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "password123");
			Statement stmt=conn.createStatement();
			ResultSet rs= null;
			
			if(keyword.equals(" ")|| keyword.equals(""))
			{
				rs=stmt.executeQuery("SELECT b.Isbn, b.Title, GROUP_CONCAT(a.Name) as Authors, (case when b.Isbn in(Select Isbn from library.book_loans where Date_in IS NULL) then 'NO' else 'YES' end) AS Available FROM library.book AS b left outer join library.book_authors AS ba on b.Isbn=ba.Isbn left outer join library.authors as a on ba.Author_id=a.Author_id group by b.Isbn; ");
			}
			else
			{
				rs= stmt.executeQuery("SELECT b.Isbn, b.Title, GROUP_CONCAT(a.Name) as Authors, (case when b.Isbn in(Select Isbn from library.book_loans where Date_in IS NULL) then 'NO' else 'YES' end) AS Available FROM library.book AS b left outer join library.book_authors AS ba on b.Isbn=ba.Isbn left outer join library.authors as a on ba.Author_id=a.Author_id group by b.Isbn having b.Isbn like '%"+keyword+"%' or b.Title like '%"+ keyword+ "%' or Authors like '%"+ keyword+"%';");
			}
			table.setModel(DbUtils.resultSetToTableModel(rs));
			table.setEnabled(false);
			
			table.getColumnModel().getColumn(0).setPreferredWidth(150);
			table.getColumnModel().getColumn(1).setPreferredWidth(500);
			table.getColumnModel().getColumn(2).setPreferredWidth(350);
			table.getColumnModel().getColumn(3).setPreferredWidth(100);
			
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

