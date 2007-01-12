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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.gmod.schema.cv.Cv;
import org.gmod.schema.cv.CvTerm;
import org.gmod.schema.dao.*;
import org.gmod.schema.general.Db;
import org.gmod.schema.general.DbXRef;
import org.gmod.schema.organism.Organism;
import org.gmod.schema.pub.Pub;
import org.gmod.schema.sequence.Feature;
import org.gmod.schema.sequence.FeatureCvTerm;
import org.gmod.schema.sequence.FeatureCvTermDbXRef;
import org.gmod.schema.sequence.FeatureCvTermProp;
import org.gmod.schema.sequence.FeatureCvTermPub;
import org.gmod.schema.sequence.FeatureDbXRef;

public abstract class GmodDAO implements SequenceDaoI, SchemaDaoI, OrganismDaoI, CvDaoI
{

  public abstract void merge(Object obj);
  public abstract void persist(Object obj);
  public abstract void delete(Object obj);
  
  
  /**
   * Return a list of FeatureCvterm's for a Feature, or a list
   * of all FeatureCvTerm's if Feature is null.
   * @param feature the Feature to retrieve associated FeatureCvTerm's
   * @return the FeatureCvTerm's
   */
  public abstract List getFeatureCvTermsByFeature(Feature feature);

  
  /**
   * Get a list of all PubDbXRef's
   * @return list of PubDbXRef's
   */
  public abstract List getPubDbXRef();
  
  /**
   * Get a list of all FeatureCvTermDbXRef's for a Feature, or a list
   * of all FeatureCvTermDbXRef's if Feature is null.
   * @param feature the Feature to retrieve associated FeatureCvTermDbXRef's
   * @return the FeatureCvTermDbXRef's
   */
  public abstract List getFeatureCvTermDbXRefByFeature(Feature feature);
  
  /**
   * Get a list of all FeatureCvTermPub's for a Feature, or a list
   * of all FeatureCvTermPub's if Feature is null.
   * @param feature the Feature to retrieve associated FeatureCvTermPub's
   * @return the FeatureCvTermPub's
   */
  public abstract List getFeatureCvTermPubByFeature(Feature feature);
  
  /**
   * Return the list of all feature_synonyms as Feature.featureSynonyms 
   * 
   * @return a (possibly empty) List<Features> of matching synonyms
   */
  public abstract List getAllFeatureSynonymsAsFeature();
  
  /**
   * Return a list of features that have this particular cvterm 
   * @param cvTermName the CvTerm name
   * @return a (possibly empty) List<Feature> of children
   */
  public List getFeaturesByCvTermNameAndCvName(String cvTermName, String cvName)
  {
    return null;
  }
  
  //////
  ////// OrganismDaoI
  //////
  //////
  /**
   * Get the organism corresponding to this id
   * 
   * @param id the organism id (primary key) to lookup by
   * @return the corresponding organism, or null
   */
  public Organism getOrganismById(int id)
  {
    return null;
  }

  /**
   * Get the organism corresponding to this common name 
   * 
   * @param commonName the short name to look up
   * @return the corresponding organism, or null
   */
  public Organism getOrganismByCommonName(String commonName)
  {
    return null;
  }

  /**
   * Get a list of the common name of all the organisms.
   * 
   * @return a (possibly empty) List<String> of all the organisms' common names
   */
  public List findAllOrganismCommonNames()
  {
    return null;
  }
 
  
  //////
  ////// CvDaoI
  //////
  //////
  
  public List getAllTermsInCvWithCount(Cv arg0)
  {
    // TODO Auto-generated method stub
    return null;
  }


  public CvTerm getCvTermByDbXRef(DbXRef arg0)
  {
    // TODO Auto-generated method stub
    return null;
  }


  public List getPossibleMatches(String arg0, Cv arg1, int arg2)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * Get a CV by id
   * 
   * @param id the cv id (primary key)
   * @return the corresponding Cv, or null
   */
  public Cv getCvById(int id)
  {
    return null;
  }

  // TODO Should this return a list or just one?
  /**
   * Retrieve a controlled vocabulary by its name
   * 
   * @param name the name to lookup
   * @return the List<Cv> of matches, or null
   */
  public List getCvByName(String name)
  {
    return null;
  }

  /**
   * Retrieve a CvTerm by id
   * 
   * @param id then cvterm id (primary key)
   * @return the corresponding CvTerm, or null
   */
  public CvTerm getCvTermById(int id)
  {
    return null;
  }


  // TODO Should this return a list or just one?
  /**
   * Retrieve a named CvTerm from a given Cv
   * 
   * @param cvTermName the name of the cvterm
   * @param cv the controlled vocabulary this cvterm is part of
   * @return a (possibly empty) list of matching cvterms
   */
  public List getCvTermByNameInCv(String cvTermName, Cv cv)
  {
    return null;
  }


  /**
   * Retrieve a CvTerm from the Gene Ontology
   * 
   * @param value the 
   * @return the corresponding CvTerm, or null
   */
  public CvTerm getGoCvTermByAcc(String value)
  {
    return null;
  }


  /**
   * Retrieve a CvTerm from the Gene Ontology via it's database entry
   * 
   * @param id the database name eg GO:123456
   * @return the corresponding CvTerm, or null
   */
  public CvTerm getGoCvTermByAccViaDb(final String id)
  {
    return null;
  }
  

