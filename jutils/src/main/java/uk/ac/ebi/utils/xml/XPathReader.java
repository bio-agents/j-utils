/*
 * __________
 * CREDITS
 * __________
 *
 * Team page: http://isatab.sf.net/
 * - Marco Brandizi (software engineer: ISAvalidator, ISAconverter, BII data management utility, BII model)
 * - Eamonn Maguire (software engineer: ISAcreator, ISAcreator configurator, ISAvalidator, ISAconverter,  BII data management utility, BII web)
 * - Nataliya Sklyar (software engineer: BII web application, BII model,  BII data management utility)
 * - Philippe Rocca-Serra (technical coordinator: user requirements and standards compliance for ISA software, ISA-tab format specification, BII model, ISAcreator wizard, ontology)
 * - Susanna-Assunta Sansone (coordinator: ISA infrastructure design, standards compliance, ISA-tab format specification, BII model, funds raising)
 *
 * Contributors:
 * - Manon Delahaye (ISA team trainee:  BII web services)
 * - Richard Evans (ISA team trainee: rISAtab)
 *
 *
 * ______________________
 * Contacts and Feedback:
 * ______________________
 *
 * Project overview: http://isatab.sourceforge.net/
 *
 * To follow general discussion: isatab-devel@list.sourceforge.net
 * To contact the developers: isaagents@googlegroups.com
 *
 * To report bugs: http://sourceforge.net/tracker/?group_id=215183&atid=1032649
 * To request enhancements:  http://sourceforge.net/tracker/?group_id=215183&atid=1032652
 *
 *
 * __________
 * License:
 * __________
 *
 * This work is licenced under the Creative Commons Attribution-Share Alike 2.0 UK: England & Wales License. To view a copy of this licence, visit http://creativecommons.org/licenses/by-sa/2.0/uk/ or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA.
 *
 * __________
 * Sponsors
 * __________
 * This work has been funded mainly by the EU Carcinogenomics (http://www.carcinogenomics.eu) [PL 037712] and in part by the
 * EU NuGO [NoE 503630](http://www.nugo.org/everyone) projects and in part by EMBL-EBI.
 */
package uk.ac.ebi.utils.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.ebi.utils.exceptions.ExceptionUtils;
import uk.ac.ebi.utils.exceptions.UnexpectedValueException;

/**
 * Created by the ISA team Modified from example here:
 * http://www.javabeat.net/tips/182-how-to-query-xml-using-xpath.html
 * 
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 * @author Marco Brandizi
 */
public class XPathReader
{

	private Document xmlDocument;
	private XPath xPath;

	public XPathReader ( Reader xmlReader )
	{
		this ( new InputSource ( xmlReader ) );
	}

	public XPathReader ( InputStream xmlStream ) {
		this ( new InputSource ( xmlStream ) );
	}

	public XPathReader ( String xmlString ) {
		this ( new StringReader ( xmlString ) );
	}
	
	public XPathReader ( InputSource xmlSource )
	{
		try
		{
			xmlDocument = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().parse ( xmlSource );
			xPath = XPathFactory.newInstance ().newXPath ();
		} 
		catch ( SAXException | ParserConfigurationException ex ) {
			ExceptionUtils.throwEx ( UnexpectedValueException.class, ex,
				"I/O error while using XPath: %s", ex.getMessage ()
			);
		} 
		catch ( IOException ex ) {
			ExceptionUtils.throwEx ( UncheckedIOException.class, ex,
				"I/O error while using XPath: %s", ex.getMessage ()
			);
		} 
	}

	@SuppressWarnings ( "unchecked" )
	public <T> T read ( String xpath, QName returnType )
	{
		try
		{
			XPathExpression xPathExpression = xPath.compile ( xpath );
			return (T) xPathExpression.evaluate ( xmlDocument, returnType );
		} 
		catch ( XPathExpressionException ex ) {
			throw new IllegalArgumentException ( "Internal error with the XPath Reader: " + ex.getMessage (), ex );
		}
	}
	
	public String readString ( String xpath ) {
		return (String) this.read ( xpath, XPathConstants.STRING );
	}

	public Double readDouble ( String xpath ) {
		return (Double) this.read ( xpath, XPathConstants.NUMBER );
	}

	public Optional<Double> readOptDouble ( String xpath ) {
		return Optional.ofNullable ( this.readDouble ( xpath ) );
	}

	public Float readFloat ( String xpath ) {
		return readOptDouble ( xpath ).map ( x -> x.floatValue () ).orElse ( null );
	}
		
	public Long readLong ( String xpath ) {
		return readOptDouble ( xpath ).map ( x -> x.longValue () ).orElse ( null );
	}
	
	public Integer readInt ( String xpath ) {
		return readOptDouble ( xpath ).map ( x -> x.intValue () ).orElse ( null );
	}
	
	public Node readNode ( String xpath ) {
		return (Node) this.read ( xpath, XPathConstants.NODE );
	}

	public NodeList readNodeList ( String xpath ) {
		return (NodeList) this.read ( xpath, XPathConstants.NODESET );
	}
}
