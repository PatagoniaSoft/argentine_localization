/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package ar.com.ergio.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.compiere.model.MBPartner;
import org.compiere.model.MClient;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;

import ar.com.ergio.util.LAR_Utils;
/**
 *  Validator for Localization Argentina
 *
 *  @author Emiliano Gonzalez - Ergio=energia+evolucion - http://www.ergio.com.ar
 *  @version $Id: LAR_Validator.java,v 1.0 2011/11/04  egonzalez Exp $
**/

 public class LAR_Validator implements ModelValidator {
     /**
      *  Constructor.
      *  The class is instantiated when logging in and client is selected/known
      */
     public LAR_Validator ()
     {
         super ();
     }   //  MyValidator

     /** Logger          */
     private static CLogger log = CLogger.getCLogger(LAR_Validator.class);
     /** Client          */
     private int     m_AD_Client_ID = -1;

     /**
      *  Initialize Validation
      *  @param engine validation engine
      *  @param client client
      */
     public void initialize (ModelValidationEngine engine, MClient client)
     {
         //client = null for global validator
         if (client != null) {
             m_AD_Client_ID = client.getAD_Client_ID();
             log.info(client.toString());
         }
         else  {
             log.info("Initializing global validator: "+this.toString());
         }

         //  Tables to be monitored
         engine.addModelChange(MBPartner.Table_Name, this);

         //  Documents to be monitored
         engine.addDocValidate(MBPartner.Table_Name, this);

     }   //  initialize

     /**
      *  Model Change of a monitored Table.
      *  Called after PO.beforeSave/PO.beforeDelete
      *  when you called addModelChange for the table
      *  @param po persistent object
      *  @param type TYPE_
      *  @return error message or null
      *  @exception Exception if the recipient wishes the change to be not accept.
      */
     public String modelChange (PO po, int type) throws Exception
     {
         log.info(po.get_TableName() + " Type: "+type);
         String msg="";

         if (po.get_TableName().equals(MBPartner.Table_Name) && type == ModelValidator.TYPE_BEFORE_CHANGE) {
             MBPartner inv = (MBPartner) po;
             String cuit = (String) inv.get_Value("TaxID");
             String nroIIBB = ((String) inv.get_Value("DUNS")).replace("-","").trim();
             String tipoIIBB;
             String sqlTipoIIBB="select value from lco_isic where " +
             		"lco_isic_id=" + (Integer) inv.get_Value("LCO_ISIC_ID");
             PreparedStatement psTIIBB = DB.prepareStatement(sqlTipoIIBB, inv.get_TrxName());
             ResultSet rsTIIBB = null;
             try {
                 rsTIIBB = psTIIBB.executeQuery();
                 rsTIIBB.next();
                 tipoIIBB=rsTIIBB.getString(1).trim();
             } catch (SQLException e) {
                 throw e;
             } finally {
                 DB.close(rsTIIBB, psTIIBB);
                 rsTIIBB = null; psTIIBB = null;
             }
             if (!LAR_Utils.validateCUIT(cuit))
                 msg = msg + "ERROR: CUIT invalido ";
             if ((tipoIIBB.equals("D") && nroIIBB.length()!=8)
                 || (tipoIIBB.equals("CM") && nroIIBB.length()!=10)
                 || nroIIBB.equals(""))
                 msg = msg + "ERROR: número de IIBB invalido ";
             if (tipoIIBB.equals("")) msg = msg + "ERROR: tipo de IIBB invalido ";
         }
         return msg;
     }

     /**
      *  Validate Document.
      *  Called as first step of DocAction.prepareIt
      *  when you called addDocValidate for the table.
      *  Note that totals, etc. may not be correct.
      *  @param po persistent object
      *  @param timing see TIMING_ constants
      *  @return error message or null
      */
     public String docValidate (PO po, int timing)
     {
         log.info(po.get_TableName() + " Timing: "+timing);
         return null;
     }   //  docValidate


     /**
      *  User Login.
      *  Called when preferences are set
      *  @param AD_Org_ID org
      *  @param AD_Role_ID role
      *  @param AD_User_ID user
      *  @return error message or null
      */
     public String login (int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
     {
         log.info("AD_User_ID=" + AD_User_ID);
         return null;
     }   //  login

     /**
      *  Get Client to be monitored
      *  @return AD_Client_ID client
      */
     public int getAD_Client_ID()
     {
         return m_AD_Client_ID;
     }   //  getAD_Client_ID

 }   //  LAR_Validator