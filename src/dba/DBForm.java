package dba;

import java.util.ArrayList;

import javax.swing.JFrame; 
import javax.swing.JLabel; 
import javax.swing.JPanel; 
import javax.swing.JTextField; 
import javax.swing.JScrollPane;
import javax.swing.JTable; 
import javax.swing.JButton; 
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.SwingUtilities;
import javax.swing.JTabbedPane;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.Font;
import java.awt.Insets;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class DBForm implements ActionListener, DocumentListener, MouseListener
{
	JFrame frame;
	EnvironmentData data;
    
	JPanel panel = new JPanel(new GridBagLayout());
    
    JTextField tableSearch;
    TableRowSorter<TableModel> tableSorter;
    
    JTextField fieldSearch;
    TableRowSorter<TableModel> fieldSorter;
    
    Table tableList;
    TabbedTable tableInfo;
    Table fieldList;
    TabbedTable fieldInfo;
    
	JButton refreshAllButton;
	JButton settingsButton;
	JButton refreshTableButton;
    
	DBForm(SettingsData settings, JFrame frame)
	{
		this.frame = frame;
        
        // initializing basic data settings
		data = new EnvironmentData(settings);
        
        // initializing table list
		tableList = new Table("Table Name", "Description");
        tableList.getTable().addMouseListener(this);
        
        // initializing table info
        tableInfo = new TabbedTable();
        tableInfo.addTab("Table Properties", new Table("Properties", "Description"));
        tableInfo.addTab("Index", new SplitPane());
        tableInfo.addTab("Table Documentation", new TextArea());
        
        JTabbedPane tabs = tableInfo.getTabs();
        Table tableProps = (Table)tabs.getComponentAt(0);
        tableProps.populateTableProps(new TableInfo2());
        
        // initializing field list
        fieldList = new Table("Field Name", "Description");
        fieldList.getTable().addMouseListener(this);
        
        // initializing field info
        fieldInfo = new TabbedTable();
        fieldInfo.addTab("Column Properties", new Table("Properties", "Description"));
        fieldInfo.addTab("Column Documentation", new TextArea());
        
        // initializing refresh button "Update All"
        refreshAllButton = new JButton("Update All");
		refreshAllButton.addActionListener(this);
		
        // initializing settings button 
		settingsButton = new JButton("Settings");
		settingsButton.addActionListener(this);
        
        // initializing refresh table button
        refreshTableButton = new JButton("Update Table");
		refreshTableButton.addActionListener(this);
		
        // initializing texfield for table list
		tableSearch = new JTextField();
        tableSorter = new TableRowSorter<TableModel>(tableList.getModel());
        tableSearch.getDocument().addDocumentListener(this);
        tableList.setRowSorter(tableSorter);
        
        // initializing texfield for field list
        fieldSearch = new JTextField();
        fieldSorter = new TableRowSorter<TableModel>(fieldList.getModel());
        fieldSearch.getDocument().addDocumentListener(this);
        fieldList.setRowSorter(fieldSorter);
        
        // initializing all objects's layouts
		this.panel.add(tableSearch,  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.01, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 100, 0));
		this.panel.add(refreshAllButton,  new GridBagConstraints(2, 0, 1, 1, 1.0, 0.01, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 80), 0, 0));
		this.panel.add(settingsButton,  new GridBagConstraints(2, 0, 1, 1, 1.0, 0.01, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		
		this.panel.add(fieldSearch,  new GridBagConstraints(3, 0, 1, 1, 1.0, 0.01, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 100, 0));
		this.panel.add(new JLabel(""),  new GridBagConstraints(5, 0, 1, 1, 1.0, 0.01, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 140), 0, 0));
		this.panel.add(refreshTableButton,  new GridBagConstraints(5, 0, 2, 1, 1.0, 0.01, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
		
		this.panel.add(tableList,  new GridBagConstraints(0, 1, 3, 1, 1.0, 0.7, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        
        this.panel.add(tableInfo, new GridBagConstraints(0, 2, 3, 1, 1.0, 0.4, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		this.panel.add(fieldList,  new GridBagConstraints(3, 1, 3, 1, 1.0, 0.7, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		this.panel.add(fieldInfo,  new GridBagConstraints(3, 2, 3, 1, 1.0, 0.4, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		
        // setting border
		this.panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }
    
    DBForm(SettingsData settings, JFrame frame, String data1[], String data2[])
    {
        this(settings, frame);
        tableList.populateTable(data1, data2);
    }
    
    JPanel createNewTable(String firstName, String secondName)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn(firstName);
		tableModel.addColumn(secondName);
		
		JTable table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.setFont(new Font("Courier New", Font.PLAIN, 14));
		table.setShowVerticalLines(true);
		JScrollPane scrollTable = new JScrollPane(table);
		
		panel.add(scrollTable, BorderLayout.CENTER);
		
		return panel;
	}
    
    public static DBForm createNewForm(SettingsData settings, JFrame frame)
	{
		DBForm dbForm = new DBForm(settings, frame);
		return dbForm;
	}
    
    
    public JPanel getPanel() {
		return panel;
	}
	public void setPanel(JPanel panel) {
		this.panel = panel;
	}
    
	public void populateData(ArrayList<TableInfo2> tableArray)
    {
        data.setTableArray(tableArray);
        tableList.populateTableArray(tableArray);
    }
    
    // event handlers
    public void actionPerformed(ActionEvent event) 
	{
        if (event.getSource() == refreshAllButton) {
            
			System.out.println("refreshAllButton");
		}
		if (event.getSource() == settingsButton) 
		{
			System.out.println("settingsButton");
			
            SettingsData newSettings = SettingsDialog.updateNewDialog(frame, data.getSettings());
			data.setSettings(newSettings);
		}
	}
    
    
    @Override
        public void insertUpdate(DocumentEvent event)
    {
        JTextField tmpSearch;
        TableRowSorter tmpSorter;
        if (event.getDocument() == tableSearch.getDocument()) 
        {
            String text = tableSearch.getText();
            if (text.trim().length() == 0)
            {
                tableSorter.setRowFilter(null);
            }
            else
            {
                tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            } 
        }
        else if (event.getDocument() == fieldSearch.getDocument())
        {
            String text = fieldSearch.getText();
            if (text.trim().length() == 0)
            {
                fieldSorter.setRowFilter(null);
            }
            else
            {
                fieldSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            } 
        }
    }
    
    @Override
        public void removeUpdate(DocumentEvent event)
    {
        insertUpdate(event);
    }
    
    @Override
        public void changedUpdate(DocumentEvent event)
    {
        
    }
    
    @Override
        public void mouseClicked(MouseEvent event)
    {
        if (event.getSource() == tableList.getTable())
        {
            if (SwingUtilities.isLeftMouseButton(event))
            {
                int rowIndex = tableList.getTable().getSelectedRow();
                rowIndex = tableList.getTable().convertRowIndexToModel(rowIndex);
                
                if (rowIndex != -1)
                {
                    if (rowIndex < data.getTableArray().size())
                    {
                        TableInfo2 tableInfo2 = data.getTableArray().get(rowIndex);
                        fieldList.populateFieldArray(tableInfo2.getFields());
                        
                        JTabbedPane tabs = tableInfo.getTabs();
                        for (int tabIndex = 0; tabIndex < tabs.getTabCount(); ++tabIndex)
                        {
                            //Component component = tabs.getTabComponentAt(tabIndex);
                            switch(tabIndex)
                            {
                                case 0: // Table Properties
                                {
                                    Table tableProps = (Table)tabs.getComponentAt(tabIndex);
                                    tableProps.populateTableProps(tableInfo2);
                                    
                                } break;
                                
                                case 1: // Table Indexes
                                {
                                    
                                } break;
                                
                                case 2: // Table Documentation
                                {
                                    
                                } break;
                            }
                        }
                        
                    }
                }
            }
        }
        else if (event.getSource() == fieldList.getTable())
        {
            
        }
    }
    
    @Override
		public void mouseExited(MouseEvent event) {}
	
	@Override
		public void mouseEntered(MouseEvent event) {}
	
	@Override
		public void mousePressed(MouseEvent event) {}
	
	@Override
		public void mouseReleased(MouseEvent event) {}
}