package ceid.netcins.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Andreas Loupasakis
 */

@XmlType
public class Scenario {

	@XmlElement
	public List<?> index;

	@XmlElement
	public List<?> search;

	@XmlElement
	public List<?> retrieve;

	@XmlElement
	public List<?> tag;

}
