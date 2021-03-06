/* 
 *  Copyright (C) 2000 - 2008 TagServlet Ltd
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
 *  http://www.openbluedragon.org/
 */

package com.naryx.tagfusion.expression.function;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.nary.io.FileUtils;
import com.naryx.tagfusion.cfm.engine.cfData;
import com.naryx.tagfusion.cfm.engine.cfProfileSectionData;
import com.naryx.tagfusion.cfm.engine.cfSession;
import com.naryx.tagfusion.cfm.engine.cfStringData;
import com.naryx.tagfusion.cfm.engine.cfmRunTimeException;

public class setProfileString extends functionBase {

	private static final long serialVersionUID = 1L;

	public setProfileString() {
		min = max = 4;
	}

	public String[] getParamInfo(){
		return new String[]{
			"ini path",
			"section key",
			"property",
			"value"
		};
	}
	
	public java.util.Map getInfo(){
		return makeInfo(
				"engine", 
				"Sets the given property/value within the INI file given", 
				ReturnType.STRING );
	}

	
	public cfData execute(cfSession _session, List<cfData> parameters) throws cfmRunTimeException {
		String iniPath = parameters.get(3).getString();
		String sectionKey = parameters.get(2).getString();
		String propertyKey = parameters.get(1).getString();
		String propertyValue = parameters.get(0).getString();
		File file = null;

		try {
			// If .ini file not exists, create one
			if (!FileUtils.exists(iniPath)) {
				file = FileUtils.getFile(_session, iniPath, false);
				file.createNewFile();
			}
			
			cfProfileSectionData profileSectionData = new cfProfileSectionData(iniPath);
			profileSectionData.setProperty(sectionKey, propertyKey, propertyValue);
		} catch (IOException e) {
			throwException(_session, e.getMessage());
		}
		return new cfStringData("");
	}
}