  public List getFeatureCvTermsByFeatureAndCvTermAndNot(Feature arg0, CvTerm arg1, boolean arg2)
  {
    // TODO Auto-generated method stub
    return null;
  }


  public FeatureDbXRef getFeatureDbXRefByFeatureAndDbXRef(Feature arg0, DbXRef arg1)
  {
    // TODO Auto-generated method stub
    return null;
  }


  public boolean existsNameInOntology(String arg0, Cv arg1)
  {
    // TODO Auto-generated method stub
    return false;
  }
  
  
  
  
  
  //
  //
  //
  //
  
  
  /**
   * Find a dbxref in the database and retrieve the associated 
   * dbxref_id and db_id
   * @param dbXRef
   * @return
   */
  protected DbXRef loadDbXRef(DbXRef dbXRef)
  {
    Integer db_id = getDbId(dbXRef.getDb());

    if(db_id == null)
      throw new RuntimeException("No database called " + 
          dbXRef.getDb().getName() +
          " found -check the spelling!");
    
    dbXRef.getDb().setDbId(db_id.intValue());
     
    Integer dbxref_id = getDbXRefId(dbXRef);
    if(dbxref_id == null)
    {
      dbXRef.setVersion("1");
      // create a new accession entry in dbxref
      insertDbXRef(dbXRef);
      // now get the new dbxref_id
      dbxref_id = getDbXRefId(dbXRef);
    }

    dbXRef.setDbXRefId(dbxref_id.intValue());
    return dbXRef;
  }
  
  /**
   * Find a Pub if it exists. If the Pub does not exist then create
   * one.
   * @param pub
   * @return
   */
  protected Pub loadPub(Pub pub)
  {
    Pub pubResult = getPubByUniqueName(pub);
    
    if(pubResult == null)
    {
      // define the pub.type_id !!!!!!! TODO !!!!!!!!!
      //
      insertPub(pub);
      pubResult = getPubByUniqueName(pub);
    }
    
    return pubResult;
  }
  
  protected void insertAllFeatureCvTerm(final FeatureCvTerm feature_cvterm)
  {
    // get the pub_id and create a new Pub if necessary
    if(feature_cvterm.getPub() != null)
      feature_cvterm.setPub( loadPub(feature_cvterm.getPub()) );
    
    insertFeatureCvTerm(feature_cvterm); 
    
    //
    // get the current feature_id sequence value
    int feature_cvterm_id = getCurrval("feature_cvterm_feature_cvterm_id_seq");
    feature_cvterm.setFeatureCvTermId(feature_cvterm_id);
    
    if(feature_cvterm.getFeatureCvTermProps() != null)
    {
      Collection featureCvTermProps = feature_cvterm.getFeatureCvTermProps();
      Iterator it = featureCvTermProps.iterator();
      while(it.hasNext())
      {
        FeatureCvTermProp featureCvTermProp = (FeatureCvTermProp)it.next();
        featureCvTermProp.setFeatureCvTerm(feature_cvterm);

        insertFeatureCvTermProp(featureCvTermProp);
      }
    }
    
    // feature_cvterm_pub's
    if(feature_cvterm.getFeatureCvTermPubs() != null)
    {
      Collection featureCvTermPubs = feature_cvterm.getFeatureCvTermPubs();
      Iterator it = featureCvTermPubs.iterator();
      while(it.hasNext())
      {
        FeatureCvTermPub featureCvTermPub = (FeatureCvTermPub)it.next();
        featureCvTermPub.setFeatureCvTerm(feature_cvterm);

        // get the pub_id and create a new Pub if necessary
        featureCvTermPub.setPub( loadPub(featureCvTermPub.getPub()) );
        
        insertFeatureCvTermPub(featureCvTermPub);
      }
    
    }
    // feature_cvterm_dbxref's
    if(feature_cvterm.getFeatureCvTermDbXRefs() != null)
    {
      Collection featureCvTermDbXRefs = feature_cvterm.getFeatureCvTermDbXRefs();
      Iterator it = featureCvTermDbXRefs.iterator();
      while(it.hasNext())
      {
        FeatureCvTermDbXRef featureCvTermDbXRef = (FeatureCvTermDbXRef)it.next();
        featureCvTermDbXRef.setFeatureCvTerm(feature_cvterm);
        
        // look for dbxref in the database
        DbXRef dbxref = loadDbXRef(featureCvTermDbXRef.getDbXRef());
        insertFeatureCvTermDbXRef(featureCvTermDbXRef);
      }
    }
  }
  
  protected abstract Integer getDbId(Db db);
  protected abstract Integer getDbXRefId(DbXRef dbXRef);
  protected abstract void insertDbXRef(DbXRef dbXRef);
  protected abstract Pub getPubByUniqueName(Pub pub);
  protected abstract void insertPub(Pub pub);
  protected abstract void insertFeatureCvTerm(final FeatureCvTerm feature_cvterm);
  protected abstract int getCurrval(String seq_id);
  protected abstract void insertFeatureCvTermProp(FeatureCvTermProp featureCvTermProp);
  protected abstract void insertFeatureCvTermPub(FeatureCvTermPub featureCvTermPub);
  protected abstract void insertFeatureCvTermDbXRef(FeatureCvTermDbXRef featureCvTermDbXRef);
}