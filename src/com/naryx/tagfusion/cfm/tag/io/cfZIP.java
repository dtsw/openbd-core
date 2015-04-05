/* 
 * Copyright (C) 2000 - 2008 TagServlet Ltd
 *
 * This file is part of the BlueDragon Java Open Source Project.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.naryx.tagfusion.cfm.tag.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import com.naryx.tagfusion.cfm.engine.cfArgStructData;
import com.naryx.tagfusion.cfm.engine.cfData;
import com.naryx.tagfusion.cfm.engine.cfQueryResultData;
import com.naryx.tagfusion.cfm.engine.cfSession;
import com.naryx.tagfusion.cfm.engine.cfStructData;
import com.naryx.tagfusion.cfm.engine.cfmBadFileException;
import com.naryx.tagfusion.cfm.engine.cfmRunTimeException;
import com.naryx.tagfusion.cfm.tag.cfOptionalBodyTag;
import com.naryx.tagfusion.cfm.tag.cfTag;
import com.naryx.tagfusion.cfm.tag.cfTagReturnType;
import com.naryx.tagfusion.cfm.tag.tagLocator;
import com.naryx.tagfusion.cfm.tag.tagReader;
import com.naryx.tagfusion.expression.function.file.Unzip;
import com.naryx.tagfusion.expression.function.file.Zip;
import com.naryx.tagfusion.expression.function.file.ZipList;


public class cfZIP extends cfTag implements cfOptionalBodyTag, Serializable {

	static final long serialVersionUID = 1;

	public static final String DATA_BIN_KEY = "CFZIP_DATA";

	private static final String TAG_NAME = "CFZIP";
	private String endMarker = null;


	public java.util.Map getInfo() {
		return createInfo( "file", "The CFZIP tag is used to create, expand, and list the contents of zip files. CFZIP is optionally used in conjunction with the CFZIPPARAM tag." );
	}


	/*
	 * DOCUMENTATION FROM WIKI PAGE: http://wiki.openbluedragon.org/wiki/index.php/CFZIP
	 */
	public java.util.Map[] getAttInfo() {
		return new java.util.Map[] { createAttInfo( "ATTRIBUTECOLLECTION", "A structure containing the tag attributes", "", false ),
				createAttInfo( "ZIPFILE", "The path and file name of the zip file on which the action will be performed.", "", true ),
				createAttInfo( "SOURCE", "REQUIRED IF ACTION=CREATE. The path to a file or directory that will be included in the created zip file. " +
						"CFZIPPARAM can be used to specify multiple files or directories.", "", false ),
				createAttInfo( "ACTION", "The action to perform on the zip file. " +
						"Valid values are: CREATE (creates a new zip file containing the source file/directory), " +
						"EXTRACT (extracts the contents of an existing zip file), " +
						"LIST (generates a query object containing a list of the contents of an existing zip file), " +
						"DEFAULT:CREATE", "CREATE", false ),
				createAttInfo( "RECURSE", "If ACTION=CREATE, recurse indicates whether or not the subdirectories of the directory specified in the source attribute should be included in the zip file.", "FALSE", false ),
				createAttInfo( "FILTER", "A filter to apply against the files in the source directory. For example, *.txt would include only files with a .txt extension.", "", false ),
				createAttInfo( "COMPRESSIONLEVEL", "The compression level to apply when creating a zip file. The range is 0 (no compression) to 9 (maximum compression). DEFAULT:8", "", false ),
				createAttInfo( "NEWPATH", "If the source attribute is a file as opposed to a directory, " +
						"					the newpath attribute can be used to specify a new path for the file being included in the created zip file. If the source attribute is a directory the newpath attribute is ignored.", "", false ),
				createAttInfo( "PREFIX", "Used with a create action to prepend a prefix to the path of all files in the created zip file.", " ", false ),
				createAttInfo( "VARIABLE", "REQUIRED IF ACTION=LIST, The variable name in which the query generated by the list action will be stored.", "", false ),
				createAttInfo( "DESTINATION", "REQUIRED IF ACTION=EXTRACT, The directory into which the contents of the zip file will be extracted.", "", false ),
				createAttInfo( "FLATTEN", "When extracting the contents of an existing zip file, the flatten attribute indicates whether or not to retain the directory structure of the zip file. " +
						"A value of true indicates the directory structure will not be retained, " +
						"while a value of false indicates that the directory structure will be retained. DEFAULT:FALSE", "FALSE", false ),
				createAttInfo( "CHARSET", "Used to specify a character set to be used for file operations.", "Host machine default encoding", false ),
				createAttInfo( "OVERWRITE",
						"Zip: whether to overwrite the contents of a given file." +
								"true: overwrites all of the content given file if it exists." +
								"false: updates  entries of existing file and append new entries to the given file if it exists." +
								"UnZip: whether to overwrite the files already extracted:" +
								"true: If the files are already extracted at the destination specified, the file is overwritten." +
								"false: If the files are already extracted at the destination specified, the file is not overwritten and not extracted." +
								"DEFAULT: TRUE", "TRUE", false )

		};
	}


	protected void defaultParameters( String _tag ) throws cfmBadFileException {
		defaultAttribute( "RECURSE", "true" );
		defaultAttribute( "COMPRESSIONLEVEL", ZipArchiveOutputStream.DEFLATED );
		defaultAttribute( "PREFIX", "" );
		defaultAttribute( "OVERWRITE", "true" );
		defaultAttribute( "FLATTEN", "false" );
		defaultAttribute( "CHARSET", System.getProperty( "file.encoding" ) );

		parseTagHeader( _tag );

		if ( containsAttribute( "ATTRIBUTECOLLECTION" ) )
			return;

		if ( !containsAttribute( "ACTION" ) )
			throw newBadFileException( "Missing ACTION", "You need to specify a ACTION - valid actions are CREATE/ZIP, LIST or EXTRACT/UNZIP" );

		if ( !containsAttribute( "ZIPFILE" ) && !containsAttribute( "FILE" ) )
			throw newBadFileException( "Missing ZIPFILE/FILE", "You need to specify a ZIPFILE/FILE" );

	}


	protected cfStructData setAttributeCollection( cfSession _Session ) throws cfmRunTimeException {
		cfStructData attributes = super.setAttributeCollection( _Session );

		if ( !containsAttribute( "ACTION" ) )
			throw newBadFileException( "Missing ACTION", "You need to specify a ACTION - valid actions are CREATE/ZIP, LIST or EXTRACT/UNZIP" );

		if ( !containsAttribute( "ZIPFILE" ) && !containsAttribute( "FILE" ) )
			throw newBadFileException( "Missing ZIPFILE/FILE", "You need to specify a ZIPFILE/FILE" );

		return attributes;
	}


	public String getEndMarker() {
		return endMarker;
	}


	public void setEndTag() {
		endMarker = null;
	}


	public void lookAheadForEndTag( tagReader inFile ) {
		endMarker = ( new tagLocator( TAG_NAME, inFile ) ).findEndMarker();
	}


	public cfTagReturnType render( cfSession _Session ) throws cfmRunTimeException {
		try {
			cfStructData attributes = setAttributeCollection( _Session );

			List<cfZipItem> zipData = new ArrayList<cfZipItem>();
			_Session.setDataBin( DATA_BIN_KEY, zipData );

			realRender( attributes, _Session, zipData );
		} finally {
			_Session.deleteDataBin( DATA_BIN_KEY );
		}
		return cfTagReturnType.NORMAL;

	}// render()


	private void realRender( cfStructData _attributes, cfSession _Session, List<cfZipItem> _zipData ) throws cfmRunTimeException {
		renderToString( _Session );
		String mode = getDynamic( _attributes, _Session, "ACTION" ).getString().toUpperCase();
		cfData zipFile;
		if ( containsAttribute( _attributes, "ZIPFILE" ) ) {
			zipFile = getDynamic( _attributes, _Session, "ZIPFILE" );
		} else {
			zipFile = getDynamic( _attributes, _Session, "FILE" );
		}

		// CREATE/ZIP
		if ( mode.equals( "CREATE" ) || mode.equals( "ZIP" ) ) {
			createZip( _Session, zipFile, _attributes, _zipData );

			// EXTRACT/UNZIP
		} else if ( mode.equals( "EXTRACT" ) || mode.equals( "UNZIP" ) ) {
			extract( _Session, zipFile, _attributes );

			// LIST
		} else if ( mode.equals( "LIST" ) ) {
			listZip( _Session, zipFile, _attributes );

		} else {
			throw newRunTimeException( "Invalid ACTION specified - valid actions are CREATE, LIST or EXTRACT." );
		}

	}// render()


	private void createZip( cfSession _Session, cfData _zipFile, cfStructData _attributes, List<cfZipItem> _zipData ) throws cfmRunTimeException {
		// Instantiate zip class
		Zip zipFunction = new Zip();

		// Instantiate cfArgStructData class
		cfArgStructData functionArgs = new cfArgStructData( true );

		functionArgs.setData( "zipfile", _zipFile );

		if ( containsAttribute( _attributes, "SOURCE" ) ) {
			functionArgs.setData( "source", getDynamic( _attributes, _Session, "SOURCE" ) );

			if ( containsAttribute( _attributes, "RECURSE" ) ) {
				functionArgs.setData( "recurse", getDynamic( _attributes, _Session, "RECURSE" ) );
			}

			if ( containsAttribute( _attributes, "PREFIX" ) ) {
				functionArgs.setData( "prefix", getDynamic( _attributes, _Session, "PREFIX" ) );
			}

			if ( containsAttribute( _attributes, "OVERWRITE" ) ) {
				functionArgs.setData( "overwrite", getDynamic( _attributes, _Session, "OVERWRITE" ) );
			}

			if ( containsAttribute( _attributes, "FILTER" ) ) {
				functionArgs.setData( "filter", getDynamic( _attributes, _Session, "FILTER" ) );
			}

			if ( containsAttribute( _attributes, "NEWPATH" ) ) {
				functionArgs.setData( "newpath", getDynamic( _attributes, _Session, "NEWPATH" ) );
			}

			if ( containsAttribute( _attributes, "COMPRESSIONLEVEL" ) ) {
				functionArgs.setData( "compressionlevel", getDynamic( _attributes, _Session, "COMPRESSIONLEVEL" ) );
			}

			if ( containsAttribute( _attributes, "CHARSET" ) ) {
				functionArgs.setData( "charset", getDynamic( _attributes, _Session, "CHARSET" ) );
			}
		}

		zipFunction.execute( _Session, functionArgs, _zipData );

	}


	private void extract( cfSession _Session, cfData _zipFile, cfStructData _attributes ) throws cfmRunTimeException {
		// Instantiate unzip class
		Unzip unzipFunction = new Unzip();

		// Instantiate cfArgStructData class
		cfArgStructData functionArgs = new cfArgStructData( true );

		functionArgs.setData( "zipfile", _zipFile );

		if ( containsAttribute( _attributes, "DESTINATION" ) ) {
			functionArgs.setData( "destination", getDynamic( _attributes, _Session, "DESTINATION" ) );
		}

		if ( containsAttribute( _attributes, "CHARSET" ) ) {
			functionArgs.setData( "charset", getDynamic( _attributes, _Session, "CHARSET" ) );
		}

		if ( containsAttribute( _attributes, "FLATTEN" ) ) {
			functionArgs.setData( "flatten", getDynamic( _attributes, _Session, "FLATTEN" ) );
		}

		if ( containsAttribute( _attributes, "OVERWRITE" ) ) {
			functionArgs.setData( "overwrite", getDynamic( _attributes, _Session, "OVERWRITE" ) );
		}

		unzipFunction.execute( _Session, functionArgs );
	}


	private cfQueryResultData listZip( cfSession _Session, cfData _zipFile, cfStructData _attributes ) throws cfmRunTimeException {

		// Instantiate ziplist class
		ZipList zipListFunction = new ZipList();

		// Instantiate cfArgStructData class
		cfArgStructData functionArgs = new cfArgStructData( true );

		functionArgs.setData( "zipfile", _zipFile );

		if ( containsAttribute( _attributes, "CHARSET" ) ) {
			functionArgs.setData( "charset", getDynamic( _attributes, _Session, "CHARSET" ) );

		}

		cfQueryResultData fileData = (cfQueryResultData) zipListFunction.execute( _Session, functionArgs );
		if ( containsAttribute( _attributes, "VARIABLE" ) ) {
			String result = getDynamic( _attributes, _Session, "VARIABLE" ).getString();
			if ( result.length() == 0 ) {
				throw newRunTimeException( "Invalid RESULT attribute value. The name given must be at least 1 character in length." );
			}

			_Session.setData( result, fileData );
		}

		return fileData;
	}

}