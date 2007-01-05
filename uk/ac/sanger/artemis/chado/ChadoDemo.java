/* 
 *
 * created: 2006
 *
 * This file is part of Artemis
 *
 * Copyright (C) 2006  Genome Research Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package uk.ac.sanger.artemis.chado;

import org.gmod.schema.cv.CvTerm;
import org.gmod.schema.sequence.Feature;
import org.gmod.schema.sequence.FeatureCvTerm;
import org.gmod.schema.sequence.FeatureDbXRef;
import org.gmod.schema.sequence.FeatureSynonym;
import org.gmod.schema.sequence.FeatureProp;
import org.gmod.schema.sequence.FeatureLoc;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.Box;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import uk.ac.sanger.artemis.components.genebuilder.cv.CVPanel;
import uk.ac.sanger.artemis.io.GFFStreamFeature;
import uk.ac.sanger.artemis.io.ReadFormatException;
import uk.ac.sanger.artemis.util.DatabaseDocument;
import uk.ac.sanger.artemis.util.ByteBuffer;


/**
 * Chado data access example code. This searches for features by their
 * uniquename and returns their properties and attributes.
 * 
 * @author tjc
 * 
 */
public class ChadoDemo
{
  /** JDBC DAO */
  private JdbcDAO jdbcDAO = null;

  /** iBatis DAO */
  private IBatisDAO connIB = null;

  /** database URL */
  private String location;

  /** password fields */
  private JPasswordField pfield;

  /** results table */
  private JTable result_table;

  /** feature attributes */
  private JTextArea attr_text;

  /** <code>List</code> of <code>Feature</code> objects */
  private List featureList;
  
  /** row data containing results */
  private String rowData[][];

  private static Hashtable cvterms;
  
  private List pubDbXRefs[];
  
  private JTabbedPane tabbedPane;
  
