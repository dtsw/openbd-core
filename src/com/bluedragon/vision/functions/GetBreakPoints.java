/* 
 *  Copyright (C) 2000 - 2010 TagServlet Ltd
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
package com.bluedragon.vision.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bluedragon.vision.engine.BreakPoint;
import com.bluedragon.vision.engine.CoreServer;
import com.naryx.tagfusion.cfm.engine.cfData;
import com.naryx.tagfusion.cfm.engine.cfNumberData;
import com.naryx.tagfusion.cfm.engine.cfQueryResultData;
import com.naryx.tagfusion.cfm.engine.cfSession;
import com.naryx.tagfusion.cfm.engine.cfStringData;
import com.naryx.tagfusion.cfm.engine.cfmRunTimeException;
import com.naryx.tagfusion.expression.function.functionBase;

public class GetBreakPoints extends functionBase {
	private static final long serialVersionUID = 1L;

	public GetBreakPoints(){
		min = max = 0;
	}
	
	public cfData execute( cfSession _session, List<cfData> parameters )throws cfmRunTimeException { 
	  cfQueryResultData queryResult = new cfQueryResultData(new String[] { "f", "line" }, "GetBreakPoints");
	  
	  HashMap<String,BreakPoint> fileBreakPoints	= CoreServer.thisInst.fileBreakPoints;

	  List<Map<String, cfData>> vResults	= new ArrayList<Map<String, cfData>>();
	  
	  Iterator<BreakPoint>	it	= fileBreakPoints.values().iterator();
	  while (it.hasNext() ){
	  	BreakPoint bp = it.next();
	  	
	  	HashMap<String, cfData>	sd = new HashMap<String, cfData>();

	  	sd.put( "f", 			new cfStringData( bp.realFile ) );
	  	sd.put( "line", 	new cfNumberData( bp.line ) );

	  	vResults.add( sd );
	  }
	  
	  queryResult.populateQuery( vResults );

	  return queryResult;
	}
	
}