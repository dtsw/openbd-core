/* 
 *  Copyright (C) 2000 - 2011 TagServlet Ltd
 *
 *  This file is part of Open BlueDragon (OpenBD) CFML Server Engine.
 *  
 *  OpenBD is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  Free Software Foundation,version 3.
 *  
 *  OpenBD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with OpenBD.  If not, see http://www.gnu.org/licenses/
 *  
 *  Additional permission under GNU GPL version 3 section 7
 *  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 *  README.txt @ http://www.openbluedragon.org/license/README.txt
 *  
 *  http://openbd.org/
 *  $Id: $
 */

package com.naryx.tagfusion.expression.function;

import java.io.File;
import java.util.List;

import org.aw20.collections.FastStack;

import com.naryx.tagfusion.cfm.engine.cfComponentData;
import com.naryx.tagfusion.cfm.engine.cfData;
import com.naryx.tagfusion.cfm.engine.cfSession;
import com.naryx.tagfusion.cfm.engine.cfStringData;
import com.naryx.tagfusion.cfm.file.cfFile;
import com.naryx.tagfusion.cfm.tag.cfTag;

/**
 * This returns the current active file
 */

public class getCurrentTemplatePath extends functionBase {
  
	private static final long serialVersionUID = 1L;

  public getCurrentTemplatePath(){
     min = max = 0;
  }
  
	public java.util.Map getInfo() {
		return makeInfo("system", "Returns the filepath of the current template", ReturnType.STRING);
	}

  
  public cfData execute( cfSession _session, List<cfData> parameters ) {
	  /* Generally we want the path to the current active file, but there are two exceptions.
	   * Exception #1: if we're executing within a CFC, then the current template path is the
	   * path to the ".cfc" file, even if we're executing a function within a superclass of that
	   * CFC (see bug #2924).
	   */
	  cfComponentData activeComponent = _session.getActiveComponentData();
	  if ( activeComponent != null ) {
		  return new cfStringData( activeComponent.getComponentPath() );
	  }
	  
  	cfFile activeFile = _session.activeFile();
  	
    /*  
     * Exception #2: when the call to getCurrentTemplate() originates 
     * from within a CFFUNCTION that is not within a CFCOMPONENT - in which case we need the
     * active file to be the file of the tag where this <CFFUNCTION> call originates from
     */
    if ( _session.executingUDF() ){
      FastStack<cfTag> tagStack = _session.getTagStack();
      for( int i = tagStack.size()-1; i >= 0 ; i-- ){
        cfTag nextTag = tagStack.elementAt( i );
        if ( nextTag.getTagName().equals( "CFFUNCTION" ) && !nextTag.parentTag.getTagName().equals( "CFCOMPONENT" ) ){
          nextTag = (cfTag) tagStack.elementAt( i-1 );
          activeFile = nextTag.getFile();
          break;       
        }
      }
    }
    
    return new cfStringData( activeFile.getPath().replace( '/', File.separatorChar ) );
  }
}