  /** 
   * Chado demo
   */
  public ChadoDemo()
  {
    uk.ac.sanger.artemis.components.Splash.initLogger();
    
    try
    {
      setLocation();
      final GmodDAO dao = getDAO();
      showFeatureSearchPanel(dao);
      //getCvterm(dao);
    }
    catch(java.net.ConnectException ce)
    {
      ce.printStackTrace();
    }
    catch(SQLException sqlExp)
    {
      JOptionPane.showMessageDialog(null, "SQL Problems...\n"
          + sqlExp.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
      sqlExp.printStackTrace();
    }

  }

  /**
   * Display a window for searching for features.
   * 
   * @throws java.net.ConnectException
   * @throws SQLException
   */
  private void showFeatureSearchPanel(final GmodDAO dao)
      throws java.net.ConnectException, SQLException
  {
    int index = location.indexOf('=') + 1;
    String schema = location.substring(index);

    final List schemas = dao.getSchema();

    Vector v_schemas = new Vector(schemas);
    v_schemas.add(0, "All");

    final JPanel panel = new JPanel(new BorderLayout());
    final JList schema_list = new JList(v_schemas);
    schema_list.setSelectedValue(schema, true);
    if(schema_list.getSelectedIndex() == -1)
      schema_list.setSelectedValue("All", true);

    Box xbox2 = Box.createHorizontalBox();
    JScrollPane jsp = new JScrollPane(schema_list);

    Box xbox = Box.createHorizontalBox();
    final JTextField gene_text = new JTextField(20);
    gene_text.setText("SPAC19G12.09:pep");
    xbox.add(gene_text);
    gene_text.selectAll();

    result_table = new JTable();

    final JScrollPane scrollpane = new JScrollPane(result_table);
    scrollpane.setPreferredSize(new Dimension(600, 250));

    //panel.add(scrollpane, BorderLayout.CENTER);
    pubDbXRefs = new List[schemas.size()];
    
    JButton findButt = new JButton("FIND");
    findButt.addActionListener(new ActionListener()
    {
      private String columnNames[] = { "schema", "name", "type",
          "feature ID", "loc", "strand", "time modified" };

      
      public void actionPerformed(ActionEvent event)
      {
        String search_gene = gene_text.getText();
        String schema = (String)schema_list.getSelectedValue();
        List schema_search;
        if(schema.equalsIgnoreCase("All"))
          schema_search = schemas;
        else
        {
          schema_search = new Vector();
          schema_search.add(schema);
        }

        try
        {
          rowData = search(search_gene, schema_search, dao);

          result_table = new JTable(rowData, columnNames);
          result_table.getSelectionModel().addListSelectionListener(
                                         new SelectionListener());
          result_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

          result_table.addMouseListener(new MouseAdapter()
          {
            public void mouseClicked(MouseEvent e)
            {
              int row = result_table.getSelectedRow();
              reset(location, rowData[row][0]);

              try
              {
                GmodDAO dao2 = getDAO();

                if(pubDbXRefs[row] == null)
                  pubDbXRefs[row] = dao2.getPubDbXRef();
                showAttributes(dao2, pubDbXRefs[row]);
              }
              catch(ConnectException e1)
              {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              }
              catch(SQLException e1)
              {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              }
            }
          });

          scrollpane.setViewportView(result_table);
        }
        catch(SQLException sqlExp)
        {
          JOptionPane.showMessageDialog(null, "SQL Problems...\n"
              + sqlExp.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
          sqlExp.printStackTrace();
        }
        catch(ConnectException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
    xbox.add(findButt);
    xbox.add(Box.createHorizontalGlue());
    
    Box ybox = Box.createVerticalBox();
    xbox2.add(scrollpane);
    xbox2.add(jsp);
    xbox2.add(Box.createHorizontalGlue());
    
    ybox.add(xbox);
    ybox.add(xbox2);
    
    panel.add(ybox, BorderLayout.NORTH);
    
    attr_text = new JTextArea();
    JScrollPane jsp_attr = new JScrollPane(attr_text);
    jsp_attr.setPreferredSize(new Dimension(600, 150));
    
    
    tabbedPane = new JTabbedPane();
    tabbedPane.add("Core", jsp_attr);
    
    panel.add(tabbedPane, BorderLayout.CENTER);

    JFrame frame = new JFrame("Feature Search");
    frame.getContentPane().add(panel);
    frame.setJMenuBar(getJMenuBar(dao));
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Build a <code>JMenuBar</code>.
   * 
   * @return a <code>JMenuBar</code>
   */
  public JMenuBar getJMenuBar(final GmodDAO dao)
  {
    JMenuBar mbar = new JMenuBar();
    JMenu file = new JMenu("File");
    mbar.add(file);

    JMenuItem exit = new JMenuItem("Exit");
    exit.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        System.exit(0);
      }
    });
    file.add(exit);

    return mbar;
  }

  /**
   * Show the attributes of a selected feature.
   */
  private void showAttributes(final GmodDAO dao,
                              final List pubDbXRefs)
  {
    int row = result_table.getSelectedRow();
    ByteBuffer attr_buff = new ByteBuffer();
    ByteBuffer gff_buff = new ByteBuffer();
    
    Feature chado_feature = (Feature)featureList.get(row);
    
    
    Collection locs = chado_feature.getFeatureLocsForFeatureId();
    
    int fmin;
    int fmax;
    FeatureLoc loc;
    
    if(locs != null && locs.size() > 0)
    {
      Iterator it = locs.iterator();
      loc = (FeatureLoc)it.next();
    }
    else
      loc = chado_feature.getFeatureLoc();
    
    fmin = loc.getFmin().intValue() + 1;
    fmax = loc.getFmax().intValue();
    
    gff_buff.append(chado_feature.getUniqueName()+"\t"+
                    rowData[row][0]+"\t"+
                    rowData[row][2]+"\t"+
                    fmin+"\t"+
                    fmax+"\t"+
                    ".\t"+
                    loc.getStrand()+"\t"+
                    loc.getPhase()+"\t");
    
    
    String uniquename = chado_feature.getUniqueName();
    
    attr_buff.append("/ID="+uniquename+"\n");
    List attributes = (List)chado_feature.getFeatureProps();
    List dbxrefs = dao.getFeatureDbXRefsByFeatureUniquename(uniquename);
    List featureCvTerms       = dao.getFeatureCvTermsByFeature(chado_feature);
    List featureCvTermDbXRefs = dao.getFeatureCvTermDbXRefByFeature(chado_feature);
    List featureCvTermPubs    = dao.getFeatureCvTermPubByFeature(chado_feature);
    
    if(dbxrefs.size() > 0)
    {
      attr_buff.append("/Dbxref=");
      //gff_buff.append("Dbxref=");
      for(int i = 0; i < dbxrefs.size(); i++)
      {
        FeatureDbXRef dbxref = (FeatureDbXRef) dbxrefs.get(i);
        attr_buff.append(dbxref.getDbXRef().getDb().getName() + ":"
            + dbxref.getDbXRef().getAccession() + "; ");
        
        //gff_buff.append(dbxref.getDbXRef().getDb().getName() + ":"
        //    + dbxref.getDbXRef().getAccession() + "; ");
      }
      attr_buff.append("\n");
    }

    Collection synonyms = chado_feature.getFeatureSynonyms();

    // append synonyms
    if(synonyms != null && synonyms.size() > 0)
    {
      FeatureSynonym alias;

      System.out.println("\n\nNow get synonym & type_id.......\n\n");
      Iterator it = synonyms.iterator();
      while(it.hasNext())
      {
        alias = (FeatureSynonym)it.next();
        attr_buff.append("/");
        attr_buff.append(alias.getSynonym().getCvTerm().getName() + "=");
        attr_buff.append(alias.getSynonym().getName());
        attr_buff.append(";");
        attr_buff.append("\n");
        
        //gff_buff.append(alias.getSynonym().getCvTerm().getName() + "=");
        //gff_buff.append(alias.getSynonym().getName()+";");
      }
    }

    if(attributes != null)
    {
      Iterator it = attributes.iterator();
      while(it.hasNext())
      {
        FeatureProp featprop = (FeatureProp)it.next();

        attr_buff.append("/" + featprop.getCvTerm().getName() + "="
            + GFFStreamFeature.decode(featprop.getValue()) + "\n");
        
        //gff_buff.append(featprop.getCvTerm().getName() + "="
        //    + GFFStreamFeature.decode(featprop.getValue()));
      }
    }
    
    if(featureCvTerms != null)
    {
      for(int j=0; j<featureCvTerms.size(); j++)
      {
        attr_buff.append("/");
        FeatureCvTerm feature_cvterm = (FeatureCvTerm)featureCvTerms.get(j);

        DatabaseDocument.appendControlledVocabulary(attr_buff, dao, feature_cvterm,
            featureCvTermDbXRefs, featureCvTermPubs, pubDbXRefs);
        
        DatabaseDocument.appendControlledVocabulary(gff_buff, dao, feature_cvterm,
            featureCvTermDbXRefs, featureCvTermPubs, pubDbXRefs);
        attr_buff.append("\n");
      }
    }
    
    
    attr_text.setText( GFFStreamFeature.decode((new String(attr_buff.getBytes()))) );
    
    if(tabbedPane.getTabCount() == 1)
      try
      {
        GFFStreamFeature gff_feature = new  GFFStreamFeature(new String(gff_buff.getBytes()));
        CVPanel cvPanel = new CVPanel(new uk.ac.sanger.artemis.Feature(gff_feature));
        JScrollPane jsp = new JScrollPane(cvPanel);
        
        tabbedPane.add("CV Terms", jsp);
      }
      catch(ReadFormatException e)
      {
      // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }

  
  /**
   * Search for a feature/gene name from a <code>List</code> of schemas.
   * 
   * @param search_gene
   *          the feature name
   * @param schema_search
   *          the <code>List</code> to search
   * @param dao
   *          the data access object
   * @return string array of results
   * @throws SQLException
   * @throws ConnectException
   */
  public String[][] search(final String search_gene, final List schema_search,
      GmodDAO dao) throws SQLException, ConnectException
  {
    final String search_name = search_gene.replaceAll("[*]","%");
    Feature feature = new Feature();
    //feature.setUniquename(search_gene.replaceAll("[*]","%"));
    featureList = new Vector();
    
    for(int i=0; i<schema_search.size(); i++)
    {
      reset(location, (String)schema_search.get(i));
      dao = getDAO();
      featureList.addAll(dao.getFeaturesByAnyCurrentName(search_name));
    }
    
    String rowData[][] = new String[featureList.size()][7];

    for(int i = 0; i < featureList.size(); i++)
    {
      feature = (Feature) featureList.get(i);
      
      // assume only one featureloc
      Collection locs = feature.getFeatureLocsForFeatureId();
      
      if(locs != null && locs.size() > 0)
      {
        Iterator it = locs.iterator();
        FeatureLoc loc = (FeatureLoc)it.next();
        int fmin = loc.getFmin().intValue() + 1;
        int fmax = loc.getFmax().intValue();
        rowData[i][4] = fmin + "..." + fmax;
        rowData[i][5] = Integer.toString(loc.getStrand().shortValue());
      }
      else if(feature.getFeatureLoc() != null)
      {
        FeatureLoc loc = feature.getFeatureLoc();
        int fmin = loc.getFmin().intValue() + 1;
        int fmax = loc.getFmax().intValue();
        rowData[i][4] = fmin + "..." + fmax;
        rowData[i][5] = Integer.toString(loc.getStrand().shortValue());
      }
      
      String schema = feature.getOrganism().getAbbreviation().toLowerCase();
      int ind = schema.indexOf('.');
      if(ind > 0)
        schema = schema.substring(0,ind)+schema.substring(ind+1);
      
      System.out.println("\n\nNow get feature type_id.......\n\n");
      rowData[i][0] = schema;
      rowData[i][1] = feature.getUniqueName();
      rowData[i][2] = feature.getCvTerm().getName();
      //rowData[i][2] = (String)cvterm.get(new Long(feature.getType_id()));
      rowData[i][3] = Integer.toString(feature.getFeatureId());
      rowData[i][6] = feature.getTimeLastModified().toString();
    
    }
    return rowData;
  }

  /**
   * Reset the schema.
   * @param location
   * @param schema
   */
  private void reset(String location, String schema)
  {
    if(!location.endsWith("="+schema))
    {
      int index = location.lastIndexOf('=');
      location = location.substring(0,index+1) + schema;
      connIB  = null;
      jdbcDAO = null;
      System.setProperty("chado", location);
    }
  }
  
  /**
   * Get the data access object (DAO).
   * 
   * @return data access object
   */
  private GmodDAO getDAO() throws java.net.ConnectException, SQLException
  {
    if(System.getProperty("ibatis") == null)
    {
      if(jdbcDAO == null)
        jdbcDAO = new JdbcDAO(location, pfield);
      return jdbcDAO;
    }
    else
    {
      if(connIB == null)
        connIB = new IBatisDAO(pfield);
      return connIB;
    }
  }

  /**
   * Set the database location as:
   * jdbc:postgresql://localhost:13001/chadoCVS?user=es2
   * 
   * @return true if location chosen
   */
  private boolean setLocation()
  {
    Container bacross = new Container();
    bacross.setLayout(new GridLayout(6, 2, 5, 5));

    JLabel lServer = new JLabel("Server : ");
    bacross.add(lServer);
    JTextField inServer = new JTextField("localhost");
    bacross.add(inServer);

    JLabel lPort = new JLabel("Port : ");
    bacross.add(lPort);
    JTextField inPort = new JTextField("5432");
    bacross.add(inPort);

    JLabel lDB = new JLabel("Database : ");
    bacross.add(lDB);
    JTextField inDB = new JTextField("chado");
    bacross.add(inDB);

    JLabel lUser = new JLabel("User : ");
    bacross.add(lUser);
    JTextField inUser = new JTextField("afumigatus");
    bacross.add(inUser);

    JLabel lpasswd = new JLabel("Password : ");
    bacross.add(lpasswd);
    pfield = new JPasswordField(16);
    bacross.add(pfield);

    // given -Dchado=localhost:port/dbname?username
    if(System.getProperty("chado") != null)
    {
      String db_url = System.getProperty("chado").trim();
      int index;
      if((index = db_url.indexOf(":")) > -1)
      {
        inServer.setText(db_url.substring(0, index));
        int index2;
        if((index2 = db_url.indexOf("/")) > -1)
        {
          inPort.setText(db_url.substring(index + 1, index2));
          int index3;
          if((index3 = db_url.indexOf("?")) > -1)
          {
            inDB.setText(db_url.substring(index2 + 1, index3));
            inUser.setText(db_url.substring(index3 + 1));

            /*
             * if(!prompt_user) { location = "jdbc:postgresql://"
             * +inServer.getText().trim()+ ":" +inPort.getText().trim()+ "/"
             * +inDB.getText().trim()+ "?user=" +inUser.getText().trim(); return
             * true; }
             */
          }
        }
      }
    }

    int n = JOptionPane.showConfirmDialog(null, bacross,
        "Enter Database Address", JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE);
    if(n == JOptionPane.CANCEL_OPTION)
      return false;

    location = "jdbc:postgresql://" + inServer.getText().trim() + ":"
        + inPort.getText().trim() + "/" + inDB.getText().trim() + "?user="
        + inUser.getText().trim();

    return true;
  }

  /**
   * Look up the cvterm_id for a controlled vocabulary name.
   * @param name  
   * @return
   */
  public static Long getCvtermID(String name)
  {
    Enumeration enum_cvterm = cvterms.keys();
    while(enum_cvterm.hasMoreElements())
    {
      Long key = (Long)enum_cvterm.nextElement();
      if(name.equals( ((CvTerm)cvterms.get(key)).getName() ))
        return key;
    }
    return null;
  }

  /**
   * Look up a cvterm name from the collection of cvterms.
   * @param id  a cvterm_id  
   * @return    the cvterm name
   */
  private static String getCvtermName(int id, GmodDAO dao)
  {
    return getCvTerm(id, dao).getName();
  }
  
  private static CvTerm getCvTerm(int id, GmodDAO dao)
  {
    if(cvterms == null)
      getCvterms(dao);

    return (CvTerm)cvterms.get(new Integer(id));
  }
  
  public static CvTerm getCvTermByCvTermName(String cvterm_name)
  {
    Enumeration enum_cvterm = cvterms.elements();
    while(enum_cvterm.hasMoreElements())
    {
      CvTerm cvterm = (CvTerm)enum_cvterm.nextElement();
      if(cvterm_name.equals( cvterm.getName() ))
        return cvterm;
    }
    
    return null;
  }

  /**
   * Look up cvterms names and id and return in a hashtable.
   * @param dao the data access object
   * @return    the cvterm <code>Hashtable</code>
   */
  private static Hashtable getCvterms(GmodDAO dao)
  {
    cvterms = new Hashtable();

    try
    {
      List cvterm_list = dao.getCvTerms();
      Iterator it = cvterm_list.iterator();

      while(it.hasNext())
      {
        CvTerm cvterm = (CvTerm)it.next();
        cvterms.put(new Integer(cvterm.getCvTermId()), cvterm);
      }
    }
    catch(RuntimeException sqle)
    {
      System.err.println("SQLException retrieving CvTerms");
      System.err.println(sqle);
    }

    return cvterms;
  }
  
  
  public class SelectionListener implements ListSelectionListener
  {
    
    public void valueChanged(ListSelectionEvent e)
    {
      int row = result_table.getSelectedRow();
      reset(location, rowData[row][0]);

      try
      {
        GmodDAO dao2 = getDAO();
        if(pubDbXRefs[row] == null)
          pubDbXRefs[row] = dao2.getPubDbXRef();
        showAttributes(dao2, pubDbXRefs[row]);
      }
      catch(ConnectException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      catch(SQLException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }
  }
  
  

  public static void main(String args[])
  {
    new ChadoDemo();
  }
}
