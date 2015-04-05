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

import java.util.List;

import com.naryx.tagfusion.cfm.engine.cfArrayData;
import com.naryx.tagfusion.cfm.engine.cfBooleanData;
import com.naryx.tagfusion.cfm.engine.cfData;
import com.naryx.tagfusion.cfm.engine.cfSession;
import com.naryx.tagfusion.cfm.engine.cfmRunTimeException;
import com.naryx.tagfusion.cfm.xml.cfXmlDataArray;

public class isArray extends functionBase {

	private static final long serialVersionUID = 1L;

	public isArray() {
		max = 2;
		min = 1;
	}

  public String[] getParamInfo(){
		return new String[]{
			"object",
			"index"
		};
	}
	
	public java.util.Map getInfo(){
		return makeInfo(
				"decision", 
				"Determines if the object is a an array or xml object", 
				ReturnType.BOOLEAN );
	}
	
	public cfData execute(cfSession _session, List<cfData> parameters) throws cfmRunTimeException {
		cfData a = parameters.get(0);
		int dimensions = 0;
		if (parameters.size() > 1) {
			dimensions = a.getInt();
			a = parameters.get(1);
		}

		if (a instanceof cfXmlDataArray) {
			return cfBooleanData.TRUE;
		}

		if (a.getDataType() == cfData.CFARRAYDATA) {
			if (dimensions > 0) {
				return (dimensions == ((cfArrayData) a).getDimension() ? cfBooleanData.TRUE : cfBooleanData.FALSE);
			} else {
				return cfBooleanData.TRUE;
			}
		} else {
			return cfBooleanData.FALSE;
		}
	}
}